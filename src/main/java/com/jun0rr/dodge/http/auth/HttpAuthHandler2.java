/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.google.gson.JsonObject;
import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.header.JsonContentHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.util.match.Match;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 *
 * @author F6036477
 */
public class HttpAuthHandler2 extends ChannelInboundHandlerAdapter {
  
  public static final String URI_USER_EMAIL = String.format("\\/?auth\\/users\\/(%s)", User.EMAIL_REGEX);
  
  public static final HttpRoute ROUTE_USER_GET_ALL = new HttpRoute("\\/?auth\\/users\\/?", HttpMethod.GET);
  
  public static final HttpRoute ROUTE_USER_GET_ONE = new HttpRoute(URI_USER_EMAIL, HttpMethod.GET);
  
  
  private final HttpServer server;
  
  public HttpAuthHandler2(HttpServer server) {
    this.server = Match.notNull(server).getOrFail("Bad null HttpServer");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(HttpRequest.class.isAssignableFrom(o.getClass())) {
      HttpRequest req = (HttpRequest) o;
      if(ROUTE_USER_GET_ALL.test(req)) userGetAll(chc, req);
      else if(ROUTE_USER_GET_ONE.test(req)) userGetOne(chc, req);
    }
  }
  
  public void userGetOne(ChannelHandlerContext chc, HttpRequest req) {
    Matcher m = ROUTE_USER_GET_ONE.matcher(req.uri());
    if(m.find()) {
      String email = m.group(1);
      Optional<User> opt = server.storage().users().filter(u->u.getEmail().equals(email)).findFirst();
      FullHttpResponse res;
      if(opt.isPresent()) {
        String json = opt.map(server.gson()::toJson).get();
        ByteBuf content = chc.alloc().buffer(json.length());
        content.writeCharSequence(json, StandardCharsets.UTF_8);
        res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
      }
      else {
        JsonObject err = new JsonObject();
        err.addProperty("uri", req.uri());
        err.addProperty("error", "User e-mail not found");
        String json = server.gson().toJson(err);
        ByteBuf content = chc.alloc().buffer(json.length());
        content.writeCharSequence(json, StandardCharsets.UTF_8);
        res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, content);
      }
      res.headers()
          .add(new JsonContentHeader(res.content().readableBytes()))
          .add(new DateHeader())
          .add(new ConnectionCloseHeaders())
          .add(new ServerHeader());
      chc.writeAndFlush(res);
    }
  }
  
  public void userGetAll(ChannelHandlerContext chc, HttpRequest req) {
    List<User> all = server.storage().users().collect(Collectors.toList());
    String json = server.gson().toJson(all);
    ByteBuf content = chc.alloc().directBuffer(json.length());
    content.writeCharSequence(json, StandardCharsets.UTF_8);
    FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
    res.headers()
        .add(new JsonContentHeader(res.content().readableBytes()))
        .add(new DateHeader())
        .add(new ConnectionCloseHeaders())
        .add(new ServerHeader());
    chc.writeAndFlush(res);
  }
  
}
