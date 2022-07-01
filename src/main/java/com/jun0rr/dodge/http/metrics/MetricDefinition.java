/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import com.jun0rr.util.match.Match;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public class MetricDefinition {
  
  private final String name;
  
  private final String help;
  
  public MetricDefinition(String name, String help) {
    this.name = Match.notEmpty(name).getOrFail("Bad empty name String");
    this.help = Match.notEmpty(help).getOrFail("Bad empty help String");
  }
  
  public String name() {
    return name;
  }
  
  public String help() {
    return help;
  }
  
  @Override
  public int hashCode() {
    int hash = 5;
    hash = 67 * hash + Objects.hashCode(this.name);
    hash = 67 * hash + Objects.hashCode(this.help);
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
    final MetricDefinition other = (MetricDefinition) obj;
    return Objects.equals(this.name, other.name)
        && Objects.equals(this.help, other.help);
  }

  @Override
  public String toString() {
    return "Metric{" + "name=" + name + ", help=" + help + '}';
  }
  
}
