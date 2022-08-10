/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http;

import com.jun0rr.dodge.http.handler.HttpMessageLogger;
import com.jun0rr.dodge.http.handler.ProxyAuthOutboundHandler;
import com.jun0rr.util.Host;
import com.jun0rr.util.match.Match;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class HttpClient extends Http {
  
  private Host proxyAddress;

  private String proxyUser;

  private String proxyPass;
  
  public HttpClient() {
    super(BOOTSTRAP);
    addConfLog("Proxy Auth Enabled", this::isProxyAuthEnabled);
    addConfLog("Proxy Address", this::getProxyAddress);
    addConfLog("Proxy User", this::getProxyUser);
  }
  
  public Host getProxyAddress() {
    return proxyAddress;
  }
  
  public HttpClient setProxyAddress(Host proxyAddress) {
    this.proxyAddress = proxyAddress;
    return this;
  }
  
  public String getProxyUser() {
    return proxyUser;
  }
  
  public HttpClient setProxyUser(String proxyUser) {
    this.proxyUser = proxyUser;
    return this;
  }
  
  public String getProxyPassword() {
    return proxyPass;
  }
  
  public HttpClient setProxyPassword(String proxyPass) {
    this.proxyPass = proxyPass;
    return this;
  }
  
  public boolean isProxyAuthEnabled() {
    return getProxyUser() != null 
        && getProxyPassword() != null 
        && !getProxyUser().isBlank() 
        && !getProxyPassword().isBlank();
  }
  
  @Override
  public ChannelInitializer<SocketChannel> createInitializer() {
    Match.notEmpty(handlers).failIfNotMatch("Bad empty ChannelHandler List");
    return new ChannelInitializer<>() {
      @Override
      protected void initChannel(SocketChannel c) throws Exception {
        initSslHandler(c);
        c.pipeline().addLast(new HttpClientCodec());
        if(isHttpMessageBufferEnabled()) {
          c.pipeline().addLast(new HttpObjectAggregator(getBufferSize()));
        }
        if(isHttpMessageLoggerEnabled()) {
          c.pipeline().addLast(new HttpMessageLogger());
        }
        if(isProxyAuthEnabled()) {
          c.pipeline().addLast(new ProxyAuthOutboundHandler(getProxyUser(), getProxyPassword()));
        }
        initHandlers(c);
      }
    };
  }

  @Override
  public int hashCode() {
    int hash = super.hashCode();
    hash = 71 * hash + Objects.hashCode(this.proxyAddress);
    hash = 71 * hash + Objects.hashCode(this.proxyUser);
    hash = 71 * hash + Objects.hashCode(this.proxyPass);
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
    if(!Http.class.isAssignableFrom(obj.getClass()) || !super.equals((Http)obj)) {
      return false;
    }
    final HttpClient other = (HttpClient) obj;
    if (!Objects.equals(this.proxyUser, other.proxyUser)) {
      return false;
    }
    if (!Objects.equals(this.proxyPass, other.proxyPass)) {
      return false;
    }
    return Objects.equals(this.proxyAddress, other.proxyAddress);
  }

  @Override
  public String toString() {
    return "HttpClient{" + "masterThreads=" + getMasterThreads() + ", workerThreads=" + getWorkerThreads()  + ", address=" + getAddress() + ", logLevel=" + getLogLevel() + ", keystorePath=" + getKeystorePath() + ", sslEnabled=" + isSslEnabled() + ", bufferSize=" + getBufferSize() + ", privkeyPath=" + getPrivateKeyPath() + ", pubkeyPath=" + getPublicKeyPath() + ", fullHttpMessageEnabled=" + isHttpMessageBufferEnabled() + ", httpMessageLoggerEnabled=" + isHttpMessageLoggerEnabled() + ", proxyAddress=" + proxyAddress + ", proxyUser=" + proxyUser + ", proxyPass=" + proxyPass + '}';
  }
  
}
