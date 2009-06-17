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
package org.eclipse.osee.ote.message.elements;

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class RecordMap<T extends RecordElement> extends RecordElement{

   private final int NUMBER_OF_RECORDS;
   
   MessageData messageData;

   private HashMap<Integer, T> records;
   private IRecordFactory factory;

   public RecordMap(Message<?,?,?> message, MessageData messageData, String elementName, int numberOfRecords, IRecordFactory factory) {
      super(message, elementName, 1, messageData, 0, factory.getBitLength());
      this.messageData = messageData;
      NUMBER_OF_RECORDS = numberOfRecords;
      records = new HashMap<Integer, T>(numberOfRecords);
      this.factory = factory;
   }
   
   public RecordMap(Message<?,?,?> message, MessageData messageData, int firstRecordByteOffset, int recordByteSize,
         int numberOfRecords) {
      super(message, "", 1, messageData, 0, 0);
      this.messageData = messageData;
     NUMBER_OF_RECORDS = numberOfRecords;
      records = new HashMap<Integer, T>(numberOfRecords);
   }
   
   public T get(int index) {
      T val =  records.get(index);
      if(val == null){
         val = (T)factory.create(index);
         for(Object obj: getElementPath()){
        	 val.getElementPath().add(obj);
         }
         records.put(index, val);
      }
      return val;
   }

   public void addPath(Object... objs){
	   for(Object obj: objs){
		   getElementPath().add(obj);
	   }
	   getElementPath().add(this.getName());	  
   }
   
   public void put(int index, RecordElement newRecord) {
      records.put(index, (T)newRecord);
   }

   public int length(){
      return this.NUMBER_OF_RECORDS;
   }
   
   public MessageData getMessageData() {
      return messageData;
   }

   public RecordMap<T> switchRecordMapMessages(Collection<? extends Message<?,?,?>> messages) {
      for (RecordElement element : this.records.values()) {
         element.switchMessages(messages);
      }

      return this;
   }

//   public <U extends Message<? extends ITestEnvironmentMessageSystemAccessor, ? extends MessageData, U>> RecordMap<T> switchMessages(Collection<U> messages) {
//	      for (RecordElement element : this.records.values()) {
//	         element.switchMessages(messages);
//	      }
//
//	      return this;
//   }
   
   public  RecordMap<T> switchMessages(Collection<? extends Message<?,?,?>> messages) {
	   for (RecordElement element : this.records.values()) {
	         element.switchMessages(messages);
	      }
	   return this;
  }
   
   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asRecordMap(this);
   }

   public int compareTo(RecordElement o) {
	   return 0;
   }

   @Override
   public void zeroize() {
	   super.zeroize();
	   for (RecordElement element : this.records.values()) {
		   element.zeroize();
	   }
   }
   
   public String getDescriptiveName(){
      return String.format("%s[0...%d]", getName(), length()-1);
   }
   
}