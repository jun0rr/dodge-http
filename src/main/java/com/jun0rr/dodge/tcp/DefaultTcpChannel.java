/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import ch.qos.logback.classic.LoggerContext;
import com.jun0rr.dodge.http.auth.Storage;
import com.jun0rr.dodge.http.handler.EventInboundHandler;
import com.jun0rr.dodge.http.handler.EventOutboundHandler;
import com.jun0rr.dodge.http.handler.SSLConnectHandler;
import com.jun0rr.dodge.metrics.Metric;
import com.jun0rr.dodge.metrics.TcpMetricsHandler;
import com.jun0rr.util.Host;
import com.jun0rr.util.ResourceLoader;
import com.jun0rr.util.match.Match;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author F6036477
 */
public class DefaultTcpChannel implements TcpChannel {
  
  public static final int DEFAULT_BUFFER_SIZE = 128 * 1024 * 1024;
  
  private final Function<TcpChannel,AbstractBootstrap> bootstrap;
  
  private int masterThreads = 1;
  
  private int workerThreads = Runtime.getRuntime().availableProcessors()*2;
  
  private EventLoopGroup masterGroup;
  
  private EventLoopGroup workerGroup;
  
  private Host address;
  
  private Level level = Level.INFO;
  
  private Path keystorePath = ResourceLoader.caller().loadPath("keystore.jks");
  
  private char[] keystorePass = {'k', 'p', 'K', 'p', 'l', 'E', 'F', 'a', 'J', 'k', 'x', '2'};
  
  private boolean sslEnabled = false;
  
  protected boolean metricsEnabled = true;
  
  protected boolean storageEnabled = true;
  
  protected Path storagePath = ResourceLoader.caller().loadPath("storage");
  
  protected Storage storage;
  
  private SSLHandlerFactory sslFactory;
  
  private int bufferSize = DEFAULT_BUFFER_SIZE;
  
  protected final List<Supplier<ChannelHandler>> handlers;
  
  protected final Attributes attrs;
  
  protected final List<Metric> metrics;
  
  public DefaultTcpChannel(Function<TcpChannel,AbstractBootstrap> bootstrap) {
    this.bootstrap = Match.notNull(bootstrap).getOrFail("Bad null Bootstrap");
    this.handlers = new LinkedList<>();
    this.metrics = new CopyOnWriteArrayList<>();
    this.attrs = new Attributes();
  }
  
  @Override
  public TcpChannel setBufferSize(int size) {
    this.bufferSize = Match.between(size, 1024, Integer.MAX_VALUE).getOrFail("Bad buffer size: " + size);
    return this;
  }
  
  @Override
  public int getBufferSize() {
    return bufferSize;
  }
  
  @Override
  public int getMasterThreads() {
    return masterThreads;
  }

  @Override
  public TcpChannel setMasterThreads(int masterThreads) {
    this.masterThreads = masterThreads;
    return this;
  }

  @Override
  public int getWorkerThreads() {
    return workerThreads;
  }

  @Override
  public TcpChannel setWorkerThreads(int workerThreads) {
    this.workerThreads = workerThreads;
    return this;
  }

  @Override
  public EventLoopGroup getMasterGroup() {
    if(masterThreads < 1) {
      throw new IllegalStateException("Master threads not defined");
    }
    else if(masterGroup == null) {
      masterGroup = new NioEventLoopGroup(masterThreads);
    }
    return masterGroup;
  }

  @Override
  public TcpChannel setMasterGroup(EventLoopGroup masterGroup) {
    this.masterGroup = masterGroup;
    return this;
  }

  @Override
  public EventLoopGroup getWorkerGroup() {
    if(workerThreads < 1) {
      throw new IllegalStateException("Worker threads not defined");
    }
    else if(workerGroup == null) {
      workerGroup = new NioEventLoopGroup(workerThreads);
    }
    return workerGroup;
  }

  @Override
  public TcpChannel setWorkerGroup(EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
    return this;
  }
  
  @Override
  public TcpChannel setMetricsEnabled(boolean metrics) {
    this.metricsEnabled = metrics;
    return this;
  }
  
  @Override
  public boolean isMetricsEnabled() {
    return this.metricsEnabled;
  }
  
  @Override
  public List<Metric> metrics() {
    return this.metrics;
  }
  
  @Override
  public Host getAddress() {
    return address;
  }

  @Override
  public TcpChannel setAddress(Host address) {
    this.address = address;
    return this;
  }

  @Override
  public Level getLogLevel() {
    return level;
  }

