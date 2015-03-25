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

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;

public class OteEventMessageHeader implements IMessageHeader{

   public static final int HEADER_SIZE = 164+53; 
   public static final int MARKER_VALUE = 0xFADE;
   private final HeaderData headerData;
   private final Object[] paths;

   public final IntegerElement MARKER;
   public final StringElement TOPIC;
   public final IntegerElement MESSAGE_ID;
   public final IntegerElement MESSAGE_SEQUENCE_NUMBER;
   public final LongIntegerElement UUID_LOW;
   public final LongIntegerElement UUID_HIGH;
   public final IntegerElement TTL;
   public final StringElement RESPONSE_TOPIC;
   public final IntegerElement RESPONSE_ID;
   public final SOCKET_ADDRESS_RECORD ADDRESS;   
   
   private final String name;

   public OteEventMessageHeader(OteEventMessage msg, String topic, int messageId, MemoryResource data) {
     this.name = msg.getName();
     headerData = new HeaderData("OteEventMessageHeader", data);
      paths = new Object[]{(msg == null ? "message" : msg.getClass().getName()), "HEADER(OteEventMessageHeader)"};
      MARKER = new IntegerElement(msg, "MARKER", headerData, 0, 0, 15);
      TOPIC = new StringElement(msg, "TOPIC", headerData, 2, 0, 8*96);
      MESSAGE_ID = new IntegerElement(msg, "MESSAGE_ID", headerData, 66+32, 0, 31);
      MESSAGE_SEQUENCE_NUMBER = new IntegerElement(msg, "MESSAGE_ID", headerData, 66+32, 0, 31);
      UUID_LOW = new LongIntegerElement(msg, "UUID_LOW", headerData, 74+32, 0, 63);
      UUID_HIGH = new LongIntegerElement(msg, "UUID_HIGH", headerData, 82+32, 0, 63);
      TTL = new IntegerElement(msg, "TTL", headerData, 90+32, 0, 31);
      RESPONSE_TOPIC = new StringElement(msg, "TOPIC", headerData, 94+32, 0, 8*64);
      RESPONSE_ID = new IntegerElement(msg, "RESPONSE_ID", headerData, 158+32, 0, 31);
      ADDRESS = new SOCKET_ADDRESS_RECORD(msg, "ADDRESS", headerData, 164+32, 0, SOCKET_ADDRESS_RECORD.SIZE*8-1);

      TOPIC.setValue(topic);
      MARKER.setValue(MARKER_VALUE);
      MESSAGE_ID.setValue(messageId);
      addElement(MARKER);
      addElement(TOPIC);
      addElement(MESSAGE_ID);
   }
   
   private <T extends Element> T addElement(T instance) {
      instance.addPath(paths);
      return instance;
   }

   @Override
   public int getHeaderSize() {
      return HEADER_SIZE;
   }

   @Override
   public byte[] getData() {
      return headerData.toByteArray();
   }

   @Override
   public Element[] getElements() {
      return new Element[]{MARKER, TOPIC};
   }

   @Override
   public void setNewBackingBuffer(byte[] data) {
      headerData.setNewBackingBuffer(data);
   }

   @Override
   public String toXml() {
      StringBuilder builder = new StringBuilder(256);
      builder.append("<OteEventMessageHeader> ").
         append("MARKER=\"").append(MARKER.getValue()).append("\" ").
         append("TOPIC=\"").append(TOPIC.getValue()).append("\" ").
         append("MESSAGE_ID=\"").append(MESSAGE_ID.getValue()).append("\" ").
      append("</OteEventMessageHeader>");
      return builder.toString();
   }

   @Override
   public String getMessageName() {
      return name;
   }

   public InetSocketAddress getSourceInetSocketAddress() throws UnknownHostException {
      return new InetSocketAddress(ADDRESS.getAddress(), ADDRESS.getPort());
   }

}
