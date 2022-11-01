/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.dodge.http.handler;

import static com.jun0rr.dodge.http.Http.DEFAULT_BUFFER_SIZE;
import com.jun0rr.dodge.http.header.Range;
import com.jun0rr.dodge.http.util.FileUtil;
import com.jun0rr.dodge.http.util.HttpConstants;
import com.jun0rr.dodge.tcp.ChannelExchange;
import com.jun0rr.util.Unchecked;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 *
 * @author F6036477
 */
public class FileUploadHandler implements Consumer<ChannelExchange<HttpObject>> {
  
  private final FileUtil file;
  
  public FileUploadHandler(Path p) {
    this(p, DEFAULT_BUFFER_SIZE);
  }
  
  public FileUploadHandler(Path file, int bufsize) {
    this.file = new FileUtil(file, bufsize);
  }
  
  @Override
  public void accept(ChannelExchange<HttpObject> x) {
    HttpRequest req = x.attributes().get(HttpRequest.class).get();
    Upload up = x.attributes().get(Upload.class).orElseGet(()->upload(x));
    if(HttpConstants.isValidHttpContent(x.message())) {
      ByteBuf content = ((HttpContent)x.message()).content();
      x.attributes().put(Upload.class, up.write(content));
      ReferenceCountUtil.safeRelease(content);
    }
    if(HttpConstants.isLastHttpContent(x.message())) {
      Unchecked.call(()->up.channel().close());
      x.attributes().remove(Upload.class);
    }
  }
  
  public Upload upload(ChannelExchange<HttpObject> x) {
    FileChannel fc = file.openWriteChannel();
    x.context().channel().closeFuture().addListener(f->Unchecked.call(()->fc.close()));
    return null; //new Upload(fc, Range.of(x.attributes().get(HttpRequest.class).get()));
  }
  
  
  public static class Upload {
    
    private final FileChannel channel;
    
    private final Range range;
    
    public Upload(FileChannel fc, Range ran) {
      this.channel = Objects.requireNonNull(fc);
      this.range = ran;
    }
    
    public FileChannel channel() {
      return this.channel;
    }
    
    public Range range() {
      return range;
    }
    
    public Upload range(UnaryOperator<Range> op) {
      return new Upload(channel, op.apply(range));
    }
    
    public Upload write(ByteBuf buf) {
      return range(r->r.incrementStart(Unchecked.call(()->
          buf.readBytes(channel, r.start(), buf.readableBytes())))
      );
    }
    
  }
  
}
