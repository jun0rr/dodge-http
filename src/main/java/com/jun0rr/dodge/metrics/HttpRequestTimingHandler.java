/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import static com.jun0rr.dodge.metrics.HttpResponseTimingHandler.HTTP_RESPONSE_TIMING;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpRequestTimingHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  public static final Counter HTTP_REQUEST_COUNT = new Counter("dodge_http_request_count", "HTTP request count");
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    Optional<Metric> metric = x.tcpChannel().metrics().stream()
        .filter(m->m.name().equals(HTTP_RESPONSE_TIMING.name()))
        .filter(m->x.message().uri().equals(m.labels().get("uri")))
        .findAny();
    Counter count = metric.orElseGet(()->HTTP_REQUEST_COUNT.newCopy("uri", x.message().uri())).asCounter();
    if(metric.isEmpty()) {
      x.tcpChannel().metrics().add(count);
    }
    x.attributes().put("request", x.message().uri()).put("timing", Instant.now());
  }
  
}
