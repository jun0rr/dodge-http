/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import ch.qos.logback.classic.LoggerContext;
import com.jun0rr.dodge.http.handler.EventInboundHandler;
import com.jun0rr.dodge.http.handler.EventOutboundHandler;
import com.jun0rr.dodge.http.handler.HttpMessageLogger;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.http.handler.HttpRouteSelector;
import com.jun0rr.dodge.http.handler.ReleaseInboundHandler;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.dodge.tcp.ChannelHandlers;
import com.jun0rr.dodge.tcp.ChannelSetup;
import com.jun0rr.dodge.tcp.ConsumerType;
import com.jun0rr.dodge.tcp.DefaultChannelHandlers;
import com.jun0rr.dodge.tcp.SSLHandlerFactory;
import com.jun0rr.dodge.tcp.TcpClient;
import com.jun0rr.dodge.tcp.TcpServer;
import com.jun0rr.util.Host;
import com.jun0rr.util.ResourceLoader;
import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Crypto;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author F6036477
 */
public class Http {
  
  public static final String VERSION = "0.1";
  
  public static final int DEFAULT_BUFFER_SIZE = 128 * 1024 * 1024;
  
  private static final Logger logger = LoggerFactory.getLogger(Http.class);
  
  
  private int masterThreads = 1;
  
  private int workerThreads = Runtime.getRuntime().availableProcessors()*2;
  
  private EventLoopGroup masterGroup;
  
  private EventLoopGroup workerGroup;
  
  private Host listenAddress;
  
  private Level level = Level.INFO;
  
  private Host proxyAddress;

  private String proxyUser;

  private String proxyPass;
  
  private Path privkeyPath = ResourceLoader.caller().loadPath("dodge-pk.der");
  
  private Path pubkeyPath = ResourceLoader.caller().loadPath("dodge-pub.der");
  
  private Path keystorePath = ResourceLoader.caller().loadPath("keystore.jks");
  
  private char[] keystorePass = {'k', 'p', 'K', 'p', 'l', 'E', 'F', 'a', 'J', 'k', 'x', '2'};
  
  private boolean sslEnabled;
  
  private final List<Supplier<ChannelHandler>> handlers;
  
  private final Map<String,Object> attrs;
  
  private final Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> routes;
  
  private boolean fullHttpMessage = false;
  
  private boolean httpMessageLogger = true;
  
  private int bufferSize = DEFAULT_BUFFER_SIZE;
  
  public Http() {
    this.handlers = new LinkedList<>();
    this.attrs = new ConcurrentHashMap<>();
    this.routes = new ConcurrentHashMap<>();
  }
  
  public Http setFullHttpMessageEnabled(boolean enabled) {
    this.fullHttpMessage = enabled;
    return this;
  }
  
  public boolean isFullHttpMessageEnabled() {
    return this.fullHttpMessage;
  }
  
  public Http setHttpMessageLoggerEnabled(boolean enabled) {
    this.httpMessageLogger = enabled;
    return this;
  }
  
  public boolean isHttpMessageLoggerEnabled() {
    return this.httpMessageLogger;
  }
  
  public Http setBufferSize(int size) {
    this.bufferSize = Match.between(size, 1024, Integer.MAX_VALUE).getOrFail("Bad buffer size: " + size);
    return this;
  }
  
  public int getBufferSize() {
    return bufferSize;
  }
  
  public int getMasterThreads() {
    return masterThreads;
  }

  public Http setMasterThreads(int masterThreads) {
    this.masterThreads = masterThreads;
    return this;
  }

  public int getWorkerThreads() {
    return workerThreads;
  }

  public Http setWorkerThreads(int workerThreads) {
    this.workerThreads = workerThreads;
    return this;
  }

  public EventLoopGroup getMasterGroup() {
    if(masterThreads < 1) {
      throw new IllegalStateException("Master threads not defined");
    }
    else if(masterGroup == null) {
      masterGroup = new NioEventLoopGroup(masterThreads);
    }
    return masterGroup;
  }

