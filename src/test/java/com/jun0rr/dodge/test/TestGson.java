/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jun0rr.dodge.http.Method;
import com.jun0rr.dodge.http.auth.Group;
import com.jun0rr.dodge.http.auth.JsonIgnoreStrategy;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.http.handler.HttpRoute;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestGson {
  
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().setExclusionStrategies(new JsonIgnoreStrategy()).create();
  
  private static final HttpRoute route = new HttpRoute("\\/?hello(\\/.*)?", Method.GET, Method.POST);
  
  private static final User user = new User("Juno", "juno.rr@gmail.com", Password.of("juno.rr@gmail.com", "32132155"), LocalDate.of(1980, 7, 7), List.of(new Group("default"), new Group("admin")));
  
  @Test
  public void testHttpRoute() {
    String json = gson.toJson(route);
    HttpRoute r = gson.fromJson(json, HttpRoute.class);
    Assertions.assertEquals(route.pattern(), r.pattern());
    Assertions.assertEquals(route.methods(), r.methods());
    Assertions.assertEquals(route, r);
  }
  
  @Test
  public void testUser() {
    String json = gson.toJson(user);
    User u = gson.fromJson(json, User.class);
    Assertions.assertEquals(user.getName(), u.getName());
    Assertions.assertEquals(user.getEmail(), u.getEmail());
    Assertions.assertEquals(user.getBirthday(), u.getBirthday());
    Assertions.assertEquals(user.getGroups(), u.getGroups());
    Assertions.assertEquals(user.getCreated(), u.getCreated());
    Assertions.assertEquals(user, u);
  }
  
}
