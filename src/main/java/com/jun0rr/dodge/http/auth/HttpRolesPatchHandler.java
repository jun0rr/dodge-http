/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.util.ErrMessage;
import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.handler.HttpRequestException;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.ConnectionHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import com.jun0rr.dodge.tcp.ChannelExchange;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class HttpRolesPatchHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpRolesPatchHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/roles/(allow|deny)/?([\\?\\&]((uri=[a-zA-Z0-9\\/\\-_]+)|(methods=[A-Z\\,]+)|(group=[a-zA-Z_]+[a-zA-Z0-9_\\-\\.]*)))*", HttpMethod.PATCH);
  
  public static HttpRolesPatchHandler get() {
    return new HttpRolesPatchHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    if(HttpConstants.isValidHttpContent(x.message())) {
      HttpRequest req = x.attributes().get(HttpRequest.class).get();
      UriParam up = new UriParam(req.uri());
      RequestParam pars = new RequestParam(req.uri());
      String uri = pars.get("uri");
      List<Object> ls = pars.getList("methods");
      List<HttpMethod> meths = ls != null 
          ? ls.stream()
              .map(Objects::toString)
              .map(HttpMethod::valueOf)
              .collect(Collectors.toList())
          : Collections.EMPTY_LIST;
      String group = pars.get("group");
      Predicate<Role> type = up.getParam(1).equals("allow") ? r->!r.isDeny() : r->r.isDeny();
      Stream<Role> roles = x.channel().storage().roles().filter(type);
      if(uri != null) {
        roles = roles.filter(r->uri.matches(r.route().regexString()));
      }
      if(!meths.isEmpty()) {
        roles = roles.filter(r->r.route().methods().stream().anyMatch(meths::contains));
      }
      if(group != null) {
        roles = roles.filter(r->r.groups().stream().anyMatch(g->g.getName().equals(group)));
      }
      ByteBuf buf = ((HttpContent)x.message()).content();
      try {
        Role role = roles.findFirst().orElseThrow(()->new HttpRequestException(
            new ErrMessage(HttpResponseStatus.NOT_FOUND, "Role Not Found")));
        String json = buf.toString(StandardCharsets.UTF_8);
        Role newRole = ((Http)x.channel()).gson().fromJson(json, Role.class);
        ReferenceCountUtil.safeRelease(x.message());
        if(newRole.route() != null) {
          role.setRoute(newRole.route());
        }
        if(newRole.groups() != null && !newRole.groups().isEmpty()) {
          role.setGroups(x.channel().storage().groups()
              .filter(g->newRole.groups().stream().anyMatch(h->g.getName().equals(h.getName())))
              .collect(Collectors.toList()));
        }
        x.channel().storage().set(role);
        json = ((Http)x.channel()).gson().toJson(role);
        buf = x.context().alloc().buffer(json.length());
        buf.writeCharSequence(json, StandardCharsets.UTF_8);
        HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED, buf);
        res.headers()
            .add(new JsonContentHeader(buf.readableBytes()))
            .add(new ConnectionHeaders(x))
            .add(new DateHeader())
            .add(new ServerHeader());
        HttpConstants.sendAndCheckConnection(x, res);
      }
      catch(HttpRequestException e) {
        HttpConstants.sendError(x, e.errMessage());
      }
      catch(Exception e) {
        ErrMessage msg = new ErrMessage(HttpResponseStatus.BAD_REQUEST, e.getMessage())
            .put("type", e.getClass())
            .put("cause", e.getCause());
        HttpConstants.sendError(x, msg);
      }
    }
  }
  
}
