/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestMapStream {
  
  private Map<String,Integer> createMap() {
    Map<String,Integer> map = new ConcurrentHashMap<>();
    for(int i = 0; i < 10; i++) {
      map.put(String.valueOf(i), i);
    }
    return map;
  }
  
  @Test
  public void test() {
    Map<String,Integer> map = createMap();
    System.out.println("map.size=" + map.size());
    map.entrySet().stream()
        .peek(e->System.out.println(e.toString()))
        .peek(e->System.out.printf("  - %s = %s%n", e.getKey(), e.getValue()))
        .filter(e->e.getValue() % 2 == 0)
        .map(Map.Entry::getKey)
        //.collect(Collectors.toList())
        .forEach(map::remove);
    System.out.println("map.size=" + map.size());
    map.entrySet().stream()
        .forEach(e->System.out.printf("  - %s = %s%n", e.getKey(), e.getValue()));
  }
  
}
