/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.auth.Group;
import com.jun0rr.dodge.http.auth.HttpAuthFilter;
import com.jun0rr.dodge.http.auth.HttpGetUserHandler;
import com.jun0rr.dodge.http.auth.HttpLoginHandler;
import com.jun0rr.dodge.http.auth.Login;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.tcp.ChannelEvent;
import static com.jun0rr.dodge.test.TestEmbeddedStorage.storagePath;
import com.jun0rr.util.Host;
import com.jun0rr.util.Unchecked;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class TestHttpAuthServer {
  
  private static final Logger logger = LoggerFactory.getLogger(TestHttpAuthServer.class);
  
  private static final String email = "juno.rr@gmail.com";
  
  private static final Group admin = new Group("admin");
  
  private static final User user = new User("Juno", email, Password.of(new Login(email, "32132155".toCharArray())), LocalDate.of(1980, 7, 7), List.of(admin));
  
  @Test
  public void test() {
    try {
      HttpServer server = new HttpServer();
      if(Files.exists(server.getStoragePath())) {
        Files.walk(server.getStoragePath())
            .filter(p->!Files.isDirectory(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
        Files.walk(server.getStoragePath())
            .filter(p->!server.getStoragePath().equals(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
      }
      server.addHandler(ChannelEvent.Inbound.READ, HttpLoginHandler::get)
          .addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, HttpAuthFilter::get);
      server.addRoute(HttpGetUserHandler.ROUTE, HttpGetUserHandler::get);
      server.startStorage().add(admin).add(user);
      server.setAddress(Host.localhost(8090))
          .start()
          .acceptNext(f->logger.info("HttpServer started and listening on {}", f.channel().localAddress()))
          .acceptOnClose(f->logger.info("HttpServer stopped!"))
          .syncUninterruptibly();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
