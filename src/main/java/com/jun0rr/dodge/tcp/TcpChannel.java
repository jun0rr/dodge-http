/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.dodge.http.auth.Storage;
import com.jun0rr.dodge.metrics.Metric;
import com.jun0rr.util.Host;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author F6036477
 */
public interface TcpChannel {
  
  public static final String VERSION = "0.1";
  
  public static final Logger logger = LoggerFactory.getLogger(TcpChannel.class);
  
  
  public TcpChannel setBufferSize(int size);
  
  public int getBufferSize();
  
  public int getMasterThreads();

  public TcpChannel setMasterThreads(int masterThreads);

  public int getWorkerThreads();

  public TcpChannel setWorkerThreads(int workerThreads);

  public EventLoopGroup getMasterGroup();

  public TcpChannel setMasterGroup(EventLoopGroup masterGroup);

  public EventLoopGroup getWorkerGroup();

  public TcpChannel setWorkerGroup(EventLoopGroup workerGroup);
  
  public TcpChannel setMetricsEnabled(boolean metrics);
  
  public boolean isMetricsEnabled();
  
  public List<Metric> metrics();
  
  public Host getAddress();

  public TcpChannel setAddress(Host listenAddress);

  public Level getLogLevel();

  public TcpChannel setLogLevel(Level level);
  
  public boolean isSslEnabled();
  
  public TcpChannel setSslEnabled(boolean enabled);
  
  public Path getKeystorePath();
  
  public TcpChannel setKeystorePath(Path p);
  
  public char[] getKeystorePass();
  
  public TcpChannel setKeystorePass(char[] pass);
  
  public Storage startStorage();
  
  public Path getStoragePath();
  
  public TcpChannel setStoragePath(Path path);
  
  public Storage storage();
  
  public Duration uptime();
  
  public Instant startup();

  
  public <T> TcpChannel addHandler(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs);

  public TcpChannel addHandler(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs);

  public TcpChannel addHandler(Supplier<ChannelHandler> cih);
  
  public Attributes attributes();

  public ChannelInitializer<SocketChannel> createInitializer();
  
  public FutureEvent start();
  
  
  public static TcpChannel newClient() {
    return new DefaultTcpChannel(BOOTSTRAP);
  }
  
  public static TcpChannel newServer() {
    return new DefaultTcpChannel(SERVER_BOOTSTRAP);
  }
  
  
  
  public static final Function<TcpChannel,AbstractBootstrap> BOOTSTRAP = c->{
    return new Bootstrap()
        .group(c.getMasterGroup())
        .channel(NioSocketChannel.class)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .option(ChannelOption.AUTO_READ, Boolean.TRUE)
        .handler(c.createInitializer());
  };
  
  public static final Function<TcpChannel,AbstractBootstrap> SERVER_BOOTSTRAP = c->{
    return new ServerBootstrap()
        .group(c.getMasterGroup(), c.getWorkerGroup())  
        .channel(NioServerSocketChannel.class)
        .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_CLOSE, Boolean.TRUE)
        .childOption(ChannelOption.AUTO_READ, Boolean.TRUE)
        .childHandler(c.createInitializer());
  };
  
}
