/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

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
import static com.jun0rr.dodge.metrics.HttpRequestTimingHandler.ATTR_REQUEST_URI;

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
    Optional<String> uri = x.attributes().remove(ATTR_REQUEST_URI);
    Optional<Instant> timing = x.attributes().remove(ATTR_TIMING);
    if(uri.isPresent() && timing.isPresent()) {
      if(!x.channel().metrics().contains(HTTP_RESPONSE_TIMING_AVG)) {
        x.channel().metrics().put(HTTP_RESPONSE_TIMING_AVG);
      }
      double duration = Duration.between(timing.get(), Instant.now()).toMillis();
      HTTP_RESPONSE_TIMING_AVG.updateDouble(d->(duration + d) / (d < 1.0 ? 1.0 : 2.0));
      Optional<Metric> opt = x.channel().metrics().get(HTTP_RESPONSE_TIMING.name(), 
          LABEL_URI, uri.get(), 
          LABEL_STATUS, String.valueOf(x.message().status().code())
      );
      Metric metric = opt.orElseGet(()->HTTP_RESPONSE_TIMING.newCopy(LABEL_URI, uri.get()))
          .putLabel(LABEL_STATUS, x.message().status().code())
          .updateDouble(d->(duration + d) / (d < 1 ? 1 : 2));
      if(opt.isEmpty()) x.channel().metrics().put(metric);
      opt = x.channel().metrics().get(HTTP_RESPONSE_COUNT.name(), 
          LABEL_URI, uri.get(), 
          LABEL_STATUS, String.valueOf(x.message().status().code())
      );
      metric = opt.orElseGet(()->HTTP_RESPONSE_COUNT.newCopy(LABEL_URI, uri.get()))
          .putLabel(LABEL_STATUS, x.message().status().code())
          .updateLong(i->i+1);
      if(opt.isEmpty()) x.channel().metrics().put(metric);
    }
    x.forwardMessage();
  }
  
}
