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
import com.jun0rr.dodge.http.auth.HttpBindGroupHandler;
import com.jun0rr.dodge.http.auth.HttpDeleteGroupHandler;
import com.jun0rr.dodge.http.auth.HttpDeleteUserHandler;
import com.jun0rr.dodge.http.auth.HttpGetAllGroupsHandler;
import com.jun0rr.dodge.http.auth.HttpGetAllRolesHandler;
import com.jun0rr.dodge.http.auth.HttpGetAllUsersHandler;
import com.jun0rr.dodge.http.auth.HttpGetUserHandler;
import com.jun0rr.dodge.http.auth.HttpLoginHandler;
import com.jun0rr.dodge.http.auth.HttpPutGroupHandler;
import com.jun0rr.dodge.http.auth.HttpPutUserHandler;
import com.jun0rr.dodge.http.auth.HttpShutdownHandler;
import com.jun0rr.dodge.http.auth.Login;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.tcp.ChannelEvent;
import com.jun0rr.util.Host;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
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
public class TestHttpAuthServer {
  
  private static final Logger logger = LoggerFactory.getLogger(TestHttpAuthServer.class);
  
  private static final String email = "juno.rr@gmail.com";
  
  private static final Group admin = new Group("admin");
  
  private static final Group auth = new Group("auth");
  
  private static final User user = new User("Juno", email, Password.of(new Login(email, "32132155".toCharArray())), LocalDate.of(1980, 7, 7), List.of(auth, admin));
  
  private static final Role authUser = new AllowRole(HttpGetUserHandler.ROUTE, auth);
  
  private static final Role allUsers = new AllowRole(HttpGetAllUsersHandler.ROUTE, admin);
  
  private static final Role delUser = new AllowRole(HttpDeleteUserHandler.ROUTE, admin);
  
  private static final Role allGroups = new AllowRole(HttpGetAllGroupsHandler.ROUTE, admin);
  
  private static final Role delGroup = new AllowRole(HttpDeleteGroupHandler.ROUTE, admin);
  
  private static final Role bindGroups = new AllowRole(HttpBindGroupHandler.ROUTE, admin);
  
  private static final Role allRoles = new AllowRole(HttpGetAllRolesHandler.ROUTE, admin);
  
  private static final Role putGroup = new AllowRole(HttpPutGroupHandler.ROUTE, admin);
  
  private static final Role putUser = new AllowRole(HttpPutUserHandler.ROUTE, admin);
  
  private static final Role shutdownServer = new AllowRole(HttpShutdownHandler.ROUTE, admin);
  
  @Test
  public void test() {
    try {
      HttpServer server = new HttpServer();
      //if(Files.exists(server.getStoragePath())) {
        //Files.walk(server.getStoragePath())
            //.filter(p->!Files.isDirectory(p))
            //.forEach(p->Unchecked.call(()->Files.delete(p)));
        //Files.walk(server.getStoragePath())
            //.filter(p->!server.getStoragePath().equals(p))
            //.forEach(p->Unchecked.call(()->Files.delete(p)));
      //}
      server.addHandler(ChannelEvent.Inbound.READ, HttpObject.class, HttpLoginHandler::get)
          .addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, HttpAuthFilter::get)
          .addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, HttpAccessFilter::get)
          .addRoute(HttpPutGroupHandler.ROUTE, HttpObject.class, HttpPutGroupHandler::get)
          .addRoute(HttpPutUserHandler.ROUTE, HttpObject.class, HttpPutUserHandler::get)
          .addRoute(HttpGetUserHandler.ROUTE, HttpRequest.class, HttpGetUserHandler::get)
          .addRoute(HttpGetAllUsersHandler.ROUTE, HttpRequest.class, HttpGetAllUsersHandler::get)
          .addRoute(HttpDeleteUserHandler.ROUTE, HttpRequest.class, HttpDeleteUserHandler::get)
          .addRoute(HttpGetAllGroupsHandler.ROUTE, HttpRequest.class, HttpGetAllGroupsHandler::get)
          .addRoute(HttpDeleteGroupHandler.ROUTE, HttpRequest.class, HttpDeleteGroupHandler::get)
          .addRoute(HttpGetAllRolesHandler.ROUTE, HttpRequest.class, HttpGetAllRolesHandler::get)
          .addRoute(HttpBindGroupHandler.ROUTE, HttpRequest.class, HttpBindGroupHandler::get)
          .addRoute(HttpShutdownHandler.ROUTE, HttpRequest.class, HttpShutdownHandler::get)
          ;
      server.startStorage()
          .set(auth)
          .set(admin)
          .set(authUser)
          .set(allUsers)
          .set(delUser)
          .set(allGroups)
          .set(delGroup)
          .set(bindGroups)
          .set(allRoles)
          .set(putGroup)
          .set(putUser)
          .set(shutdownServer)
          .set(user);
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
