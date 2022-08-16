/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.util.Host;
import com.jun0rr.util.Unchecked;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

/**
 *
 * @author F6036477
 */
public class TestHttpAuthServer2 {
  
  private static final Logger logger = LoggerFactory.getLogger(TestHttpAuthServer2.class);
  
  @Test
  public void test() {
    try {
      HttpServer server = new HttpServer();
      //server.setLogLevel(Level.INFO);
      if(Files.exists(server.getStoragePath())) {
        Files.walk(server.getStoragePath())
            .filter(p->!Files.isDirectory(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
        Files.walk(server.getStoragePath())
            .filter(p->!server.getStoragePath().equals(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
      }
      server.setAddress(Host.of("0.0.0.0", 8090))
      //server.setAddress(Host.localhost(8090))
          .start()
          .acceptNext(f->logger.info("HttpServer started and listening on {}", f.channel().localAddress()))
          .acceptOnClose(f->logger.info("HttpServer stopped!"));
      server.getMasterGroup().awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
