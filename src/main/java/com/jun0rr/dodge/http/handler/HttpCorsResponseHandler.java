/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpCorsResponseHandler implements Consumer<ChannelExchange<HttpResponse>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpCorsResponseHandler.class);
  
  public static HttpCorsResponseHandler get() {
    return new HttpCorsResponseHandler();
  }
  
  public void accept(ChannelExchange<HttpResponse> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    String origin = req.headers().get(HttpHeaderNames.ORIGIN);
    if(origin != null
        && !x.message().headers().contains(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN)) {
      x.message().headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
      if(!x.message().headers().contains(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
        x.message().headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
      }
    }
    x.forwardMessage();
  }
  
}
