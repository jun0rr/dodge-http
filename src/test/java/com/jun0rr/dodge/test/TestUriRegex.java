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
public class TestUriRegex {
  
  @Test
  public void test() {
    String uri = "/auth/roles/deny?methods=GET";
    String regex = "^(/?[\\w_\\-\\.]+)+";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(uri);
    System.out.println("* m.find(): " + m.find());
    System.out.println("* m.groupCount(): " + m.groupCount());
    System.out.println("* m.group(): " + m.group());
    System.out.println("* m.group(0): " + m.group(0));
    for(int i = 0; i < m.groupCount(); i++) {
      System.out.println(" => " + m.group(i));
    }
  }
  
}
