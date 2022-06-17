/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRequestNotFoundHandler extends ChannelInboundHandlerAdapter {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpRequestNotFoundHandler.class);
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(o instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) o;
      logger.info("Request Not Found (404): {}", req.uri());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
      res.headers()
          .add(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
          .add(new DateHeader())
          .add(new ServerHeader());
      ReferenceCountUtil.release(req);
      chc.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
    } else {
      chc.fireChannelRead(o);
    }
  }
  
}
