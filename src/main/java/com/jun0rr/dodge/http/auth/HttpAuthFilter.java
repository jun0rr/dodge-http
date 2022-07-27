/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
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
      HttpConstants.sendError(x, 
          new ErrMessage(HttpResponseStatus.FORBIDDEN, "Unauthenticated user")
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
  
}
  
