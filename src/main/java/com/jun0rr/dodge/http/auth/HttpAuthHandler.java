/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.Gson;
import com.jun0rr.dodge.http.Http;
import static com.jun0rr.dodge.http.auth.HttpAuthHandler6.DEFAULT_TOKEN_DURATION;
import static com.jun0rr.dodge.http.auth.HttpAuthHandler6.JWT_AUDIENCE;
import static com.jun0rr.dodge.http.auth.HttpAuthHandler6.JWT_ISSUER;
import static com.jun0rr.dodge.http.auth.HttpAuthHandler6.JWT_SUBJECT;
import static com.jun0rr.dodge.http.auth.HttpAuthHandler6.ROUTE;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.jsonwebtoken.Jwts;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public class HttpAuthHandler implements Consumer<ChannelExchange<Object>> {
  
  public void accept(ChannelExchange<Object> x) {
    if(HttpRequest.class.isAssignableFrom(x.message().getClass())) {
      x.attributes().put("request", x.message());
    }
    if(HttpContent.class.isAssignableFrom(x.message().getClass())) {
      Optional<HttpRequest> req = x.attributes().get("request");
      HttpContent c = (HttpContent) x.message();
      if(req.isPresent() && ROUTE.test(req.get())) {
        Gson gson = ((Http)x.channel()).gson();
        login(x, gson.fromJson(c.content().toString(StandardCharsets.UTF_8), Login.class));
      }
    }
    else {
      
    }
  }
  
  private void login(ChannelExchange x, Login l) {
    
  }
  
}
