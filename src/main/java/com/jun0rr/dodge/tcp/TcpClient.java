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
public class TcpClient extends AbstractChannelSetup {
  
  private static final Logger logger = LoggerFactory.getLogger(TcpClient.class);
  
  public TcpClient(EventLoopGroup elg, Host address) {
    this(elg, address, new DefaultChannelHandlers());
  }
  
  public TcpClient(EventLoopGroup elg, Host address, ChannelHandlers hds) {
    super(elg, address, hds);
  }
  
  public TcpClient(Host address) {
    this(new NioEventLoopGroup(1), address);
  }
  
  @Override
  public FutureEvent start() {
    ChannelFuture f = bootstrap().connect(address().toSocketAddr());
    return FutureEvent.of(this, f, f.channel(), masterGroup(), workerGroup());
  }
  
}
