/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.jun0rr.util.match.Match;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Juno
 */
public class DistinctStream<T> {
  
  private Stream<T> stream;
  
  public DistinctStream(Stream<T> st) {
    this.stream = Match.notNull(st).getOrFail("Bad null Stream");
  }
  
  public static <U> DistinctStream<U> of(Stream<U> st) {
    return new DistinctStream(st);
  }
  
  public DistinctStream<T> sortBy(Function<T,Comparable> by) {
    stream = stream
        .map(t->SortBy.of(t, by))
        .sorted()
        .map(SortBy::get);
    return this;
  }
  
  public DistinctStream<T> distinctBy(Function<T,Object> by) {
    Match.notNull(by).failIfNotMatch("Bad null Function");
    Map<Object,T> map = new HashMap<>();
    stream = stream.filter(t->!map.containsKey(by.apply(t)))
        .peek(t->map.put(by.apply(t), t));
    return this;
  }
  
  public Stream<T> stream() {
    return stream;
  }
  
}
