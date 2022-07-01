/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.match.Match;
import io.netty.channel.Channel;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 *
 * @author F6036477
 */
public class Attributes {
  
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
        .map(Map.Entry::getKey)
        //.collect(Collectors.toList())
        .forEach(attrs::remove);
    return this;
  }
  
  private String key(String key) {
    return (keyPrefix == null || keyPrefix.isBlank() ? "" : keyPrefix.concat(".")).concat(key);
  }
  
  public Attributes parent() {
    return parent;
  }
  
  public Attributes put(String key, Object val) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    Match.notNull(val).failIfNotMatch("Bad null value Object");
    attrs.put(key(key), val);
    return this;
  }
  
  public <T> Optional<T> get(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    T val = (T) attrs.get(key(key));
    return Optional.ofNullable(val);
  }
  
  public <T> Optional<T> remove(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    T val = (T) attrs.remove(key(key));
    return Optional.ofNullable(val);
  }
  
  public boolean contains(String key) {
    Match.notEmpty(key).failIfNotMatch("Bad null/empty key String");
    return attrs.containsKey(key(key));
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
