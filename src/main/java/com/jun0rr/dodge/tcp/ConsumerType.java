/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.tcp;

import com.jun0rr.util.match.Match;
import java.util.Optional;
import java.util.function.Consumer;

/**
 *
 * @author F6036477
 */
public interface ConsumerType<T> extends Consumer<ChannelExchange<T>> {
  
  public Class<T> type();
  
  public default boolean isTypeOf(Class<T> cls) {
    return type().isAssignableFrom(cls);
  }
  
  public default Optional<T> cast(Object o) {
    try {
      return Optional.of(type().cast(o));
    }
    catch(ClassCastException e) {
      return Optional.empty();
    }
  }
  
  
  public static <U> ConsumerType of(Class<U> type, Consumer<ChannelExchange<U>> cons) {
    return new ConsumerTypeImpl(type, cons);
  }
  
  
  
  static class ConsumerTypeImpl<T> implements ConsumerType<T> {
    
    private final Class<T> type;
    
    private final Consumer<ChannelExchange<T>> handler;
    
    public ConsumerTypeImpl(Class<T> type, Consumer<ChannelExchange<T>> hnd) {
      this.type = Match.notNull(type).getOrFail("Bad null Class");
      this.handler = Match.notNull(hnd).getOrFail("Bad null ChannelExchangeHandler");
    }

    @Override
    public Class<T> type() {
      return type;
    }

    @Override
    public void accept(ChannelExchange<T> t) {
      handler.accept(t);
    }
    
  }
  
}
