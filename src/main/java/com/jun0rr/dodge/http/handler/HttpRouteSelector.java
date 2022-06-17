/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.match.Match;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author F6036477
 */
public class HttpRouteSelector implements Consumer<ChannelExchange<HttpRequest>> {
  
  private final Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> handlers;
  
  private final boolean sendNotFound;
  
  public HttpRouteSelector(Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> handlers, boolean sendNotFound) {
    this.handlers = Match.notNull(handlers).getOrFail("Bad null handlers Map");
    this.sendNotFound = sendNotFound;
  }
  
  public HttpRouteSelector(boolean sendNotFound) {
    this.handlers = new HashMap<>();
    this.sendNotFound = sendNotFound;
  }
  
  public HttpRouteSelector() {
    this(true);
  }
  
  public HttpRouteSelector addHandler(String pattern, List<HttpMethod> meths, Supplier<Consumer<ChannelExchange<HttpRequest>>> cs) {
    return this.addHandler(new HttpRoute(pattern, meths), cs);
  }
  
  public HttpRouteSelector addHandler(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpRequest>>> cs) {
    Match.notNull(route).failIfNotMatch("Bad null HttpRoute");
    Match.notNull(cs).failIfNotMatch("Bad null Consumer handler");
    handlers.put(route, cs);
    return this;
  }
  
  public Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> handlers() {
    return handlers;
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    handlers.entrySet().stream()
        .filter(e->e.getKey().test(x.message()))
        .map(e->e.getValue())
        .map(Supplier::get)
        .findAny()
        .ifPresentOrElse(c->c.accept(x), ()->sendNotFound(x));
  }
  
  private void sendNotFound(ChannelExchange<HttpRequest> t) {
    if(!sendNotFound) return;
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    res.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    res.headers().setAll(new DateHeader());
    res.headers().setAll(new ServerHeader());
    t.context().writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
  }
  
  public void channelInactive(ChannelExchange x) {
    x.rmAttr("httpRequest").ifPresent(ReferenceCountUtil::release);
  }
  
}
