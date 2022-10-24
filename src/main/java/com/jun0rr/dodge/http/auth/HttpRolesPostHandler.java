/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.util.ErrMessage;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRequestException;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRolesPostHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpRolesPostHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/roles/?", HttpMethod.POST);
  
  public static HttpRolesPostHandler get() {
    return new HttpRolesPostHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf buf = ((HttpContent)x.message()).content();
      try {
        String json = buf.toString(StandardCharsets.UTF_8);
        Role role = ((Http)x.channel()).gson().fromJson(json, Role.class);
        ReferenceCountUtil.safeRelease(x.message());
        if(role.route().methods() == null || role.route().methods().isEmpty()) {
          throw new HttpRequestException(new ErrMessage(HttpResponseStatus.BAD_REQUEST, "HttpRoute methods missing"));
        }
        role.setGroups(role.groups().stream()
            .map(Group::getName)
            .map(n->x.channel().storage().groups().filter(g->g.getName().equals(n)).findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()));
        x.channel().storage().set(role);
        json = ((Http)x.channel()).gson().toJson(role);
        buf = x.context().alloc().buffer(json.length());
        buf.writeCharSequence(json, StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED, buf);
        res.headers()
            .add(new JsonContentHeader(buf.readableBytes()))
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
      catch(HttpRequestException e) {
        HttpConstants.sendError(x, e.errMessage());
      }
      catch(Exception e) {
        ErrMessage msg = new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
            .put("type", e.getClass())
            .put("cause", e.getCause());
        HttpConstants.sendError(x, msg);
      }
    }
  }
  
}
