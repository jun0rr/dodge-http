/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.Host;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
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
  
  public <T> TcpChannel addHandler(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs);

  public TcpChannel addHandler(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs);

  public TcpChannel addHandler(Supplier<ChannelHandler> cih);
  
  public Map<String,Object> attributes();

  public ChannelInitializer<SocketChannel> createInitializer();
  
  public FutureEvent startClient();
  
  public FutureEvent startServer();
  
  
  public static TcpChannel newChannel() {
    return new DefaultTcpChannel();
  }
  
}
