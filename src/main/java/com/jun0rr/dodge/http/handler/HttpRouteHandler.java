/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRouteHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpRouteHandler.class);
  
  private final HttpRoute route;
  
  private final Consumer<ChannelExchange<HttpRequest>> handler;
  
  public HttpRouteHandler(HttpRoute r, Consumer<ChannelExchange<HttpRequest>> h) {
    this.route = Match.notNull(r).getOrFail("Bad null HttpRoute");
    this.handler = Match.notNull(h).getOrFail("Bad null Consumer<ChannelExchange>");
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    logger.debug("URI={}, ROUTE={}, MATCH={}", x.message().uri(), route, route.test(x.message()));
    if(route.test(x.message())) {
      handler.accept(x);
    }
    else {
      x.forwardMessage();
    }
  }
  
}