  @Override
  public TcpChannel setLogLevel(Level level) {
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    switch(level) {
      case TRACE:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.TRACE));
        break;
      case DEBUG:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.DEBUG));
        break;
      case INFO:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.INFO));
        break;
      case WARN:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.WARN));
        break;
      case ERROR:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.ERROR));
        break;
      default:
        lc.getLoggerList().forEach(l->l.setLevel(ch.qos.logback.classic.Level.INFO));
        break;
    }
    return this;
  }
  
  @Override
  public boolean isSslEnabled() {
    return this.sslEnabled;
  }
  
  @Override
  public TcpChannel setSslEnabled(boolean enabled) {
    this.sslEnabled = enabled;
    return this;
  }
  
  @Override
  public Path getKeystorePath() {
    return keystorePath;
  }
  
  @Override
  public TcpChannel setKeystorePath(Path p) {
    this.keystorePath = p;
    return this;
  }
  
  @Override
  public char[] getKeystorePass() {
    return keystorePass;
  }
  
  @Override
  public TcpChannel setKeystorePass(char[] pass) {
    this.keystorePass = pass;
    return this;
  }
  
  public TcpChannel setStorageEnabled(boolean enabled) {
    this.storageEnabled = enabled;
    return this;
  }
  
  public boolean isStorageEnabled() {
    return storageEnabled;
  }
  
  @Override
  public Path getStorageDir() {
    return storagePath;
  }
  
  @Override
  public TcpChannel setStorageDir(Path path) {
    this.storagePath = Match.notNull(path).getOrFail("Bad null storage Path");
    return this;
  }
  
  @Override
  public Storage storage() {
    return storage;
  }

  @Override
  public <T> TcpChannel addHandler(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs) {
    Match.notNull(evt).failIfNotMatch("Bad null ChannelEvent");
    Match.notNull(cs).failIfNotMatch("Bad null ChannelHandler Supplier");
    ConsumerType.<T>of(type, cs.get());
    if(evt.isInboundEvent()) {
      handlers.add(()->new EventInboundHandler(this, attrs, evt.asInboundEvent(), ConsumerType.of(type, cs.get())));
    }
    else {
      handlers.add(()->new EventOutboundHandler(this, attrs, evt.asOutboundEvent(), ConsumerType.of(type, cs.get())));
    }
    return this;
  }

  @Override
  public TcpChannel addHandler(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs) {
    return addHandler(evt, Object.class, cs);
  }

  @Override
  public TcpChannel addHandler(Supplier<ChannelHandler> cih) {
    Match.notNull(cih).failIfNotMatch("Bad null ChannelInboundHandler");
    handlers.add(cih);
    return this;
  }
  
  @Override
  public Attributes attributes() {
    return attrs;
  }

  protected void initHandlers(SocketChannel sc) {
    handlers.stream()
        .map(Supplier::get)
        .forEach(sc.pipeline()::addLast);
  }
  
  protected void initSslHandler(SocketChannel sc) {
    if(sslFactory != null) {
      sc.pipeline().addLast(sslFactory.create(sc.alloc()));
      sc.pipeline().addLast(new SSLConnectHandler());
    }
  }
  
  @Override
  public ChannelInitializer<SocketChannel> createInitializer() {
    Match.notEmpty(handlers).failIfNotMatch("Bad empty ChannelHandler List");
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        initSslHandler(c);
        if(isMetricsEnabled()) {
          c.pipeline().addLast(new TcpMetricsHandler(DefaultTcpChannel.this));
        }
        initHandlers(c);
      }
    };
  }
  
  @Override
  public FutureEvent start() {
    if(storageEnabled) {
      storage = new Storage(storagePath);
    }
    AbstractBootstrap boot = bootstrap.apply(this);
    if(ServerBootstrap.class.isAssignableFrom(boot.getClass())) {
      if(sslEnabled) {
        sslFactory = SSLHandlerFactory.forServer(keystorePath, keystorePass);
      }
      ChannelFuture cf = ((ServerBootstrap)boot).bind(getAddress().toSocketAddr());
      return FutureEvent.of(this, cf)
          .acceptNext(f->logger.debug("Listening on {}", f.channel().localAddress()));
    }
    else {
      if(sslEnabled) {
        sslFactory = SSLHandlerFactory.forClient();
      }
      ChannelFuture cf = ((Bootstrap)boot).connect(getAddress().toSocketAddr());
      return FutureEvent.of(this, cf)
          .acceptNext(f->logger.debug("Connected to {}", f.channel().remoteAddress()));
    }
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 83 * hash + this.masterThreads;
    hash = 83 * hash + this.workerThreads;
    hash = 83 * hash + Objects.hashCode(this.address);
    hash = 83 * hash + Objects.hashCode(this.level);
    hash = 83 * hash + Objects.hashCode(this.keystorePath);
    hash = 83 * hash + (this.sslEnabled ? 1 : 0);
    hash = 83 * hash + this.bufferSize;
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
    final DefaultTcpChannel other = (DefaultTcpChannel) obj;
    if (this.masterThreads != other.masterThreads) {
      return false;
    }
    if (this.workerThreads != other.workerThreads) {
      return false;
    }
    if (this.sslEnabled != other.sslEnabled) {
      return false;
    }
    if (this.bufferSize != other.bufferSize) {
      return false;
    }
    if (!Objects.equals(this.address, other.address)) {
      return false;
    }
    if (this.level != other.level) {
      return false;
    }
    return Objects.equals(this.keystorePath, other.keystorePath);
  }

  @Override
  public String toString() {
    return "TcpChannel{" + "masterThreads=" + masterThreads + ", workerThreads=" + workerThreads + ", address=" + address + ", logLevel=" + level + ", keystorePath=" + keystorePath + ", sslEnabled=" + sslEnabled + ", bufferSize=" + bufferSize + '}';
  }
  
}
