/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.dodge.http.handler.EventInboundHandler;
import com.jun0rr.dodge.http.handler.EventOutboundHandler;
import com.jun0rr.dodge.http.handler.SSLConnectHandler;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class DefaultChannelHandlers implements ChannelHandlers {
  
  static final Logger logger = LoggerFactory.getLogger(DefaultChannelHandlers.class);
  
  private final List<Supplier<ChannelHandler>> handlers;
  
  private final Map<String,Object> attrs;
  
  private SSLHandlerFactory sslfactory;
  
  public DefaultChannelHandlers(SSLHandlerFactory shf) {
    this.handlers = new LinkedList<>();
    this.attrs = new ConcurrentHashMap<>();
    this.sslfactory = shf;
  }

  public DefaultChannelHandlers() {
    this(null);
  }

  @Override
  public <T> ChannelHandlers add(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs) {
    Match.notNull(evt).failIfNotMatch("Bad null ChannelEvent");
    Match.notNull(cs).failIfNotMatch("Bad null Consumer");
    ConsumerType.<T>of(type, cs.get());
    if(evt.isInboundEvent()) {
      handlers.add(()->new EventInboundHandler(attrs, evt.asInboundEvent(), ConsumerType.of(type, cs.get())));
    }
    else {
      handlers.add(()->new EventOutboundHandler(attrs, evt.asOutboundEvent(), ConsumerType.of(type, cs.get())));
    }
    return this;
  }

  @Override
  public ChannelHandlers add(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs) {
    return add(evt, Object.class, cs);
  }

  @Override
  public ChannelHandlers add(Supplier<ChannelHandler> cih) {
    Match.notNull(cih).failIfNotMatch("Bad null ChannelInboundHandler");
    handlers.add(cih);
    return this;
  }

  @Override
  public ChannelHandlers setSSLHandlerFactory(SSLHandlerFactory shf) {
    this.sslfactory = shf;
    return this;
  }
  
  @Override
  public SSLHandlerFactory sslHandlerFactory() {
    return sslfactory;
  }
  
  protected void initHandlers(SocketChannel sc) {
    handlers.stream()
        .map(Supplier::get)
        .forEach(sc.pipeline()::addLast);
  }
  
  protected void initSslHandler(SocketChannel sc) {
    if(sslfactory != null) {
      sc.pipeline().addLast(sslfactory.create(sc.alloc()));
      sc.pipeline().addLast(new SSLConnectHandler());
    }
  }
  
  @Override
  public ChannelInitializer<SocketChannel> createInitializer() {
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        initSslHandler(c);
        initHandlers(c);
      }
    };
  }
  
}
