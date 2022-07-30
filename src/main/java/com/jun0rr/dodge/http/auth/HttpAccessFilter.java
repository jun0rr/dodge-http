/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpAccessFilter implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpAccessFilter.class);
  
  public static HttpAccessFilter get() {
    return new HttpAccessFilter();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    User user = x.attributes().get(User.class).get();
    //logger.debug("{}", user);
    if(x.channel().storage().roles()
        //.peek(r->logger.debug("FILTER ROLE: {}", r))
        .filter(r->r.match(x.message()))
        //.peek(r->logger.debug("ACCESS ROLE: {}", r))
        .anyMatch(r->r.allow(user))) {
      x.forwardMessage();
    }
    else {
      HttpConstants.sendError(x, 
          new ErrMessage(HttpResponseStatus.FORBIDDEN, "Forbidden Access")
              .put("user", user.getEmail())
              .put("method", x.message().method().name())
              .put("uri", x.message().uri()));
    }
  }
  
}
