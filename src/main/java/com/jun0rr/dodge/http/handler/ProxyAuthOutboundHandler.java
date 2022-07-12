/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.dodge.http.header.ProxyAuthorizationHeader;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class ProxyAuthOutboundHandler extends ChannelOutboundHandlerAdapter {
  
  private static final Logger logger = LoggerFactory.getLogger(ProxyAuthOutboundHandler.class);
  
  private final String proxyUser;
  
  private final String proxyPwd;
  
  public ProxyAuthOutboundHandler(String user, String pwd) {
    this.proxyUser = Match.notEmpty(user).getOrFail("Bad null proxy user");
    this.proxyPwd = Match.notEmpty(pwd).getOrFail("Bad null proxy password");
  }
  
  @Override
  public void write(ChannelHandlerContext chc, Object o, ChannelPromise cp) throws Exception {
    if(o instanceof HttpRequest) {
      HttpRequest req = (HttpRequest) o;
      req.headers().add(new ProxyAuthorizationHeader(proxyUser, proxyPwd));
    }
    chc.write(o, cp);
  }
  
}
