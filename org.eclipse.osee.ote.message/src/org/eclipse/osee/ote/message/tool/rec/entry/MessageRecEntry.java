/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.tool.rec.entry;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;
import org.eclipse.osee.ote.message.tool.rec.ElementEntryFactory;
import org.eclipse.osee.ote.message.tool.rec.MessageRecorder;
import org.eclipse.osee.ote.message.tool.rec.RecUtil;

public class MessageRecEntry<T extends Message<?, ?, T>> implements IMessageEntry, IOSEEMessageListener {
   protected static final byte COMMA = ',';
   protected static final byte EQUALS = '=';
   protected static final byte NL = '\n';
   protected static final byte[] BODY_HEX_DUMP_STR = "BODY HEX DUMP".getBytes();
   protected static final byte[] HEADER_HEX_DUMP_STR = "HEADER HEX DUMP".getBytes();
   protected final Benchmark bm;
   protected final Benchmark pbm;
   private final T msg;
   private final byte[] nameAsBytes;
   private final byte[] headerTypeAsBytes;
   private static final int MAX_BUFFERS = 2;
   private final ArrayBlockingQueue<ByteBuffer> buffers = new ArrayBlockingQueue<ByteBuffer>(MAX_BUFFERS);
   private ByteBuffer currentBuffer;
   private final int lowCapacitiyLevel;
   private final IElementEntry[] headerEntries;
   private final IElementEntry[] bodyEntries;
   private volatile boolean enabled = false;
   private int longestLength = 0;
   private final MessageRecorder recorder;
   private final MemType type;
   private final int headerSize;
   private final HashMap<MessageData, Byte> dataMap = new HashMap<MessageData, Byte>(16);
   private boolean bodyDump;
   private boolean headerDump;

   public MessageRecEntry(T msg, MemType type, boolean headerDump, Element[] hdrElements, boolean bodyDump, Element[] bdyElements, MessageRecorder recorder) {
      this.msg = msg;
      this.type = type;
      this.recorder = recorder;
      this.bodyDump = bodyDump;
      this.headerDump = headerDump;
      bm = new Benchmark("rec time for " + msg.getName(), 0300);
      pbm = new Benchmark("post process for " + msg.getName(), 10000);
      headerEntries = new IElementEntry[hdrElements.length];
      int i = 0;

      for (MessageData data : msg.getMessageData(type)) {
         dataMap.put(data, (byte) i);
         i++;
      }

      i = 0;
      for (Element element : hdrElements) {
         headerEntries[i] = ElementEntryFactory.createEntry(element);
         i++;
      }
      bodyEntries = new IElementEntry[bdyElements.length];

      i = 0;
      for (Element element : bdyElements) {
         if (element.getMsgData().getType() != type) {
            throw new IllegalArgumentException(String.format("all elements(%s) must have a mem type of %s",
                  element.getName(), type.toString()));
         }
         bodyEntries[i] = ElementEntryFactory.createEntry(element);
         i++;
      }
      Arrays.sort(bodyEntries, new Comparator<IElementEntry>() {

         public int compare(IElementEntry o1, IElementEntry o2) {
            Element element1 = o1.getElement();
            Element element2 = o2.getElement();
            if (element1.getByteOffset() < element2.getByteOffset()) {
               return -1;
            } else if (element1.getByteOffset() == element2.getByteOffset()) {
               if (element1.getMsb() < element2.getMsb()) {
                  return -1;
               } else if (element1.getMsb() > element2.getMsb()) {
                  return 1;
               } else if (element1.getLsb() < element2.getLsb()) {
                  return -1;
               } else if (element1.getLsb() > element2.getLsb()) {
                  return 1;
               }
               return element1.getName().compareTo(element2.getName());

            } else {
               return 1;
            }
         }

      });

      // make the buffer big enough for about 2 seconds worth of message transmissions
      //int size = (int) ((float)msg.getMaxDataSize() * msg.getRate()) * 3;

      int size = 16384;
      // if the buffer has capacity left for one more message then we should send it off to the file
      lowCapacitiyLevel = 512;

      for (i = 0; i < MAX_BUFFERS - 1; i++) {
         buffers.add(ByteBuffer.allocate(size));
      }
      currentBuffer = ByteBuffer.allocate(size);

      nameAsBytes = msg.getName().getBytes();
      IMessageHeader header = msg.getActiveDataSource(type).getMsgHeader();
      headerSize = header.getHeaderSize();
      String hName = header.getClass().getCanonicalName();
      hName = hName.substring(hName.lastIndexOf('.') + 1);
      headerTypeAsBytes = hName.getBytes();
   }

   public T getMessage() {
      return msg;
   }

   public synchronized void enable(boolean enable) {
      T msg = getMessage();
      if (enable) {
         enabled = true;
         msg.addListener(this);
      } else {
         enabled = false;
         msg.removeListener(this);
         finishUp();
      }
   }

