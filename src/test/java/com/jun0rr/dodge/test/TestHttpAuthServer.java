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
import com.jun0rr.dodge.http.auth.HttpRolesPostHandler;
import com.jun0rr.dodge.http.auth.HttpUsersPutHandler;
import com.jun0rr.dodge.http.handler.HttpShutdownHandler;
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
public class TestHttpAuthServer {
  
  private static final Logger logger = LoggerFactory.getLogger(TestHttpAuthServer.class);
  
  private static final String email = "juno.rr@gmail.com";
  
  private static final Group admin = new Group("admin");
  
  private static final Group auth = new Group("auth");
  
  private static final User user = new User("Juno", email, Password.of(new Login(email, "32132155".toCharArray())), LocalDate.of(1980, 7, 7), List.of(auth, admin));
  
  private static final Role authUser = new AllowRole(HttpUserGetHandler.ROUTE, auth);
  
  private static final Role allUsers = new AllowRole(HttpUsersGetAllHandler.ROUTE, admin);
  
  private static final Role delUser = new AllowRole(HttpUsersDeleteHandler.ROUTE, admin);
  
  private static final Role allGroups = new AllowRole(HttpGroupsGetAllHandler.ROUTE, admin);
  
  private static final Role delGroup = new AllowRole(HttpGroupsDeleteHandler.ROUTE, admin);
  
  private static final Role bindGroups = new AllowRole(HttpGroupsBindHandler.ROUTE, admin);
  
  private static final Role allRoles = new AllowRole(HttpRolesGetAllHandler.ROUTE, admin);
  
  private static final Role putRoles = new AllowRole(HttpRolesPostHandler.ROUTE, admin);
  
  private static final Role putGroup = new AllowRole(HttpGroupsPutHandler.ROUTE, admin);
  
  private static final Role putUser = new AllowRole(HttpUsersPutHandler.ROUTE, admin);
  
  private static final Role shutdownServer = new AllowRole(HttpShutdownHandler.ROUTE, admin);
  
  //@Test
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
      server.addHandler(ChannelEvent.Inbound.READ, HttpObject.class, HttpLoginHandler::get)
          .addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, HttpAuthFilter::get)
          .addHandler(ChannelEvent.Inbound.READ, HttpRequest.class, HttpAccessFilter::get)
          .addRoute(HttpUserGetHandler.ROUTE, HttpRequest.class, HttpUserGetHandler::get)
          .addRoute(HttpUsersGetAllHandler.ROUTE, HttpRequest.class, HttpUsersGetAllHandler::get)
          .addRoute(HttpUsersPutHandler.ROUTE, HttpObject.class, HttpUsersPutHandler::get)
          .addRoute(HttpUsersDeleteHandler.ROUTE, HttpRequest.class, HttpUsersDeleteHandler::get)
          .addRoute(HttpGroupsGetAllHandler.ROUTE, HttpRequest.class, HttpGroupsGetAllHandler::get)
          .addRoute(HttpGroupsPutHandler.ROUTE, HttpObject.class, HttpGroupsPutHandler::get)
          .addRoute(HttpGroupsBindHandler.ROUTE, HttpRequest.class, HttpGroupsBindHandler::get)
          .addRoute(HttpGroupsDeleteHandler.ROUTE, HttpRequest.class, HttpGroupsDeleteHandler::get)
          .addRoute(HttpRolesGetAllHandler.ROUTE, HttpRequest.class, HttpRolesGetAllHandler::get)
          .addRoute(HttpRolesPostHandler.ROUTE, HttpObject.class, HttpRolesPostHandler::get)
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
          .set(putRoles)
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
