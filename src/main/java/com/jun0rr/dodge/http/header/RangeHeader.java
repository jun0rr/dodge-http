/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class RangeHeader extends DefaultHttpHeaders {
  
  public static final Pattern HEADER_PATTERN = Pattern.compile("([0-9]+)-([0-9]+)");
  
  private final List<Range> ranges;
  
  public RangeHeader(List<Range> ranges) {
    this.ranges = Objects.requireNonNull(ranges);
    if(!ranges.isEmpty()) {
      StringBuilder sb = new StringBuilder("bytes=");
      ranges.stream()
          .map(Range::toRangeString)
          .forEach(r->sb.append(r).append(", "));
      sb.delete(sb.length()-2, sb.length());
      add(HttpHeaderNames.RANGE, sb.toString());
    }
  }
  
  public RangeHeader(Range r) {
    this(List.of(r));
  }
  
  public List<Range> ranges() {
    return ranges;
  }
  
  public static RangeHeader parse(HttpHeaders hdr) {
    return parse(hdr.get(HttpHeaderNames.RANGE));
  }
  
  public static RangeHeader parse(String str) {
    List<Range> ranges = new LinkedList<>();
    if(str != null && !str.isBlank()) {
      Matcher m = HEADER_PATTERN.matcher(str);
      while(m.find()) {
        ranges.add(new Range(Long.parseLong(m.group(1)), Long.parseLong(m.group(2))));
      }
    }
    return new RangeHeader(ranges);
  }
  
}
