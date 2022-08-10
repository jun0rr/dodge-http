/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.auth.ErrMessage;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.metrics.Metric;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpMetricsDeleteHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpMetricsDeleteHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?metrics/[a-zA-Z0-9_\\.\\-]+\\/?([\\?\\&][a-zA-Z0-9_\\.\\-]+=[a-zA-Z0-9_\\.\\-]+)*", HttpMethod.DELETE);
  
  public static HttpMetricsDeleteHandler get() {
    return new HttpMetricsDeleteHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam upar = new UriParam(x.message().uri()).asRequestParam("/store/key");
    RequestParam rpar = new RequestParam(x.message().uri());
    try {
      Map<String,List<String>> map = rpar.getParameters();
      List<String> labels = map.entrySet().stream()
          .flatMap(e->Stream.of(e.getKey(), e.getValue().get(0)))
          .collect(Collectors.toList());
      List<Metric> ms = x.channel().metrics().removeAll(upar.get("key"), labels.toArray(new String[labels.size()]));
      if(ms.isEmpty()) {
        HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.NOT_FOUND, "Metric Not Found: %s", upar.get("key")));
      }
      else {
        String json = ((Http)x.channel()).gson().toJson(ms);
        ByteBuf buf = x.context().alloc().buffer(json.length());
        buf.writeCharSequence(json, StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        res.headers()
            .add(new JsonContentHeader(buf.readableBytes()))
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
    }
    catch(Exception e) {
      HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
          .put("type", e.getClass())
          .put("cause", e.getCause()));
    }
  }
  
}
