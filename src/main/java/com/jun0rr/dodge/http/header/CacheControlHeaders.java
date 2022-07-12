/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class CacheControlHeaders extends DefaultHttpHeaders {
  
  public CacheControlHeaders() {
    super();
    add(HttpHeaderNames.CACHE_CONTROL, List.of(HttpHeaderValues.NO_CACHE, HttpHeaderValues.NO_STORE, HttpHeaderValues.MUST_REVALIDATE));
    add(HttpHeaderNames.PRAGMA, HttpHeaderValues.NO_CACHE);
    addInt(HttpHeaderNames.EXPIRES, -1);
  }
  
}
