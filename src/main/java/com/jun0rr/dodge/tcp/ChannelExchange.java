/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 *
 * @author Juno
 */
public interface ChannelExchange<T> {
  
  public ChannelHandlerContext context();
  
  public T message();
  
  public ChannelPromise promise();
  
  public ChannelEvent event();
  
  public void putAttr(String key, Object val);
  
  public <O> Optional<O> getAttr(String key);
  
  public <O> Optional<O> rmAttr(String key);
  
  public void forwardMessage();
  
  public void read(Object o);
  
  public void write(Object o);
  
  public void writeAndFlush(Object o);
  
  
  public static <U> ChannelExchange of(ChannelEvent evt, ChannelHandlerContext ctx, U msg, Map<String,Object> attrs, ChannelPromise prom) {
    return new ChannelExchangeImpl(evt, ctx, msg, attrs, prom);
  }
  
  public static <U> ChannelExchange of(ChannelEvent evt, ChannelHandlerContext ctx, U msg, Map<String,Object> attrs) {
    return new ChannelExchangeImpl(evt, ctx, msg, attrs);
  }
  
  
  
  
  
  static class ChannelExchangeImpl<T> implements ChannelExchange<T> {
    
    private final ChannelHandlerContext context;
    
    private final T message;
    
    private final ChannelPromise promise;
    
    private final ChannelEvent event;
    
    private final Map<String,Object> attrs;
    
    public ChannelExchangeImpl(ChannelEvent evt, ChannelHandlerContext ctx, T msg, Map<String,Object> attrs, ChannelPromise prom) {
      this.event = Match.notNull(evt).getOrFail("Bad null ChannelEvent");
      this.context = Match.notNull(ctx).getOrFail("Bad null ChannelHandlerContext");
      this.message = msg;
      this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
      this.promise = Optional.ofNullable(prom).orElse(context.newPromise());
    }
    
    public ChannelExchangeImpl(ChannelEvent evt, ChannelHandlerContext ctx, T msg, Map<String,Object> attrs) {
      this(evt, ctx, msg, attrs, null);
    }

    @Override
    public ChannelHandlerContext context() {
      return context;
    }
    
    @Override
    public T message() {
      return message;
    }
    
    @Override
    public ChannelPromise promise() {
      return promise;
    }
    
    @Override
    public ChannelEvent event() {
      return event;
    }
    
    @Override
    public void putAttr(String key, Object val) {
      if(key != null && val != null) {
        attrs.put(String.format("%s.%s", context.channel().id().asShortText(), key), val);
      }
    }

    @Override
    public <O> Optional<O> getAttr(String key) {
      if(key == null || key.isBlank()) return Optional.empty();
      try {
        return Optional.ofNullable(attrs.get(
            String.format("%s.%s", context.channel().id().asShortText(), key))
        ).map(o->(O)o);
      }
      catch(ClassCastException e) {
        return Optional.empty();
      }
    }

    @Override
    public <O> Optional<O> rmAttr(String key) {
      if(key == null || key.isBlank()) return Optional.empty();
      try {
        return Optional.ofNullable(attrs.remove(
            String.format("%s.%s", context.channel().id().asShortText(), key))
        ).map(o->(O)o);
      }
      catch(ClassCastException e) {
        return Optional.empty();
      }
    }
  
    @Override
    public void forwardMessage() {
      if(ChannelEvent.Inbound.READ == event) {
        context.fireChannelRead(message);
      }
      else if(ChannelEvent.Outbound.WRITE == event) {
        context.write(message, promise);
      }
    }
    
    @Override
    public void read(Object o) {
      context.fireChannelRead(o);
    }
    
    @Override
    public void write(Object o) {
      context.write(o, promise);
    }
  
    @Override
    public void writeAndFlush(Object o) {
      context.writeAndFlush(o, promise);
    }
    
    @Override
    public int hashCode() {
      int hash = 5;
      hash = 41 * hash + Objects.hashCode(this.context);
      hash = 41 * hash + Objects.hashCode(this.event);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final ChannelExchangeImpl other = (ChannelExchangeImpl) obj;
      if (!Objects.equals(this.context, other.context)) {
        return false;
      }
      return Objects.equals(this.event, other.event);
    }
    
  }
  
}
