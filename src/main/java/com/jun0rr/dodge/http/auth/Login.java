/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Login {
  
  private String email;
  
  private char[] password;

  public Login() {
  }
  
  public Login(String email, char[] password) {
    this.email = Match.notEmpty(email).getOrFail("Bad empty email String");
    this.password = Match.notNull(password)
        .and(p->p.length > 0)
        .getOrFail("Bad empty password");
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = Match.notEmpty(email).getOrFail("Bad empty email String");
  }
  
  public char[] getPassword() {
    return password;
  }
  
  public byte[] getPasswordBytes() {
    byte[] bs = new byte[password.length];
    for(int i = 0; i < password.length; i++) {
      bs[i] = (byte) password[i];
    }
    return bs;
  }
  
  public void setPassword(char[] password) {
    this.password = Match.notNull(password)
        .and(p->p.length > 0)
        .getOrFail("Bad empty password");
  }
  
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.email);
    hash = 59 * hash + Arrays.hashCode(this.password);
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
    final Login other = (Login) obj;
    if (!Objects.equals(this.email, other.email)) {
      return false;
    }
    return Arrays.equals(this.password, other.password);
  }
  
  @Override
  public String toString() {
    return "Login{" + "email=" + email + '}';
  }
  
}
