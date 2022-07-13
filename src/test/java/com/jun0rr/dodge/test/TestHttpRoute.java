/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.auth.HttpAuthHandler6;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.dodge.http.handler.HttpRoute;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestHttpRoute {
  
  public static final HttpRoute route = new HttpRoute(String.format("\\/?auth\\/user\\/(%s)", User.EMAIL_REGEX), HttpMethod.GET);
  
  public static final HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "auth/user/juno.rr@gmail.com");
  
  @Test
  public void testPatternRegex() {
    Assertions.assertTrue(route.test(request));
    Matcher m = Pattern.compile(route.regexString()).matcher(request.uri());
    if(m.find()) {
      System.out.println("* groupCount = " + m.groupCount());
      System.out.println("* group      = " + m.group());
      System.out.println("* group( count ) = " + m.group(m.groupCount()));
    }
  }
  
  @Test
  public void test() {
    System.out.println("PUT_REQUEST..: " + HttpAuthHandler6.PUT_REQUEST.get());
    System.out.println("PUT_REQUEST..: " + (HttpAuthHandler6.PUT_REQUEST.get() == HttpAuthHandler6.PUT_REQUEST.get()));
    System.out.println("putRequest...: " + HttpAuthHandler6.putRequest());
    System.out.println("putRequest...: " + (HttpAuthHandler6.putRequest() == HttpAuthHandler6.putRequest()));
  }
  
}