  public Http setMasterGroup(EventLoopGroup masterGroup) {
    this.masterGroup = masterGroup;
    return this;
  }

  public EventLoopGroup getWorkerGroup() {
    if(workerThreads < 1) {
      throw new IllegalStateException("Worker threads not defined");
    }
    else if(workerGroup == null) {
      workerGroup = new NioEventLoopGroup(workerThreads);
    }
    return workerGroup;
  }

  public Http setWorkerGroup(EventLoopGroup workerGroup) {
    this.workerGroup = workerGroup;
    return this;
  }

  public Host getListenAddress() {
    return listenAddress;
  }

  public Http setListenAddress(Host listenAddress) {
    this.listenAddress = listenAddress;
    return this;
  }

  public Level getLogLevel() {
    return level;
  }

  public Http setLogLevel(Level level) {
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
  
  public Host getProxyAddress() {
    return proxyAddress;
  }
  
  public Http setProxyAddress(Host proxyAddress) {
    this.proxyAddress = proxyAddress;
    return this;
  }
  
  public String getProxyUser() {
    return proxyUser;
  }
  
  public Http setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
    return this;
  }
  
  public String getProxyPassword() {
    return proxyPass;
  }
  
  public Http setProxyPassword(String proxyPass) {
    this.proxyPass = proxyPass;
    return this;
  }
  
  public Path getPrivateKeyPath() {
    return privkeyPath;
  }
  
  public Http setPrivateKeyPath(Path p) {
    if(p == null || !Files.exists(p)) {
      throw new IllegalArgumentException("Private Key Path does not exists");
    }
    this.privkeyPath = p;
    return this;
  }
  
  public PrivateKey loadPrivateKey() {
    return Unchecked.call(()->Crypto.createPrivateKey(Files.readAllBytes(privkeyPath)));
  }
  
  public Path getPublicKeyPath() {
    return pubkeyPath;
  }
  
  public Http setPublicKeyPath(Path p) {
    if(p == null || !Files.exists(p)) {
      throw new IllegalArgumentException("Private Key Path does not exists");
    }
    this.pubkeyPath = p;
    return this;
  }
  
  public PublicKey loadPublicKey() {
    return Unchecked.call(()->Crypto.createPublicKey(Files.readAllBytes(pubkeyPath)));
  }
  
  public boolean isSslEnabled() {
    return this.sslEnabled;
  }
  
  public Http setSslEnabled(boolean enabled) {
    this.sslEnabled = enabled;
    return this;
  }
  
  public Path getKeystorePath() {
    return keystorePath;
  }
  
  public Http setKeystorePath(Path p) {
    this.keystorePath = p;
    return this;
  }
  
  public char[] getKeystorePass() {
    return keystorePass;
  }
  
  public Http setKeystorePass(char[] pass) {
    this.keystorePass = pass;
    return this;
  }
  
  public Map<String,Object> attributes() {
    return attrs;
  }
  
  public Map<HttpRoute,Supplier<Consumer<ChannelExchange<HttpRequest>>>> serverRoutes() {
    return routes;
  }
  
  public Http addRoute(HttpRoute route, Supplier<Consumer<ChannelExchange<HttpRequest>>> sup) {
    routes.put(
        Match.notNull(route).getOrFail("Bad null HttpRoute"), 
        Match.notNull(sup).getOrFail("Bad null Supplier")
    );
    return this;
  }
  
