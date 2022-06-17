/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.Host;
import com.jun0rr.util.match.Match;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 *
 * @author F6036477
 */
public abstract class AbstractChannelSetup implements ChannelSetup {

  protected final EventLoopGroup masterGroup;
  
  protected final EventLoopGroup workerGroup;
  
  protected final Host address;
  
  protected final ChannelHandlers handlers;
  
  protected AbstractChannelSetup(EventLoopGroup masterGroup, EventLoopGroup workerGroup, Host address, ChannelHandlers ch) {
    this.masterGroup = Match.notNull(masterGroup).getOrFail("Bad null EventLoopGroup");
    this.workerGroup = Match.notNull(masterGroup).getOrFail("Bad null EventLoopGroup");
    this.address = Match.notNull(address).getOrFail("Bad null Host address");
    this.handlers = Match.notNull(ch).getOrFail("Bad null ChannelHandlers");
  }
  
  protected AbstractChannelSetup(EventLoopGroup masterGroup, Host address, ChannelHandlers ch) {
    this(masterGroup, masterGroup, address, ch);
  }
  
  protected ServerBootstrap serverBootstrap() {
    return new ServerBootstrap()
        .group(masterGroup, workerGroup)  
        .channel(NioServerSocketChannel.class)
        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_READ, Boolean.TRUE)
        .childHandler(handlers.createInitializer());
  }
  
  protected Bootstrap bootstrap() {
    return new Bootstrap()
        .group(masterGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .option(ChannelOption.AUTO_READ, Boolean.TRUE)
        .handler(handlers.createInitializer());
  }
  
  @Override
  public Host address() {
    return address;
  }
  
  @Override
  public ChannelHandlers handlers() {
    return handlers;
  }

  @Override
  public EventLoopGroup masterGroup() {
    return masterGroup;
  }

  @Override
  public EventLoopGroup workerGroup() {
    return workerGroup;
  }

  @Override public abstract FutureEvent start();
  
}
