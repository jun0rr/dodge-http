/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.header.XErrorHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpCorsRequestHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpCorsRequestHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?.*", HttpMethod.OPTIONS);
  
  public static HttpCorsRequestHandler get() {
    return new HttpCorsRequestHandler();
  }
  
  public void accept(ChannelExchange<HttpRequest> x) {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
    res.headers().addInt(HttpHeaderNames.CONTENT_LENGTH, 0);
    String header = x.message().headers().get(HttpHeaderNames.ORIGIN);
    if(header != null) {
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, header);
    }
    header = x.message().headers().get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    if(header != null) {
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, header.concat(", OPTIONS"));
    }
    header = x.message().headers().get(HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS);
    if(header != null) {
      res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, header);
    }
    res.headers().add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
    res.headers().add(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, String.format("%s, %s, %s, %s", 
        XErrorHeader.X_ERROR_MESSAGE, 
        XErrorHeader.X_ERROR_CLASS, 
        XErrorHeader.X_ERROR_CAUSE, 
        XErrorHeader.X_ERROR_TRACE)
    );
    res.headers().addInt(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 86400);
    res.headers().add(new ConnectionHeaders(x));
    res.headers().add(new DateHeader());
    res.headers().add(new ServerHeader());
    HttpConstants.sendAndCheckConnection(x, res);
  }
  
}