  public <T> Http addHandler(ChannelEvent evt, Class<T> type, Supplier<Consumer<ChannelExchange<T>>> cs) {
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
  
  public Http addHandler(ChannelEvent evt, Supplier<Consumer<ChannelExchange<Object>>> cs) {
    return addHandler(evt, Object.class, cs);
  }
  
  public Http addHandler(Supplier<ChannelHandler> cih) {
    Match.notNull(cih).failIfNotMatch("Bad null ChannelInboundHandler");
    handlers.add(cih);
    return this;
  }
  
  public ChannelSetup createServer() {
    ChannelHandlers hds = new DefaultChannelHandlers();
    if(sslEnabled) {
      hds.setSSLHandlerFactory(SSLHandlerFactory.forServer(keystorePath, keystorePass));
    }
    hds.add(()->new HttpServerCodec());
    if(fullHttpMessage) {
      hds.add(()->new HttpObjectAggregator(bufferSize));
    }
    if(httpMessageLogger) {
      hds.add(()->new HttpMessageLogger());
    }
    handlers.forEach(hds::add);
    hds.add(ChannelEvent.Inbound.READ, HttpRequest.class, ()->new HttpRouteSelector(routes, true));
    hds.add(()->new ReleaseInboundHandler());
    return new TcpServer(masterGroup, workerGroup, listenAddress, hds);
  }
  
  public ChannelSetup createClient() {
    ChannelHandlers hds = new DefaultChannelHandlers();
    if(sslEnabled) {
      hds.setSSLHandlerFactory(SSLHandlerFactory.forClient());
    }
    hds.add(()->new HttpClientCodec());
    if(fullHttpMessage) {
      hds.add(()->new HttpObjectAggregator(bufferSize));
    }
    if(httpMessageLogger) {
      hds.add(()->new HttpMessageLogger());
    }
    handlers.forEach(hds::add);
    hds.add(()->new ReleaseInboundHandler());
    return new TcpClient(workerGroup, listenAddress, hds);
  }
  
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 37 * hash + this.masterThreads;
    hash = 37 * hash + this.workerThreads;
    hash = 37 * hash + Objects.hashCode(this.fullHttpMessage);
    hash = 37 * hash + Objects.hashCode(this.httpMessageLogger);
    hash = 37 * hash + Objects.hashCode(this.masterGroup);
    hash = 37 * hash + Objects.hashCode(this.workerGroup);
    hash = 37 * hash + Objects.hashCode(this.listenAddress);
    hash = 37 * hash + Objects.hashCode(this.proxyAddress);
    hash = 37 * hash + Objects.hashCode(this.proxyPass);
    hash = 37 * hash + Objects.hashCode(this.proxyUser);
    hash = 37 * hash + Objects.hashCode(this.level);
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
    final Http other = (Http) obj;
    if (this.fullHttpMessage != other.fullHttpMessage) {
      return false;
    }
    if (this.httpMessageLogger != other.httpMessageLogger) {
      return false;
    }
    if (this.masterThreads != other.masterThreads) {
      return false;
    }
    if (this.workerThreads != other.workerThreads) {
      return false;
    }
    if (!Objects.equals(this.masterGroup, other.masterGroup)) {
      return false;
    }
    if (!Objects.equals(this.workerGroup, other.workerGroup)) {
      return false;
    }
    if (!Objects.equals(this.listenAddress, other.listenAddress)) {
      return false;
    }
    if (!Objects.equals(this.proxyAddress, other.proxyAddress)) {
      return false;
    }
    if (!Objects.equals(this.proxyPass, other.proxyPass)) {
      return false;
    }
    if (!Objects.equals(this.proxyUser, other.proxyUser)) {
      return false;
    }
    if (!Objects.equals(this.level, other.level)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "DodgeConfig{" + "httpMessageLogger=" + httpMessageLogger + ", fullHttpMessage=" + fullHttpMessage + ", masterThreads=" + masterThreads + ", workerThreads=" + workerThreads + ", masterGroup=" + masterGroup + ", workerGroup=" + workerGroup + ", listenAddress=" + listenAddress + ", level=" + level + ", proxyAddress=" + proxyAddress + ", proxyUser=" + proxyUser + ", proxyPass=" + proxyPass + '}';
  }
  
}
