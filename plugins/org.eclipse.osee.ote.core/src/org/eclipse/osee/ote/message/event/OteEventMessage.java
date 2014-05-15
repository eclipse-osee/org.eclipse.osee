/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.elements.ArrayElement;
import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.elements.IEnumValue;
import org.eclipse.osee.ote.message.elements.IntegerElement;

/**
 * This class is a version of OTEByteMessage used for code that is part of the release.
 * 
 *
 */
public class OteEventMessage extends Message<MessageSystemTestEnvironment, OteEventMessageData, OteEventMessage>{

   private int currentOffset = 0;
   
   private final OteEventMessageData data;
   
   public OteEventMessage(String name, String topic, int defaultByteSize) {
      super(name, defaultByteSize, 0, false, 0, 0);
      data = new OteEventMessageData(this, topic, defaultByteSize);
      setDefaultMessageData(data);
      setMemSource(OteEventMessageType.OTE_EVENT_MESSAGE);
   }
   
   public OteEventMessage(byte[] bytedata) {
	  super("holder", 0, 0, false, 0, 0);
	  data = new OteEventMessageData(this, bytedata.length);
      data.getMem().setData(bytedata);
	  setDefaultMessageData(data);
      setMemSource(OteEventMessageType.OTE_EVENT_MESSAGE);
   }

   public OteEventMessageHeader getHeader(){
	   return getDefaultMessageData().getMsgHeader();
   }
   
   protected <T extends Enum<T> & IEnumValue<T>> EnumeratedElement<T> createEnumeratedElement(String name, int size, Class<T> clazz) {
      EnumeratedElement<T> el = new EnumeratedElement<T>(this, name, clazz, getDefaultMessageData(), currentOffset, 0, size*8-1);
      currentOffset+=size;
      return el;
   }
   
   protected IntegerElement createIntegerElement(String string, int size) {
      IntegerElement el = new IntegerElement(this, string, getDefaultMessageData(), currentOffset, 0, size*8-1);
      currentOffset+=size;
      return el;
   }

   protected ArrayElement createArrayElement(String string, int size) {
      ArrayElement el = new ArrayElement(this, string, getDefaultMessageData(), currentOffset, 0, size*8-1);
      currentOffset+=size;
      return el;
   }
   
   public static int sizeBytesBits(int size){
      return 8*size-1;
   }
   
}
