/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import static com.jun0rr.dodge.metrics.HttpRequestTimingHandler.ATTR_REQUEST;
import static com.jun0rr.dodge.metrics.HttpRequestTimingHandler.ATTR_TIMING;
import static com.jun0rr.dodge.metrics.HttpRequestTimingHandler.LABEL_URI;
import com.jun0rr.dodge.tcp.ChannelExchange;
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
  
  public static final Gauge HTTP_RESPONSE_TIMING_AVG = new Gauge("dodge_http_response_timing_avg", "Average HTTP response timing millis");
  
  public static final Counter HTTP_RESPONSE_COUNT = new Counter("dodge_http_response_count", "HTTP response status count");
  
  public static final String LABEL_STATUS = "status";
  
  @Override
  public void accept(ChannelExchange<HttpResponse> x) {
    Optional<String> req = x.attributes().remove(ATTR_REQUEST);
    Optional<Instant> timing = x.attributes().remove(ATTR_TIMING);
    if(req.isPresent() && timing.isPresent()) {
      Optional<Metric> opt = x.channel().metrics().stream()
          .filter(m->m.name().equals(HTTP_RESPONSE_TIMING.name()))
          .filter(m->req.get().equals(m.labels().get(LABEL_URI)))
          .filter(m->String.valueOf(x.message().status().code()).equals(m.labels().get(LABEL_STATUS)))
          .findAny();
      double duration = Duration.between(timing.get(), Instant.now()).toMillis();
      Metric metric = opt.orElseGet(()->HTTP_RESPONSE_TIMING.newCopy(LABEL_URI, req.get()))
          .putLabel(LABEL_STATUS, x.message().status().code())
          .update(d->duration);
      if(opt.isEmpty()) x.channel().metrics().add(metric);
      opt = x.channel().metrics().stream()
          .filter(m->m.name().equals(HTTP_RESPONSE_COUNT.name()))
          .filter(m->req.get().equals(m.labels().get(LABEL_URI)))
          .filter(m->String.valueOf(x.message().status().code()).equals(m.labels().get(LABEL_STATUS)))
          .findAny();
      metric = opt.orElseGet(()->HTTP_RESPONSE_COUNT.newCopy(LABEL_URI, req.get()))
          .putLabel(LABEL_STATUS, x.message().status().code())
          .update(i->i+1);
      if(opt.isEmpty()) x.channel().metrics().add(metric);
      opt = x.channel().metrics().stream()
          .filter(m->m.name().equals(HTTP_RESPONSE_TIMING_AVG.name()))
          .filter(m->req.get().equals(m.labels().get(LABEL_URI)))
          .findAny();
      metric = opt.orElseGet(()->HTTP_RESPONSE_TIMING_AVG.newCopy(LABEL_URI, req.get()))
          .update(d->(duration + d) / (d < 1 ? 1 : 2));
      if(opt.isEmpty()) x.channel().metrics().add(metric);
    }
    x.forwardMessage();
  }
  
}
