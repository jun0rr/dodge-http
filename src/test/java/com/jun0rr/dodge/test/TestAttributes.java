/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.tcp.Attributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestAttributes {
  
  @Test
  public void test() {
    Attributes attrs = new Attributes("global");
    attrs.put("storage", "The Storage");
    attrs = new Attributes(attrs, "channel1");
    attrs.put("myKey1", "myVal1")
        .put("myKey2", "myVal2");
    attrs.stream().forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
    attrs.parent().stream().forEach(e->System.out.printf("- %s: %s%n", e.getKey(), e.getValue()));
    Assertions.assertTrue(attrs.contains("myKey1"));
    Assertions.assertEquals("myVal1", attrs.get("myKey1").get());
    Assertions.assertFalse(attrs.parent().contains("myKey1"));
    Assertions.assertFalse(attrs.contains("storage"));
    Assertions.assertTrue(attrs.contains("myKey2"));
    Assertions.assertEquals("myVal2", attrs.get("myKey2").get());
    Assertions.assertFalse(attrs.parent().contains("myKey2"));
    Assertions.assertEquals("The Storage", attrs.parent().get("storage").get());
  }
  
  @Test
  public void test2() {
    Attributes attrs = new Attributes();
    attrs.put("storage", "The Storage");
    attrs = new Attributes(attrs, "channel1");
    attrs.put("myKey1", "myVal1")
        .put("myKey2", "myVal2");
    attrs.stream().forEach(e->System.out.printf("  - %s: %s%n", e.getKey(), e.getValue()));
    attrs.parent().stream().forEach(e->System.out.printf("- %s: %s%n", e.getKey(), e.getValue()));
    Assertions.assertTrue(attrs.contains("myKey1"));
    Assertions.assertEquals("myVal1", attrs.get("myKey1").get());
    Assertions.assertFalse(attrs.parent().contains("myKey1"));
    Assertions.assertFalse(attrs.contains("storage"));
    Assertions.assertTrue(attrs.contains("myKey2"));
    Assertions.assertEquals("myVal2", attrs.get("myKey2").get());
    Assertions.assertFalse(attrs.parent().contains("myKey2"));
    Assertions.assertEquals("The Storage", attrs.parent().get("storage").get());
  }
  
}
