/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonObject;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpAuthFilter implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpAuthFilter.class);
  
  public static HttpAuthFilter get() {
    return new HttpAuthFilter();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    String scookie = x.message().headers().get(HttpHeaderNames.COOKIE);
    Set<Cookie> cookies = Collections.EMPTY_SET;
    if(scookie != null && !scookie.isBlank()) {
      cookies = ServerCookieDecoder.STRICT.decode(scookie);
    }
    Optional<User> opt = cookies.stream()
        .filter(c->c.name().equals(HttpLoginHandler.DODGE_TOKEN))
        .map(c->parseJwt(x, c.value()))
        .map(j->j.getBody())
        .filter(c->c.getExpiration().toInstant().isAfter(Instant.now()))
        .map(c->x.channel().storage().users().filter(u->u.getEmail().equals(c.getId())).findAny())
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findAny();
    if(opt.isPresent()) {
      x.attributes()
          .put(HttpRequest.class, x.message())
          .put(User.class, opt.get());
      x.forwardMessage();
    }
    else {
      send(x, new ErrMessage(HttpResponseStatus.FORBIDDEN, "Unauthenticated user")
          .put("method", x.message().method().name())
          .put("uri", x.message().uri()));
    }
  }
  
  private Jws<Claims> parseJwt(ChannelExchange<HttpRequest> x, String jwt) {
    try {
      return Jwts.parserBuilder()
          .requireIssuer(HttpLoginHandler.JWT_ISSUER)
          .requireAudience(HttpLoginHandler.JWT_AUDIENCE)
          .requireSubject(HttpLoginHandler.JWT_SUBJECT)
          .setSigningKey(((Http)x.channel()).loadPublicKey())
          .build()
          .parseClaimsJws(jwt);
    }
    catch(InvalidClaimException e) {
      return null;
    }
  }
  
  private void send(ChannelExchange<HttpRequest> x, ErrMessage msg) {
    String json = ((Http)x.channel()).gson().toJson(msg);
    ByteBuf buf = x.context().alloc().heapBuffer(json.length());
    buf.writeCharSequence(json, StandardCharsets.UTF_8);
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, msg.getStatus(), buf);
    res.headers()
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader())
        .add(new JsonContentHeader(buf.readableBytes()));
    x.writeAndFlush(res).channelClose();
  }
  
}
