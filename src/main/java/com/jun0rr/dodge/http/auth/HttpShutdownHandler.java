/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpShutdownHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?shutdown/?", HttpMethod.GET);
  
  public static HttpShutdownHandler get() {
    return new HttpShutdownHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.ACCEPTED);
    res.headers()
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader());
    x.writeAndFlush(res)
        .channelClose()
        .acceptNext(f->f.channel().parent().close())
        .acceptNext(f->x.channel().storage().shutdown())
        .syncUninterruptibly();
    x.channel().getWorkerGroup().shutdownGracefully();
    x.channel().getMasterGroup().shutdownGracefully();
  }
  
}
