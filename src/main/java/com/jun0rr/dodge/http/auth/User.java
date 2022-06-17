/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class User {
  
  public static final String EMAIL_REGEX = "[a-zA-Z0-9_.]+@[a-zA-Z0-9_]+\\.[a-zA-Z0-9_.]+";
  
  public String name;
  
  public String email;
  
  public Password password;
  
  public LocalDate birthday;
  
  public List<Group> groups;
  
  public Instant created;
  
  public User() {
    this.groups = new LinkedList<>();
    this.created = Instant.now();
  }
  
  public User(String name, String email, Password password) {
    this(name, email, password, null, new LinkedList<>());
  }
  
  public User(String name, String email, Password password, LocalDate birthday, List<Group> groups) {
    this.name = Match.notEmpty(name).getOrFail("Bad null/empty name");
    this.email = email;
    validateEmail();
    this.password = Match.notNull(password).getOrFail("Bad null Password");
    this.birthday = birthday;
    this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
    this.created = Instant.now();
  }
  
  public void validateEmail() {
    if(email == null || email.isBlank() || !email.matches(EMAIL_REGEX)) {
      throw new IllegalStateException("Bad e-mail format: " + email);
    }
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(this.name);
    hash = 37 * hash + Objects.hashCode(this.email);
    hash = 37 * hash + Objects.hashCode(this.birthday);
    hash = 37 * hash + Objects.hashCode(this.groups);
    hash = 37 * hash + Objects.hashCode(this.created);
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
    final User other = (User) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.email, other.email)) {
      return false;
    }
    if (!Objects.equals(this.birthday, other.birthday)) {
      return false;
    }
    if (!Objects.equals(this.groups, other.groups)) {
      return false;
    }
    return Objects.equals(this.created, other.created);
  }

  @Override
  public String toString() {
    return "User{" + "name=" + name + ", email=" + email + ", birthday=" + birthday + ", groups=" + groups + ", created=" + created + '}';
  }
  
}
