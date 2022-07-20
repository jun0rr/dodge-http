/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import java.util.List;

/**
 *
 * @author F6036477
 */
public class AllowRole extends Role {
  
  public AllowRole(HttpRoute route, List<Group> groups) {
    super(route, groups);
  }
  
  public AllowRole(HttpRoute route, Group... groups) {
    super(route, List.of(groups));
  }
  
  @Override
  public boolean allow(User usr) {
    return groups.stream().anyMatch(usr.getGroups()::contains);
  }

}
