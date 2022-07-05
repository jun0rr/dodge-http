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
  
  public static final String LABEL_URI = "uri";
  
  public static final String ATTR_REQUEST = "request";
  
  public static final String ATTR_TIMING = "timing";
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    Optional<Metric> metric = x.tcpChannel().metrics().stream()
        .filter(m->m.name().equals(HTTP_RESPONSE_TIMING.name()))
        .filter(m->m.labels().containsKey(LABEL_URI))
        .filter(m->x.message().uri().equals(m.labels().get(LABEL_URI)))
        .findAny();
    Counter count = metric.orElseGet(()->HTTP_REQUEST_COUNT
        .newCopy(LABEL_URI, x.message().uri())).asCounter();
    count.update(i->i + 1);
    if(metric.isEmpty()) {
      x.tcpChannel().metrics().add(count);
    }
    x.attributes()
        .put(ATTR_REQUEST, x.message().uri())
        .put(ATTR_TIMING, Instant.now());
    x.forwardMessage();
  }
  
}
