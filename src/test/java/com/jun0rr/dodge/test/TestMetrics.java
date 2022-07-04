/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.metrics.Counter;
import com.jun0rr.dodge.metrics.Gauge;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestMetrics {
  
  public static final Random rdm = new Random();
  
  @Test
  public void testCounter() {
    System.out.println("------ testCounter ------");
    Counter ct = new Counter("dodge_http_request_count", "Count of HTTP requests", Math.abs(rdm.nextLong()));
    ct.labels().put("uri", "/t40");
    ct.labels().put("hello", "world");
    List<String> ls = new LinkedList();
    ct.collect(ls);
    ls.forEach(System.out::println);
  }
  
  @Test
  public void testGauge() {
    System.out.println("------ testGauge ------");
    Gauge gg = new Gauge("dodge_http_request_timing", "HTTP request time millis", Math.abs(rdm.nextDouble() * rdm.nextInt(500)));
    gg.labels().put("uri", "/t40");
    gg.labels().put("hello", "world");
    List<String> ls = new LinkedList();
    gg.collect(ls);
    ls.forEach(System.out::println);
  }
  
}
