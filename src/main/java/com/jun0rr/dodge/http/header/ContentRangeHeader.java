/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class ContentRangeHeader extends DefaultHttpHeaders {
  
  public static final String HEADER_FORMAT = "bytes %d-%d/%d";
  
  private final Range range;
  
  public ContentRangeHeader(Range r) {
    this.range = Objects.requireNonNull(r);
    add(HttpHeaderNames.CONTENT_RANGE, String.format(HEADER_FORMAT, r.start(), r.end(), r.total()));
  }
  
  public Range range() {
    return range;
  }
  
}
