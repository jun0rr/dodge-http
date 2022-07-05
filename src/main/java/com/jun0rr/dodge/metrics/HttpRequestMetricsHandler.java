/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.handler.CacheControlHeaders;
import com.jun0rr.dodge.http.handler.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.handler.DateHeader;
import com.jun0rr.dodge.http.handler.ServerHeader;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRequestMetricsHandler extends ChannelInboundHandlerAdapter {
  
  public static final String URI = "metrics";
  
  private static final Logger logger = LoggerFactory.getLogger(HttpRequestMetricsHandler.class);
  
  private final HttpServer server;
  
  public HttpRequestMetricsHandler(HttpServer cfg) {
    this.server = Match.notNull(cfg).getOrFail();
  }

  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(o instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) o;
      int index = req.uri().indexOf(URI);
      if(index >= 0) {
        logger.debug("Request Metrics: {}", req.uri());
        List<String> ls = new LinkedList<>();
        server.metrics().forEach(m->m.collect(ls));
        int mlen = ls.stream().mapToInt(s->s.length() + 1).sum();
        ByteBuf content = chc.alloc().directBuffer(mlen);
        ls.stream()
            .map(s->s.concat("\n"))
            .forEach(s->content.writeCharSequence(s, StandardCharsets.UTF_8));
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
        res.headers()
            .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
            .addInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes())
            .add(new CacheControlHeaders())
            .add(new ConnectionCloseHeaders())
            .add(new DateHeader())
            .add(new ServerHeader());
        ReferenceCountUtil.release(req);
        chc.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
      }
      else {
        chc.fireChannelRead(o);
      }
    } else {
      chc.fireChannelRead(o);
    }
  }
  
}
