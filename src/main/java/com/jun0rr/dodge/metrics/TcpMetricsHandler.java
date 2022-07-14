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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class TcpMetricsHandler extends ChannelDuplexHandler {
  
  static final Logger logger = LoggerFactory.getLogger(TcpMetricsHandler.class);
  
  public static final Counter INBOUND_BYTES_TOTAL = new Counter("dodge_inbound_bytes_total", "Total inbound bytes");
  
  public static final Counter OUTBOUND_BYTES_TOTAL = new Counter("dodge_inbound_bytes_total", "Total inbound bytes");
  
  public static final Gauge CONNECTIONS_COUNT = new Gauge("dodge_connections_count", "Number of current connections");
  
  private final TcpChannel server;
  
  public TcpMetricsHandler(TcpChannel server) {
    this.server = Match.notNull(server).getOrFail("Bad null HttpServer");
    server.metrics().add(INBOUND_BYTES_TOTAL);
    server.metrics().add(OUTBOUND_BYTES_TOTAL);
    server.metrics().add(CONNECTIONS_COUNT);
  }
  
  @Override
  public void channelActive(ChannelHandlerContext chc) throws Exception {
    CONNECTIONS_COUNT.update(d->d + 1.0);
    chc.fireChannelActive();
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext chc) throws Exception {
    CONNECTIONS_COUNT.update(d->d - 1.0);
    chc.fireChannelInactive();
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(o instanceof ByteBuf) {
      INBOUND_BYTES_TOTAL.update(l->l + ((ByteBuf)o).readableBytes());
    }
    chc.fireChannelRead(o);
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) {
    logger.info("(Write) >> {}" + o);
    if(o instanceof ByteBuf) {
      OUTBOUND_BYTES_TOTAL.update(l->l + ((ByteBuf)o).readableBytes());
    }
    chc.writeAndFlush(o, cp);
  }
  
}
