/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

/**
 *
 * @author F6036477
 */
public class JsonContentHeader extends DefaultHttpHeaders {
  
  public static final int INIT_CONTENT_LENGTH = 256 * 1024 * 1024;
  
  public JsonContentHeader(int len) {
    super();
    if(len > 0) {
      add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
      addInt(HttpHeaderNames.CONTENT_LENGTH, len);
    }
  }
  
}
