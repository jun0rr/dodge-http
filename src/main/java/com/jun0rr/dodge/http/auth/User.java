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
  
  public static final String EMAIL_REGEX = "[a-zA-Z_]+[a-zA-Z0-9_\\.\\-]*@[a-zA-Z_]+\\.[a-zA-Z0-9_.]+";
  
  private String name;
  
  private String email;
  
  @JsonIgnore
  private Password password;
  
  private LocalDate birthday;
  
  private List<Group> groups;
  
  private Instant created;
  
  public User() {
    this.groups = new LinkedList<>();
    this.created = Instant.now();
  }
  
  public User(String name, String email, Password password) {
    this(name, email, password, null, new LinkedList<>());
  }
  
  public User(String name, String email, Password password, LocalDate birthday) {
    this(name, email, password, null, new LinkedList<>());
  }
  
  public User(String name, String email, Password password, LocalDate birthday, List<Group> groups) {
    this.name = Match.notEmpty(name).getOrFail("Bad null/empty name");
    setEmail(email);
    this.password = Match.notNull(password).getOrFail("Bad null Password");
    this.birthday = birthday;
    this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
    this.created = Instant.now();
  }

  public String getName() {
    return name;
  }

  public User setName(String name) {
    this.name = name;
    return this;
  }

  public String getEmail() {
    return email;
  }

  public User setEmail(String email) {
    this.email = Match.notEmpty(email)
        .and(e->e.matches(EMAIL_REGEX))
        .getOrFail("Bad e-mail format: %s", email);
    return this;
  }

  public Password getPassword() {
    return password;
  }

  public User setPassword(Password password) {
    this.password = password;
    return this;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public User setBirthday(LocalDate birthday) {
    this.birthday = birthday;
    return this;
  }

  public List<Group> getGroups() {
    return groups;
  }

  public User setGroups(List<Group> groups) {
    this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
    return this;
  }
  
  public User add(Group g) {
    this.groups.add(Match.notNull(g).getOrFail("Bad null Group"));
    return this;
  }

  public Instant getCreated() {
    return created;
  }

  public User setCreated(Instant created) {
    this.created = Match.notNull(created).getOrFail("Bad null Instant");
    return this;
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
