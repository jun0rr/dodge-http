/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.header.ConnectionCloseHeaders;
import com.jun0rr.dodge.http.header.DateHeader;
import com.jun0rr.dodge.http.header.ServerHeader;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.Host;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import java.net.SocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class AuthTry {
  
  static final Logger logger = LoggerFactory.getLogger(AuthTry.class);
  
  public static final String X_LOGIN = "x-login";
  
  public static final String X_LOGIN_VALUE = "You are banned!";
  
  public static final String X_BANNED = "x-banned";
  
  public static final String X_MAX_BAN = "x-max-ban";
  
  public static final int MAX_TRIES = 4;
  
  public static final Duration MAX_BAN_DURATION = Duration.ofMinutes(2);
  
  private final String addr;
  
  private final AtomicInteger tries;
  
  private volatile boolean isBanned;
  
  private volatile Instant ban;
  
  public AuthTry(String addr, int tries) {
    this.addr = Match.notEmpty(addr).getOrFail("Bad null address String");
    this.tries = new AtomicInteger(tries);
  }
  
  public AuthTry(String addr) {
    this(addr, 0);
  }
  
  public static AuthTry of(SocketAddress addr) {
    return new AuthTry(Host.of(addr).getIPAddress());
  }
  
  public static AuthTry of(ChannelExchange x) {
    return AuthTry.of(x.context().channel().remoteAddress());
  }
  
  public String address() {
    return addr;
  }
  
  public int tries() {
    return tries.get();
  }
  
  public String attributeKey() {
    return getClass().getName().concat(addr);
  }
  
  public AuthTry ban() {
    if(!isBanned) {
      isBanned = true;
      ban = Instant.now();
    }
    return this;
  }
  
  public boolean isBanned() {
    if(isBanned) {
      if(Duration.between(ban, Instant.now())
          .compareTo(MAX_BAN_DURATION) < 0) {
        return true;
      }
      else {
        isBanned = false;
        ban = null;
        tries.set(0);
        return false;
      }
    }
    return false;
  }
  
  public boolean incrementAndBan() {
    return tries.incrementAndGet() >= MAX_TRIES && ban().isBanned();
  }
  
  public AuthTry increment() {
    tries.incrementAndGet();
    return this;
  }
  
  public void sendBan(ChannelExchange x) {
    HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
    res.headers()
        .add(X_LOGIN, X_LOGIN_VALUE)
        .add(X_BANNED, ban.getEpochSecond())
        .add(X_MAX_BAN, MAX_BAN_DURATION.toSeconds())
        .add(new ConnectionCloseHeaders())
        .add(new DateHeader())
        .add(new ServerHeader());
    x.writeAndFlush(res).close();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 19 * hash + Objects.hashCode(this.addr);
    hash = 19 * hash + Objects.hashCode(this.tries);
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
    final AuthTry other = (AuthTry) obj;
    if (!Objects.equals(this.addr, other.addr)) {
      return false;
    }
    return Objects.equals(this.tries, other.tries);
  }

  @Override
  public String toString() {
    return "AuthTry{" + "addr=" + addr + ", tries=" + tries.get() + '}';
  }
  
}
