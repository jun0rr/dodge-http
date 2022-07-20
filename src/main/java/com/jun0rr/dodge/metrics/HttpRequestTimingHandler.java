/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRequestTimingHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpRequestTimingHandler.class);
  
  public static final Counter HTTP_REQUEST_COUNT = new Counter("dodge_http_request_count", "HTTP request count");
  
  public static final String LABEL_URI = "uri";
  
  public static final String ATTR_REQUEST = "request_uri";
  
  public static final String ATTR_TIMING = "timing_uri";
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    Optional<Metric> opt = x.channel().metrics().stream()
        .filter(m->m.name().equals(HTTP_REQUEST_COUNT.name()))
        .filter(m->m.labels().containsKey(LABEL_URI))
        .filter(m->x.message().uri().equals(m.labels().get(LABEL_URI)))
        .findAny();
    Metric metric = opt.orElseGet(()->HTTP_REQUEST_COUNT
        .newCopy(LABEL_URI, x.message().uri()))
        .update(i->i + 1);
    if(opt.isEmpty()) {
      x.channel().metrics().add(metric);
    }
    x.attributes()
        .put(ATTR_REQUEST, x.message().uri())
        .put(ATTR_TIMING, Instant.now());
    x.forwardMessage();
  }
  
}
