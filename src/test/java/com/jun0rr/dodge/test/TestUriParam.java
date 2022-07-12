/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.util.RequestParam;
import com.jun0rr.dodge.http.util.UriParam;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestUriParam {
  
  private static final String stringVal = "hello";
  
  private static final double doubleVal = 5.2;
  
  private static final long longVal = 52;
  
  private static final boolean booleanVal = false;
  
  private static final LocalDateTime localDateTimeVal = LocalDateTime.of(2022, 7, 11, 14, 47, 10);
  
  private static final OffsetDateTime offsetDateTimeVal = localDateTimeVal.atOffset(ZoneOffset.ofHours(-3));
  
  private static final Instant instantVal = offsetDateTimeVal.toInstant();
  
  private static final DecimalFormat df = new DecimalFormat("#.0#", new DecimalFormatSymbols(Locale.US));
  
  private static final String uri = String.format("/users/%s/%d/%s/%s/%s,%s,%d,%s,%s,%s,%s", 
      df.format(doubleVal), longVal, booleanVal, 
      localDateTimeVal, stringVal, df.format(doubleVal), 
      longVal, booleanVal, localDateTimeVal, 
      offsetDateTimeVal, instantVal
  );
  
  private static final String aliases = "/users/double/long/boolean/date/list";
  
  @Test
  public void test() {
    try {
      UriParam pars = new UriParam(uri);
      //RequestParam pars = new RequestParam(uri);
      Assertions.assertEquals(5, pars.size());
      
      //Object o = pars.getObject("double");
      Object o = pars.getObject(0);
      Assertions.assertEquals(Double.class, o.getClass());
      Assertions.assertEquals(doubleVal, o);
      
      //o = pars.getObject("long");
      o = pars.getObject(1);
      Assertions.assertEquals(Long.class, o.getClass());
      Assertions.assertEquals(longVal, o);
      
      //o = pars.getObject("boolean");
      o = pars.getObject(2);
      Assertions.assertEquals(Boolean.class, o.getClass());
      Assertions.assertEquals(booleanVal, o);
      
      //o = pars.getObject("date");
      o = pars.getObject(3);
      Assertions.assertEquals(LocalDateTime.class, o.getClass());
      Assertions.assertEquals(localDateTimeVal, o);
      
      //List<Object> list = pars.getList("list");
      List<Object> list = pars.getList(4);
      Assertions.assertEquals(7, list.size());
      Assertions.assertEquals(String.class, list.get(0).getClass());
      Assertions.assertEquals(stringVal, list.get(0));
      
      Assertions.assertEquals(Double.class, list.get(1).getClass());
      Assertions.assertEquals(doubleVal, list.get(1));
      
      Assertions.assertEquals(Long.class, list.get(2).getClass());
      Assertions.assertEquals(longVal, list.get(2));
      
      Assertions.assertEquals(Boolean.class, list.get(3).getClass());
      Assertions.assertEquals(booleanVal, list.get(3));
      
      Assertions.assertEquals(LocalDateTime.class, list.get(4).getClass());
      Assertions.assertEquals(localDateTimeVal, list.get(4));
      
      Assertions.assertEquals(OffsetDateTime.class, list.get(5).getClass());
      Assertions.assertEquals(offsetDateTimeVal, list.get(5));
      
      Assertions.assertEquals(Instant.class, list.get(6).getClass());
      Assertions.assertEquals(instantVal, list.get(6));
      
      RequestParam rpars = pars.asRequestParam(aliases);
      Assertions.assertEquals(5, rpars.size());
      
      o = rpars.getObject("double");
      Assertions.assertEquals(Double.class, o.getClass());
      Assertions.assertEquals(doubleVal, o);
      
      o = rpars.getObject("long");
      Assertions.assertEquals(Long.class, o.getClass());
      Assertions.assertEquals(longVal, o);
      
      o = rpars.getObject("boolean");
      Assertions.assertEquals(Boolean.class, o.getClass());
      Assertions.assertEquals(booleanVal, o);
      
      o = rpars.getObject("date");
      Assertions.assertEquals(LocalDateTime.class, o.getClass());
      Assertions.assertEquals(localDateTimeVal, o);
      
      list = rpars.getList("list");
      Assertions.assertEquals(7, list.size());
      Assertions.assertEquals(String.class, list.get(0).getClass());
      Assertions.assertEquals(stringVal, list.get(0));
      
      Assertions.assertEquals(Double.class, list.get(1).getClass());
      Assertions.assertEquals(doubleVal, list.get(1));
      
      Assertions.assertEquals(Long.class, list.get(2).getClass());
      Assertions.assertEquals(longVal, list.get(2));
      
      Assertions.assertEquals(Boolean.class, list.get(3).getClass());
      Assertions.assertEquals(booleanVal, list.get(3));
      
      Assertions.assertEquals(LocalDateTime.class, list.get(4).getClass());
      Assertions.assertEquals(localDateTimeVal, list.get(4));
      
      Assertions.assertEquals(OffsetDateTime.class, list.get(5).getClass());
      Assertions.assertEquals(offsetDateTimeVal, list.get(5));
      
      Assertions.assertEquals(Instant.class, list.get(6).getClass());
      Assertions.assertEquals(instantVal, list.get(6));
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
}
