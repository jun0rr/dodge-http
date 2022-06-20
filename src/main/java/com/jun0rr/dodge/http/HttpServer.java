/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author F6036477
 */
public class HttpServer extends Http {
  
  private final Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> routes;
  
  public HttpServer() {
    super();
    this.routes = new ConcurrentHashMap<>();
  }
  
  public Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> serverRoutes() {
    return routes;
  }
  
  public HttpServer addRoute(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpRequest>>> sup) {
    routes.put(
        Match.notNull(route).getOrFail("Bad null HttpRoute"), 
        Match.notNull(sup).getOrFail("Bad null Supplier")
    );
    return this;
  }
  
}
