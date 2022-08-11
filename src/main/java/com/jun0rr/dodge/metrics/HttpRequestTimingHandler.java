/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import java.time.Instant;
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
  
  public static final Counter HTTP_REQUEST_COUNT = new Counter("dodge_http_request_count", "Http Request count");
  
  public static final Counter HTTP_REQUEST_TOTAL = new Counter("dodge_http_request_total", "Total Http Requests count");
  
  public static final Gauge HTTP_REQUEST_RATE = new Gauge("dodge_http_request_rate", "Http Request rate / minute");
  
  public static final String LABEL_URI = "uri";
  
  public static final String LABEL_METHOD = "method";
  
  public static final String ATTR_REQUEST_URI = "request_uri";
  
  public static final String ATTR_TIMING = "timing_uri";
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    x.channel().metrics().hit();
    Optional<Metric> opt = x.channel().metrics().get(HTTP_REQUEST_TOTAL);
    Metric metric = opt.orElse(HTTP_REQUEST_TOTAL).updateLong(d->d + 1);
    if(opt.isEmpty()) x.channel().metrics().put(metric);
    
    opt = x.channel().metrics().get(HTTP_REQUEST_COUNT.name(), 
        LABEL_URI, x.message().uri(),
        LABEL_METHOD, x.message().method().name()
    );
    metric = opt.orElseGet(()->HTTP_REQUEST_COUNT
        .newCopy(LABEL_URI, x.message().uri()))
        .putLabel(LABEL_METHOD, x.message().method().name())
        .updateLong(i->i + 1);
    if(opt.isEmpty()) x.channel().metrics().put(metric);
    
    opt = x.channel().metrics().get(HTTP_REQUEST_RATE);
    metric = opt.orElse(HTTP_REQUEST_RATE)
        .updateDouble(i->x.channel().metrics().hitsLastMin());
    if(opt.isEmpty()) x.channel().metrics().put(metric);
    
    x.attributes()
        .put(ATTR_REQUEST_URI, x.message().uri())
        .put(ATTR_TIMING, Instant.now());
    x.forwardMessage();
  }
  
}
