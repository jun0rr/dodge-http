/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRouteHandler<T> implements Consumer<ChannelExchange<T>> {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpRouteHandler.class);
  
  private final HttpRoute route;
  
  private final Consumer<ChannelExchange<T>> handler;
  
  public HttpRouteHandler(HttpRoute r, Consumer<ChannelExchange<T>> h) {
    this.route = Match.notNull(r).getOrFail("Bad null HttpRoute");
    this.handler = Match.notNull(h).getOrFail("Bad null Consumer<ChannelExchange>");
  }
  
  @Override
  public void accept(ChannelExchange<T> x) {
    Optional<HttpRequest> req = x.attributes().get(HttpRequest.class.getName());
    if(req.isEmpty()) {
      throw new IllegalStateException("HttpRequest not present in channel attributes");
    }
    logger.debug("uri={}, handler={}", req.get().uri(), handler);
    if(route.test(req.get())) {
      logger.debug("ACCEPT: {}", req.get().uri());
      handler.accept(x);
    }
    else {
      x.forwardMessage();
    }
  }
  
}
