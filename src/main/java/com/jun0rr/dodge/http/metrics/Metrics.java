/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.jun0rr.dodge.http.metrics;

import io.netty.buffer.PooledByteBufAllocatorMetric;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author F6036477
 */
public interface Metrics {
  
  public static final Metrics INSTANCE = new Metrics() {
    private final Map<String,ConnectionMetrics> conns = new ConcurrentHashMap();
    private final Instant startup = Instant.now();
    private final AtomicLong closeCount = new AtomicLong(0L);
    private final AtomicLong downloadCount = new AtomicLong(0L);
    private final AtomicLong uploadCount = new AtomicLong(0L);
    public Map<String,ConnectionMetrics> connections() {
      return conns;
    }
    public Instant startup() {
      return startup;
    }
    public AtomicLong httpCloseRequestCount() {
      return closeCount;
    }
    public AtomicLong httpDownloadRequestCount() {
      return downloadCount;
    }
    public AtomicLong httpUploadRequestCount() {
      return uploadCount;
    }
  };
  
  public Instant startup();
  
  public AtomicLong httpCloseRequestCount();
  
  public AtomicLong httpDownloadRequestCount();
  
  public AtomicLong httpUploadRequestCount();
  
  public Map<String,ConnectionMetrics> connections();
  
  public default Duration uptime() {
    return Duration.between(startup(), Instant.now());
  }
  
  public default Set<ConnectionMetrics> connectionSet() {
    return connections().values().stream().collect(Collectors.toSet());
  }
  
  public default long totalInboundBytes() {
    return connectionSet().stream()
        .mapToLong(c->c.inboundBytes().get())
        .sum();
  }
  
  public default long totalOutboundBytes() {
    return connectionSet().stream()
        .mapToLong(c->c.outboundBytes().get())
        .sum();
  }
  
  public default long totalInboundExchangeBytes() {
    return connectionSet().stream()
        .mapToLong(c->c.inboundExchangeBytes().get())
        .sum();
  }
  
  public default long totalOutboundExchangeBytes() {
    return connectionSet().stream()
        .mapToLong(c->c.outboundExchangeBytes().get())
        .sum();
  }
  
  public default void ifConnectionPresent(String id, Consumer<ConnectionMetrics> cs) {
    ConnectionMetrics cm = connections().get(id);
    if(cm != null) cs.accept(cm);
  }
  
