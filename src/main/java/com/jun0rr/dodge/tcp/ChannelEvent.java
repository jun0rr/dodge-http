/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.tcp;

/**
 *
 * @author F6036477
 */
public interface ChannelEvent {
  
  public static enum Inbound implements ChannelEvent {
    ACTIVE, INACTIVE, REGISTERED, UNREGISTERED, READ, READ_COMPLETE, USER_EVENT, WRITABILITY_CHANGED, EXCEPTION, HANDLER_ADDED, HANDLER_REMOVED;
  }
  
  public static enum Outbound implements ChannelEvent {
    BIND, CONNECT, DISCONNECT, CLOSE, DEREGISTER, WRITE, READ, FLUSH;
  }
  
  public default boolean isInboundEvent() {
    return Inbound.class.isAssignableFrom(this.getClass());
  }
  
  public default boolean isOutboundEvent() {
    return Inbound.class.isAssignableFrom(this.getClass());
  }
  
  public default Inbound asInboundEvent() {
    return (Inbound) this;
  }
  
  public default Outbound asOutboundEvent() {
    return (Outbound) this;
  }
  
}
