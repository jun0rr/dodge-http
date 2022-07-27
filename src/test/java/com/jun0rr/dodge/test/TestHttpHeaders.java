/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestHttpHeaders {
  
  @Test
  public void test() {
    System.out.println(HttpHeaderNames.CONNECTION);
    System.out.println(HttpHeaderValues.KEEP_ALIVE);
    String conn = "keep-alive";
    System.out.println(HttpHeaderValues.KEEP_ALIVE.equals(conn));
    System.out.println(conn.toLowerCase().equals(HttpHeaderValues.KEEP_ALIVE));
    System.out.println(HttpHeaderValues.KEEP_ALIVE.toString().equalsIgnoreCase(conn));
  }
  
}
