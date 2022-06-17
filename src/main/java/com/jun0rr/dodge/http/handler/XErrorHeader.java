/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class XErrorHeader extends DefaultHttpHeaders {
  
  public static final String X_ERROR_MESSAGE = "x-error-message";
  
  public static final String X_ERROR_CLASS = "x-error-class";
  
  public static final String X_ERROR_CAUSE = "x-error-cause";
  
  public static final String X_ERROR_TRACE = "x-error-trace";
  
  public static final String BAD_STACK_TRACE_LEVEL = "Bad stack trace level (min=1, val=%d)";
  
  public static final int DEFAULT_STACK_TRACE_LEVEL = 10;
  
  public XErrorHeader(String err) {
    super();
    add(X_ERROR_MESSAGE, Match.notEmpty(err).getOrFail());
  }
  
  public XErrorHeader(String err, Object... args) {
    super();
    add(X_ERROR_MESSAGE, Match.notEmpty(String.format(err, args)).getOrFail());
  }
  
  public XErrorHeader(String err, Throwable th, int stackTraceLevel) {
    super();
    Match.notEmpty(err).failIfNotMatch("Bad null error message");
    Match.notNull(th).failIfNotMatch("Bad null Throwable");
    if(stackTraceLevel < 1) {
      throw new IllegalArgumentException(String.format(BAD_STACK_TRACE_LEVEL, stackTraceLevel));
    }
    add(X_ERROR_MESSAGE, String.format("%s <<%s>>", err, th.getMessage()));
    add(th, stackTraceLevel);
  }
  
  public XErrorHeader(String err, Throwable th) {
    this(err, th, DEFAULT_STACK_TRACE_LEVEL);
  }
  
  public XErrorHeader(Throwable th, int stackTraceLevel) {
    super();
    Match.notNull(th).failIfNotMatch("Bad null Throwable");
    if(stackTraceLevel < 1) {
      throw new IllegalArgumentException(String.format(BAD_STACK_TRACE_LEVEL, stackTraceLevel));
    }
    add(X_ERROR_MESSAGE, th.getMessage());
    add(th, stackTraceLevel);
  }
  
  public XErrorHeader(Throwable th) {
    this(th, DEFAULT_STACK_TRACE_LEVEL);
  }
  
  private void add(Throwable th, int stackTraceLevel) {
    add(X_ERROR_CLASS, th.getClass().getName());
    if(th.getCause() != null) {
      add(X_ERROR_CAUSE, th.getCause().toString());
    }
    List.of(th.getStackTrace()).stream()
        .limit(stackTraceLevel)
        .forEach(e->add(X_ERROR_TRACE, String.format("%s[%s](%d)", e.getClassName(), e.getMethodName(), e.getLineNumber())));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("XErrorHeader{\n");
    forEach(e->sb.append(String.format("   - %s: %s%n", e.getKey(), e.getValue())));
    return sb.append("}").toString();
  }
  
}
