/*
 * Direitos Autorais Reservados (c) 2011 Juno Roesler
 * Contato: juno.rr@gmail.com
 * 
 * Esta biblioteca é software livre; você pode redistribuí-la e/ou modificá-la sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation; tanto a versão 2.1 da Licença, ou qualquer
 * versão posterior.
 * 
 * Esta biblioteca é distribuída na expectativa de que seja útil, porém, SEM
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE
 * OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública
 * Geral Menor do GNU para mais detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto
 * com esta biblioteca; se não, acesse 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html, 
 * ou escreva para a Free Software Foundation, Inc., no
 * endereço 59 Temple Street, Suite 330, Boston, MA 02111-1307 USA.
 */

package com.jun0rr.dodge.http.util;

import com.jun0rr.dodge.http.*;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 03/04/2017
 */
public class RequestParam {

  public static final String BOOLEAN_PATTERN = "(?i)(true|false)";
  
  public static final String DOUBLE_PATTERN = "(-|\\+)?[0-9]+\\.[0-9]+";
  
  public static final String LONG_PATTERN = "(-|\\+)?[0-9]+";
  
  public static final String LOCAL_DATE_DDMMYYYY_PATTERN = "[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}";
  
  public static final String LOCAL_DATE_YYYYMMDD_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}";
  
  public static final String LOCAL_DATE_TIME_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}";
  
  public static final String INSTANT_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z";
  
  public static final String OFFSET_DATE_TIME_PATTERN = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}[\\+\\-]{1}[0-9]{2}:[0-9]{2}";
  
  public static final String LOCAL_TIME_PATTERN = "[0-9]{2}:[0-9]{2}(:[0-9]{2})?(\\.[0-9]+)?";
  
  public static final DateTimeFormatter LOCAL_DATE_DDMMYYYY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  
  public static final DateTimeFormatter LOCAL_DATE_YYYYMMDD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  
  public static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
  
  public static final DateTimeFormatter OFFSET_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXXXX");
  
  public static final Predicate<String> BOOLEAN_TEST = Pattern.compile(BOOLEAN_PATTERN).asPredicate();
  
  public static final Predicate<String> DOUBLE_TEST = Pattern.compile(DOUBLE_PATTERN).asPredicate();
  
  public static final Predicate<String> LONG_TEST = Pattern.compile(LONG_PATTERN).asPredicate();
  
  public static final Predicate<String> LOCAL_DATE_DDMMYYYY_TEST = Pattern.compile(LOCAL_DATE_DDMMYYYY_PATTERN).asPredicate();
  
  public static final Predicate<String> LOCAL_DATE_YYYYMMDD_TEST = Pattern.compile(LOCAL_DATE_YYYYMMDD_PATTERN).asPredicate();
  
  public static final Predicate<String> LOCAL_DATE_TIME_TEST = Pattern.compile(LOCAL_DATE_TIME_PATTERN).asPredicate();
  
  public static final Predicate<String> OFFSET_DATE_TIME_TEST = Pattern.compile(OFFSET_DATE_TIME_PATTERN).asPredicate();
  
  public static final Predicate<String> INSTANT_TEST = Pattern.compile(INSTANT_PATTERN).asPredicate();
  
  
  private final Map<String,List<String>> pars;
  
  public RequestParam(String uri) {
    Match.notEmpty(uri).failIfNotMatch("Bad null empty URI");
    pars = new QueryStringDecoder(uri).parameters();
  }
  
  public RequestParam(Map<String,List<String>> pars) {
    Match.notNull(pars).failIfNotMatch("Bad null empty URI");
    this.pars = pars;
  }
  
  public Map<String,List<String>> getParameters() {
    return this.pars;
  }
  
  public boolean containsValid(String key) {
    String val = pars.get(key).get(0);
    return val != null && !val.trim().isEmpty();
  }
  
  public boolean contains(String key) {
    return pars.containsKey(key);
  }
  
  public int size() {
    return pars.size();
  }
  
  public boolean isEmpty() {
    return pars.isEmpty();
  }
  
  public String get(String key) {
    List<String> ls = pars.get(key);
    return ls != null ? ls.get(0) : null;
  }
  
  public Number getNumber(String key) {
    try {
      return Double.parseDouble(get(key));
    } catch(Exception e) {
      return null;
    }
  }
  
  public int getInt(String key) {
    try {
      return Integer.parseInt(get(key));
    } catch(Exception e) {
      return -1;
    }
  }
  
  public long getLong(String key) {
    try {
      return Long.parseLong(get(key));
    } catch(Exception e) {
      return -1;
    }
  }
  
  public LocalDate getLocalDate(String key) {
    String sdt = get(key);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    DateTimeFormatter dtf = LOCAL_DATE_DDMMYYYY_TEST.test(sdt) ? LOCAL_DATE_DDMMYYYY_FORMAT : LOCAL_DATE_YYYYMMDD_FORMAT;
    return LocalDate.parse(sdt, dtf);
  }
  
  public LocalDateTime getLocalDateTime(String key) {
    String sdt = get(key);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return LocalDateTime.parse(sdt, LOCAL_DATE_TIME_FORMAT);
  }
  
  public OffsetDateTime getOffsetDateTime(String key) {
    String sdt = get(key);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return OffsetDateTime.parse(sdt, OFFSET_DATE_TIME_FORMAT);
  }
  
  public Instant getInstant(String key) {
    String sdt = get(key);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return Instant.parse(sdt);
  }
  
  public List<Object> getList(String key) {
    String val = get(key);
    if(val == null || val.isBlank()) {
      return Collections.EMPTY_LIST;
    }
    return List.of(val.split(","))
        .stream()
        .map(this::getObjectValue)
        .collect(Collectors.toList());
  }
  
  public boolean getBoolean(String key) {
    String sb = get(key);
    if(sb == null) return false;
    return Boolean.parseBoolean(sb);
  }
  
  public Object getObject(String key) {
    String val = get(key);
    if(val == null) return null;
    return getObjectValue(val);
  }

  private Object getObjectValue(String value) {
    if(BOOLEAN_TEST.test(value)) {
      return Boolean.parseBoolean(value);
    }
    else if(INSTANT_TEST.test(value)) {
      return Instant.parse(value);
    }
    else if(OFFSET_DATE_TIME_TEST.test(value)) {
      return OffsetDateTime.parse(value, OFFSET_DATE_TIME_FORMAT);
    }
    else if(LOCAL_DATE_TIME_TEST.test(value)) {
      return LocalDateTime.parse(value, LOCAL_DATE_TIME_FORMAT);
    }
    else if(LOCAL_DATE_YYYYMMDD_TEST.test(value)) {
      return LocalDate.parse(value, LOCAL_DATE_YYYYMMDD_FORMAT);
    }
    else if(LOCAL_DATE_DDMMYYYY_TEST.test(value)) {
      return LocalDate.parse(value, LOCAL_DATE_TIME_FORMAT);
    }
    else if(DOUBLE_TEST.test(value)) {
      return Double.parseDouble(value);
    }
    else if(LONG_TEST.test(value)) {
      return Long.parseLong(value);
    }
    else {
      return value;
    }
  }


  @Override
  public String toString() {
    return pars.toString();
  }
  
  
}
