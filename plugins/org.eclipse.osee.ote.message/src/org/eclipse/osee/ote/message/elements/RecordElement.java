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
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.nonmapping.NonMappingRecordElement;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public abstract class RecordElement extends Element {

   private Map<String, Element> elementMap;
   private int recordBitSize;

   private int bitOffset;
   private int index;
   private boolean isPartOfMap = true;
   private int firstRecordBitOffset;
   public int BIT_OFFSET;

   /**
    * @param message -
    * @param elementName -
    */
   public RecordElement(Message<?,?,?> message, String elementName, int index, MessageData messageData, int firstRecordBitOffset, int recordBitSize) {
      super(message, elementName, messageData, firstRecordBitOffset + (index * recordBitSize), recordBitSize);
      elementMap = new LinkedHashMap<String, Element>();
      BIT_OFFSET = this.bitOffset = firstRecordBitOffset + (index * recordBitSize);
      this.recordBitSize = recordBitSize;
      this.firstRecordBitOffset = firstRecordBitOffset;
      this.index = index;
   }

   public RecordElement(Message<?,?,?> message, String elementName, MessageData messageData, int firstRecordBitOffset, int recordBitSize) {
      this(message, elementName, 0, messageData, firstRecordBitOffset, recordBitSize);
      isPartOfMap = false;
   }

   public void addElements(Element... elements) {
      for (Element element : elements) {
         elementMap.put(element.elementName, element);
      }
   }

   public void addPath(Object... objs) {
      for (Object obj : objs) {
         getElementPath().add(obj);
      }
      if (isPartOfMap) {
         getElementPath().add(index);
      }
      else {
         getElementPath().add(getElementName());
      }
      Object[] myPath = getElementPath().toArray();
      for (Element el : elementMap.values()) {
         el.addPath(myPath);
      }
   }

   public RecordElement(RecordElement message, String elementName, int offset, MessageData messageData, int firstRecordByteOffset, int recordByteSize) {
      this(message.getMessage(), elementName, offset, messageData, firstRecordByteOffset, recordByteSize);
      isPartOfMap = false;
   }

   public Map<String, Element> getElementMap() {
      return this.elementMap;
   }

   @Override
   protected NonMappingRecordElement getNonMappingElement() {
      return (NonMappingRecordElement) new NonMappingRecordElement(this);
   }

   public  RecordElement switchMessages(Collection<? extends Message<?,?,?>> messages) {
		return (RecordElement) super.switchMessages(messages);
   }

   
   public void put(int index, RecordElement newRecord) {
      // records.put(index, (T)newRecord);
   }

   public int length() {
      return -1;
   }

   public RecordElement get(int index) {
       // T val = records.get(index);
       // if(val == null){
       // val = (T)factory.create(index);
       // records.put(index, val);
       // }
       return null;
    }
   
   /**
    * @return the firstRecordBitOffset
    */
   public int getFirstRecordBitOffset() {
      return firstRecordBitOffset;
   }

   /**
    * @return the bitOffset
    */
   public int getBitOffset() {
      return bitOffset;
   }

   /**
    * @return the recordBitSize
    */
   public int getRecordBitSize() {
      return recordBitSize;
   }

   @Override
   public void visit(IElementVisitor visitor) {
      visitor.asRecordElement(this);
   }

   @Override
   public void zeroize(){
	   for(Element el:elementMap.values()){
		   el.zeroize();
	   }
   }
   
   public String getDescriptiveName(){
      if(isPartOfMap){
         return String.format("%s[%d]", getName(), index);
      }
      return getName();
   }
   
   
}