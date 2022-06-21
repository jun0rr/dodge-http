/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Persistence {
  
  public List<User> users;
  
  public List<Role> roles;
  
  public Map<String,Object> map;
  
  public Persistence(List<User> users, List<Role> roles, Map<String,Object> map) {
    this.users = Match.notNull(users).getOrFail("Bad null User List");
    this.roles = Match.notNull(roles).getOrFail("Bad null Role List");
    this.map = Match.notNull(map).getOrFail("Bad null Map<String,Object>");
  }
  
  public Persistence() {
    this(new LinkedList<>(), new LinkedList<>(), new HashMap<>());
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode(this.users);
    hash = 59 * hash + Objects.hashCode(this.roles);
    hash = 59 * hash + Objects.hashCode(this.map);
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
    if (!Objects.equals(this.roles, other.roles)) {
      return false;
    }
    return Objects.equals(this.map, other.map);
  }

  @Override
  public String toString() {
    return "Persistence{" + "users=" + users + ", roles=" + roles + ", map=" + map + '}';
  }

}
