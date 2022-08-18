/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/module-info.java to edit this template
 */

module jun0rr.dodge {
  requires jun0rr.util;
  requires gson;
  requires io.netty.all;
  //requires mapdb;
  requires jjwt.api;
  requires org.slf4j;
  requires info.picocli;
  //requires logback.core;
  requires logback.classic;
  requires microstream.storage;
  requires microstream.storage.embedded;
  exports com.jun0rr.dodge.http.auth;
}
