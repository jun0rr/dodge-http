/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class ContentRangeHeader extends DefaultHttpHeaders {
  
  public static final Pattern HEADER_PATTERN = Pattern.compile("[a-zA-Z]+\\s([0-9]+)-([0-9]+)/([0-9]+)");
  
  public static final String HEADER_FORMAT = "bytes %d-%d/%d";
  
  private final Range range;
  
  public ContentRangeHeader(Range r) {
    this.range = Objects.requireNonNull(r);
    add(HttpHeaderNames.CONTENT_RANGE, String.format(HEADER_FORMAT, r.start(), r.end(), r.total()));
  }
  
  public Range range() {
    return range;
  }
  
  public static ContentRangeHeader parse(HttpHeaders hdr) {
    return parse(hdr.get(HttpHeaderNames.CONTENT_RANGE));
  }
  
  public static ContentRangeHeader parse(String str) {
    Range range = Range.EMPTY;
    if(str != null && !str.isBlank()) {
      Matcher m = HEADER_PATTERN.matcher(str);
      if(m.find() && m.groupCount() == 3) {
        range = new Range(Long.parseLong(m.group(1)), Long.parseLong(m.group(2)), Long.parseLong(m.group(3)));
      }
    }
    return new ContentRangeHeader(range);
  }
  
}
