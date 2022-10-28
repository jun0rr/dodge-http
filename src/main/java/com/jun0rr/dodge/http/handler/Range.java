/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class Range {
  
  public static final Pattern HEADER_RANGE = Pattern.compile("[a-zA-Z]+=([0-9]+)-([0-9]+)");
  
  public static final Range EMPTY = new Range(0L, 0L);
  
  private final long position;
  
  private final long length;
  
  public Range(long pos, long len) {
    this.position = pos;
    this.length = len;
  }
  
  public static Range of(HttpRequest req) {
    String value = req.headers().get(HttpHeaderNames.RANGE);
    Matcher m = value != null ? HEADER_RANGE.matcher(value) : null;
    if(m == null || !m.find()) {
      return EMPTY;
    }
    return new Range(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)));
  }
  
  public long position() {
    return position;
  }
  
  public long length() {
    return length;
  }
  
  public boolean isEmpty() {
    return position == 0 && length == 0;
  }
  
  public Range position(long pos) {
    return new Range(pos, length);
  }
  
  public Range length(long len) {
    return new Range(position, len);
  }
  
  public Range incrementPos(long pos) {
    return position(position + pos);
  }
  
  public Range incrementLen(long len) {
    return length(length + len);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (int) (this.position ^ (this.position >>> 32));
    hash = 97 * hash + (int) (this.length ^ (this.length >>> 32));
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
    if (this.position != other.position) {
      return false;
    }
    return this.length == other.length;
  }

  @Override
  public String toString() {
    return "Range{" + "position=" + position + ", length=" + length + '}';
  }
  
}
