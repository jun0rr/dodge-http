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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpGroupsBindHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpGroupsBindHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/groups/bind/[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+/.+/?", HttpMethod.GET);
  
  public static final String ALIAS_URI = "/auth/groups/bind/email/group";
  
  public static HttpGroupsBindHandler get() {
    return new HttpGroupsBindHandler();
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
      List<Group> gs = new LinkedList<>();
      gs.add(grp.get());
      usr.get().getGroups().stream()
          .filter(g->!g.getName().equals(grp.get().getName()))
          .forEach(gs::add);
      usr.get().setGroups(gs);
      x.channel().storage().set(usr.get());
      HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
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
