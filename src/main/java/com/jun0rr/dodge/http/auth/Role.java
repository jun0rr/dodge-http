/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.dodge.http.handler.HttpRoute;
import com.jun0rr.util.match.Match;
import io.netty.handler.codec.http.HttpRequest;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public interface Role {
  
  public boolean match(HttpRequest req);
  
  public boolean allow(User usr);
  
  
  public static Role allowRole(HttpRoute r, Group... gs) {
    return allowRole(r, List.of(gs));
  }
  
  public static Role allowRole(HttpRoute r, List<Group> gs) {
    return new AbstractRole(r, gs) {
      public boolean allow(User usr) {
        return gs.stream().anyMatch(g->usr.groups.contains(g));
      }
    };
  }
  
  public static Role denyRole(HttpRoute r, Group... gs) {
    return denyRole(r, List.of(gs));
  }
  
  public static Role denyRole(HttpRoute r, List<Group> gs) {
    return new AbstractRole(r, gs) {
      public boolean allow(User usr) {
        return gs.stream().noneMatch(g->usr.groups.contains(g));
      }
    };
  }
  
  
  
  
  
  static abstract class AbstractRole implements Role {
    
    private final HttpRoute route;
    
    private final List<Group> groups;
    
    public AbstractRole(HttpRoute route, List<Group> groups) {
      this.route = Match.notNull(route).getOrFail("Bad null HttpRoute");
      this.groups = Match.notNull(groups).getOrFail("Bad null Groups List");
    }
    
    @Override
    public boolean match(HttpRequest req) {
      return route.test(req);
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 19 * hash + Objects.hashCode(this.route);
      hash = 19 * hash + Objects.hashCode(this.groups);
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
      final AbstractRole other = (AbstractRole) obj;
      if (!Objects.equals(this.route, other.route)) {
        return false;
      }
      return Objects.equals(this.groups, other.groups);
    }

    @Override
    public String toString() {
      return "Role{" + "route=" + route + ", groups=" + groups + '}';
    }
    
  }
  
}
