/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.auth;

import com.jun0rr.util.match.Match;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
  
  public static final Group GROUP_AUTH = new Group("auth");
  
  public static final Group GROUP_ADMIN = new Group("admin");
  
  public static final User USER_ADMIN = User.create("admin", "admin@dodgehttp.com", "admin".toCharArray())
      .setGroups(List.of(GROUP_AUTH, GROUP_ADMIN));
  
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
    this.set(GROUP_AUTH).set(GROUP_ADMIN).set(USER_ADMIN);
  }
  
  public Storage(Path path) {
    this(path, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>(), new ConcurrentHashMap<>());
  }
  
  public Storage set(User u) {
    Match.notNull(u).failIfNotMatch("Bad null User");
    //same object reference check
    if(!users.contains(u)) {
      users.stream()
          .filter(o->o.getEmail().equals(u.getEmail()))
          .findFirst()
          .ifPresent(users::remove);
      users.add(u);
    }
    manager.store(u);
    manager.store(users);
    manager.store(this);
    if(!USER_ADMIN.equals(u) && u.getGroups().contains(GROUP_ADMIN)) {
      rmUser(USER_ADMIN.getEmail());
    }
    return this;
  }
  
  public Storage set(Group g) {
    Match.notNull(g).failIfNotMatch("Bad null Group");
    //same object reference check
    if(!groups.contains(g)) {
      groups.stream()
          .filter(o->o.getName().equals(g.getName()))
          .findFirst()
          .ifPresent(groups::remove);
      groups.add(g);
    }
    manager.store(g);
    manager.store(groups);
    manager.store(this);
    return this;
  }
  
  public Storage set(Role r) {
    Match.notNull(r).failIfNotMatch("Bad null Role");
    //same object reference check
    if(!roles.contains(r)) {
      roles.add(r);
    }
    manager.store(r);
    manager.store(roles);
    manager.store(this);
    return this;
  }
  
  public boolean rmUser(String email) {
    Match.notEmpty(email).failIfNotMatch("Bad null User email");
    boolean ok = users.stream()
        .filter(u->u.getEmail().equals(email))
        .findAny()
        .map(users::remove)
        .orElse(Boolean.FALSE);
    manager.store(users);
    manager.store(this);
    return ok;
  }
  
  public boolean rmGroup(String name) {
    Match.notNull(name).failIfNotMatch("Bad null Group name");
    Optional<Group> opt = groups.stream()
        .filter(o->o.getName().equals(name))
        .findAny();
    boolean ok = false;
    if(opt.isPresent()) {
      ok = groups.remove(opt.get());
      users.stream()
          .filter(u->u.getGroups().contains(opt.get()))
          .forEach(u->rmUserGroup(u, opt.get()));
      roles.stream()
          .filter(r->r.groups().contains(opt.get()))
          .forEach(r->rmRoleGroup(r, opt.get()));
    }
    manager.store(groups);
    manager.store(this);
    return ok;
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
    r.groups().stream()
        .filter(o->!o.equals(g))
        .forEach(gs::add);
    r.setGroups(gs);
    set(r);
  }
  
  public boolean rm(Role r) {
    Match.notNull(r).failIfNotMatch("Bad null Role");
    boolean ok = roles.remove(r);
    manager.store(roles);
    manager.store(this);
    return ok;
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
