/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Stream;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

/**
 *
 * @author F6036477
 */
public class Storage {
  
  private final transient EmbeddedStorageManager manager;
  
  private final List<User> users;
  
  private final List<Group> groups;
  
  private final List<Role> roles;
  
  private final Map<String,Object> map;
  
  public Storage(Path path, List<User> users, List<Group> groups, List<Role> roles, Map<String,Object> map) {
    this.users = Match.notNull(users).getOrFail("Bad null User List");
    this.groups = Match.notNull(groups).getOrFail("Bad null Group List");
    this.roles = Match.notNull(roles).getOrFail("Bad null Role List");
    this.map = Match.notNull(map).getOrFail("Bad null Map<String,Object>");
    this.manager = EmbeddedStorage.start(this, Match.exists(path).getOrFail("Path does not exists"));
  }
  
  public Storage(Path path) {
    this(path, new LinkedList<>(), new LinkedList<>(), new LinkedList<>(), new HashMap<>());
  }
  
  public Storage add(User u) {
    Match.notNull(u).failIfNotMatch("Bad null User");
    manager.store(u);
    if(users.stream().noneMatch(s->s.getEmail().equals(u.getEmail()))) {
      users.add(u);
      users.stream().map(Indexed::)
      manager.store(users);
    }
    return this;
  }
  
  public Storage add(Group g) {
    Match.notNull(g).failIfNotMatch("Bad null Group");
    manager.store(g);
    groups.add(g);
    manager.store(groups);
    return this;
  }
  
  public Storage add(Role r) {
    Match.notNull(r).failIfNotMatch("Bad null Role");
    manager.store(r);
    roles.add(r);
    manager.store(roles);
    return this;
  }
  
  public Storage add(String key, Object obj) {
    Match.notEmpty(key).failIfNotMatch("Bad empty key String");
    Match.notNull(obj).failIfNotMatch("Bad empty value Object");
    manager.store(obj);
    map.put(key, obj);
    manager.store(map);
    return this;
  }
  
  public <T> T get(String key) {
    return (T) map.get(key);
  }
  
  public <T> T remove(String key) {
    return (T) map.remove(key);
  }
  
  public Stream<Entry<String,Object>> objects() {
    return map.entrySet().stream();
  }
  
  public Stream<User> users() {
    return users.stream();
  }
  
  public Stream<Group> groups() {
    return groups.stream();
  }
  
  public Stream<Role> roles() {
    return roles.stream();
  }
  
  public void shutdown() {
    manager.shutdown();
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
    final Storage other = (Storage) obj;
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
