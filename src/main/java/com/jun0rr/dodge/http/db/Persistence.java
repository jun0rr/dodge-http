/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.db;

import com.jun0rr.dodge.http.auth.Role;
import com.jun0rr.dodge.http.auth.User;
import com.jun0rr.util.match.Match;
import java.util.LinkedList;
import java.util.List;

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
  
}
