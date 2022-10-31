/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestCreateFile {
  
  private static final Path path = Paths.get("C:/Java/numbers.txt");
  
  @Test
  public void test() {
    try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
      ByteBuffer buf = ByteBuffer.allocateDirect(4096);
      for(int i = 0; i < 10000; i++) {
        String ln = String.format("number: %d%n", i);
        if(buf.remaining() < ln.length()) {
          fc.write(buf.flip());
          buf.compact();
        }
        buf.put(ln.getBytes(StandardCharsets.UTF_8));
      }
      if(buf.flip().hasRemaining()) {
        fc.write(buf);
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }
  
}
