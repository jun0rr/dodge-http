/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.util.match.Match;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class Attributes {
  
  static final Logger logger = LoggerFactory.getLogger(Attributes.class);
  
  private final Map<String,Object> attrs;
  
  private final String keyPrefix;
  
  private final Attributes parent;
  
  public Attributes(Attributes parent, String keyPrefix, Map<String,Object> attrs) {
    this.keyPrefix = keyPrefix;
    this.attrs = Match.notNull(attrs).getOrFail("Bad null attributes Map");
    this.parent = parent;
  }
  
  public Attributes(String keyPrefix) {
    this(null, keyPrefix, new ConcurrentHashMap<>());
  }
  
  public Attributes(Attributes attrs, String keyPrefix) {
    this(attrs, (attrs.keyPrefix == null || attrs.keyPrefix.isBlank() ? "" : attrs.keyPrefix.concat(".")).concat(keyPrefix), attrs.attrs);
  }
  
  public Attributes() {
    this(null, "", new ConcurrentHashMap<>());
  }
  
  public Attributes channelAttrs(Channel ch) {
    return new Attributes(this, ch.id().asShortText());
  }
  
  public Attributes clearChannel(Channel ch) {
    attrs.entrySet().stream()
        .filter(e->e.getKey().startsWith(ch.id().asShortText()))
        .peek(e->{
          if(HttpConstants.isHttpRequest(e.getValue())) {
            logger.debug("Releasing HttpRequest: {}", ((HttpRequest)e.getValue()).uri());
          }
          else if(User.class.isAssignableFrom(e.getValue().getClass())) {
            logger.debug("Releasing User: {}", ((User)e.getValue()).getEmail());
          }
        })
        .peek(e->ReferenceCountUtil.safeRelease(e.getValue()))
        .map(Map.Entry::getKey)
        .forEach(attrs::remove);
    return this;
  }
  
  private String key(String key) {
    return (keyPrefix == null || keyPrefix.isBlank() ? "" : keyPrefix.concat(".")).concat(key);
  }
  
  public Attributes parent() {
    return parent;
  }
  
  public Attributes put(Class cls, Object val) {
    return put(cls.getName(), val);
  }
  
  public Attributes put(String key, Object val) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key");
    Match.notNull(val).failIfNotMatch("Bad null value Object");
    String k = key(key);
    Optional.ofNullable(attrs.get(k))
        .ifPresent(ReferenceCountUtil::safeRelease);
    attrs.put(k, val);
    return this;
  }
  
  public <T> Optional<T> get(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    T val = (T) attrs.get(key(key));
    return Optional.ofNullable(val);
  }
  
  public <T> Optional<T> get(Class<T> key) {
    Match.notNull(key).failIfNotMatch("Bad null Class key");
    return Optional.ofNullable(key.cast(attrs.get(key(key.getName()))));
  }
  
  public <T> Optional<T> remove(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    T val = (T) attrs.remove(key(key));
    return Optional.ofNullable(val);
  }
  
  public <T> Optional<T> remove(Class<T> key) {
    Match.notNull(key).failIfNotMatch("Bad null Class key");
    return Optional.ofNullable(key.cast(attrs.remove(key(key.getName()))));
  }
  
  public boolean contains(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    return attrs.containsKey(key(key));
  }
  
  public boolean contains(Class key) {
    Match.notNull(key).failIfNotMatch("Bad null Class key");
    return attrs.containsKey(key(key.getName()));
  }
  
  public Stream<Entry<String,Object>> stream() {
    Stream<Entry<String,Object>> stream = attrs.entrySet().stream();
    if(keyPrefix != null && !keyPrefix.isBlank()) {
      stream = stream.filter(e->e.getKey().startsWith(keyPrefix))
          .map(e->new AbstractMap.SimpleEntry<>(e.getKey().substring(keyPrefix.length() + 1), e.getValue()));
    }
    return stream;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 53 * hash + Objects.hashCode(this.attrs);
    hash = 53 * hash + Objects.hashCode(this.keyPrefix);
    hash = 53 * hash + Objects.hashCode(this.parent);
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
    final Attributes other = (Attributes) obj;
    if (!Objects.equals(this.keyPrefix, other.keyPrefix)) {
      return false;
    }
    if (!Objects.equals(this.attrs, other.attrs)) {
      return false;
    }
    return Objects.equals(this.parent, other.parent);
  }

  @Override
  public String toString() {
    return "Attributes{" + "keyPrefix=" + keyPrefix + ", parent=" + parent + '}';
  }
  
}
