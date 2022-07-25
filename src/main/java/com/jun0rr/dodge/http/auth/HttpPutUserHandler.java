/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
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
public class HttpPutUserHandler implements Consumer<ChannelExchange<HttpContent>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpPutUserHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/users\\/?", HttpMethod.PUT);
  
  public static HttpPutUserHandler get() {
    return new HttpPutUserHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpContent> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    if(HttpConstants.isValidHttpContent(x.message())) {
      String json = x.message().content().toString(StandardCharsets.UTF_8);
      CreatingUser u = ((Http)x.channel()).gson().fromJson(json, CreatingUser.class);
      if(!u.getGroups().isEmpty()) {
        u.getGroups().forEach(x.channel().storage()::set);
      }
      Group auth = x.channel().storage().groups().filter(g->g.getName().equals("auth")).findAny().get();
      u.getGroups().add(auth);
      x.channel().storage().set(u.toUser());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER);
      res.headers()
          .add(new ConnectionCloseHeaders())
          .add(new DateHeader())
          .add(new ServerHeader());
      x.writeAndFlush(res).channelClose();
    }
  }
  
}
