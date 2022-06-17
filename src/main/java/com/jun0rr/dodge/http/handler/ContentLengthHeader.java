/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.util.Random;

/**
 *
 * @author F6036477
 */
public class ContentLengthHeader extends DefaultHttpHeaders {
  
  public static final int INIT_CONTENT_LENGTH = 256 * 1024 * 1024;
  
  public ContentLengthHeader(int len) {
    super();
    addInt(HttpHeaderNames.CONTENT_LENGTH, len);
  }
  
  public ContentLengthHeader() {
    super();
    Random r = new Random();
    addInt(HttpHeaderNames.CONTENT_LENGTH, r.nextInt(INIT_CONTENT_LENGTH) + INIT_CONTENT_LENGTH);
  }
  
}
