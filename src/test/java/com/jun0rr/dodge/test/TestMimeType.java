/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.util.MimeType;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestMimeType {
  
  @Test
  public void test() {
    System.out.println(MimeType.fromFile(Paths.get("C:/Users/f6036477/Downloads/application.csv")).orElse(MimeType.BIN));
    System.out.println(MimeType.fromFile(Paths.get("C:/Users/f6036477/Downloads/20221020124626000841.pdf")).orElse(MimeType.BIN));
    System.out.println(MimeType.fromFile(Paths.get("C:/Users/f6036477/Downloads/ctrlTcrd.war")).orElse(MimeType.BIN));
  }
  
}
