/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 *
 * @author F6036477
 */
public class DateHeader extends DefaultHttpHeaders {
  
  public DateHeader() {
    super();
    add(HttpHeaderNames.DATE, new java.util.Date());
  }
  
}
