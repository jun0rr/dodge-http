/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.metrics;

import com.jun0rr.dodge.http.util.SortBy;
import com.jun0rr.util.crypto.Hash;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Juno
 */
public class Metrics {
  
  private final Map<String,Metric> map;
  
  public Metrics() {
    this.map = new ConcurrentHashMap<>();
  }
  
  private String hash(Metric m) {
    Hash hash = Hash.sha1().put(m.name());
    m.labels().entrySet().forEach(e->hash.put(e.toString()));
    return hash.get();
  }
  
  public Metrics put(Metric m) {
    map.put(hash(m), m);
    return this;
  }
  
  public boolean contains(Metric m) {
    return map.containsKey(hash(m));
  }
  
  public Optional<Metric> get(Metric m) {
    return Optional.ofNullable(map.get(hash(m)));
  }
  
  public Optional<Metric> getByName(String name) {
    return map.values().stream()
        .filter(m->m.name().equals(name))
        .findFirst();
  }
  
  public Optional<Metric> get(String name, String... labels) {
    Map<String,String> lbs = new HashMap<>();
    for(int i = 0; i < labels.length -1; i+=2) {
      lbs.put(labels[i], labels[i+1]);
    }
    Hash hash = Hash.sha1().put(name);
    lbs.entrySet().forEach(e->hash.put(e.toString()));
    return Optional.ofNullable(map.get(hash.get()));
  }
  
  public List<Metric> getAll(String name, String... labels) {
    Stream<Metric> stm = map.values().stream()
        .filter(m->m.name().equals(name));
    if(labels != null && labels.length > 0) {
      for(int i = 0; i < labels.length -1; i+=2) {
        String k = labels[i], v = labels[i+1];
        stm = stm.filter(m->hasLabel(m, k, v));
      }
    }
    return stm.collect(Collectors.toList());
  }
  
  public List<Metric> removeAll(String name, String... labels) {
    List<Metric> ms = getAll(name, labels);
    ms.forEach(m->map.remove(hash(m)));
    return ms;
  }
  
  public boolean hasLabel(Metric m, String k, String v) {
    Object lb = m.labels().get(k);
    return lb != null && lb.toString().equals(v);
  }
  
  public Stream<Metric> stream() {
    return map.values().stream()
        .map(m->SortBy.of(m, Metric::name))
        .sorted()
        .map(SortBy::get);
  }
  
  private List<String> toList() {
    Map<String,Metric> names = new HashMap<>();
    stream()
        .filter(m->!names.containsKey(m.name()))
        .forEach(m->names.put(m.name(), m));
    List<String> ls = new LinkedList<>();
    names.values().stream()
        .map(m->SortBy.of(m, Metric::name))
        .sorted()
        .map(SortBy::get)
        .peek(m->ls.add(m.helpAndType()))
        .forEach(m->stream()
            .filter(n->n.name().equals(m.name()))
            .forEach(n->n.collect(ls)));
    return ls;
  }
  
  public ByteBuf collect(ByteBufAllocator alloc) {
    List<String> ls = toList();
    int size = ls.stream().mapToInt(String::length).sum();
    ByteBuf buf = alloc.buffer(size);
    ls.stream()
        .map(s->s.concat("\n"))
        .forEach(s->buf.writeCharSequence(s, StandardCharsets.UTF_8));
    return buf;
  }
  
}
