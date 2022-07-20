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
import java.util.regex.Matcher;
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

  private final String regex;
  
  private final List<HttpMethod> methods;
  
  private final transient Pattern pattern;
  
  public HttpRoute(String regex, List<HttpMethod> meths) {
    this.regex = Match.notEmpty(regex).getOrFail("Bad empty pattern");
    this.methods = Match.notEmpty(meths).getOrFail("Bad empty methods List");
    this.pattern = Pattern.compile(regex);
  }
  
  public HttpRoute(String regex, HttpMethod... meths) {
    this.regex = Match.notEmpty(regex).getOrFail("Bad empty pattern");
    this.methods = List.of(Match.notEmpty(meths).getOrFail("Bad empty methods List"));
    this.pattern = Pattern.compile(regex);
  }
  
  
  public static HttpRoute of(String regex, HttpMethod... meths) {
    return new HttpRoute(regex, meths);
  }
  
  public static HttpRoute of(String regex, List<HttpMethod> meths) {
    return new HttpRoute(regex, meths);
  }
  
  
  public HttpRoute(String ptrn) {
    this(ptrn, ALL_METHODS);
  }
  
  public List<HttpMethod> methods() {
    return methods;
  }
  
  public String regexString() {
    return regex;
  }
  
  public Matcher matcher(String uri) {
    return pattern.matcher(uri);
  }
  
  @Override
  public boolean test(HttpRequest req) {
    return methods.stream().anyMatch(m->m.equals(req.method())) && matcher(req.uri()).matches();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + Objects.hashCode(this.regex);
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
    if (!Objects.equals(this.regex, other.regex)) {
      return false;
    }
    if (!Objects.equals(this.methods, other.methods)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "HttpRoute{" + "regex=" + regex + ", methods=" + methods + '}';
  }
  
}
