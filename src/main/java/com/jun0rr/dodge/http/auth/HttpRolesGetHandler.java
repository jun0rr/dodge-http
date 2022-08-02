/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonArray;
import com.jun0rr.dodge.http.Http;
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
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
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
public class HttpRolesGetHandler implements Consumer<ChannelExchange<HttpRequest>> {
  
  static final Logger logger = LoggerFactory.getLogger(HttpRolesGetHandler.class);
  
  public static final HttpRoute ROUTE = HttpRoute.of("/?auth/roles/(allow|deny)/?([\\?\\&]((uri=[a-zA-Z0-9\\/\\-_]+)|(methods=[A-Z\\,]+)|(group=[a-zA-Z_]+[a-zA-Z0-9_\\-\\.]*)))*", HttpMethod.GET);
  
  public static HttpRolesGetHandler get() {
    return new HttpRolesGetHandler();
  }
  
  @Override
  public void accept(ChannelExchange<HttpRequest> x) {
    UriParam up = new UriParam(x.message().uri());
    RequestParam pars = new RequestParam(x.message().uri());
    String uri = pars.get("uri");
    List<Object> ls = pars.getList("methods");
    List<HttpMethod> meths = ls != null 
        ? ls.stream().map(Objects::toString).map(HttpMethod::valueOf).collect(Collectors.toList())
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
      roles = roles.filter(r->r.getGroups().stream().anyMatch(g->g.getName().equals(group)));
    }
    JsonArray array = new JsonArray();
    roles.forEach(r->array.add( ((Http)x.channel()).gson().toJsonTree(r) ));
    ByteBuf buf = x.context().alloc().directBuffer();
    buf.writeCharSequence(((Http)x.channel()).gson().toJson(array), StandardCharsets.UTF_8);
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
    res.headers()
        .add(new JsonContentHeader(buf.readableBytes()))
        .add(new ConnectionHeaders(x))
        .add(new DateHeader())
        .add(new ServerHeader());
    HttpConstants.sendAndCheckConnection(x, res);
  }
  
}
