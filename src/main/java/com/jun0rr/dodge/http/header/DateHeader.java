/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import com.jun0rr.dodge.http.util.HttpConstants;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.util.AsciiString;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DateHeader extends DefaultHttpHeaders {
  
  private final LocalDateTime date;
  
  public DateHeader(CharSequence name, Date d) {
    super();
    this.date = LocalDateTime.ofInstant(Objects.requireNonNull(d).toInstant(), ZoneOffset.UTC);
    HttpConstants.setDateHeader(this, name, date);
  }
  
  public DateHeader(AsciiString name, Date d) {
    super();
    this.date = LocalDateTime.ofInstant(Objects.requireNonNull(d).toInstant(), ZoneOffset.UTC);
    HttpConstants.setDateHeader(this, name, date);
  }
  
  public DateHeader(CharSequence name, LocalDateTime d) {
    super();
    this.date = Objects.requireNonNull(d);
    HttpConstants.setDateHeader(this, name, date);
  }
  
  public DateHeader(AsciiString name, LocalDateTime d) {
    super();
    this.date = Objects.requireNonNull(d);
    HttpConstants.setDateHeader(this, name, date);
  }
  
  public DateHeader() {
    this(HttpHeaderNames.DATE, LocalDateTime.now());
  }
  
  public DateHeader(Date d) {
    this(HttpHeaderNames.DATE, d);
  }
  
  public DateHeader(LocalDateTime d) {
    this(HttpHeaderNames.DATE, d);
  }
  
  public DateHeader(CharSequence name) {
    this(name, LocalDateTime.now());
  }
  
  public DateHeader(AsciiString name) {
    this(name, LocalDateTime.now());
  }
  
  public LocalDateTime getDate() {
    return date;
  }
  
}
