/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.http.handler.HttpRequestMetricsHandler;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpResponseTimingHandler implements Consumer<ChannelExchange<HttpResponse>> {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpResponseTimingHandler.class);
  
  public static final Gauge HTTP_RESPONSE_TIMING = new Gauge("dodge_http_response_timing", "HTTP response timing millis");
  
  public static final Counter HTTP_RESPONSE_STATUS = new Counter("dodge_http_response_status_count", "HTTP response status count");
  
  @Override
  public void accept(ChannelExchange<HttpResponse> x) {
    Optional<String> req = x.attributes().remove("request");
    Optional<Instant> timing = x.attributes().remove("timing");
    if(req.isPresent() && timing.isPresent()) {
      Optional<Metric> metric = x.tcpChannel().metrics().stream()
          .filter(m->m.name().equals(HTTP_RESPONSE_TIMING.name()))
          .filter(m->req.get().equals(m.labels().get("uri")))
          .findAny();
      Gauge gauge = metric.orElse(HTTP_RESPONSE_TIMING.newCopy("uri", req.get())).asGauge();
      gauge.update(d->Long.valueOf(Duration.between(timing.get(), Instant.now()).toMillis()).doubleValue());
      logger.debug("{}", gauge);
      if(metric.isEmpty()) {
        x.tcpChannel().metrics().add(gauge);
      }
    }
    Optional<Metric> status = x.tcpChannel().metrics().stream()
        .filter(m->m.name().equals(HTTP_RESPONSE_STATUS.name()))
        .filter(m->req.get().equals(m.labels().get("uri")))
        .filter(m->String.valueOf(x.message().status().code()).equals(m.labels().get("status")))
        .findAny();
    Counter count = status.orElseGet(()->HTTP_RESPONSE_STATUS.newCopy("uri", req.get())
        .putLabel("status", String.valueOf(x.message().status().code()))).asCounter();
    count.update(l->l + 1);
    if(status.isEmpty()) {
      x.tcpChannel().metrics().add(count);
    }
    x.writeAndFlush(x.message());
  }
  
}
