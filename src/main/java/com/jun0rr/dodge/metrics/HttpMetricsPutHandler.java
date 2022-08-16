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
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.metrics.Metric;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpMetricsPutHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpMetricsPutHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?metrics/?", HttpMethod.PUT);
  
  public static HttpMetricsPutHandler get() {
    return new HttpMetricsPutHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf cont = ((HttpContent)x.message()).content();
      String json = cont.toString(StandardCharsets.UTF_8);
      HttpRequest req = x.attributes().get(HttpRequest.class).get();
      try {
        Metric m = ((Http)x.channel()).gson().fromJson(json, Metric.class);
        x.channel().metrics().put(m);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
        res.headers()
            .addInt(HttpHeaderNames.CONTENT_LENGTH, 0)
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
      catch(Exception e) {
        HttpConstants.sendError(x, new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
            .put("type", e.getClass())
            .put("cause", e.getCause()));
      }
    }
  }
  
}
