/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class HttpAuthHandler extends ChannelInboundHandlerAdapter {
  
  public static final Predicate<String> URI_GET_USER = Pattern.compile("\\/?auth\\/user\\/" + User.EMAIL_REGEX).asPredicate();
  
  public static final Predicate<String> URI_GET_GROUP = Pattern.compile("\\/?auth\\/group\\/(GET|POST|PUT|DELETE)" + User.EMAIL_REGEX).asPredicate();
  
  private final HttpServer server;
  
  public HttpAuthHandler(HttpServer server) {
    this.server = Match.notNull(server).getOrFail("Bad null HttpServer");
  }
  
  @Override
  public void channelRead(ChannelHandlerContext chc, Object o) {
    
  }
  
  public User get(ChannelHandlerContext chc, HttpRequest req) {
    return null;
  }
  
}
