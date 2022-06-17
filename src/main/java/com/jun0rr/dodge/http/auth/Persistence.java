/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Persistence {
  
  public List<User> users;
  
  public List<Role> roles;
  
  public Persistence(List<User> users, List<Role> roles) {
    this.users = Match.notNull(users).getOrFail("Bad null User List");
    this.roles = Match.notNull(roles).getOrFail("Bad null Role List");
  }
  
  public Persistence() {
    this(new LinkedList<>(), new LinkedList<>());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 71 * hash + Objects.hashCode(this.users);
    hash = 71 * hash + Objects.hashCode(this.roles);
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
    final Persistence other = (Persistence) obj;
    if (!Objects.equals(this.users, other.users)) {
      return false;
    }
    return Objects.equals(this.roles, other.roles);
  }

  @Override
  public String toString() {
    return "Persistence{" + "users=" + users + ", roles=" + roles + '}';
  }
  
}
