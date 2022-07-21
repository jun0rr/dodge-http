/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpBindGroupHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpBindGroupHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("\\/?auth\\/groups\\/bind\\/[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+\\/.+\\/?", HttpMethod.GET);
  
  public static final String ALIAS_URI = "/auth/groups/bind/email/group";
  
  public static HttpBindGroupHandler get() {
    return new HttpBindGroupHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam rp = new UriParam(x.message().uri()).asRequestParam(ALIAS_URI);
    Optional<User> usr = x.channel().storage().users()
        .filter(u->u.getEmail().equals(rp.get("email")))
        .findAny();
    Optional<Group> grp = x.channel().storage().groups()
        .filter(g->g.getName().equals(rp.get("group")))
        .findAny();
    if(usr.isPresent() && grp.isPresent()) {
      if(usr.get().getGroups().stream()
          .noneMatch(g->g.getName().equals(grp.get().getName()))) {
        usr.get().getGroups().add(grp.get());
        x.channel().storage().
      }
    }
    else {
      
    }
  }
  
}
