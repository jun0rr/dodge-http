/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestStringChars {
  
  private static final String text = "Hello World!";
  
  private static final char[] chars = text.toCharArray();
  
  @Test
  public void test() {
    byte[] iso88591 = text.getBytes(StandardCharsets.ISO_8859_1);
    byte[] utf16 = text.getBytes(StandardCharsets.UTF_16);
    byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
    byte[] bc = new byte[chars.length];
    for(int i = 0; i < chars.length; i++) {
      bc[i] = (byte) chars[i];
    }
    System.out.println("ISO-8859-1 = " + Arrays.toString(iso88591));
    System.out.println("UTF-16.... = " + Arrays.toString(utf16));
    System.out.println("UTF-8..... = " + Arrays.toString(utf8));
    System.out.println("chars..... = " + Arrays.toString(bc));
  }
  
}
