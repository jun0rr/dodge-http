/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.util.Indexed;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestIndexed {
  
  @Test
  public void test() {
    List<String> ls = new LinkedList<>();
    for(int i = 1; i <= 10; i++) {
      ls.add("Object#" + i);
    }
    ls.stream().map(Indexed.mapper()).forEach(System.out::println);
    ls.stream().map(Indexed.mapper()).forEach(System.out::println);
  }
  
}
