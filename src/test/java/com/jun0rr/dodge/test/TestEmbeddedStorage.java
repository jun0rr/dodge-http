/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import com.jun0rr.dodge.http.Http;
import com.jun0rr.dodge.http.auth.Persistence;
import com.jun0rr.util.ResourceLoader;
import java.nio.file.Path;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.Test;

/**
 *
 * @author F6036477
 */
public class TestEmbeddedStorage {
  
  public static final Path storage = ResourceLoader.of(Http.class).loadPath("storage");
  
  @Test
  public void testEmbeddedStorage() {
    EmbeddedStorageManager storage = EmbeddedStorage.start(new Persistence(), storage);
  }
  
}
