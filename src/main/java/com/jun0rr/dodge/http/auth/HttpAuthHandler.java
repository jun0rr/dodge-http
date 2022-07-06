/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.Method;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class HttpAuthHandler extends ChannelInboundHandlerAdapter {
  
  public static final HttpRoute USER_GET_ALL = new HttpRoute("\\/?auth\\/user\\/?", Method.GET);
  
  public static final HttpRoute USER_GET_ONE = new HttpRoute(String.format("\\/?auth\\/user\\/(%s)", User.EMAIL_REGEX), Method.GET);
  
  private final HttpServer server;
  
  public HttpAuthHandler(HttpServer server) {
    this.server = Match.notNull(server).getOrFail("Bad null HttpServer");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    if(HttpRequest.class.isAssignableFrom(o.getClass())) {
      HttpRequest req = (HttpRequest) o;
      if(USER_GET_ONE.test(req)) userGetOne(chc, req);
    }
  }
  
  public User userGetOne(ChannelHandlerContext chc, HttpRequest req) {
    Pattern p = Pattern.compile(USER_GET_ONE.pattern());
    Matcher m = p.matcher(req.uri());
    if(m.find()) {
      String email = m.group();
      System.out.println("**** HttpAuthHandler.email = " + email);
    }
  }
  
}
