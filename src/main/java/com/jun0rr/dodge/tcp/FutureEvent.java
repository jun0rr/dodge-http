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
  
  
  public ChannelSetup setup();
  
  public Future future();
  
  public Channel channel();
  
  public default boolean isChannelClosed() {
    return channel().closeFuture().isDone();
  }
  
  public default FutureEvent channelClose() {
    return applyNext(f->f.channel().close());
  }
  
  public default FutureEvent channelWrite(Object o) {
    return applyNext(f->f.channel().write(o));
  }
  
  public default FutureEvent channelWriteAndFlush(Object o) {
    return applyNext(f->f.channel().writeAndFlush(o));
  }
  
  public EventLoopGroup masterGroup();
  
  public EventLoopGroup workerGroup();
  
  public default FutureEvent shutdownMasterGroup() {
    return applyNext(f->f.masterGroup().shutdownGracefully());
  }
  
  public default FutureEvent shutdownWorkerGroup() {
    return applyNext(f->f.workerGroup().shutdownGracefully());
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
  
  
  public static FutureEvent of(ChannelSetup cs, ChannelFuture cf) {
    EventLoopGroup g = cf.channel().eventLoop().parent();
    return new FutureEventImpl(cs, cf, cf.channel(), g, g);
  }
  
  public static FutureEvent of(ChannelSetup cs, ChannelFuture cf, EventLoopGroup g) {
    EventLoopGroup m = cf.channel().eventLoop().parent();
    return new FutureEventImpl(cs, cf, cf.channel(), m, g);
  }
  
  public static FutureEvent of(ChannelSetup cs, Future f, Channel c, EventLoopGroup m, EventLoopGroup w) {
    return new FutureEventImpl(cs, f, c, m, w);
  }
  
  
  
  
  
  static class FutureEventImpl implements FutureEvent {
    
    private final ChannelSetup setup;
    
    private final Channel channel;
    
    private final Future future;
    
    private final EventLoopGroup master;
    
    private final EventLoopGroup worker;
    
    public FutureEventImpl(ChannelSetup cs, Future f, Channel c, EventLoopGroup m, EventLoopGroup w) {
      this.setup = Match.notNull(cs).getOrFail("Bad null ChannelSetup");
      this.future = Match.notNull(f).getOrFail("Bad null Future");
      this.channel = Match.notNull(c).getOrFail("Bad null Channel");
      this.master = Match.notNull(m).getOrFail("Bad null master EventLoopGroup");
      this.worker = Match.notNull(w).getOrFail("Bad null worker EventLoopGroup");
    }

    @Override
    public ChannelSetup setup() {
      return setup;
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
    public EventLoopGroup masterGroup() {
      return master;
    }

    @Override
    public EventLoopGroup workerGroup() {
      return worker;
    }

    @Override
    public FutureEvent with(Future f) {
      return new FutureEventImpl(setup, f, channel, master, worker);
    }
  
  }
  
}
