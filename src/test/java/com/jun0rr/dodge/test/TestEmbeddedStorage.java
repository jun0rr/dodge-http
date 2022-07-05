/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.Method;
import com.jun0rr.dodge.http.auth.AllowRole;
import com.jun0rr.dodge.http.auth.Group;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.Storage;
import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.util.Unchecked;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestEmbeddedStorage {
  
  public static final Path storagePath = Path.of("src/test/resources/storage");
  
  public static final Group def = new Group("default");
  
  public static final Group admin = new Group("admin");
  
  public static final String email = "juno.rr@gmail.com";
  
  public static final User user = new User("juno", email, Password.of(email, "123456"), LocalDate.of(2022, 7, 7), List.of(def, admin));
  
  public static final Role allow = new AllowRole(HttpRoute.of(".*", HttpRoute.ALL_METHODS), admin);
  
  public static final Role deny = new AllowRole(HttpRoute.of("usr", Method.POST), def);
  
  
  @Test
  public void test() throws IOException {
    Files.walk(storagePath)
        .filter(p->!Files.isDirectory(p))
        .forEach(p->Unchecked.call(()->Files.delete(p)));
    Files.walk(storagePath)
        .filter(p->!storagePath.equals(p))
        .forEach(p->Unchecked.call(()->Files.delete(p)));
    storageWrite();
    storageRead();
  }
  
  public void storageWrite() {
    System.out.println("-------- storageWrite --------");
    try {
      Storage s = new Storage(storagePath);
      s.add(user).add(allow).add(deny).add(email, admin);
      s.shutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  public void storageRead() {
    System.out.println("-------- storageRead --------");
    try {
      Storage s = new Storage(storagePath);
      System.out.println(s.users().findFirst().get());
      Assertions.assertTrue(s.users().findFirst().get().getGroups().stream().filter(g->g.equals(def)).findAny().isPresent());
      Assertions.assertTrue(s.users().findFirst().get().getGroups().stream().filter(g->g.equals(admin)).findAny().isPresent());
      Assertions.assertTrue(s.roles().filter(r->r.equals(allow)).findAny().isPresent());
      Assertions.assertTrue(s.roles().filter(r->r.equals(deny)).findAny().isPresent());
      Assertions.assertEquals(user, s.users().findFirst().get());
      Assertions.assertEquals(admin, s.get(email));
      Assertions.assertTrue(s.users().filter(u->u.getEmail().equals(email)).findAny().get().getPassword().validate(email, "123456"));
      s.shutdown();
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
}
