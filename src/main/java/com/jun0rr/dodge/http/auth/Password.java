/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.crypto.Hash;
import com.jun0rr.util.match.Match;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author F6036477
 */
@JsonIgnore
public class Password {
  
  private final byte[] salt;
  
  private final byte[] hash;
  
  public Password(byte[] salt, byte[] hash) {
    this.salt = Match.notNull(salt).getOrFail("Bad null salt byte array");
    this.hash = Match.notNull(hash).getOrFail("Bad null hash byte array");
  }
  
  public static Password of(String email, String passwd) {
    Match.notEmpty(email).failIfNotMatch("Bad null/empty e-mail");
    Match.notEmpty(passwd).failIfNotMatch("Bad null/empty password");
    Random r = new Random();
    byte[] salt = new byte[16];
    r.nextBytes(salt);
    byte[] hash = Hash.sha512().put(salt).put(email).put(passwd).getBytes();
    return new Password(salt, hash);
  }
  
  public boolean validate(String email, String passwd) {
    Match.notEmpty(email).failIfNotMatch("Bad null/empty e-mail");
    Match.notEmpty(passwd).failIfNotMatch("Bad null/empty password");
    return Arrays.equals(hash, Hash.sha512().put(salt).put(email).put(passwd).getBytes());
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + Arrays.hashCode(this.salt);
    hash = 23 * hash + Arrays.hashCode(this.hash);
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
    final Password other = (Password) obj;
    if (!Arrays.equals(this.salt, other.salt)) {
      return false;
    }
    return Arrays.equals(this.hash, other.hash);
  }
  
}
