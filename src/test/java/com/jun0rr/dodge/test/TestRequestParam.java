/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.auth.RequestParam;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestRequestParam {
  
  private static final String uri = "/users?double=5.2&long=52&boolean=false&date=2022-07-11T14:47:10";
  
  @Test
  public void test() {
    try {
    RequestParam pars = new RequestParam(uri);
    System.out.println("size = " + pars.size());
    Object val = pars.getObject("double");
    System.out.printf("double = %s, %s%n", val, val.getClass());
    val = pars.getObject("long");
    System.out.printf("long = %s, %s%n", val, val.getClass());
    val = pars.getObject("boolean");
    System.out.printf("boolean = %s, %s%n", val, val.getClass());
    val = pars.getObject("date");
    System.out.printf("date = %s, %s%n", val, val.getClass());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
