/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.ConsumerType;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class EventInboundHandler extends ChannelInboundHandlerAdapter {
  
  static final Logger logger = LoggerFactory.getLogger(EventInboundHandler.class);
  
  private final TcpChannel tcp;
  
  private final Map<String,Object> attrs;
  
  private final ChannelEvent.Inbound event;
  
  private final ConsumerType cons;
  
  public EventInboundHandler(TcpChannel ch, Map<String,Object> attrs, ChannelEvent.Inbound evt, ConsumerType cons) {
    this.tcp = Match.notNull(ch).getOrFail("Bad null TcpChannel");
    this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
    this.cons = Match.notNull(cons).getOrFail("Bad null handler ConsumerType");
    this.event = Match.notNull(evt).getOrFail("Bad null ChannelEvent");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) throws Exception {
    if(ChannelEvent.Inbound.READ == event && cons.isTypeOf(o.getClass())) {
      cons.cast(o).ifPresent(m->cons.accept(ChannelExchange.of(tcp, event, chc, m, attrs)));
    }
    else {
      chc.fireChannelRead(o);
    }
  }
  
  @Override
  public void channelRegistered(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.REGISTERED == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelRegistered();
  }
  
  @Override
  public void channelUnregistered(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.UNREGISTERED == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelUnregistered();
  }
  
  @Override
  public void channelActive(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.ACTIVE == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelActive();
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.INACTIVE == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelInactive();
  }
  
  @Override
  public void channelReadComplete(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.READ_COMPLETE == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelReadComplete();
  }
  
  @Override
  public void userEventTriggered(ChannelHandlerContext chc, Object o) throws Exception {
    if(ChannelEvent.Inbound.USER_EVENT == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, o, attrs));
    }
    chc.fireUserEventTriggered(o);
  }
  
  @Override
  public void channelWritabilityChanged(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Inbound.WRITABILITY_CHANGED == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.fireChannelWritabilityChanged();
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext chc, Throwable th) throws Exception {
    if(ChannelEvent.Inbound.EXCEPTION == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, th, attrs));
    }
    chc.fireExceptionCaught(th);
  }

  @Override
  public String toString() {
    return "EventInboundHandler{" + "attrs=" + attrs + ", event=" + event + ", cons=" + cons + '}';
  }
  
}
