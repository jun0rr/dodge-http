/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestRange {
  
  public static final Pattern HEADER_RANGE = Pattern.compile("[a-zA-Z]+=([0-9]+)-([0-9]+)");
  
  public static final String value = "bytes=20-1000, 1200-1800";
  
  @Test
  public void test() {
    Matcher m = HEADER_RANGE.matcher(value);
    System.out.println("m.find()="+m.find());
    System.out.println(m);
    System.out.println("m.groupCount()="+m.groupCount());
    System.out.println("m.group(0)="+m.group(0));
    System.out.println("m.group(1)="+m.group(1));
    System.out.println("m.group(2)="+m.group(2));
  }
}
