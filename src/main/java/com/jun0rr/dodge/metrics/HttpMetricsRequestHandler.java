/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.CacheControlHeaders;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpMetricsRequestHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?metrics/?", HttpMethod.GET);
  
  private final TcpChannel channel;
  
  public HttpMetricsRequestHandler(TcpChannel channel) {
    this.channel = Match.notNull(channel).getOrFail("Bad null TcpChannel");
  }

  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    List<String> ls = new LinkedList<>();
    channel.metrics().forEach(m->m.collect(ls));
    int mlen = ls.stream().mapToInt(s->s.length() + 1).sum();
    ByteBuf content = x.context().alloc().directBuffer(mlen);
    ls.stream()
        .map(s->s.concat("\n"))
        .forEach(s->content.writeCharSequence(s, StandardCharsets.UTF_8));
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
    res.headers()
        .add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
        .addInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes())
        .add(new CacheControlHeaders())
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader());
    ReferenceCountUtil.safeRelease(x.message());
    HttpConstants.sendAndCheckConnection(x, res);
  }
  
}
