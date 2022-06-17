/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpMessageLogger extends ChannelDuplexHandler {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpMessageLogger.class);
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    printHttpMessage(o, "Read");
    chc.fireChannelRead(o);
  }
  
  private void printHttpMessage(Object o, String mth) {
    if(o instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) o;
      logger.debug("({}) {} {} {}", mth, req.method(), req.uri(), req.protocolVersion());
      req.headers().forEach(e->logger.debug(">>    {}: {}", e.getKey(), e.getValue()));
    }
    else if(o instanceof HttpResponse) {
      HttpResponse res = (HttpResponse) o;
      logger.debug("({}) {} {}", mth, res.status(), res.protocolVersion());
      res.headers().forEach(e->logger.debug(">>    {}: {}", e.getKey(), e.getValue()));
    }
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) {
    printHttpMessage(o, "Write");
    chc.write(o, cp);
  }
  
}
