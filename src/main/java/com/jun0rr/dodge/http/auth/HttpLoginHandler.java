/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.Gson;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.jsonwebtoken.Jwts;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpLoginHandler implements Consumer<ChannelExchange<Object>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpLoginHandler.class);
  
  public static final String DODGE_TOKEN = "dodgeToken";
  
  public static final String JWT_ISSUER = "dodge-http";
  
  public static final String JWT_AUDIENCE = "dodge-client";
  
  public static final String JWT_SUBJECT = "dodge-auth";
  
  public static final Duration DEFAULT_TOKEN_DURATION = Duration.ofMinutes(90);
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/?", HttpMethod.POST);
  
  
  public static HttpLoginHandler get() {
    return new HttpLoginHandler();
  }
  
  @Override
  public void accept(ChannelExchange<Object> x) {
    if(HttpRequest.class.isAssignableFrom(x.message().getClass())) {
      x.attributes().put("request", x.message());
    }
    Optional<HttpRequest> req = x.attributes().get("request");
    if(req.isPresent() && ROUTE.test(req.get())) {
      if(HttpContent.class.isAssignableFrom(x.message().getClass())) {
        HttpContent c = (HttpContent) x.message();
        Gson gson = ((Http)x.channel()).gson();
        login(x, gson.fromJson(c.content().toString(StandardCharsets.UTF_8), Login.class));
      }
    }
    else {
      x.forwardMessage();
    }
  }
  
  private void login(ChannelExchange x, Login l) {
    Optional<User> user = x.channel()
        .storage().users()
        .filter(u->u.getPassword().validate(l))
        .findAny();
    HttpResponse res;
    if(user.isPresent()) {
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.EMPTY_BUFFER);
      Instant now = Instant.now();
      String jwt = Jwts.builder()
          .setId(user.get().getEmail())
          .setIssuer(JWT_ISSUER)
          .setAudience(JWT_AUDIENCE)
          .setSubject(JWT_SUBJECT)
          .setIssuedAt(Date.from(now))
          .setNotBefore(Date.from(now))
          .setExpiration(Date.from(now.plusSeconds(DEFAULT_TOKEN_DURATION.toSeconds())))
          .signWith(((Http)x.channel()).loadPrivateKey())
          .compact();
      Cookie cookie = new DefaultCookie("dodgeToken", jwt);
      cookie.setMaxAge(DEFAULT_TOKEN_DURATION.toSeconds());
      cookie.setHttpOnly(true);
      logger.debug("{}", ServerCookieEncoder.STRICT.encode(cookie));
      res.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
    }
    else {
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, Unpooled.EMPTY_BUFFER);
    }
    res.headers()
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader());
    x.writeAndFlush(res).acceptNext(f->logger.debug("RESPONSE WRITED!"));
  }
  
}
