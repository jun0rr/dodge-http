/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jun0rr.dodge.http.auth.AllowRole;
import com.jun0rr.dodge.http.auth.DenyRole;
import com.jun0rr.dodge.http.auth.ErrMessage;
import com.jun0rr.dodge.http.auth.JsonErrMsgAdapter;
import com.jun0rr.dodge.http.auth.JsonHttpMethodAdapter;
import com.jun0rr.dodge.http.auth.JsonIgnoreStrategy;
import com.jun0rr.dodge.http.auth.JsonInstantAdapter;
import com.jun0rr.dodge.http.auth.JsonRoleAdapter;
import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.tcp.DefaultTcpChannel;
import com.jun0rr.dodge.tcp.TcpChannel;
import com.jun0rr.util.ResourceLoader;
import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Crypto;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.handler.codec.http.HttpMethod;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author F6036477
 */
public abstract class Http extends DefaultTcpChannel {
  
  private Path privkeyPath = ResourceLoader.caller().loadPath("dodge-pk.der");
  
  private Path pubkeyPath = ResourceLoader.caller().loadPath("dodge-pub.der");
  
  private boolean fullHttpMessage = false;
  
  private boolean httpMessageLogger = true;
  
  private final Gson gson = new GsonBuilder()
      .registerTypeAdapter(AllowRole.class, new JsonRoleAdapter())
      .registerTypeAdapter(DenyRole.class, new JsonRoleAdapter())
      .registerTypeAdapter(Role.class, new JsonRoleAdapter())
      .registerTypeAdapter(ErrMessage.class, new JsonErrMsgAdapter())
      .registerTypeAdapter(HttpMethod.class, new JsonHttpMethodAdapter())
      .registerTypeAdapter(Instant.class, new JsonInstantAdapter())
      .setExclusionStrategies(new JsonIgnoreStrategy())
      .create();
  
  public Http(Function<TcpChannel, AbstractBootstrap> bootstrap) {
    super(bootstrap);
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
  
  public Gson gson() {
    return gson;
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

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 37 * hash + Objects.hashCode(this.privkeyPath);
    hash = 37 * hash + Objects.hashCode(this.pubkeyPath);
    hash = 37 * hash + (this.fullHttpMessage ? 1 : 0);
    hash = 37 * hash + (this.httpMessageLogger ? 1 : 0);
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
    if(!TcpChannel.class.isAssignableFrom(obj.getClass()) || !super.equals((TcpChannel)obj)) {
      return false;
    }
    final Http other = (Http) obj;
    if (this.fullHttpMessage != other.fullHttpMessage) {
      return false;
    }
    if (this.httpMessageLogger != other.httpMessageLogger) {
      return false;
    }
    if (!Objects.equals(this.privkeyPath, other.privkeyPath)) {
      return false;
    }
    return Objects.equals(this.pubkeyPath, other.pubkeyPath);
  }

  @Override
  public String toString() {
    return "Http{" + "masterThreads=" + getMasterThreads() + ", workerThreads=" + getWorkerThreads()  + ", address=" + getAddress() + ", logLevel=" + getLogLevel() + ", keystorePath=" + getKeystorePath() + ", sslEnabled=" + isSslEnabled() + ", bufferSize=" + getBufferSize() + ", privkeyPath=" + getPrivateKeyPath() + ", pubkeyPath=" + getPublicKeyPath() + ", fullHttpMessageEnabled=" + fullHttpMessage + ", httpMessageLoggerEnabled=" + httpMessageLogger + '}';
  }
  
}
