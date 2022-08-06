/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.util.HttpConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpContentCache extends ChannelDuplexHandler {
  
  public static final int DEFAULT_BUFFER_SIZE = 1024;
  
  private static final Logger logger = LoggerFactory.getLogger(HttpContentCache.class);
  
  private ByteBuf buffer;
  
  @Override
  public void channelActive(ChannelHandlerContext chc) {
    buffer = chc.alloc().directBuffer(DEFAULT_BUFFER_SIZE);
    //logger.debug("Creating buffer: {}", buffer.writableBytes());
    chc.fireChannelActive();
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext chc) {
    //logger.debug("Releasing buffer...");
    ReferenceCountUtil.safeRelease(buffer);
    buffer = null;
    chc.fireChannelInactive();
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    //logger.debug("channelRead( {} }", o.getClass());
    if(HttpConstants.isFullHttpRequest(o)) {
      //logger.debug("isFullHttpRequest");
      chc.fireChannelRead(o);
    }
    else if(HttpConstants.isValidHttpContent(o)) {
      //logger.debug("isValidHttpContent");
      if(HttpConstants.isLastHttpContent(o) && !buffer.isReadable()) {
        //logger.debug("isLastHttpContent(o) && !buffer.isReadable()");
        chc.fireChannelRead(o);
      }
      else {
        //logger.debug("cache HttpContent");
        buffer.writeBytes(((HttpContent)o).content());
        ReferenceCountUtil.safeRelease(o);
      }
    }
    else if(HttpConstants.isHttpRequest(o)) {
      //logger.debug("isHttpRequest");
      chc.fireChannelRead(o);
    }
    if(HttpConstants.isLastHttpContent(o) && buffer.isReadable()) {
      //logger.debug("isLastHttpContent && buffer.readableBytes={}", buffer.readableBytes());
      chc.fireChannelRead(new DefaultHttpContent(buffer));
      buffer = chc.alloc().directBuffer(DEFAULT_BUFFER_SIZE);
    }
  }
  
}
