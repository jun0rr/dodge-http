/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 * @author juno
 */
public class HttpRoute implements Predicate<HttpRequest> {
  
  public static final List<HttpMethod> ALL_METHODS = List.of(
      HttpMethod.CONNECT,
      HttpMethod.DELETE,
      HttpMethod.GET,
      HttpMethod.HEAD,
      HttpMethod.OPTIONS,
      HttpMethod.PATCH,
      HttpMethod.POST,
      HttpMethod.PUT,
      HttpMethod.TRACE
  );

  private final String pattern;
  
  private final List<HttpMethod> methods;
  
  private final transient Predicate<String> matcher;
  
  public HttpRoute(String ptrn, List<HttpMethod> meths) {
    this.pattern = Match.notEmpty(ptrn).getOrFail("Bad empty pattern");
    this.methods = Match.notEmpty(meths).getOrFail("Bad empty methods List");
    this.matcher = Pattern.compile(ptrn).asPredicate();
  }
  
  public HttpRoute(String ptrn, HttpMethod... meths) {
    this.pattern = Match.notEmpty(ptrn).getOrFail("Bad empty pattern");
    this.methods = List.of(Match.notEmpty(meths).getOrFail("Bad empty methods List"));
    this.matcher = Pattern.compile(ptrn).asPredicate();
  }
  
  
  public static HttpRoute of(String ptrn, HttpMethod... meths) {
    return new HttpRoute(ptrn, meths);
  }
  
  public static HttpRoute of(String ptrn, List<HttpMethod> meths) {
    return new HttpRoute(ptrn, meths);
  }
  
  
  public HttpRoute(String ptrn) {
    this(ptrn, ALL_METHODS);
  }
  
  public List<HttpMethod> methods() {
    return methods;
  }
  
  public String pattern() {
    return pattern;
  }
  
  @Override
  public boolean test(HttpRequest req) {
    return methods.stream().anyMatch(m->m.equals(req.method())) && matcher.test(req.uri());
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + Objects.hashCode(this.pattern);
    hash = 43 * hash + Objects.hashCode(this.methods);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final HttpRoute other = (HttpRoute) obj;
    if (!Objects.equals(this.pattern, other.pattern)) {
      return false;
    }
    if (!Objects.equals(this.methods, other.methods)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "HttpRoute{" + "pattern=" + pattern + ", methods=" + methods + '}';
  }
  
}
