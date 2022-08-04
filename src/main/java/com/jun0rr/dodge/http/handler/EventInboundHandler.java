/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.Attributes;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.ConsumerType;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.GenericFutureListener;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class EventInboundHandler extends ChannelInboundHandlerAdapter {
  
  static final Logger logger = LoggerFactory.getLogger(EventInboundHandler.class);
  
  private final TcpChannel tcp;
  
  private final Attributes attrs;
  
  private final ChannelEvent.Inbound event;
  
  private final ConsumerType ctype;
  
  public EventInboundHandler(TcpChannel ch, Attributes attrs, ChannelEvent.Inbound evt, ConsumerType cons) {
    this.tcp = Match.notNull(ch).getOrFail("Bad null TcpChannel");
    this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
    this.ctype = Match.notNull(cons).getOrFail("Bad null handler ConsumerType");
    this.event = Match.notNull(evt).getOrFail("Bad null ChannelEvent");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) throws Exception {
    if(ChannelEvent.Inbound.READ == event && ctype.isTypeOf(o.getClass()) && chc.channel().isOpen()) {
      ctype.cast(o).ifPresent(m->ctype.accept(ChannelExchange.of(tcp, event, chc, m, attrs.channelAttrs(chc.channel()))));
    }
    else {
      chc.fireChannelRead(o);
    }
  }
  
  @Override
  public void channelRegistered(ChannelHandlerContext chc) throws Exception {
    //logger.debug("channelRegistered: {}", Instant.now());
    if(ChannelEvent.Inbound.REGISTERED == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelRegistered();
  }
  
  @Override
  public void channelUnregistered(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.UNREGISTERED == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelUnregistered();
  }
  
  @Override
  public void channelActive(ChannelHandlerContext chc) throws Exception {
    //logger.debug("channelActive: {}", Instant.now());
    GenericFutureListener<ChannelFuture> rmattrs = f->attrs.clearChannel(f.channel());
    chc.channel().closeFuture().addListener(rmattrs);
    if(ChannelEvent.Inbound.ACTIVE == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelActive();
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.INACTIVE == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelInactive();
  }
  
  @Override
  public void channelReadComplete(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.READ_COMPLETE == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelReadComplete();
  }
  
  @Override
  public void userEventTriggered(ChannelHandlerContext chc, Object o) throws Exception {
    if(ChannelEvent.Inbound.USER_EVENT == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, o, attrs.channelAttrs(chc.channel())));
    }
    chc.fireUserEventTriggered(o);
  }
  
  @Override
  public void channelWritabilityChanged(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.WRITABILITY_CHANGED == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, null, attrs.channelAttrs(chc.channel())));
    }
    chc.fireChannelWritabilityChanged();
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext chc, Throwable th) throws Exception {
    if(ChannelEvent.Inbound.EXCEPTION == event) {
      ctype.accept(ChannelExchange.of(tcp, event, chc, th, attrs.channelAttrs(chc.channel())));
    }
    chc.fireExceptionCaught(th);
  }

  @Override
  public String toString() {
    return "EventInboundHandler{" + "attrs=" + attrs + ", event=" + event + ", cons=" + ctype + '}';
  }
  
}
