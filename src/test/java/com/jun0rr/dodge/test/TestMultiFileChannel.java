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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestMultiFileChannel {
  
  public static final Path src = Paths.get("C:/Java/numbers.txt");
  
  public static final Path dst = Paths.get("C:/Java/numbers_multi.txt");
  
  public static final CountDownLatch cd = new CountDownLatch(2);
  
  @Test
  public void test() {
    System.out.println("* Starting tests...");
    ForkJoinPool.commonPool().execute(test1());
    ForkJoinPool.commonPool().execute(test2());
    Unchecked.call(()->cd.await());
  }
  
  public Runnable test1() {
    return ()->{
      try {
        System.out.println("* file.src : " + src);
        System.out.println("* file.dst : " + dst);
        long size = Files.size(src);
        long max = size / 2;
        long total = 0;
        ConsoleProgress cp = new ConsoleProgress(max);
        System.out.println("* file.size: " + size);
        try (
            FileChannel cr = FileChannel.open(src, StandardOpenOption.READ);
            FileChannel cw = FileChannel.open(dst, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {
          ByteBuffer buf = ByteBuffer.allocateDirect(4096);
          Hash hash = Hash.sha1();
          int read = -1;
          do {
            buf.limit(buf.position() + Math.min(buf.remaining(), (int)(max - total)));
            read = cr.read(buf);
            if(read == -1) break;
            total += read;
            buf.flip().mark();
            hash.put(buf);
            cw.write(buf.reset());
            System.out.println("* test1: " + cp.increment(read));
            buf.compact();
          } while(read != -1 && total < max);
          System.out.println("* test1 done: " + hash.get());
          cd.countDown();
        }
      }
      catch(IOException e) {
        throw Unchecked.unchecked(e);
      }
    };
  }
  
  public Runnable test2() {
    return ()->{
      try {
        System.out.println("* file.src : " + src);
        System.out.println("* file.dst : " + dst);
        long size = Files.size(src);
        long max = size - size / 2;
        long total = 0;
        ConsoleProgress cp = new ConsoleProgress(max);
        System.out.println("* file.size: " + size);
        try (
            FileChannel cr = FileChannel.open(src, StandardOpenOption.READ);
            FileChannel cw = FileChannel.open(dst, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ) {
          cw.position(max);
          cr.position(max);
          ByteBuffer buf = ByteBuffer.allocateDirect(4096);
          Hash hash = Hash.sha1();
          int read = -1;
          do {
            buf.limit(buf.position() + Math.min(buf.remaining(), (int)(max - total)));
            read = cr.read(buf);
            if(read == -1) break;
            total += read;
            buf.flip().mark();
            hash.put(buf);
            cw.write(buf.reset());
            System.out.println("* test2: " + cp.increment(read));
            buf.compact();
          } while(read != -1 && total < max);
          System.out.println("* test2 done: " + hash.get());
          cr.position(max -20);
          buf.clear().limit(40);
          cr.read(buf);
          System.out.println(StandardCharsets.UTF_8.decode(buf.flip()).toString());
          cd.countDown();
        }
      }
      catch(IOException e) {
        throw Unchecked.unchecked(e);
      }
    };
  }
  
}
