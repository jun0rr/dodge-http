/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.time.Instant;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class Group {
  
  private String name;
  
  private Instant created;
  
  public Group() {}
  
  public Group(String name, Instant created) {
    this.name = Match.notEmpty(name).getOrFail("Bad null/empty Group name");
    this.created = Match.notNull(created).getOrFail("Bad null created Instant");
  }
  
  public Group(String name) {
    this(name, Instant.now());
  }

  public String getName() {
    return name;
  }

  public Group setName(String name) {
    System.out.println(">>>>> setName <<<<<");
    this.name = name;
    return this;
  }

  public Instant getCreated() {
    return created;
  }

  public Group setCreated(Instant created) {
    this.created = created;
    return this;
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.name);
    hash = 53 * hash + Objects.hashCode(this.created);
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
    final Group other = (Group) obj;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public String toString() {
    return "Group{" + "name=" + name + ", created=" + created + '}';
  }
  
}
