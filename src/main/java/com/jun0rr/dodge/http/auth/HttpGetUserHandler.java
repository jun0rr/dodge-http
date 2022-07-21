/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpGetUserHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpGetUserHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/user\\/?", HttpMethod.GET);
  
  public static HttpGetUserHandler get() {
    return new HttpGetUserHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    if(ROUTE.test(x.message()) && x.attributes().contains(HttpAuthFilter.ATTR_USER)) {
      ByteBuf buf = x.context().alloc().directBuffer();
      buf.writeCharSequence(((Http)x.channel()).gson().toJson(x.attributes().get(HttpAuthFilter.ATTR_USER).get()), StandardCharsets.UTF_8);
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
      res.headers()
          .add(new JsonContentHeader(buf.readableBytes()))
          .add(new ConnectionCloseHeaders())
          .add(new DateHeader())
          .add(new ServerHeader());
      x.writeAndFlush(res).channelClose();
    }
    else {
      x.forwardMessage();
    }
  }
  
}
