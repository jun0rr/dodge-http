/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.jsonwebtoken.Jwts;
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
import io.netty.handler.codec.http.cookie.CookieHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.ReferenceCountUtil;
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
public class HttpLoginHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpLoginHandler.class);
  
  public static final String DODGE_TOKEN = "dodgeToken";
  
  public static final String JWT_ISSUER = "dodge-http";
  
  public static final String JWT_AUDIENCE = "dodge-client";
  
  public static final String JWT_SUBJECT = "dodge-auth";
  
  public static final Duration DEFAULT_TOKEN_DURATION = Duration.ofMinutes(90);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/?", HttpMethod.POST);
  
  
  public static HttpLoginHandler get() {
    return new HttpLoginHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isHttpRequest(x.message())) {
      x.attributes().put(HttpRequest.class, x.message());
    }
    Optional<HttpRequest> req = x.attributes().get(HttpRequest.class);
    if(req.isPresent() && ROUTE.test(req.get())) {
      if(HttpConstants.isValidHttpContent(x.message())) {
        ByteBuf c = ((HttpContent)x.message()).content();
        Login l = ((Http)x.channel()).gson().fromJson(c.toString(StandardCharsets.UTF_8), Login.class);
        ReferenceCountUtil.safeRelease(x.message());
        login(x, l);
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
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
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
      DefaultCookie cookie = new DefaultCookie("dodgeToken", jwt);
      cookie.setMaxAge(DEFAULT_TOKEN_DURATION.toSeconds());
      cookie.setHttpOnly(true);
      cookie.setSameSite(CookieHeaderNames.SameSite.None);
      //cookie.setSecure(true);
      res.headers().add(
          HttpHeaderNames.SET_COOKIE, 
          ServerCookieEncoder.LAX.encode(cookie)
      );
    }
    else {
      AuthTry t = AuthTry.of(x);
      Optional<AuthTry> opt = x.attributes().parent().get(t.attributeKey());
      if(opt.isPresent()) {
        if(opt.get().incrementAndBan()) {
          opt.get().sendBan(x);
          return;
        }
      }
      else {
        x.attributes().parent().put(t.attributeKey(), t.increment());
      }
      res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
      res.headers()
        .add(HttpHeaderNames.WWW_AUTHENTICATE, "Bearer realm=\"dodge-http authentication\"");
    }
    res.headers()
        .add(new ConnectionHeaders(x))
        .addInt(HttpHeaderNames.CONTENT_LENGTH, 0)
        .add(new DateHeader())
        .add(new ServerHeader());
    HttpConstants.sendAndCheckConnection(x, res);
  }
  
}