   private void finishUp() {
      if (currentBuffer.position() > 0) {
            try {
               postProcess(currentBuffer);
            } catch (Exception ex) {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.SEVERE, "failed to write remaining contents of buffer for message " + msg.getName(), ex);
            }
         }
      }

   protected final int getLowCapacitiyLevel() {
      return lowCapacitiyLevel;
   }

   protected final byte[] getNameAsBytes() {
      return nameAsBytes;
   }

   public void onInitListener() throws MessageSystemException {
      // do nothing
   }

   public synchronized void onDataAvailable(MessageData data, MemType type) throws MessageSystemException {
      if (this.type == type && enabled) {
         bm.startSample();
         final int length = data.getCurrentLength();
         if (length > longestLength) {
            longestLength = length;
         }
         if (currentBuffer.remaining() < (length + 13)) {
            try {
               // hand off for post processing
               handOffBufferForProcessing(currentBuffer);
               // switch to another buffer
               currentBuffer = buffers.take();

               currentBuffer.putInt(length);
               currentBuffer.putLong(recorder.getTimeStamp());
                  Byte dataID = dataMap.get(data);
                  if (dataID == null) {
                     throw new IllegalArgumentException(String.format("no data mapped for %s", data.getName()));
                  }
               currentBuffer.put(dataID);
               currentBuffer.put(data.toByteArray(), 0, length);
            } catch (Throwable t) {
               throw new MessageSystemException(
                     "problems handing off buffer(pos=" + currentBuffer.position() + ", remaing=" + currentBuffer.remaining() + ")  for " + msg.getName(),
                     Level.SEVERE, t);
            }
         } else {
            currentBuffer.putInt(length);
           currentBuffer.putLong(recorder.getTimeStamp());
            Byte dataID = dataMap.get(data);
            if (dataID == null) {
               throw new IllegalArgumentException(String.format("no data mapped for %s", data.getName()));
            }
            currentBuffer.put(dataID);
            currentBuffer.put(data.toByteArray(), 0, length);
         }

         bm.endSample();
      }
   }

   private void handOffBufferForProcessing(final ByteBuffer buffer) throws InterruptedException, IOException {

      recorder.submitTask(new Runnable() {

         public void run() {
            try {
                  postProcess(buffer);
            } catch (Throwable t) {
               OseeLog.log(MessageSystemTestEnvironment.class,
                     Level.SEVERE, "failed to process buffer  for message " + msg.getName(), t);
            }
         }

      });

   }

   private void postProcess(final ByteBuffer bufferToProcess) throws InterruptedException, IOException {
      pbm.startSample();
      ByteBuffer fileWriteBuffer = recorder.acquireOutputBuffer();
         try {
            bufferToProcess.flip();
            fileWriteBuffer.clear();
         //         fileWriteBuffer.put(String.format("%d, Start of post process by %s\n", recorder.getTimeStamp(),
         //               Thread.currentThread().getName()).getBytes());
            final MemoryResource mem = new MemoryResource(new byte[longestLength], 0, longestLength);
            while (bufferToProcess.hasRemaining()) {
               final int length = bufferToProcess.getInt();
               int bodySize = length - headerSize;
               final long timeStamp = bufferToProcess.getLong();
               final Byte dataID = bufferToProcess.get();
               final byte[] data = mem.getData();
               if (bufferToProcess.remaining() < length) {
                  throw new IllegalStateException(String.format(
                        "Invalid recording buffer for msg %s: buf remaining=%d, length expected=%d", msg.getName(),
                        bufferToProcess.remaining(), length));
               }
               bufferToProcess.get(data, 0, length);
               fileWriteBuffer.put(Long.toString(timeStamp).getBytes()).put(COMMA);
               fileWriteBuffer.put(nameAsBytes).put(COMMA);
               fileWriteBuffer.put(headerTypeAsBytes).put(COMMA);
               for (IElementEntry entry : headerEntries) {
                  entry.write(fileWriteBuffer, mem, headerSize);
               }

               for (IElementEntry entry : bodyEntries) {
                  final Element element = entry.getElement();
                  if (dataMap.get(element.getMsgData()).equals(dataID)) {
                     if (element.getByteOffset() < bodySize) {
                        entry.write(fileWriteBuffer, mem, bodySize);
                     } else {
                        break;
                     }
                  }
               }
               
               // hex dump
               if(headerDump){
                  fileWriteBuffer.put(HEADER_HEX_DUMP_STR).put(COMMA);
                  for (int i = 0; i < headerSize; i++) {
                     fileWriteBuffer.put((byte) ' ');
                  RecUtil.byteToAsciiHex(data[i], fileWriteBuffer);
                  }
               }
               if(bodyDump){
                  fileWriteBuffer.put(COMMA);
                  fileWriteBuffer.put(BODY_HEX_DUMP_STR).put(COMMA);
                  for (int i = headerSize; i < length; i++) {
                     fileWriteBuffer.put((byte) ' ');
                  RecUtil.byteToAsciiHex(data[i], fileWriteBuffer);
                  }
               }
               fileWriteBuffer.put(NL);
            flush(fileWriteBuffer);
         }
         bufferToProcess.clear();
      } catch (RuntimeException e) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, "problems post processing " + msg.getName(), e);
      } finally {
         recorder.releaseOutputBuffer(fileWriteBuffer);
         buffers.add(bufferToProcess);
      }
      pbm.endSample();
   }

   private void flush(ByteBuffer fileWriteBuffer) throws IOException {
               fileWriteBuffer.flip();
               final WritableByteChannel channel = recorder.getChannel();
               synchronized (channel) {
                  int totalLimit = fileWriteBuffer.limit();
                  int position = fileWriteBuffer.position();
         int chunkSize = 4096;
                  int limit = chunkSize;
                  int written = 0;
                  boolean moreToWrite = true;
                  if(totalLimit < limit){
                     limit = totalLimit;
                  }
                  while(moreToWrite){
                     fileWriteBuffer.position(position);
                     fileWriteBuffer.limit(limit);
                     int size = channel.write(fileWriteBuffer);
                     written += size;
                     position = size + position;
                     limit = position + chunkSize;
                     if(position == totalLimit){
                        moreToWrite = false;
                     } else if(totalLimit < limit){
                        limit = totalLimit;
                     }
                  }
               }
               fileWriteBuffer.clear();
            }
        

   @Override
   public String toString() {
      return String.format("Msg Rec Entry for %s memtype=%s", msg.getName(), type.name());
   }

}
