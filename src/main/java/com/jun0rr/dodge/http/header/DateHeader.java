/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Date;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class DateHeader extends DefaultHttpHeaders {
  
  private final Date date;
  
  public DateHeader(Date d) {
    super();
    this.date = Objects.requireNonNull(d);
    add(HttpHeaderNames.DATE, date);
  }
  
  public DateHeader() {
    this(new Date());
  }
  
}
