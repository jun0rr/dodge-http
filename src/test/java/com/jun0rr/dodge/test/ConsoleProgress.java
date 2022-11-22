/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.test;

import java.util.function.LongUnaryOperator;

/**
 *
 * @author F6036477
 */
public class ConsoleProgress {
  
  public static final int DEFAULT_SIZE = 25;
  
  public static final char DEFAULT_PROGRESS_CHAR = '=';
  
  public static final char DEFAULT_START_CHAR = '[';
  
  public static final char DEFAULT_END_CHAR = ']';
  
  
  private final int size;
  
  private final char progressChar;
  
  private final char startChar;
  
  private final char endChar;
  
  private final boolean showPercent;
  
  private final double total;
  
  private volatile long current;
  
  
  public ConsoleProgress(long total, int size, boolean showPercent, char progressChar, char startChar, char endChar) {
    if(total <= 0) throw new IllegalArgumentException("Bad total <= 0: " + total);
    if(size - 2 <= 0) throw new IllegalArgumentException("Bad size: " + size);
    this.size = size - 2;
    this.total = Long.valueOf(total).doubleValue();
    this.showPercent = showPercent;
    this.progressChar = progressChar;
    this.startChar = startChar;
    this.endChar = endChar;
  }
  
  public ConsoleProgress(long total, int size) {
    this(total, size, true, DEFAULT_PROGRESS_CHAR, DEFAULT_START_CHAR, DEFAULT_END_CHAR);
  }
  
  public ConsoleProgress(long total) {
    this(total, DEFAULT_SIZE, true, DEFAULT_PROGRESS_CHAR, DEFAULT_START_CHAR, DEFAULT_END_CHAR);
  }

  public int getSize() {
    return size;
  }

  public char getProgressChar() {
    return progressChar;
  }

  public char getStartChar() {
    return startChar;
  }

  public char getEndChar() {
    return endChar;
  }

  public boolean isShowPercent() {
    return showPercent;
  }

  public long getTotal() {
    return Double.valueOf(total).longValue();
  }

  public long getCurrent() {
    return current;
  }
  
  public String update(LongUnaryOperator op) {
    this.current = op.applyAsLong(current);
    if(current > total) current = Double.valueOf(total).longValue();
    return getProgressBar();
  }
  
  public String increment(long inc) {
    this.current = current + inc > total ? (long)total : current + inc;
    return getProgressBar();
  }
  
  public String getProgressBar() {
    StringBuilder sb = new StringBuilder();
    sb.append(startChar);
    double cur = current / total;
    long prog = Math.round(size * cur);
    for(int i = 0; i < prog; i++) {
      sb.append(progressChar);
    }
    long blk = size - prog;
    for(int i = 0; i < blk; i++) {
      sb.append(" ");
    }
    sb.append(endChar);
    if(showPercent) {
      sb.append(formatPercent(cur));
    }
    return sb.toString();
  }
  
  private String formatPercent(double cur) {
    StringBuilder sb = new StringBuilder();
    String s = String.format("%.1f%%", (cur * 100));
    int max = 7;
    for(int i = 0; i < (max - s.length()); i++) {
      sb.append(" ");
    }
    return sb.append(s).toString();
  }
  
}
