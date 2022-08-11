/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.dodge.metrics.Metrics;
import ch.qos.logback.classic.LoggerContext;
import com.jun0rr.dodge.http.auth.Storage;
import com.jun0rr.dodge.http.handler.EventInboundHandler;
import com.jun0rr.dodge.http.handler.EventOutboundHandler;
import com.jun0rr.dodge.http.handler.SSLConnectHandler;
import com.jun0rr.dodge.http.util.Indexed;
import com.jun0rr.dodge.metrics.TcpMetricsHandler;
import com.jun0rr.util.Host;
import com.jun0rr.util.ResourceLoader;
import com.jun0rr.util.StringPad;
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
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
  
  private final Function<TcpChannel,AbstractBootstrap> bootstrap;
  
  private int masterThreads = 1;
  
  private int workerThreads = Runtime.getRuntime().availableProcessors()*2;
  
  private EventLoopGroup masterGroup;
  
  private EventLoopGroup workerGroup;
  
  private Host address;
  
  private Level level;
  
  private Path keystorePath = ResourceLoader.caller().loadPath("keystore.jks");
  
  private char[] keystorePass = {'k', 'p', 'K', 'p', 'l', 'E', 'F', 'a', 'J', 'k', 'x', '2'};
  
  private boolean sslEnabled = false;
  
  protected boolean metricsEnabled = true;
  
  protected boolean storageEnabled = true;
  
  protected Path storagePath = ResourceLoader.caller().loadPath("storage");
  
  protected Storage storage;
  
  private SSLHandlerFactory sslFactory;
  
  protected final List<ConfLog> conflog;
  
  protected final List<Supplier<ChannelHandler>> handlers;
  
  protected final Attributes attrs;
  
  protected final Metrics metrics;
  
  protected final Instant startup;
  
  public DefaultTcpChannel(Function<TcpChannel,AbstractBootstrap> bootstrap) {
    this.bootstrap = Match.notNull(bootstrap).getOrFail("Bad null Bootstrap");
    this.handlers = new LinkedList<>();
    this.metrics = new Metrics();
    this.attrs = new Attributes();
    this.startup = Instant.now();
    this.conflog = new LinkedList<>();
    setLogLevel(Level.DEBUG);
    ConfLog.ORDER = Indexed.Ints.dozens();
    addConfLog("Dodge Version", ()->VERSION);                   //0
    addConfLog("Log Level", this::getLogLevel);                 //10
    addConfLog("Master Threads", this::getMasterThreads);       //20
    addConfLog("Worker Threads", this::getWorkerThreads);       //30
    addConfLog("SSL Enabled", this::isSslEnabled);              //40
    addConfLog("Metrics Enabled", this::isMetricsEnabled);      //50
    addConfLog("Storage Path", this::getStoragePath);           //60
    addConfLog("Keystore Path", this::getKeystorePath);         //70
    addConfLog(Integer.MAX_VALUE, "Address", this::getAddress); //MAX
  }
  
  public void addConfLog(int order, String name, Supplier<?> sup) {
    conflog.add(ConfLog.of(order, name, sup));
  }
  
  public void addConfLog(String name, Supplier<?> sup) {
    conflog.add(ConfLog.of(name, sup));
  }
  
  private void printConf() {
    logger.info("\n\n{}\n", ASCII_ART);
    logger.info("Dodge-Http:");
    int maxlen = conflog.stream()
        .mapToInt(c->c.key().length())
        .max().getAsInt();
    conflog.stream().sorted()
        .forEach(c->logger.info(">>     {}: {}", StringPad.of(c.key()).rpad(".", maxlen), c.conf()));
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
  public Metrics metrics() {
    return metrics;
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
    this.level = level;
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
    lc.getLogger("one.microstream").setLevel(ch.qos.logback.classic.Level.WARN);
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
  
  @Override
  public Storage startStorage() {
    if(storage == null) {
      this.storage = new Storage(storagePath);
    }
    return storage;
  }
  
  @Override
  public Path getStoragePath() {
    return storagePath;
  }
  
  @Override
  public TcpChannel setStoragePath(Path path) {
    this.storagePath = Match.notNull(path).getOrFail("Bad null storage Path");
    return this;
  }
  
  @Override
  public Storage storage() {
    return storage;
  }
  
  @Override
  public Instant startup() {
    return startup;
  }
  
  @Override
  public Duration uptime() {
    return Duration.between(startup, Instant.now());
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
    AbstractBootstrap boot = bootstrap.apply(this);
    addConfLog(1, "Dodge Mode", ()->ServerBootstrap.class.isAssignableFrom(boot.getClass()) ? "Server" : "Client");
    printConf();
    
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
    return "TcpChannel{" + "masterThreads=" + masterThreads + ", workerThreads=" + workerThreads + ", address=" + address + ", logLevel=" + level + ", keystorePath=" + keystorePath + ", sslEnabled=" + sslEnabled + '}';
  }
  
}
