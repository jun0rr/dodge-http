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
public class SortBy<T> implements Comparable<SortBy<T>> {
  
  private final T object;
  
  private final Function<T,Comparable> byfun;
  
  public SortBy(T obj, Function<T,Comparable> byfun) {
    this.object = Match.notNull(obj).getOrFail("Bad null object");
    this.byfun = Match.notNull(byfun).getOrFail("Bad null function");
  }
  
  public static <U> SortBy<U> of(U obj, Function<U,Comparable> fn) {
    return new SortBy(obj, fn);
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
        && compareTo((SortBy<T>)o) == 0;
  }
  
  @Override
  public int compareTo(SortBy<T> o) {
    return byfun.apply(object).compareTo(byfun.apply(o.get()));
  }

  @Override
  public String toString() {
    return "SortBy{" + "object=" + object + ", sortBy=" + byfun.apply(object) + '}';
  }

}
