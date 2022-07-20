/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import static com.jun0rr.dodge.metrics.HttpRequestTimingHandler.LABEL_URI;
import static com.jun0rr.dodge.metrics.HttpResponseTimingHandler.HTTP_RESPONSE_TIMING;
import static com.jun0rr.dodge.metrics.HttpResponseTimingHandler.LABEL_STATUS;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.time.Duration;
import java.time.Instant;
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
  public void channelActive(ChannelHandlerContext chc) throws Exception {
    Optional<Metric> opt = server.metrics().stream()
          .filter(m->m.name().equals(CONNECTIONS_COUNT.name()))
          .findAny();
    Metric metric = opt.orElseGet(()->CONNECTIONS_COUNT)
        .update(d->d + 1);
    if(opt.isEmpty()) server.metrics().add(metric);
    opt = server.metrics().stream()
          .filter(m->m.name().equals(CONNECTIONS_TOTAL.name()))
          .findAny();
    metric = opt.orElseGet(()->CONNECTIONS_TOTAL)
        .update(d->d + 1);
    if(opt.isEmpty()) server.metrics().add(metric);
    chc.fireChannelActive();
  }
  
  @Override
  public void channelInactive(ChannelHandlerContext chc) throws Exception {
    Optional<Metric> opt = server.metrics().stream()
          .filter(m->m.name().equals(CONNECTIONS_COUNT.name()))
          .findAny();
    Metric metric = opt.orElseGet(()->CONNECTIONS_COUNT)
        .update(d->d - 1);
    if(opt.isEmpty()) server.metrics().add(metric);
    chc.fireChannelInactive();
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(o instanceof ByteBuf) {
      Optional<Metric> opt = server.metrics().stream()
            .filter(m->m.name().equals(INBOUND_BYTES_TOTAL.name()))
            .findAny();
      Metric metric = opt.orElseGet(()->INBOUND_BYTES_TOTAL)
          .update(i->((ByteBuf)o).readableBytes() + i);
      if(opt.isEmpty()) server.metrics().add(metric);
    }
    chc.fireChannelRead(o);
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) {
    if(o instanceof ByteBuf) {
      Optional<Metric> opt = server.metrics().stream()
            .filter(m->m.name().equals(OUTBOUND_BYTES_TOTAL.name()))
            .findAny();
      Metric metric = opt.orElseGet(()->OUTBOUND_BYTES_TOTAL)
          .update(i->((ByteBuf)o).readableBytes() + i);
      if(opt.isEmpty()) server.metrics().add(metric);
    }
    logger.debug("metrics.size() = {}", server.metrics().size());
    server.metrics().stream().map(Metric::toString).forEach(logger::debug);
    chc.writeAndFlush(o, cp);
  }
  
}
