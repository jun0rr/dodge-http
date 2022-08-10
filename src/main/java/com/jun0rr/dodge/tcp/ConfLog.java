/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.dodge.http.util.Indexed;
import com.jun0rr.util.match.Match;
import java.util.Objects;
import java.util.function.Supplier;

/**
 *
 * @author F6036477
 */
public class ConfLog implements Comparable<ConfLog> {
  
  public static Indexed.Ints ORDER = Indexed.Ints.increment();
  
  private final int order;
  
  private final String key;
  
  private final Supplier<String> conf;
  
  public ConfLog(int order, String key, Supplier<String> conf) {
    this.order = order;
    this.key = Match.notEmpty(key).getOrFail("Bad key string");
    this.conf = Match.notNull(conf).getOrFail("Bad conf Supplier");
  }
  
  public static ConfLog of(int order, String key, Supplier<?> conf) {
    return new ConfLog(order, key, ()->Objects.toString(conf.get()));
  }
  
  public static ConfLog of(String key, Supplier<?> conf) {
    return new ConfLog(ORDER.next(), key, ()->Objects.toString(conf.get()));
  }
  
  public int order() {
    return this.order;
  }
  
  public String key() {
    return key;
  }
  
  public String conf() {
    return conf.get();
  }
  
  @Override
  public int compareTo(ConfLog o) {
    return Integer.compare(order, o.order());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79 * hash + this.order;
    hash = 79 * hash + Objects.hashCode(this.key);
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
    final ConfLog other = (ConfLog) obj;
    if (this.order != other.order) {
      return false;
    }
    return Objects.equals(this.key, other.key);
  }

  @Override
  public String toString() {
    return "ConfLog{" + "order=" + order + ", key=" + key + ", conf=" + conf + '}';
  }
  
}
