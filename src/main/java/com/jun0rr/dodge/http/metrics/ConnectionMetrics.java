/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import com.jun0rr.util.Host;
import com.jun0rr.util.match.Match;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author F6036477
 */
public interface ConnectionMetrics {
  
  public String id();
  
  public Host target();
  
  public Instant created();
  
  public Instant closed();
  
  public Instant setClosed();
  
  public boolean isClosed();
  
  public Duration uptime();
  
  public AtomicLong inboundMessages();
  
  public AtomicLong outboundMessages();
  
  public AtomicLong inboundBytes();
  
  public AtomicLong outboundBytes();
  
  public AtomicLong inboundExchangeBytes();
  
  public AtomicLong outboundExchangeBytes();
  
  public float inboundBPS();
  
  public float outboundBPS();
  
  public float inboundExchangeBPS();
  
  public float outboundExchangeBPS();
  
  
  public static ConnectionMetrics of(String id, Host target) {
    return new ConnMetricsImpl(id, target);
  }
  
  
  
  static class ConnMetricsImpl implements ConnectionMetrics {
    
    private final String id;
    
    private final Host target;
    
    private final Instant created;
    
    private Instant closed;
    
    private final AtomicLong inboundMessages;
    
    private final AtomicLong outboundMessages;
    
    private final AtomicLong inboundBytes;
    
    private final AtomicLong outboundBytes;
    
    private final AtomicLong inboundExchange;
    
    private final AtomicLong outboundExchange;
    
    public ConnMetricsImpl(String id, Host tgt) {
      this.id = Match.notEmpty(id).getOrFail("Bad connection ID: " + id);
      this.target = Match.notNull(tgt).getOrFail("Bad null target address");
      this.created = Instant.now();
      this.inboundBytes = new AtomicLong(0L);
      this.outboundBytes = new AtomicLong(0L);
      this.inboundMessages = new AtomicLong(0L);
      this.outboundMessages = new AtomicLong(0L);
      this.inboundExchange = new AtomicLong(0L);
      this.outboundExchange = new AtomicLong(0L);
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public Host target() {
      return target;
    }

    @Override
    public Instant created() {
      return created;
    }
    
    @Override
    public Instant closed() {
      return closed;
    }
    
    @Override
    public Instant setClosed() {
      return (closed = Instant.now());
    }
    
    @Override
    public boolean isClosed() {
      return closed != null;
    }
    
    @Override
    public Duration uptime() {
      return Duration.between(created, closed != null ? closed : Instant.now());
    }

    @Override
    public AtomicLong inboundMessages() {
      return inboundMessages;
    }

    @Override
    public AtomicLong outboundMessages() {
      return outboundMessages;
    }

    @Override
    public AtomicLong inboundBytes() {
      return inboundBytes;
    }

    @Override
    public AtomicLong outboundBytes() {
      return outboundBytes;
    }

    @Override
    public AtomicLong inboundExchangeBytes() {
      return inboundExchange;
    }

    @Override
    public AtomicLong outboundExchangeBytes() {
      return outboundExchange;
    }

    @Override
    public float inboundBPS() {
      float sec = uptime().toMillis() / 1000f;
      return inboundBytes.get() / sec;
    }

    @Override
    public float outboundBPS() {
      float sec = uptime().toMillis() / 1000f;
      return outboundBytes.get() / sec;
    }

    @Override
    public float inboundExchangeBPS() {
      float sec = uptime().toMillis() / 1000f;
      return inboundExchange.get() / sec;
    }

    @Override
    public float outboundExchangeBPS() {
      float sec = uptime().toMillis() / 1000f;
      return outboundExchange.get() / sec;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 29 * hash + Objects.hashCode(this.id);
      hash = 29 * hash + Objects.hashCode(this.target);
      hash = 29 * hash + Objects.hashCode(this.created);
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
      final ConnMetricsImpl other = (ConnMetricsImpl) obj;
      if (!Objects.equals(this.id, other.id)) {
        return false;
      }
      if (!Objects.equals(this.target, other.target)) {
        return false;
      }
      if (!Objects.equals(this.created, other.created)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return "ConnMetricsImpl{" + "id=" + id + ", target=" + target + ", created=" + created + ", inboundBytes=" + inboundBytes.get() + ", outboundBytes=" + outboundBytes.get() + ", inboundExchange=" + inboundExchange.get() + ", outboundExchange=" + outboundExchange.get() + '}';
    }
    
  }
  
}
