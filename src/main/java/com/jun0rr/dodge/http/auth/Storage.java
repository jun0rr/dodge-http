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
import java.util.Optional;
import java.util.stream.Stream;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author F6036477
 */
public class Storage {
  
  static final Logger logger = LoggerFactory.getLogger(Storage.class);
  
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
  
  public Storage set(User u) {
    Match.notNull(u).failIfNotMatch("Bad null User");
    users.stream()
        //.map(Indexed.mapper())
        //.filter(i->i.get().getEmail().equals(u.getEmail()))
        .filter(o->o.getEmail().equals(u.getEmail()))
        .findFirst()
        .ifPresent(users::remove);
    users.add(u);
    manager.store(u);
    manager.store(users);
    manager.store(this);
    return this;
  }
  
  public Storage set(Group g) {
    Match.notNull(g).failIfNotMatch("Bad null Group");
    groups.stream()
        //.map(Indexed.mapper())
        //.filter(i->i.get().getName().equals(g.getName()))
        .filter(o->o.getName().equals(g.getName()))
        .findFirst()
        .ifPresent(groups::remove);
    groups.add(g);
    manager.store(g);
    manager.store(groups);
    manager.store(this);
    return this;
  }
  
  public Storage set(Role r) {
    Match.notNull(r).failIfNotMatch("Bad null Role");
    roles.stream()
        //.map(Indexed.mapper())
        //.filter(i->i.get().route().equals(r.route()))
        .filter(o->o.route().equals(r.route()))
        .findFirst()
        .ifPresent(o->roles.remove(o));
    roles.add(r);
    manager.store(r);
    manager.store(roles);
    manager.store(this);
    return this;
  }
  
  public Storage rmUser(String email) {
    Match.notEmpty(email).failIfNotMatch("Bad null User email");
    users.stream()
        .filter(u->u.getEmail().equals(email))
        .forEach(users::remove);
    manager.store(users);
    manager.store(this);
    return this;
  }
  
  public Storage rmGroup(String name) {
    Match.notNull(name).failIfNotMatch("Bad null Group name");
    Optional<Group> opt = groups.stream()
        .filter(o->o.getName().equals(name))
        .findAny();
    if(opt.isPresent()) {
      groups.remove(opt.get());
      users.stream()
          .filter(u->u.getGroups().contains(opt.get()))
          .forEach(u->rmUserGroup(u, opt.get()));
    }
    manager.store(groups);
    manager.store(this);
    return this;
  }
  
  private void rmUserGroup(User u, Group g) {
    List<Group> gs = new LinkedList<>();
    u.getGroups().stream()
        .filter(o->!o.equals(g))
        .forEach(gs::add);
    u.setGroups(gs);
    set(u);
  }
  
  private void rmRoleGroup(Role r, Group g) {
    List<Group> gs = new LinkedList<>();
    r.getGroups().stream()
        .filter(o->!o.equals(g))
        .forEach(gs::add);
    r.setGroups(gs);
    set(r);
  }
  
  public Storage rm(Role r) {
    Match.notNull(r).failIfNotMatch("Bad null Role");
    roles.remove(r);
    manager.store(roles);
    manager.store(this);
    return this;
  }
  
  public Storage set(String key, Object obj) {
    Match.notEmpty(key).failIfNotMatch("Bad empty key String");
    Match.notNull(obj).failIfNotMatch("Bad empty value Object");
    map.put(key, obj);
    manager.store(obj);
    manager.store(map);
    manager.store(this);
    return this;
  }
  
  public <T> T get(String key) {
    return (T) map.get(key);
  }
  
  public <T> T rm(String key) {
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
