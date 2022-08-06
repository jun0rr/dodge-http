/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class TcpMetricsHandler extends ChannelDuplexHandler {
  
  static final Logger logger = LoggerFactory.getLogger(TcpMetricsHandler.class);
  
  public static final Counter INBOUND_BYTES_TOTAL = new Counter("dodge_inbound_bytes_total", "Total inbound bytes");
  
  public static final Counter OUTBOUND_BYTES_TOTAL = new Counter("dodge_outbound_bytes_total", "Total outbound bytes");
  
  public static final Gauge CONNECTIONS_COUNT = new Gauge("dodge_connections_count", "Number of current connections");
  
  public static final Counter CONNECTIONS_TOTAL = new Counter("dodge_connections_total", "Number of total connections");
  
  private final TcpChannel server;
  
  public TcpMetricsHandler(TcpChannel server) {
    this.server = Match.notNull(server).getOrFail("Bad null HttpServer");
  }
  
  @Override
  public void channelRegistered(ChannelHandlerContext chc) throws Exception {
    //server.metrics().forEach(m->logger.debug("channelRegistered({}): {}", chc.channel().id().asShortText(), m));
    if(!server.metrics().contains(CONNECTIONS_COUNT)) {
      server.metrics().put(CONNECTIONS_COUNT);
    }
    if(!server.metrics().contains(CONNECTIONS_TOTAL)) {
      server.metrics().put(CONNECTIONS_TOTAL);
    }
    CONNECTIONS_COUNT.updateDouble(d->d+1);
    CONNECTIONS_TOTAL.updateLong(d->d+1);
    chc.fireChannelRegistered();
  }
  
  @Override
  public void channelUnregistered(ChannelHandlerContext chc) throws Exception {
    //server.metrics().forEach(m->logger.debug("channelUnregistered({}): {}", chc.channel().id().asShortText(), m));
    CONNECTIONS_COUNT.updateDouble(d->d-1);
    chc.fireChannelUnregistered();
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(o instanceof ByteBuf) {
      if(!server.metrics().contains(INBOUND_BYTES_TOTAL)) {
        server.metrics().put(INBOUND_BYTES_TOTAL);
      }
      INBOUND_BYTES_TOTAL.updateLong(d->d + ((ByteBuf)o).readableBytes());
    }
    chc.fireChannelRead(o);
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) {
    if(o instanceof ByteBuf) {
      if(!server.metrics().contains(OUTBOUND_BYTES_TOTAL)) {
        server.metrics().put(OUTBOUND_BYTES_TOTAL);
      }
      OUTBOUND_BYTES_TOTAL.updateLong(d->d + ((ByteBuf)o).readableBytes());
    }
    chc.writeAndFlush(o, cp);
  }
  
}
