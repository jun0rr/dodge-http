/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jun0rr.dodge.http.auth.AllowRole;
import com.jun0rr.dodge.http.auth.DenyRole;
import com.jun0rr.dodge.http.auth.Group;
import com.jun0rr.dodge.http.util.JsonHttpMethodAdapter;
import com.jun0rr.dodge.http.util.JsonIgnoreStrategy;
import com.jun0rr.dodge.http.util.JsonMetricAdapter;
import com.jun0rr.dodge.http.auth.Password;
import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.http.util.JsonRoleAdapter;
import com.jun0rr.dodge.http.auth.Login;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.dodge.metrics.Counter;
import com.jun0rr.dodge.metrics.Gauge;
import com.jun0rr.dodge.metrics.Metric;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestGson {
  
  private static final Gson gson = new GsonBuilder()
      //.setPrettyPrinting()
      .registerTypeAdapter(AllowRole.class, new JsonRoleAdapter())
      .registerTypeAdapter(DenyRole.class, new JsonRoleAdapter())
      .registerTypeAdapter(Role.class, new JsonRoleAdapter())
      .registerTypeAdapter(HttpMethod.class, new JsonHttpMethodAdapter())
      .registerTypeAdapter(Metric.class, new JsonMetricAdapter())
      .registerTypeAdapter(Counter.class, new JsonMetricAdapter())
      .registerTypeAdapter(Gauge.class, new JsonMetricAdapter())
      .setExclusionStrategies(new JsonIgnoreStrategy())
      .create();
  
  private static final HttpRoute routeCreateUser = new HttpRoute("\\/?auth\\/user", HttpMethod.POST, HttpMethod.PUT);
  
  private static final Group groupDefault = new Group("default");
  
  private static final Group groupAdmin = new Group("admin");
  
  private static final User user = new User("Juno", "juno.rr@gmail.com", Password.of(new Login("juno.rr@gmail.com", "32132155".toCharArray())), LocalDate.of(1980, 7, 7), List.of(groupDefault, groupAdmin));
  
  private static final Role roleCreateUser = new AllowRole(routeCreateUser, groupAdmin);
  
  private static final Login login = new Login("juno.rr@gmail.com", "32132155".toCharArray());
  
  private static final Metric metric1 = new Counter("metric_counter", "Some metric Counter", 550L)
      .putLabel("foo", "bar").putLabel("baz", "foobar")
      ;
  
  private static final Metric metric2 = new Gauge("metric_gauge", "Some metric Gauge", 5.50)
      .putLabel("foo", "bar").putLabel("baz", "foobar")
      ;
  
  private boolean jsonArrayContains(JsonArray array, Function<JsonElement,String> prop, String val) {
    for(int i = 0; i < array.size(); i++) {
      if(prop.apply(array.get(i)).equals(val)) {
        return true;
      }
    }
    return false;
  }
  
  @Test
  public void testHttpRoute() {
    System.out.println("------ testHttpRoute ------");
    try {
    JsonObject obj = gson.toJsonTree(routeCreateUser).getAsJsonObject();
    System.out.println("JsonObject = " + obj);
    Assertions.assertEquals(routeCreateUser.regexString(), obj.get("regex").getAsString());
    Assertions.assertTrue(routeCreateUser.methods().stream()
        .allMatch(m->jsonArrayContains(
            obj.getAsJsonArray("methods"), 
            JsonElement::getAsString, 
            m.name())
        )
    );
    String json = gson.toJson(obj);
    HttpRoute r = gson.fromJson(json, HttpRoute.class);
    Assertions.assertEquals(routeCreateUser.regexString(), r.regexString());
    Assertions.assertEquals(routeCreateUser.methods(), r.methods());
    Assertions.assertEquals(routeCreateUser, r);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
  
  @Test
  public void testUser() {
    JsonObject obj = gson.toJsonTree(user).getAsJsonObject();
    String json = gson.toJson(obj);
    Assertions.assertEquals(user.getName(), obj.get("name").getAsString());
    Assertions.assertEquals(user.getEmail(), obj.get("email").getAsString());
    Assertions.assertEquals(user.getBirthday().getYear(), obj.get("birthday").getAsJsonObject().get("year").getAsInt());
    Assertions.assertEquals(user.getBirthday().getMonthValue(), obj.get("birthday").getAsJsonObject().get("month").getAsInt());
    Assertions.assertEquals(user.getBirthday().getDayOfMonth(), obj.get("birthday").getAsJsonObject().get("day").getAsInt());
    Assertions.assertTrue(user.getGroups().stream()
        .allMatch(g->jsonArrayContains(
            obj.getAsJsonArray("groups"), 
            e->e.getAsJsonObject().get("name").getAsString(), 
            g.getName())
        )
    );
    User u = gson.fromJson(json, User.class);
    Assertions.assertEquals(user.getName(), u.getName());
    Assertions.assertEquals(user.getEmail(), u.getEmail());
    Assertions.assertEquals(user.getBirthday(), u.getBirthday());
    Assertions.assertEquals(user.getGroups(), u.getGroups());
    Assertions.assertEquals(user.getCreated(), u.getCreated());
    Assertions.assertEquals(user, u);
  }
  
  @Test
  public void testRole() {
    JsonObject obj = gson.toJsonTree(roleCreateUser).getAsJsonObject();
    String json = gson.toJson(obj);
    Role role = gson.fromJson(json, Role.class);
    Assertions.assertEquals(AllowRole.class, role.getClass());
    Assertions.assertTrue(roleCreateUser.allow(user));
    Assertions.assertTrue(role.allow(user));
  }
  
  @Test
  public void testHttpResponseStatus() {
    System.out.println("------ testHttpResponseStatus ------");
    System.out.println(gson.toJson(HttpResponseStatus.NOT_FOUND));
  }
  
  @Test
  public void testLogin() {
    System.out.println("------ testLogin ------");
    System.out.printf("login...: Login{email=%s, password=%s}%n", login.getEmail(), Arrays.toString(login.getPassword()));
    String json = gson.toJson(login);
    System.out.println("toJson.: " + json);
    Login l = gson.fromJson(json, Login.class);
    System.out.printf("fromJson: Login{email=%s, password=%s}%n", l.getEmail(), Arrays.toString(l.getPassword()));
    Assertions.assertEquals(login, l);
  }
  
  @Test
  public void testMetric() {
    System.out.println("------ testMetric ------");
    System.out.println("counter.........: " + metric1);
    String scounter = gson.toJson(metric1);
    System.out.println("counter.toJson..: " + scounter);
    System.out.println("gauge...........: " + metric2);
    String sgauge = gson.toJson(metric2);
    System.out.println("gauge.toJson....: " + sgauge);
    Counter counter = gson.fromJson(scounter, Counter.class);
    System.out.println("counter.fromJson: " + counter);
    Gauge gauge = gson.fromJson(sgauge, Gauge.class);
    System.out.println("gauge.fromJson..: " + gauge);
  }
  
}
