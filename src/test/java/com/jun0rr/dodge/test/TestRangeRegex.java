/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.header.RangeHeader;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestRangeRegex {
  
  private static final String range = "bytes=10-20, 30-40, 50-60";
  
  @Test
  public void test() {
    RangeHeader hdr = RangeHeader.parse(range);
    hdr.ranges().forEach(System.out::println);
    System.out.println(hdr);
  }
  
}
