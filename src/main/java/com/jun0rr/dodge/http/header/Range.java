/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

/**
 *
 * @author F6036477
 */
public class Range {
  
  public static final Range EMPTY = new Range(0L, 0L);
  
  private final long start;
  
  private final long end;
  
  private final long total;
  
  public Range(long start, long end, long total) {
    this.start = start;
    this.end = end;
    this.total = total;
  }
  
  public Range(long start, long end) {
    this(start, end, end);
  }
  
  public long start() {
    return start;
  }
  
  public long end() {
    return end;
  }
  
  public long length() {
    return end - start;
  }
  
  public long total() {
    return total;
  }
  
  public boolean isEmpty() {
    return start == 0 && end == 0;
  }
  
  public Range start(long pos) {
    return new Range(pos, end);
  }
  
  public Range end(long len) {
    return new Range(start, len);
  }
  
  public Range total(long total) {
    return new Range(start, end, total);
  }
  
  public Range incrementStart(long start) {
    return start(this.start + start);
  }
  
  public Range incrementEnd(long end) {
    return end(this.end + end);
  }

  public Range incrementTotal(long total) {
    return total(this.total + total);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (int) (this.start ^ (this.start >>> 32));
    hash = 97 * hash + (int) (this.end ^ (this.end >>> 32));
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
    final Range other = (Range) obj;
    if (this.start != other.start) {
      return false;
    }
    if (this.end != other.end) {
      return false;
    }
    return this.total == other.total;
  }
  
  public String toContentRangeString() {
    return String.format("%d-%d/%d", start, end, total);
  }
  
  public String toRangeString() {
    return String.format("%d-%d", start, end);
  }

  @Override
  public String toString() {
    return toContentRangeString(); 
  }
  
}
