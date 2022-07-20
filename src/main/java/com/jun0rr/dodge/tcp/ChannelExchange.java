/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Juno
 */
public interface ChannelExchange<T> {
  
  public TcpChannel channel();
  
  public ChannelHandlerContext context();
  
  public T message();
  
  public ChannelPromise promise();
  
  public ChannelEvent event();
  
  public Attributes attributes();
  
  public void forwardMessage();
  
  public void read(Object o);
  
  public FutureEvent write(Object o);
  
  public FutureEvent writeAndFlush(Object o);
  
  
  public static <U> ChannelExchange of(TcpChannel ch, ChannelEvent evt, ChannelHandlerContext ctx, U msg, Attributes attrs, ChannelPromise prom) {
    return new ChannelExchangeImpl(ch, evt, ctx, msg, attrs, prom);
  }
  
  public static <U> ChannelExchange of(TcpChannel ch, ChannelEvent evt, ChannelHandlerContext ctx, U msg, Attributes attrs) {
    return new ChannelExchangeImpl(ch, evt, ctx, msg, attrs);
  }
  
  
  
  
  
  static class ChannelExchangeImpl<T> implements ChannelExchange<T> {
    
    static final Logger logger = LoggerFactory.getLogger(ChannelExchangeImpl.class);
    
    private final TcpChannel tcp;
    
    private final ChannelHandlerContext context;
    
    private final T message;
    
    private final ChannelPromise promise;
    
    private final ChannelEvent event;
    
    private final Attributes attrs;
    
    public ChannelExchangeImpl(TcpChannel ch, ChannelEvent evt, ChannelHandlerContext ctx, T msg, Attributes attrs, ChannelPromise prom) {
      this.tcp = Match.notNull(ch).getOrFail("Bad null TcpChannel");
      this.event = Match.notNull(evt).getOrFail("Bad null ChannelEvent");
      this.context = Match.notNull(ctx).getOrFail("Bad null ChannelHandlerContext");
      this.message = msg;
      this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
      this.promise = Optional.ofNullable(prom).orElse(context.newPromise());
    }
    
    public ChannelExchangeImpl(TcpChannel ch, ChannelEvent evt, ChannelHandlerContext ctx, T msg, Attributes attrs) {
      this(ch, evt, ctx, msg, attrs, null);
    }

    @Override
    public TcpChannel channel() {
      return tcp;
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
    public Attributes attributes() {
      return attrs;
    }
    
    @Override
    public void forwardMessage() {
      if(ChannelEvent.Inbound.READ == event) {
        context.fireChannelRead(message);
      }
      else if(ChannelEvent.Outbound.WRITE == event) {
        context.writeAndFlush(message, promise);
      }
    }
    
    @Override
    public void read(Object o) {
      context.fireChannelRead(o);
    }
    
    @Override
    public FutureEvent write(Object o) {
      return FutureEvent.of(tcp, context.write(o, promise));
    }
  
    @Override
    public FutureEvent writeAndFlush(Object o) {
      return FutureEvent.of(tcp, context.writeAndFlush(o, promise));
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