  public default String toMetricsText(PooledByteBufAllocatorMetric pbm) {
    StringBuilder sb = new StringBuilder();
    Instant now = Instant.now();
    sb.append(HELP_DODGE_STARTUP_TIME).append(LF)
        .append(TYPE_DODGE_STARTUP_TIME).append(LF)
        .append(String.format("%s %d%n%n", NAME_DODGE_STARTUP_TIME, startup().toEpochMilli()))
        
        .append(HELP_DODGE_CHANNEL_UPTIME).append(LF)
        .append(TYPE_DODGE_CHANNEL_UPTIME).append(LF)
        .append(String.format("%s %d%n%n", NAME_DODGE_CHANNEL_UPTIME, uptime().toMillis()))
        
        .append(HELP_DODGE_CHANNEL_COUNT).append(LF)
        .append(TYPE_DODGE_CHANNEL_COUNT).append(LF)
        .append(String.format("%s %d%n%n", NAME_DODGE_CHANNEL_COUNT, connectionSet().stream().filter(c->!c.isClosed()).count()))
        
        //.append(HELP_DODGE_MASTER_THREADS).append(LF)
        //.append(TYPE_DODGE_MASTER_THREADS).append(LF)
        //.append(String.format("%s %d%n%n", NAME_DODGE_MASTER_THREADS, cfg.getMasterThreads()))
        
        //.append(HELP_DODGE_WORKER_THREADS).append(LF)
        //.append(TYPE_DODGE_WORKER_THREADS).append(LF)
        //.append(String.format("%s %d%n%n", NAME_DODGE_WORKER_THREADS, cfg.getWorkerThreads()))
        
        .append(HELP_DODGE_HEAP_MEMORY).append(LF)
        .append(TYPE_DODGE_HEAP_MEMORY).append(LF)
        .append(String.format("%s %d%n%n", NAME_DODGE_HEAP_MEMORY, pbm.usedHeapMemory()))
        
        .append(HELP_DODGE_DIRECT_MEMORY).append(LF)
        .append(TYPE_DODGE_DIRECT_MEMORY).append(LF)
        .append(String.format("%s %d%n%n", NAME_DODGE_DIRECT_MEMORY, pbm.usedDirectMemory()))
        
        .append(HELP_HTTP_CLOSE_REQUEST_COUNT).append(LF)
        .append(TYPE_HTTP_CLOSE_REQUEST_COUNT).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_HTTP_CLOSE_REQUEST_COUNT, httpCloseRequestCount().get(), now.toEpochMilli()))
        
        .append(HELP_HTTP_DOWNLOAD_REQUEST_COUNT).append(LF)
        .append(TYPE_HTTP_DOWNLOAD_REQUEST_COUNT).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_HTTP_DOWNLOAD_REQUEST_COUNT, httpDownloadRequestCount().get(), now.toEpochMilli()))
        
        .append(HELP_HTTP_UPLOAD_REQUEST_COUNT).append(LF)
        .append(TYPE_HTTP_UPLOAD_REQUEST_COUNT).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_HTTP_UPLOAD_REQUEST_COUNT, httpUploadRequestCount().get(), now.toEpochMilli()))
        
        .append(HELP_INBOUND_BYTES_TOTAL).append(LF)
        .append(TYPE_INBOUND_BYTES_TOTAL).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_INBOUND_BYTES_TOTAL, totalInboundBytes(), now.toEpochMilli()))
        
        .append(HELP_OUTBOUND_BYTES_TOTAL).append(LF)
        .append(TYPE_OUTBOUND_BYTES_TOTAL).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_OUTBOUND_BYTES_TOTAL, totalOutboundBytes(), now.toEpochMilli()))
        
        .append(HELP_INBOUND_EXCHANGE_BYTES_TOTAL).append(LF)
        .append(TYPE_INBOUND_EXCHANGE_BYTES_TOTAL).append(LF)
        .append(String.format("%s %d %d%n%n", NAME_INBOUND_EXCHANGE_BYTES_TOTAL, totalInboundExchangeBytes(), now.toEpochMilli()))
        
        .append(HELP_OUTBOUND_EXCHANGE_BYTES_TOTAL).append(LF)
        .append(TYPE_OUTBOUND_EXCHANGE_BYTES_TOTAL).append(LF)
        .append(String.format("%s %d %d%n", NAME_OUTBOUND_EXCHANGE_BYTES_TOTAL, totalOutboundExchangeBytes(), now.toEpochMilli()));
        
    if(!connectionSet().isEmpty()) {
      //dodge_channel_uptime
      sb.append(LF)
          .append(HELP_DODGE_CHANNEL_UPTIME).append(LF)
          .append(TYPE_DODGE_CHANNEL_UPTIME).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_DODGE_CHANNEL_UPTIME, c.id(), c.target(), c.isClosed(), c.uptime().toMillis())
          ).forEach(sb::append);

      //channel_inbound_bytes
      sb.append(LF)
          .append(HELP_INBOUND_BYTES).append(LF)
          .append(TYPE_INBOUND_BYTES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_INBOUND_BYTES, c.id(), c.target(), c.isClosed(), c.inboundBytes().get())
          ).forEach(sb::append);

      //channel_outbound_bytes
      sb.append(LF)
          .append(HELP_OUTBOUND_BYTES).append(LF)
          .append(TYPE_OUTBOUND_BYTES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_OUTBOUND_BYTES, c.id(), c.target(), c.isClosed(), c.outboundBytes().get())
          ).forEach(sb::append);

      //channel_inbound_messages
      sb.append(LF)
          .append(HELP_INBOUND_MESSAGES).append(LF)
          .append(TYPE_INBOUND_MESSAGES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_INBOUND_MESSAGES, c.id(), c.target(), c.isClosed(), c.inboundMessages().get())
          ).forEach(sb::append);

      //channel_outbound_messages
      sb.append(LF)
          .append(HELP_OUTBOUND_MESSAGES).append(LF)
          .append(TYPE_OUTBOUND_MESSAGES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_OUTBOUND_MESSAGES, c.id(), c.target(), c.isClosed(), c.outboundMessages().get())
          ).forEach(sb::append);

      //channel_inbound_bps
      sb.append(LF)
          .append(HELP_INBOUND_BPS).append(LF)
          .append(TYPE_INBOUND_BPS).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %f%n", 
              NAME_INBOUND_BPS, c.id(), c.target(), c.isClosed(), c.inboundBPS())
          ).forEach(sb::append);

      //channel_outbound_bps
      sb.append(LF)
          .append(HELP_OUTBOUND_BPS).append(LF)
          .append(TYPE_OUTBOUND_BPS).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %f%n", 
              NAME_OUTBOUND_BPS, c.id(), c.target(), c.isClosed(), c.outboundBPS())
          ).forEach(sb::append);

      //channel_inbound_exchange_bytes
      sb.append(LF)
          .append(HELP_INBOUND_EXCHANGE_BYTES).append(LF)
          .append(TYPE_INBOUND_EXCHANGE_BYTES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_INBOUND_EXCHANGE_BYTES, c.id(), c.target(), c.isClosed(), c.inboundExchangeBytes().get())
          ).forEach(sb::append);

      //channel_outbound_exchange_bytes
      sb.append(LF)
          .append(HELP_OUTBOUND_EXCHANGE_BYTES).append(LF)
          .append(TYPE_OUTBOUND_EXCHANGE_BYTES).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %d%n", 
              NAME_OUTBOUND_EXCHANGE_BYTES, c.id(), c.target(), c.isClosed(), c.outboundExchangeBytes().get())
          ).forEach(sb::append);

      //channel_inbound_exchange_bps
      sb.append(LF)
          .append(HELP_INBOUND_EXCHANGE_BPS).append(LF)
          .append(TYPE_INBOUND_EXCHANGE_BPS).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %f%n", 
              NAME_INBOUND_EXCHANGE_BPS, c.id(), c.target(), c.isClosed(), c.inboundExchangeBPS())
          ).forEach(sb::append);

      //channel_inbound_exchange_bps
      sb.append(LF)
          .append(HELP_OUTBOUND_EXCHANGE_BPS).append(LF)
          .append(TYPE_OUTBOUND_EXCHANGE_BPS).append(LF);
      connectionSet().stream()
          .map(c->String.format("%s{id=\"%s\", target=\"%s\", closed=\"%s\"} %f%n", 
              NAME_OUTBOUND_EXCHANGE_BPS, c.id(), c.target(), c.isClosed(), c.outboundExchangeBPS())
          ).forEach(sb::append);
    }
    return sb.toString();
  }
  
  
  public static final String LF = "\n";
  
  public static final String HELP_DODGE_STARTUP_TIME = "# HELP dodge_startup_time Startup timestamp";
  
  public static final String TYPE_DODGE_STARTUP_TIME = "# TYPE dodge_startup_time gauge";
  
  public static final String NAME_DODGE_STARTUP_TIME = "dodge_startup_time";
  
  
  public static final String HELP_DODGE_CHANNEL_UPTIME = "# HELP dodge_channel_uptime Channel uptime milliseconds";
  
  public static final String TYPE_DODGE_CHANNEL_UPTIME = "# TYPE dodge_channel_uptime counter";
  
  public static final String NAME_DODGE_CHANNEL_UPTIME = "dodge_channel_uptime";
  
  
  public static final String HELP_DODGE_MASTER_THREADS = "# HELP dodge_master_threads Master channel thread pool size";
  
  public static final String TYPE_DODGE_MASTER_THREADS = "# TYPE dodge_master_threads counter";
  
  public static final String NAME_DODGE_MASTER_THREADS = "dodge_master_threads";
  
  
  public static final String HELP_DODGE_WORKER_THREADS = "# HELP dodge_worker_threads Worker channel thread pool size";
  
  public static final String TYPE_DODGE_WORKER_THREADS = "# TYPE dodge_worker_threads counter";
  
  public static final String NAME_DODGE_WORKER_THREADS = "dodge_worker_threads";
  
  
  public static final String HELP_DODGE_CHANNEL_COUNT = "# HELP dodge_channel_count Channels count";
  
  public static final String TYPE_DODGE_CHANNEL_COUNT = "# TYPE dodge_channel_count counter";
  
  public static final String NAME_DODGE_CHANNEL_COUNT = "dodge_channel_count";
  
  
  public static final String HELP_DODGE_HEAP_MEMORY = "# HELP dodge_heap_memory Used heap memory bytes";
  
  public static final String TYPE_DODGE_HEAP_MEMORY = "# TYPE dodge_heap_memory gauge";
  
  public static final String NAME_DODGE_HEAP_MEMORY = "dodge_heap_memory";
  
  public static final String HELP_DODGE_DIRECT_MEMORY = "# HELP dodge_direct_memory Used direct (Off-Heap) memory bytes";
  
  public static final String TYPE_DODGE_DIRECT_MEMORY = "# TYPE dodge_direct_memory gauge";
  
  public static final String NAME_DODGE_DIRECT_MEMORY = "dodge_direct_memory";
  
  
  public static final String HELP_HTTP_CLOSE_REQUEST_COUNT = "# HELP http_close_request_count Count of Http Close Requests sent/received";
  
  public static final String TYPE_HTTP_CLOSE_REQUEST_COUNT = "# TYPE http_close_request_count counter";
  
  public static final String NAME_HTTP_CLOSE_REQUEST_COUNT = "http_close_request_count";
  
  public static final String HELP_HTTP_DOWNLOAD_REQUEST_COUNT = "# HELP http_download_request_count Count of Http Download Requests sent/received";
  
  public static final String TYPE_HTTP_DOWNLOAD_REQUEST_COUNT = "# TYPE http_download_request_count counter";
  
  public static final String NAME_HTTP_DOWNLOAD_REQUEST_COUNT = "http_download_request_count";
  
  public static final String HELP_HTTP_UPLOAD_REQUEST_COUNT = "# HELP http_upload_request_count Count of Http Upload Requests sent/received";
  
  public static final String TYPE_HTTP_UPLOAD_REQUEST_COUNT = "# TYPE http_upload_request_count counter";
  
  public static final String NAME_HTTP_UPLOAD_REQUEST_COUNT = "http_upload_request_count";
  
  
  public static final String HELP_INBOUND_BYTES_TOTAL = "# HELP channel_inbound_bytes_total Channel inbound bytes count";
  
  public static final String TYPE_INBOUND_BYTES_TOTAL = "# TYPE channel_inbound_bytes_total counter";
  
  public static final String NAME_INBOUND_BYTES_TOTAL = "channel_inbound_bytes_total";
  
  public static final String HELP_OUTBOUND_BYTES_TOTAL = "# HELP channel_outbound_bytes_total Channel outbound bytes count";
  
  public static final String TYPE_OUTBOUND_BYTES_TOTAL = "# TYPE channel_outbound_bytes_total counter";
  
  public static final String NAME_OUTBOUND_BYTES_TOTAL = "channel_outbound_bytes_total";
  
  
  public static final String HELP_INBOUND_EXCHANGE_BYTES_TOTAL = "# HELP channel_inbound_exchange_bytes_total Count of inbound bytes exchanged between Dodge client/server";
  
  public static final String TYPE_INBOUND_EXCHANGE_BYTES_TOTAL = "# TYPE channel_inbound_exchange_bytes_total counter";
  
  public static final String NAME_INBOUND_EXCHANGE_BYTES_TOTAL = "channel_inbound_exchange_bytes_total";
  
  public static final String HELP_OUTBOUND_EXCHANGE_BYTES_TOTAL = "# HELP channel_outbound_exchange_bytes_total Count of outbound bytes exchanged between Dodge client/server";
  
  public static final String TYPE_OUTBOUND_EXCHANGE_BYTES_TOTAL = "# TYPE channel_outbound_exchange_bytes_total counter";
  
  public static final String NAME_OUTBOUND_EXCHANGE_BYTES_TOTAL = "channel_outbound_exchange_bytes_total";

  
  public static final String HELP_INBOUND_BYTES = "# HELP channel_inbound_bytes Channel inbound bytes count";
  
  public static final String TYPE_INBOUND_BYTES = "# TYPE channel_inbound_bytes counter";
  
  public static final String NAME_INBOUND_BYTES = "channel_inbound_bytes";
  
  public static final String HELP_OUTBOUND_BYTES = "# HELP channel_outbound_bytes Channel outbound bytes count";
  
  public static final String TYPE_OUTBOUND_BYTES = "# TYPE channel_outbound_bytes counter";
  
  public static final String NAME_OUTBOUND_BYTES = "channel_outbound_bytes";
  
  
  public static final String HELP_INBOUND_MESSAGES = "# HELP channel_inbound_messages Channel inbound messages count";
  
  public static final String TYPE_INBOUND_MESSAGES = "# TYPE channel_inbound_messages counter";
  
  public static final String NAME_INBOUND_MESSAGES = "channel_inbound_messages";
  
  public static final String HELP_OUTBOUND_MESSAGES = "# HELP channel_outbound_messages Channel outbound messages count";
  
  public static final String TYPE_OUTBOUND_MESSAGES = "# TYPE channel_outbound_messages counter";
  
  public static final String NAME_OUTBOUND_MESSAGES = "channel_outbound_messages";
  
  
  public static final String HELP_INBOUND_BPS = "# HELP channel_inbound_bps Channel inbound traffic bps (bytes per second)";
  
  public static final String TYPE_INBOUND_BPS = "# TYPE channel_inbound_bps gauge";
  
  public static final String NAME_INBOUND_BPS = "channel_inbound_bps";
  
  public static final String HELP_OUTBOUND_BPS = "# HELP channel_outbound_bps Channel outbound traffic bps (bytes per second)";
  
  public static final String TYPE_OUTBOUND_BPS = "# TYPE channel_outbound_bps gauge";
  
  public static final String NAME_OUTBOUND_BPS = "channel_outbound_bps";
  
  
  public static final String HELP_INBOUND_EXCHANGE_BYTES = "# HELP channel_inbound_exchange_bytes Count of inbound bytes exchanged between Dodge client/server";
  
  public static final String TYPE_INBOUND_EXCHANGE_BYTES = "# TYPE channel_inbound_exchange_bytes counter";
  
  public static final String NAME_INBOUND_EXCHANGE_BYTES = "channel_inbound_exchange_bytes";
  
  public static final String HELP_OUTBOUND_EXCHANGE_BYTES = "# HELP channel_outbound_exchange_bytes Count of outbound bytes exchanged between Dodge client/server";
  
  public static final String TYPE_OUTBOUND_EXCHANGE_BYTES = "# TYPE channel_outbound_exchange_bytes counter";
  
  public static final String NAME_OUTBOUND_EXCHANGE_BYTES = "channel_outbound_exchange_bytes";
  
  
  public static final String HELP_INBOUND_EXCHANGE_BPS = "# HELP channel_inbound_exchange_bps Channel inbound exchanged traffic bps (bytes per second)";
  
  public static final String TYPE_INBOUND_EXCHANGE_BPS = "# TYPE channel_inbound_exchange_bps gauge";
  
  public static final String NAME_INBOUND_EXCHANGE_BPS = "channel_inbound_exchange_bps";
  
  public static final String HELP_OUTBOUND_EXCHANGE_BPS = "# HELP channel_outbound_exchange_bps Channel outbound exchange traffic bps (bytes per second)";
  
  public static final String TYPE_OUTBOUND_EXCHANGE_BPS = "# TYPE channel_outbound_exchange_bps gauge";
  
  public static final String NAME_OUTBOUND_EXCHANGE_BPS = "channel_outbound_exchange_bps";
  
}
