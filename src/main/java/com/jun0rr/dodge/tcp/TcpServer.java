/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.Host;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class TcpServer extends AbstractChannelSetup {
  
  private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
  
  public TcpServer(EventLoopGroup master, EventLoopGroup worker, Host bind) {
    this(master, worker, bind, new DefaultChannelHandlers());
  }
  
  public TcpServer(EventLoopGroup master, EventLoopGroup worker, Host bind, ChannelHandlers hds) {
    super(master, worker, bind, hds);
  }
  
  public TcpServer(Host bind) {
    this(bind, new DefaultChannelHandlers());
  }
  
  public TcpServer(Host bind, ChannelHandlers hds) {
    this(new NioEventLoopGroup(1), new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2), bind, hds);
  }
  
  @Override
  public FutureEvent start() {
    ChannelFuture cf = serverBootstrap().bind(address().toSocketAddr());
    return FutureEvent.of(this, cf, cf.channel(), masterGroup(), workerGroup())
        .acceptNext(f->logger.debug("Listening on {}", f.channel().localAddress()));
  }
  
}
