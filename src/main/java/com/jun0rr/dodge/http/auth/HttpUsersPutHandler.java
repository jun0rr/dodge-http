/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
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
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf cont = ((HttpContent)x.message()).content();
      String json = cont.toString(StandardCharsets.UTF_8);
      CreatingUser u = ((Http)x.channel()).gson().fromJson(json, CreatingUser.class);
      if(!u.getGroups().isEmpty()) {
        u.getGroups().stream()
            .filter(g->!Storage.GROUP_AUTH.getName().equals(g.getName()))
            .filter(g->!Storage.GROUP_ADMIN.getName().equals(g.getName()))
            .forEach(x.channel().storage()::set);
      }
      if(u.getGroups().stream()
          .noneMatch(g->Storage.GROUP_AUTH.getName().equals(g.getName()))) {
        u.getGroups().add(Storage.GROUP_AUTH);
      }
      x.channel().storage().set(u.toUser());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER);
      res.headers()
          .add(new ConnectionHeaders(x))
          .add(new DateHeader())
          .add(new ServerHeader());
      HttpConstants.sendAndCheckConnection(x, res);
    }
  }
  
}
