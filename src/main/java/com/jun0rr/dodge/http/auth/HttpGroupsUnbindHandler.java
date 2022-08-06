/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpGroupsUnbindHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpGroupsUnbindHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of(String.format("/?auth/groups/bind/%s/%s/?", User.REGEX_EMAIL, Group.REGEX_NAME), HttpMethod.DELETE);
  
  public static final String ALIAS_URI = "/auth/groups/bind/email/group";
  
  public static HttpGroupsUnbindHandler get() {
    return new HttpGroupsUnbindHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    RequestParam rp = new UriParam(x.message().uri()).asRequestParam(ALIAS_URI);
    Optional<User> usr = x.channel().storage().users()
        .filter(u->u.getEmail().equals(rp.get("email")))
        .findAny();
    Optional<Group> grp = usr.map(User::getGroups)
        .map(List::stream)
        .map(s->s.filter(g->g.getName().equals(rp.get("group"))).findAny())
        .orElseGet(Optional::empty);
    if(usr.isPresent() && grp.isPresent()) {
      usr.get().getGroups().remove(grp.get());
      x.channel().storage().set(usr.get());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
      res.headers()
          .add(new ConnectionHeaders(x))
          .add(new DateHeader())
          .add(new ServerHeader());
      HttpConstants.sendAndCheckConnection(x, res);
    }
    else {
      HttpConstants.sendError(x, 
          new ErrMessage(HttpResponseStatus.NOT_FOUND, "User/Group Not Found")
              .put("user", usr.map(User::getEmail).orElse("Not Found"))
              .put("group", grp.map(Group::getName).orElse("Not Found")));
    }
  }
  
}
