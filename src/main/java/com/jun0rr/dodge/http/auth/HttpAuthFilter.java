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
    Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(
        x.message().headers().get(HttpHeaderNames.COOKIE)
    );
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
          .put("http-request", x.message())
          .put("user", opt.get());
      x.forwardMessage();
    }
    else {
      sendForbidden(x, "User not found");
    }
    x.attributes().stream().forEach(e->logger.debug("attribute: {}={} - {}", e.getKey(), e.getValue(), e.getValue().getClass()));
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
  
  private void sendForbidden(ChannelExchange<HttpRequest> x, String msg) {
    ByteBuf buf = Unpooled.EMPTY_BUFFER;
    if(msg != null) {
      JsonObject obj = new JsonObject();
      obj.addProperty("status", HttpResponseStatus.FORBIDDEN.code());
      obj.addProperty("message", msg);
      String json = ((Http)x.channel()).gson().toJson(obj);
      buf = x.context().alloc().heapBuffer(json.length());
      buf.writeCharSequence(json, StandardCharsets.UTF_8);
    }
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, buf);
    res.headers()
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader());
    if(msg != null) {
      res.headers().add(new JsonContentHeader(buf.readableBytes()));
    }
    x.writeAndFlush(res).channelClose();
  }
  
}