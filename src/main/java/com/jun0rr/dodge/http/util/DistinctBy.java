/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.jun0rr.util.match.Match;
import java.util.Objects;
import java.util.function.Function;

/**
 *
 * @author F6036477
 */
public class DistinctBy<T> {
  
  private final T object;
  
  private final Function<T,Object> byfun;
  
  public DistinctBy(T object, Function<T,Object> byfun) {
    this.object = Match.notNull(object).getOrFail("Bad null object");
    this.byfun = Match.notNull(byfun).getOrFail("Bad null function");
  }
  
  public static <U> DistinctBy<U> of(U obj, Function<U,Object> fn) {
    return new DistinctBy(obj, fn);
  }
  
  public T get() {
    return object;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 97 * hash + Objects.hashCode(byfun.apply(object));
    return hash;
  }
  
  @Override
  public boolean equals(Object o) {
    return object.getClass() == o.getClass() 
        && Objects.equals(byfun.apply(object), byfun.apply((T)o));
  }

  @Override
  public String toString() {
    return "DistinctBy{" + "object=" + object + ", distinctBy=" + byfun.apply(object) + '}';
  }
  
}
