/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

/**
 *
 * @author F6036477
 */
public class HttpRouteNotFoundHandler extends ChannelInboundHandlerAdapter {
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(HttpRequest.class.isAssignableFrom(o.getClass())) {
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
      res.headers()
          .add(new ConnectionCloseHeaders())
          .add(new DateHeader())
          .add(new ServerHeader());
      ReferenceCountUtil.release(o);
      chc.write(res);
    }
  }
  
}
