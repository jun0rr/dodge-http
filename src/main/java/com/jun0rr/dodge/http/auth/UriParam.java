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

package com.jun0rr.dodge.http.auth;

import static com.jun0rr.dodge.http.auth.RequestParam.BOOLEAN_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.DOUBLE_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.INSTANT_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_DDMMYYYY_FORMAT;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_DDMMYYYY_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_TIME_FORMAT;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_TIME_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_YYYYMMDD_FORMAT;
import static com.jun0rr.dodge.http.auth.RequestParam.LOCAL_DATE_YYYYMMDD_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.LONG_TEST;
import static com.jun0rr.dodge.http.auth.RequestParam.OFFSET_DATE_TIME_FORMAT;
import static com.jun0rr.dodge.http.auth.RequestParam.OFFSET_DATE_TIME_TEST;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Juno Roesler - juno@pserver.us
 * @version 0.0 - 22/07/2016
 */
public class UriParam {

  private final String uri;
  
  private final List<String> params;
  
  public UriParam(String uri) {
    if(uri == null || uri.trim().isEmpty() 
        || !uri.trim().contains("/")) {
      throw new IllegalArgumentException("Bad URI: "+ uri);
    }
    this.uri = uri;
    params = Arrays.asList(this.uri.startsWith("/") 
        ? this.uri.substring(1).split("/") 
        : this.uri.split("/")
    );
  }
  
  public int length() {
    return params.size() -1;
  }
  
  public String getURI() {
    return uri;
  }
  
  public String getContext() {
    return params.get(0);
  }
  
  public String getParam(int index) {
    if(index < 0 || index > params.size() -2) {
      return null;
    }
    return params.get(index+1);
  }
  
  public boolean isNumber(int index) {
    Number n = this.getNumber(index);
    return n != null && n.doubleValue() != Double.NaN;
  }
  
  public Number getNumber(int index) {
    try {
      return Double.parseDouble(getParam(index));
    } catch(NumberFormatException e) {
      return Double.NaN;
    }
  }
  
  public int getInt(int index) {
    try {
      return Integer.parseInt(getParam(index));
    } catch(Exception e) {
      return -1;
    }
  }
  
  public long getLong(int index) {
    try {
      return Long.parseLong(getParam(index));
    } catch(Exception e) {
      return -1;
    }
  }
  
  public List getList(int index) {
    String svl = getParam(index);
    if(svl == null) return Collections.EMPTY_LIST;
    String[] vls = svl.split(",");
    List lst = new ArrayList<>(vls.length);
    for(String v : vls) {
      lst.add(getObjectValue(v));
    }
    return lst;
  }
  
  public LocalDate getLocalDate(int index) {
    String sdt = getParam(index);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    DateTimeFormatter dtf = LOCAL_DATE_DDMMYYYY_TEST.test(sdt) ? LOCAL_DATE_DDMMYYYY_FORMAT : LOCAL_DATE_YYYYMMDD_FORMAT;
    return LocalDate.parse(sdt, dtf);
  }
  
  public LocalDateTime getLocalDateTime(int index) {
    String sdt = getParam(index);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return LocalDateTime.parse(sdt, LOCAL_DATE_TIME_FORMAT);
  }
  
  public OffsetDateTime getOffsetDateTime(int index) {
    String sdt = getParam(index);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return OffsetDateTime.parse(sdt, OFFSET_DATE_TIME_FORMAT);
  }
  
  public Instant getInstant(int index) {
    String sdt = getParam(index);
    if(sdt == null || sdt.trim().isEmpty()) {
      return null;
    }
    return Instant.parse(sdt);
  }
  
  public boolean getBoolean(int index) {
    String sb = getParam(index);
    if(sb == null) return false;
    return Boolean.parseBoolean(sb);
  }
  
  public Object getObject(int index) {
    String val = getParam(index);
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

  public Object[] getObjectArgs() {
    Object[] args = new Object[this.length()];
    for(int i = 0; i < this.length(); i++) {
      args[i] = this.getObject(i);
    }
    return args;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + Objects.hashCode(this.uri);
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
    final UriParam other = (UriParam) obj;
    if (!Objects.equals(this.uri, other.uri)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "URIParam{" + "uri=" + uri + ", params=" + params + '}';
  }
  
}
