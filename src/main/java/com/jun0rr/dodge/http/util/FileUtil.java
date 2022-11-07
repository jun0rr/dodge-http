/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.util;

import static com.jun0rr.dodge.http.handler.FileDownloadHandler.WEB_DATE_FORMAT;
import com.jun0rr.dodge.http.header.ContentRangeHeader;
import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.header.RangeHeader;
import com.jun0rr.util.Unchecked;
import com.jun0rr.util.crypto.Hash;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 *
 * @author F6036477
 */
public class FileUtil {
  
  public static final Predicate<String> WEB_DATE_PATTERN = Pattern.compile(
      "[a-zA-Z]{3}, [0-9]{2} [a-zA-Z]{3} [0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2} GMT"
  ).asMatchPredicate();
  
  public static final int DEFAULT_BUFFER_SIZE = 4096;
  
  
  private final Path file;
  
  private final int bufferSize;
  
  private LocalDateTime lastModified;
  
  private String etag;
  
  private Long size;
  
  private Boolean exists;
  
  public FileUtil(Path file, int bufsize) {
    this.file = Objects.requireNonNull(file);
    this.bufferSize = bufsize > 0 ? bufsize : DEFAULT_BUFFER_SIZE;
  }
  
  public FileUtil(Path file) {
    this(file, DEFAULT_BUFFER_SIZE);
  }
  
  public Path getFilePath() {
    return file;
  }
  
  public String getFileName() {
    return file.getFileName().toString();
  }
  
  public int getBufferSize() {
    return bufferSize;
  }
  
  public long getSize() {
    if(size == null) {
      this.size = Unchecked.call(()->Files.size(file));
    }
    return size;
  }
  
  public boolean isFileExists() {
    if(exists == null) {
      this.exists = Unchecked.call(()->Files.exists(file));
    }
    return exists;
  }
  
  public FileChannel openReadChannel() {
    return Unchecked.call(()->FileChannel.open(file, StandardOpenOption.READ));
  }
  
  public FileChannel openWriteChannel() {
    return Unchecked.call(()->FileChannel.open(file, StandardOpenOption.CREATE, StandardOpenOption.WRITE));
  }
  
  public boolean isNotModified(HttpRequest req) throws IOException {
    LocalDateTime modifiedSince = HttpConstants.getDateHeader(req.headers(), HttpHeaderNames.IF_MODIFIED_SINCE);
    return req.headers().contains(HttpHeaderNames.IF_NONE_MATCH, getEtag(), true)
        || modifiedSince != null 
        && !getLastModified().isAfter(modifiedSince);
  }
  
  public boolean isPreconditionFailed(HttpRequest req) {
    LocalDateTime unmodifiedSince = HttpConstants.getDateHeader(req.headers(), HttpHeaderNames.IF_UNMODIFIED_SINCE);
    LocalDateTime lastModified = getLastModified();
    String ifMatch = req.headers().get(HttpHeaderNames.IF_MATCH);
    String ifRange = req.headers().get(HttpHeaderNames.IF_RANGE);
    String weakEtag = getWeakEtag();
    return (ifMatch != null && !ifMatch.equals(weakEtag))
        || (unmodifiedSince != null && lastModified.isAfter(unmodifiedSince))
        || (ifRange != null 
        && (WEB_DATE_PATTERN.test(ifRange) 
        && lastModified.isAfter(HttpConstants.getDateHeader(req.headers(), HttpHeaderNames.IF_RANGE)) 
        || !ifRange.equals(weakEtag)));
  }
  
  public Range getRange(HttpRequest req) throws IOException {
    RangeHeader hdr = RangeHeader.parse(req.headers());
    Range r = hdr.ranges().isEmpty() 
        ? new Range(0, Files.size(file)) 
        : hdr.ranges().get(0).total(Files.size(file));
    String etag = getEtag();
    LocalDateTime lastModified = getLastModified();
    String ifrange = req.headers().get(HttpHeaderNames.IF_RANGE);
    return ifrange == null || ((WEB_DATE_PATTERN.test(ifrange) 
        && !lastModified.isAfter(HttpConstants.getDateHeader(req.headers(), HttpHeaderNames.IF_RANGE)))
        || ifrange.equals(etag))
        ? r : new Range(0, r.total(), r.total());
  }
  
  public String getWeakEtag(Range r) {
    return getWeakEtag(r, LocalDateTime.now());
  }
  
  public String getWeakEtag(Range r, LocalDateTime lastModified) {
    Objects.requireNonNull(r);
    Objects.requireNonNull(lastModified);
    ByteBuffer buf = ByteBuffer.allocate(getFileName().length() + Long.BYTES * 2);
    buf.put(getFileName().getBytes(StandardCharsets.UTF_8));
    buf.putLong(r.total());
    buf.putLong(lastModified.toEpochSecond(ZoneOffset.UTC));
    return String.format("W/\"%s\"", Hash.sha1().of(buf.flip()));
  }
  
  public String getWeakEtag() {
    ByteBuffer buf = ByteBuffer.allocate(getFileName().length() + Long.BYTES * 2);
    buf.put(getFileName().getBytes(StandardCharsets.UTF_8));
    buf.putLong(getSize());
    buf.putLong(getLastModified().toEpochSecond(ZoneOffset.UTC));
    return String.format("W/\"%s\"", Hash.sha1().of(buf.flip()));
  }
  
  public String getEtag() throws IOException {
    if(etag == null) {
      Hash hash = Hash.sha1();
      ByteBuffer buf = ByteBuffer.allocateDirect(4096);
      try (FileChannel fc = openReadChannel()) {
        long total = 0;
        while(total < getSize()) {
          buf.limit(buf.position() + Math.min(buf.remaining(), (int)(getSize() - total)));
          int read = fc.read(buf);
          if(read == -1) break;
          total += read;
          hash.put(buf.flip());
          buf.compact();
        }
        etag = String.format("\"%s\"", hash.put(
            buf.clear().putLong(total).flip()
        ).get());
      }
    }
    return etag;
  }
  
  public LocalDateTime getLastModified() {
    if(lastModified == null) {
      LocalDateTime date = Unchecked.call(()->LocalDateTime.ofInstant(Files.getFileAttributeView(file, BasicFileAttributeView.class)
          .readAttributes().lastModifiedTime()
          .toInstant(), ZoneOffset.UTC));
      lastModified = date.minusNanos(date.getNano());
    }
    return lastModified;
  }
  
}
