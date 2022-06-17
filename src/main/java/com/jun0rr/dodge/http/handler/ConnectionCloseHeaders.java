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
public class ConnectionCloseHeaders extends DefaultHttpHeaders {
  
  public ConnectionCloseHeaders() {
    super();
    add(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
    add(HttpConstants.Header.PROXY_CONNECTION.with(HttpHeaderValues.CLOSE));
  }
  
}
