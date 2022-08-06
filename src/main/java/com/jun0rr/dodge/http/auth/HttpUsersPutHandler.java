/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.DistinctStream;
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
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpUsersPutHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpUsersPutHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/users/?", HttpMethod.PUT);
  
  public static HttpUsersPutHandler get() {
    return new HttpUsersPutHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf cont = ((HttpContent)x.message()).content();
      String json = cont.toString(StandardCharsets.UTF_8);
      ReferenceCountUtil.safeRelease(x.message());
      try {
        CreatingUser u = ((Http)x.channel()).gson().fromJson(json, CreatingUser.class);
        User user = u.toUser();
        if(!user.getGroups().isEmpty()) {
          List<Group> gs = new LinkedList<>();
          user.setGroups(DistinctStream.of(user.getGroups().stream())
              .sortBy(Group::getName)
              .distinctBy(Group::getName)
              .stream()
              .collect(Collectors.toList()))
              .getGroups().stream()
              .filter(g->x.channel().storage().groups().noneMatch(h->h.getName().equals(g.getName())))
              .forEach(x.channel().storage()::set);
        }
        if(user.getGroups().stream()
            .noneMatch(g->Storage.GROUP_AUTH.getName().equals(g.getName()))) {
          user.getGroups().add(Storage.GROUP_AUTH);
        }
        x.channel().storage().set(user);
        json = ((Http)x.channel()).gson().toJson(user);
        ByteBuf buf = x.context().alloc().buffer(json.length());
        buf.writeCharSequence(json, StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED, buf);
        res.headers()
            .add(new JsonContentHeader(buf.readableBytes()))
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
      catch(Exception e) {
        HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
            .put("type", e.getClass())
            .put("cause", e.getCause()));
      }
    }
  }
  
}
