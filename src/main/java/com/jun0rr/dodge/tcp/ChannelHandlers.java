/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 *
 * @author F6036477
 */
public interface ChannelHandlers {
  
  public <T> ChannelHandlers add(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs);
  
  public ChannelHandlers add(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs);
  
  public ChannelHandlers add(Supplier<ChannelHandler> cih);
  
  public ChannelHandlers setSSLHandlerFactory(SSLHandlerFactory shf);
  
  public SSLHandlerFactory sslHandlerFactory();
  
  public ChannelInitializer<SocketChannel> createInitializer();
  
}
