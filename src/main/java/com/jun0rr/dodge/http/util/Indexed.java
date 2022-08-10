/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import com.jun0rr.util.match.Match;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

/**
 *
 * @author F6036477
 */
public class Indexed<T> {
  
  private final T obj;
  
  private final int index;
  
  public Indexed(T obj, int index) {
    this.obj = Match.notNull(obj).getOrFail("Bad null object");
    this.index = index;
  }
  
  public static <U> Function<U,Indexed<U>> mapper() {
    Ints is = Ints.increment();
    return o->new Indexed(o, is.next());
  }
  
  public T get() {
    return obj;
  }
  
  public int index() {
    return index;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 89 * hash + Objects.hashCode(this.obj);
    hash = 89 * hash + this.index;
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
    final Indexed<?> other = (Indexed<?>) obj;
    if (this.index != other.index) {
      return false;
    }
    return Objects.equals(this.obj, other.obj);
  }

  @Override
  public String toString() {
    return "Indexed{" + "index=" + index + ", obj=" + obj + '}';
  }
  
  
  
  public static class Ints {
    
    private final AtomicInteger index;
    
    private final int end;
    
    private final IntUnaryOperator oper;
    
    public Ints(int start, int end, IntUnaryOperator op) {
      if(end != -1 && start == end) throw new IllegalArgumentException(
          "Start index cannot be equals end: " + start + " == " + end
      );
      if(op == null) throw new IllegalArgumentException(
          "IntUnaryOperator cannot be null"
      );
      this.index = new AtomicInteger(start);
      this.end = end;
      this.oper = op;
    }
    
    public static Ints increment() {
      return new Ints(-1, -1, i->i+1);
    }
    
    public static Ints odds() {
      return new Ints(-1, -1, i->i+2);
    }
    
    public static Ints evens() {
      return new Ints(-2, -1, i->i+2);
    }
    
    public static Ints dozens() {
      return new Ints(-10, -1, i->i+10);
    }
    
    public static Ints hundreds() {
      return new Ints(-100, -1, i->i+100);
    }
    
    public static Ints thousends() {
      return new Ints(-1000, -1, i->i+1000);
    }
    
    public int next() {
      int n = oper.applyAsInt(index.get());
      if(end != -1 && n >= end) {
        throw new IndexOutOfBoundsException(String.format("index[%d] >= end[%d]", index.get(), end));
      }
      return index.updateAndGet(oper);
    }
    
    @Override
    public int hashCode() {
      int hash = 3;
      hash = 11 * hash + Objects.hashCode(this.index);
      hash = 11 * hash + this.end;
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
      final Ints other = (Ints) obj;
      if (this.end != other.end) {
        return false;
      }
      return Objects.equals(this.index, other.index);
    }
    
    @Override
    public String toString() {
      return "Ints{" + "index=" + index + ", end=" + end + '}';
    }
  
  }
  
}
