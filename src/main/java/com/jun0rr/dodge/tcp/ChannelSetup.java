/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.jun0rr.util.Host;
import io.netty.channel.EventLoopGroup;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public interface ChannelSetup {
  
  public Host address();
  
  public ChannelHandlers handlers();
  
  public EventLoopGroup masterGroup();
  
  public EventLoopGroup workerGroup();
  
  public FutureEvent start();
  
  
  public static void setLogLevel(Level lvl) {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    lc.getLoggerList().forEach(l->l.setLevel(lvl));
  }
  
}
