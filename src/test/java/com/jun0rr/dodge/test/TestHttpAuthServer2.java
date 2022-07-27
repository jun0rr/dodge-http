/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.HttpServer;
import com.jun0rr.dodge.http.auth.AllowRole;
import com.jun0rr.dodge.http.auth.Group;
import com.jun0rr.dodge.http.auth.HttpAccessFilter;
import com.jun0rr.dodge.http.auth.HttpAuthFilter;
import com.jun0rr.dodge.http.auth.HttpGroupsBindHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsDeleteHandler;
import com.jun0rr.dodge.http.auth.HttpUsersDeleteHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpRolesGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpUsersGetAllHandler;
import com.jun0rr.dodge.http.auth.HttpUserGetHandler;
import com.jun0rr.dodge.http.auth.HttpLoginHandler;
import com.jun0rr.dodge.http.auth.HttpGroupsPutHandler;
import com.jun0rr.dodge.http.auth.HttpRolesPutHandler;
import com.jun0rr.dodge.http.auth.HttpUsersPutHandler;
import com.jun0rr.dodge.http.auth.HttpShutdownHandler;
import com.jun0rr.dodge.http.auth.Login;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.util.Host;
import com.jun0rr.util.Unchecked;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      if(Files.exists(server.getStoragePath())) {
        Files.walk(server.getStoragePath())
            .filter(p->!Files.isDirectory(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
        Files.walk(server.getStoragePath())
            .filter(p->!server.getStoragePath().equals(p))
            .forEach(p->Unchecked.call(()->Files.delete(p)));
      }
      server.setAddress(Host.localhost(8090))
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
