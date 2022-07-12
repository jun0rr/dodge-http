/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.header;

import com.jun0rr.util.Base64Codec;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;

/**
 *
 * @author F6036477
 */
public class ProxyAuthorizationHeader extends DefaultHttpHeaders {
  
  public ProxyAuthorizationHeader(String usr, String pwd) {
    super();
    String enc = Base64Codec.encodeToString(String.format("%s:%s", usr, pwd));
    add(HttpHeaderNames.PROXY_AUTHORIZATION, String.format("Basic %s", enc));
  }
  
}
