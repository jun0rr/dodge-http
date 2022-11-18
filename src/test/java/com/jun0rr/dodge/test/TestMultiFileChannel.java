/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Hash;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *
 * @author F6036477
 */
public class TestMultiFileChannel {
  
  public static final Path src = Paths.get("C:/Java/numbers.txt");
  
  public static final Path dst = Paths.get("C:/Java/numbers_multi.txt");
  
  public static final FileChannel ch0 = Unchecked.call(()->FileChannel.open(src, StandardOpenOption.READ));
  
  public Runnable test1() {
    return ()->{
      try {
        System.out.println("* file.src : " + src);
        System.out.println("* file.dst : " + dst);
        long size = Files.size(src);
        long max = size / 2;
        long total = 0;
        System.out.println("* file.size: " + size);
        try (
            FileChannel ch = FileChannel.open(dst, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {
          ByteBuffer buf = ByteBuffer.allocateDirect(4096);
          Hash hash = Hash.sha1();
          int read = -1;
          do {
            buf.limit(buf.position() + Math.min(read, read))
          } while();
          while((read = ch0.read(buf)) != -1 && total < max) {
            buf.flip().mark();
            hash.put(buf);
            buf.reset();
            ch.write(buf);
            buf.reset();
            ch.write(buf);
          }
        }
      }
      catch(IOException e) {
        throw Unchecked.unchecked(e);
      }
    };
  }
  
}
