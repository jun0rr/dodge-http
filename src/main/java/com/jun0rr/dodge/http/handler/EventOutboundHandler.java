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
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class EventOutboundHandler extends ChannelOutboundHandlerAdapter {
  
  private static final Logger logger = LoggerFactory.getLogger(EventOutboundHandler.class);
  
  private final TcpChannel tcp;
  
  private final Map<String,Object> attrs;
  
  private final ChannelEvent.Outbound event;
  
  private final ConsumerType cons;
  
  public EventOutboundHandler(TcpChannel ch, Map<String,Object> attrs, ChannelEvent.Outbound evt, ConsumerType consumer) {
    this.tcp = Match.notNull(ch).getOrFail("Bad null TcpChannel");
    this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
    this.cons = Match.notNull(consumer).getOrFail("Bad null handler Consumer");
    this.event = Match.notNull(evt).getOrFail("Bad null ChannelEvent");
  }
  
  @Override
  public void bind(ChannelHandlerContext chc, SocketAddress sa, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.BIND == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, sa, attrs, cp));
    }
    chc.bind(sa, cp);
  }
  
  @Override
  public void connect(ChannelHandlerContext chc, SocketAddress sa, SocketAddress sa1, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.CONNECT == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, sa, attrs, cp));
    }
    chc.connect(sa, sa1, cp);
  }
  
  @Override
  public void disconnect(ChannelHandlerContext chc, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.DISCONNECT == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs, cp));
    }
    chc.disconnect(cp);
  }
  
  @Override
  public void close(ChannelHandlerContext chc, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.CLOSE == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs, cp));
    }
    chc.close(cp);
  }
  
  @Override
  public void deregister(ChannelHandlerContext chc, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.DEREGISTER == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs, cp));
    }
    chc.deregister(cp);
  }
  
  @Override
  public void read(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Outbound.READ == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.read();
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) throws Exception {
    if(ChannelEvent.Outbound.WRITE == event && cons.isTypeOf(o.getClass())) {
      cons.cast(o).ifPresent(m->cons.accept(ChannelExchange.of(tcp, event, chc, m, attrs, cp)));
    }
    else {
      chc.write(o, cp);
    }
  }
  
  @Override
  public void flush(ChannelHandlerContext chc) throws Exception {
    if(ChannelEvent.Outbound.FLUSH == event) {
      cons.accept(ChannelExchange.of(tcp, event, chc, null, attrs));
    }
    chc.flush();
  }

  @Override
  public String toString() {
    return "EventOutboundHandler{" + "attrs=" + attrs + ", event=" + event + ", cons=" + cons + '}';
  }
  
}
