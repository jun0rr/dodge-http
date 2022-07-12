/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 *
 * @author F6036477
 */
public class ServerHeader extends DefaultHttpHeaders {
  
  public static final String HTTP_SERVER_VALUE = "dodge-http";
  
  public ServerHeader() {
    super();
    add(HttpHeaderNames.SERVER, HTTP_SERVER_VALUE);
  }
  
}
