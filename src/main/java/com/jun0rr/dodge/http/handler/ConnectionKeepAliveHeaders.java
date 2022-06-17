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
public class ConnectionKeepAliveHeaders extends DefaultHttpHeaders {
  
  public ConnectionKeepAliveHeaders() {
    super();
    add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
    add(HttpConstants.Header.PROXY_CONNECTION.with(HttpHeaderValues.KEEP_ALIVE));
  }
  
}
