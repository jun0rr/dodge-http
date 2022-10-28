/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.Unchecked;
import com.jun0rr.util.match.Match;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public interface FutureEvent {
  
  static final Logger logger = LoggerFactory.getLogger(FutureEvent.class);
  
  static final int[] COUNT = new int[]{0, 0};
  
  public TcpChannel tcpChannel();
  
  public Future future();
  
  public Channel channel();
  
  public default boolean isChannelClosed() {
    return channel().closeFuture().isDone();
  }
  
  public default FutureEvent close() {
    return applyNext(f->f.channel().close());
  }
  
  public default FutureEvent write(Object o) {
    return applyNext(f->f.channel().write(o));
  }
  
  public default FutureEvent writeAndFlush(Object o) {
    return applyNext(f->f.channel().writeAndFlush(o));
  }
  
  public default FutureEvent shutdownMasterGroup() {
    return applyNext(f->f.tcpChannel().getMasterGroup().shutdownGracefully());
  }
  
  public default FutureEvent shutdownWorkerGroup() {
    return applyNext(f->f.tcpChannel().getWorkerGroup().shutdownGracefully());
  }
  
  public default FutureEvent syncUninterruptibly() {
    final CountDownLatch count = new CountDownLatch(1);
    FutureEvent fe = acceptNext(f->count.countDown());
    Unchecked.call(()->count.await());
    return fe;
  }
  
  public FutureEvent with(Future f);
  
  public default FutureEvent applyNext(Function<FutureEvent,Future> fn) {
    ChannelPromise p = channel().newPromise();
    future().addListener(f->{
      fn.apply(with(f)).addListener(g->{
        if(g.isSuccess()) p.setSuccess();
        else p.setFailure(g.cause());
      });
    });
    return with(p);
  }
  
  public default FutureEvent acceptNext(Consumer<FutureEvent> c) {
    return applyNext(f->{
      c.accept(f);
      return f.future();
    });
  }

  public default FutureEvent acceptOnClose(Consumer<FutureEvent> c) {
    return with(channel().closeFuture()).acceptNext(c);
  }
  
  
  public static FutureEvent of(TcpChannel ch, ChannelFuture cf) {
    EventLoopGroup g = cf.channel().eventLoop().parent();
    return new FutureEventImpl(ch, cf, cf.channel());
  }
  
  public static FutureEvent of(TcpChannel ch, Future f, Channel c) {
    return new FutureEventImpl(ch, f, c);
  }
  
  
  
  
  
  static class FutureEventImpl implements FutureEvent {
    
    private final TcpChannel tcp;
    
    private final Channel channel;
    
    private final Future future;
    
    public FutureEventImpl(TcpChannel ch, Future f, Channel c) {
      this.tcp = Match.notNull(ch).getOrFail("Bad null TcpChannel");
      this.future = Match.notNull(f).getOrFail("Bad null Future");
      this.channel = Match.notNull(c).getOrFail("Bad null Channel");
    }
    
    @Override
    public TcpChannel tcpChannel() {
      return tcp;
    }

    @Override
    public Future future() {
      return future;
    }
    
    @Override
    public Channel channel() {
      return channel;
    }

    @Override
    public FutureEvent with(Future f) {
      return new FutureEventImpl(tcp, f, channel);
    }
  
  }
  
}
