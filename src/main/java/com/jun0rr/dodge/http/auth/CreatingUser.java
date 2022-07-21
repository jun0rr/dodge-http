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
public class CreatingUser {
  
  public static final String EMAIL_REGEX = "[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+";
  
  private String name;
  
  private String email;
  
  private char[] password;
  
  private LocalDate birthday;
  
  private List<Group> groups;
  
  public CreatingUser() {
    this.groups = new LinkedList<>();
  }
  
  public CreatingUser(String name, String email, char[] password) {
    this(name, email, password, null, new LinkedList<>());
  }
  
  public CreatingUser(String name, String email, char[] password, LocalDate birthday) {
    this(name, email, password, null, new LinkedList<>());
  }
  
  public CreatingUser(String name, String email, char[] password, LocalDate birthday, List<Group> groups) {
    this.name = Match.notEmpty(name).getOrFail("Bad null/empty name");
    setEmail(email);
    this.password = Match.notNull(password).getOrFail("Bad null Password");
    this.birthday = birthday;
    this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
  }

  public String getName() {
    return name;
  }

  public CreatingUser setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public CreatingUser setEmail(String email) {
    this.email = Match.notEmpty(email)
        .and(e->e.matches(EMAIL_REGEX))
        .getOrFail("Bad e-mail format: %s", email);
    return this;
  }

  public char[] getPassword() {
    return password;
  }

  public CreatingUser setPassword(char[] password) {
    this.password = password;
    return this;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public CreatingUser setBirthday(LocalDate birthday) {
    this.birthday = birthday;
    return this;
  }

  public List<Group> getGroups() {
    return groups;
  }

  public CreatingUser setGroups(List<Group> groups) {
    this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
    return this;
  }
  
  public User toUser() {
    return new User(name, email, Password.of(new Login(email, password)), birthday, groups);
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(this.name);
    hash = 37 * hash + Objects.hashCode(this.email);
    hash = 37 * hash + Objects.hashCode(this.birthday);
    hash = 37 * hash + Objects.hashCode(this.groups);
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
    final CreatingUser other = (CreatingUser) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    if (!Objects.equals(this.email, other.email)) {
      return false;
    }
    if (!Objects.equals(this.birthday, other.birthday)) {
      return false;
    }
    return Objects.equals(this.groups, other.groups);
  }

  @Override
  public String toString() {
    return "CreatingUser{" + "name=" + name + ", email=" + email + ", birthday=" + birthday + ", groups=" + groups + '}';
  }
  
}
