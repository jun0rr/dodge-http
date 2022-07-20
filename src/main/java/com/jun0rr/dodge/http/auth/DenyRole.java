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
public class DenyRole extends Role {
  
  public DenyRole(HttpRoute route, List<Group> groups) {
    super(route, groups);
  }
  
  public DenyRole(HttpRoute route, Group... groups) {
    super(route, List.of(groups));
  }
  
  @Override
  public boolean allow(User usr) {
    return groups.stream().noneMatch(usr.getGroups()::contains);
  }

}
