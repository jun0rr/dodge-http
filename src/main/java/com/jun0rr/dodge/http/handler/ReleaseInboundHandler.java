/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class ReleaseInboundHandler extends ChannelInboundHandlerAdapter {
  
  private static final Logger logger = LoggerFactory.getLogger(ReleaseInboundHandler.class);
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    ReferenceCountUtil.release(o);
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext chc, Throwable th) {
    logger.warn("Exception caught: {}", th.getMessage(), th);
  }
  
}
