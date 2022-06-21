/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpRouteHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  private final HttpRoute route;
  
  private final Consumer<ChannelExchange<HttpRequest>> handler;
  
  public HttpRouteHandler(HttpRoute r, Consumer<ChannelExchange<HttpRequest>> h) {
    this.route = Match.notNull(r).getOrFail("Bad null HttpRoute");
    this.handler = Match.notNull(h).getOrFail("Bad null Consumer<ChannelExchange>");
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    if(route.test(x.message())) {
      handler.accept(x);
    }
    else {
      x.forwardMessage();
    }
  }
  
}
