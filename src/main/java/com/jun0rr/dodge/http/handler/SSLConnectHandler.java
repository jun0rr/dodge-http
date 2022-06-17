/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
@Sharable
public class SSLConnectHandler extends ChannelInboundHandlerAdapter {
  
  private static final Logger logger = LoggerFactory.getLogger(SSLConnectHandler.class);
  
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    SslHandler ssl = ctx.pipeline().get(SslHandler.class);
    if(ssl != null) {
      ssl.handshakeFuture()
          .addListener(f->ctx.fireChannelActive());
    }
    else {
      ctx.fireChannelActive();
    }
  }
  
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object o) {
    ctx.fireChannelRead(o);
  }
  
}
