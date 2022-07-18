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
public class HttpResponseTimingHandler implements Consumer<ChannelExchange<HttpResponse>> {
  
  private static final Logger logger = LoggerFactory.getLogger(HttpResponseTimingHandler.class);
  
  public static final Gauge HTTP_RESPONSE_TIMING = new Gauge("dodge_http_response_timing", "HTTP response timing millis");
  
  public static final Counter HTTP_RESPONSE_STATUS = new Counter("dodge_http_response_count", "HTTP response status count");
  
  public static final String LABEL_STATUS = "status";
  
  @Override
  public void accept(ChannelExchange<HttpResponse> x) {
    logger.debug("Writing HttpResponse: {}", x.message());
    Optional<String> req = x.attributes().remove(ATTR_REQUEST);
    Optional<Instant> timing = x.attributes().remove(ATTR_TIMING);
    logger.debug("Request: {} - Timing: {}", req, timing);
    if(req.isPresent() && timing.isPresent()) {
      Optional<Metric> metric = x.channel().metrics().stream()
          .peek(m->logger.debug("{}", m))
          .filter(m->m.name().equals(HTTP_RESPONSE_TIMING.name()))
          .filter(m->m.labels().containsKey(LABEL_URI))
          .filter(m->req.get().equals(m.labels().get(LABEL_URI)))
          .findAny();
      logger.debug("metric: {}", metric);
      Gauge gauge = metric.orElse(HTTP_RESPONSE_TIMING
          .newCopy(LABEL_URI, req.get())).asGauge();
      logger.debug("gauge: {}", gauge);
      gauge.update(d->Long.valueOf(Duration.between(timing.get(), Instant.now()).toMillis()).doubleValue());
      logger.debug("gauge: {}", gauge);
      if(metric.isEmpty()) {
        x.channel().metrics().add(gauge);
      }
      Optional<Metric> status = x.channel().metrics().stream()
          .peek(m->logger.debug("{}", m))
          .filter(m->m.name().equals(HTTP_RESPONSE_STATUS.name()))
          .filter(m->m.labels().containsKey(LABEL_URI))
          .filter(m->req.get().equals(m.labels().get(LABEL_URI)))
          .filter(m->m.labels().containsKey(LABEL_STATUS))
          .filter(m->String.valueOf(x.message().status().code()).equals(m.labels().get(LABEL_STATUS)))
          .findAny();
      Counter count = status.orElseGet(()->HTTP_RESPONSE_STATUS
          .newCopy(LABEL_URI, req.get())
          .putLabel(LABEL_STATUS, String.valueOf(x.message().status().code()))).asCounter();
      count.update(i->i + 1);
      if(status.isEmpty()) {
        x.channel().metrics().add(count);
      }
      List<String> ll = new LinkedList<>();
      count.collect(ll);
      ll.forEach(s->logger.debug(s));
    }
    x.context().writeAndFlush(x.message(), x.promise()).addListener(f->{
      logger.debug("Writed from HttpResponseTimingHandler");
    });
    //x.writeAndFlush(x.message()).acceptNext(f->logger.debug("Writed from HttpResponseTimingHandler"));
  }
  
}
