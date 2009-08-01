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
package org.eclipse.osee.ote.message.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.log.Env;
import org.eclipse.osee.ote.message.IMessageDisposeListener;
import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.IMessageSendListener;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.Namespace;
import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;
import org.eclipse.osee.ote.messaging.dds.service.Key;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.PublicationMatchStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class MessageData implements DataReaderListener, DataWriterListener, Data, Key {

   private DataWriter writer;
   private DataReader reader;
   private DataSample myDataSample;

   private final MemoryResource mem;
   private final String typeName;
   private final String name;
   @SuppressWarnings("unchecked")
   private final CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<Message>();
   private List<IMessageSendListener> messageSendListeners = new CopyOnWriteArrayList<IMessageSendListener>();
   private final int defaultDataByteSize;
   private final MemType memType;
   private boolean isEnabled = true;
   private long activityCount = 0;
   private long sentCount;
   private int currentLength;
   private boolean isScheduled = false;

   public MessageData(String typeName, String name, int dataByteSize, int offset, MemType memType) {
      mem = new MemoryResource(new byte[dataByteSize], offset, dataByteSize - offset);
      myDataSample = new DataSample(this);
      this.typeName = typeName;
      this.name = name;
      this.defaultDataByteSize = dataByteSize;
      this.currentLength = dataByteSize;
      this.memType = memType;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(String typeName, String name, MemoryResource mem, MemType memType) {
      this.mem = mem;
      myDataSample = new DataSample(this);
      this.typeName = typeName;
      this.name = name;
      this.defaultDataByteSize = mem.getLength();
      this.currentLength = mem.getLength();
      this.memType = memType;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(String name, int dataByteSize, int offset, MemType memType) {
      this(name, name, dataByteSize, offset, memType);
   }

   public MessageData(byte[] data, int dataByteSize, int offset) {
      this.mem = new MemoryResource(data, offset, dataByteSize - offset);
      this.typeName = "";
      this.name = "";
      this.defaultDataByteSize = dataByteSize;
      this.currentLength = dataByteSize;
      this.memType = null;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public MessageData(MemoryResource memoryResource) {
      this("", memoryResource);
   }

   public MessageData(String name, MemoryResource memoryResource) {
      this.mem = memoryResource;
      this.typeName = "";
      this.name = name;
      this.defaultDataByteSize = memoryResource.getLength();
      this.currentLength = memoryResource.getLength();
      this.memType = null;
      GCHelper.getGCHelper().addRefWatch(this);
   }

   public abstract IMessageHeader getMsgHeader();

   public abstract void zeroize();

   public MemType getType() {
      return memType;
   }

   /**
    * Returns the number of byte words in the payload of this message.
    * 
    * @return the number of bytes in the message payload
    */
   public int getPayloadSize() {
      return currentLength;
   }

   public String getName() {
      return name;
   }

   /**
    * adds a {@link Message} who are mapped to this data object
    * 
    * @param message
    */
   @SuppressWarnings("unchecked")
   public void addMessage(Message message) {
      if (!messages.contains(message)) {
         messages.add(message);
         message.addPreMessageDisposeListener(disposeListener);
      }
   }

   /**
    * returns a list of the message that this data is a source for. <BR>
    * 
    * @return a collection of messages
    */
   @SuppressWarnings("unchecked")
   public Collection<Message> getMessages() {
      return new ArrayList<Message>(messages);
   }

   /**
    * @return Returns the activityCount.
    */
   public long getActivityCount() {
      return activityCount;
   }

   /**
    * @param activityCount The activityCount to set.
    */
   public void setActivityCount(long activityCount) {
      this.activityCount = activityCount;
   }

   public void incrementActivityCount() {
      activityCount++;
   }

   public void incrementSentCount() {
      sentCount++;
   }

   public long getSentCount() {
      return sentCount;
   }

   public boolean isEnabled() {
      return isEnabled;
   }

   public abstract void visit(IMessageDataVisitor visitor);

   /**
    * 
    */
   public void dispose() {
      messages.clear();
      if (writer != null) {
         writer.getPublisher().deleteDataWriter(writer);
         writer = null;
      } else if (reader != null && reader.getSubscriber() != null) {
         reader.getSubscriber().deleteDataReader(reader);
      }
      reader = null;
   }

   public void copyData(int destOffset, byte[] data, int srcOffset, int length) {
      setCurrentLength(length + destOffset);
      mem.copyData(destOffset, data, srcOffset, length);
   }

   public void copyData(int destOffset, ByteBuffer data, int length) throws MessageSystemException {
      try {
         setCurrentLength(destOffset + length);
         mem.copyData(destOffset, data, length);
      } catch (MessageSystemException e) {
		   OseeLog.log(MessageSystemTestEnvironment.class,Level.WARNING, 
				   String.format(
						   "increasing backing store for %s to %d. prev length: %d, recv cnt: %d", 
						   getName(), 
						   destOffset + length, 
						   mem.getData().length,
						   this.activityCount),
				   e);
         setNewBackingBuffer(data, destOffset, length);
      }
   }

   public void copyData(ByteBuffer data) {
      copyData(0, data, data.remaining());
   }

   /**
    * Notifies all {@link Message}s that have this registered as a data source of the update
    * 
    * @throws MessageSystemException
    */
   @SuppressWarnings("unchecked")
   public void notifyListeners() throws MessageSystemException {
      final MemType memType = getType();
      for (Message message : messages) {
         try {
            if (!message.isDestroyed()) {
               message.notifyListeners(this, memType);
            }
		   }
		   catch (Throwable t) {
			   final String msg = String.format("Problem during listener notification for message %s. Data=%s, MemType=%s", message.getName(), this.getName(), this.getType());
            OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, msg, t);
         }
      }
   }

   /**
    * @return the currentLength
    */
   public int getCurrentLength() {
      return currentLength;
   }

   /**
    * @param currentLength the currentLength to set
    */
   public void setCurrentLength(int currentLength) {
      this.currentLength = currentLength;
   }

   /**
    * Override this method if you need to set some default data in the backing buffer.
    * 
    * @param data
    */
   public void setNewBackingBuffer(byte[] data) {
      setCurrentLength(data.length);
      this.mem.setData(data);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public void setNewBackingBuffer(ByteBuffer buffer) {
      byte[] data = new byte[buffer.remaining()];
      buffer.get(data);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }
   }

   public void setNewBackingBuffer(ByteBuffer buffer, int offset, int length) {
      byte[] data = new byte[offset + length];
      buffer.get(data, offset, length);
      this.mem.setData(data);
      setCurrentLength(data.length);
      if (this.getMsgHeader() != null) {
         initializeDefaultHeaderValues();
      } else {
         // System.out.println("what??-- bad HeaderData");
      }

   }

   public abstract void initializeDefaultHeaderValues();

   /**
    * @return the mem
    */
   public MemoryResource getMem() {
      return mem;
   }

   public int getDefaultDataByteSize() {
      return defaultDataByteSize;
   }

   public synchronized void onDataAvailable(DataReader theReader) {
      // System.out.println(String.format("data available %s %s", this.getName(),
      // this.getNamespace()));
      if (isEnabled()) {
         ReturnCode val = theReader.takeNextSample(myDataSample);
         if (val == ReturnCode.OK) {
            incrementActivityCount();
            notifyListeners();
         } else {
            Env.getInstance().severe(val.getDescription());
         }
      }
   }

   public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status) {
   }

   public void onRequestedDeadlineMissed(DataReader theReader, RequestedDeadlineMissedStatus status) {
   }

   public void onRequestedIncompatibleQos(DataReader theReader, RequestedIncompatibleQosStatus status) {
   }

   public void onSampleLost(DataReader theReader, SampleLostStatus status) {
   }

   public void onSampleRejected(DataReader theReader, SampleRejectedStatus status) {
   }

   public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status) {
   }

   public synchronized void onDataSentToMiddleware(DataWriter theWriter) {
      // header.setSequenceNumber(header.getSequenceNumber() + 1);
      notifyListeners();
   }

   public void onLivelinessLost(DataWriter theWriter, LivelinessLostStatus status) {
   }

   public void onOfferedDeadlineMissed(DataWriter theWriter, OfferedDeadlineMissedStatus status) {
   }

   public void onOfferedIncompatibleQos(DataWriter theWriter, OfferedIncompatibleQosStatus status) {
   }

   public void onPublicationMatch(DataWriter theWriter, PublicationMatchStatus status) {
   }

   public Object getKeyValue() {
      return null;
   }

   public void setFromByteArray(byte[] input) {
      try {
         copyData(0, input, 0, input.length);
      }
      catch (MessageSystemException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.WARNING,

     			 String.format("Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(), this.mem.getData().length, input.length));
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteBuffer(ByteBuffer buffer) {
      try {
         copyData(buffer);
      } catch (Exception e) {
      OseeLog.log(MessageSystemTestEnvironment.class, 
            	   Level.SEVERE,  
				   String.format("Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(), this.mem.getData().length, buffer.limit()));
         setNewBackingBuffer(buffer);
      }
   }

   public ByteBuffer toByteBuffer() {
      return mem.getAsBuffer();
   }

   public void setFromByteArray(byte[] input, int length) {
      try {
         copyData(0, input, 0, length);
	   }
	   catch (MessageSystemException ex) {
	      OseeLog.log(MessageSystemTestEnvironment.class,		   Level.SEVERE,  
				   String.format("Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(), this.mem.getData().length, length));
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(int destOffset, byte[] input, int srcOffset, int length) {
      try {
         copyData(destOffset, input, srcOffset, length);
      }
      catch (MessageSystemException ex) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.SEVERE,  
               String.format("Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(), this.mem.getData().length, length));
         setNewBackingBuffer(input);
      }
   }

   public void setFromByteArray(ByteBuffer input, int length) {
      try {
         copyData(0, input, length);
	   }
	   catch (MessageSystemException ex) {
	      OseeLog.log(MessageSystemTestEnvironment.class,	   Level.SEVERE,  
				   String.format("Copy Failed: setting new backing buffer.  msg[%s], oldSize[%d] newSize[%d]", this.getName(), this.mem.getData().length, length));
         setNewBackingBuffer(input);
      }
   }

   public byte[] toByteArray() {
      return mem.getData();
   }

   public void setReader(DataReader reader) {
      this.reader = reader;
   }

   public void setWriter(DataWriter writer) {
      this.writer = writer;
   }

   public void send() throws MessageSystemException {
      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class, Level.SEVERE, getName() + " - the writer is null");
      }
      else if (shouldSendData()) {
         try {
            notifyPreSendListeners();
            // this.initializeDefaultHeaderValues();
            getMem().setDataHasChanged(false);
            writer.write(null, null, this, null);
            incrementSentCount();
            notifyPostSendListeners();
         } catch (Throwable ex) {
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }
   }

   protected void sendTo(IDestination destination, ISource source) throws MessageSystemException {
      if (writer == null) {
         OseeLog.log(MessageSystemTestEnvironment.class,
               Level.WARNING, getName() + " - the writer is null");
      } else if (shouldSendData()) {
         try {
            notifyPreSendListeners();
            // this.initializeDefaultHeaderValues();
            getMem().setDataHasChanged(false);
            writer.write(destination, source, this, null);
            incrementSentCount();
            notifyPostSendListeners();
         } catch (Throwable ex) {
            throw new MessageSystemException("Could not send message data " + getName(), Level.SEVERE, ex);
         }
      }
   }

   /**
    * Override this method if you want to specialize the send criteria in a data source. For example, if you only want
    * to send data to the MUX driver if the data has changed.
    * 
    * @return boolean
    */
   protected boolean shouldSendData() {
      return true;
   }

   public TypeSupport getTypeSupport() {
      return new DDSTypeSupport(this, getName(), getName(), getPayloadSize());
   }

   public String getTopicName() {
      return getName();
   }

   public String getTypeName() {
      return typeName;
   }

   public boolean isSameInstance(byte[] data1, byte[] data2) {
      return true;
   }

   public Namespace getNamespace() {
      if (isWriter()) {
         return new Namespace(writer.getTopic().getNamespace());
      } else {
         return new Namespace(reader.getTopicDescription().getNamespace());
      }
   }

   /*
    * each type that extends DDSData needs to have it's own namespace.... we need to go through each
    * DDSData child and determine all of it's possible namespaces
    */
   public boolean isWriter() {
      if (writer != null && reader == null) {
         return true;
      } else if (writer == null && reader != null) {
         return false;
      } else {
         throw new MessageSystemException(
               "This is an illegal message it has neither a reader or a writer [" + this.getName() + "].", Level.SEVERE);
      }
   }

   private IMessageDisposeListener disposeListener = new IMessageDisposeListener() {

      @SuppressWarnings("unchecked")
      public void onPreDispose(Message message) {
         messages.remove(message);
      }

      @SuppressWarnings("unchecked")
      public void onPostDispose(Message message) {
      }

   };

   public void copyFrom(Data data) {
      ByteBuffer buffer = data.toByteBuffer();
      copyData(data.getOffset(), buffer, buffer.remaining());
   }

   @Override
   public String toString() {
      return getClass().getName() + ": name=" + getName();
   }

   public int getOffset() {
      return 0;
   }

   /**
    * @return the isScheduled
    */
   public boolean isScheduled() {
      return isScheduled;
   }

   /**
    * @param isScheduled the isScheduled to set
    */
   public void setScheduled(boolean isScheduled) {
      this.isScheduled = isScheduled;
   }

   /**
    * 
    */
   private void notifyPostSendListeners() {
      try {
         for (IMessageSendListener listener : messageSendListeners) {
            listener.onPostSend(this);
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   /**
    * 
    */
   private void notifyPreSendListeners() {
      try {
         for (IMessageSendListener listener : messageSendListeners) {
            listener.onPreSend(this);
         }
      } catch (Exception ex) {
         OseeLog.log(Message.class, Level.SEVERE, ex);
      }
   }

   public void addSendListener(IMessageSendListener listener) {
      messageSendListeners.add(listener);
   }

   public void removeSendListener(IMessageSendListener listener) {
      messageSendListeners.remove(listener);
   }

   public boolean containsSendListener(IMessageSendListener listener) {
      return messageSendListeners.contains(listener);
   }
}
