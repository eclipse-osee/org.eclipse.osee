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
package org.eclipse.osee.ote.ui.mux.model;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.HashMap;
import org.eclipse.osee.ote.ui.mux.msgtable.MessageNode;

/**
 * @author Ky Komadino
 */
public class MessageModel {
   private HashMap<String, MessageNode> messageNodes;
   private final CharBuffer buffer = ByteBuffer.allocate(16).asCharBuffer();
   
   public MessageModel() {
      messageNodes = new HashMap<String, MessageNode>();
   };

   /**
    * @param muxId - message ID
    * @param node - node to add to list
    */
   public void addNode(String muxId, MessageNode node) {
      messageNodes.put(muxId, node);
   }
   
   /**
    * @return - values in list
    */
   public Collection<MessageNode> getChildren() {
      return messageNodes.values();
   }
   
   public void removeMessages() {
      messageNodes.clear();
   }
   
   public void onDataAvailable(ByteBuffer data) {
      // if this is the "T" side of an RT-RT message, then discard
      if ((data.array()[1] & 0x04) >> 2 == 1 && data.array()[3] != 0 && data.array()[4] != 0)
         return;
      
      buffer.clear();
      buffer.append(String.format("%02d", ((short)(data.array()[1] & 0x00F8)) >> 3));
      final char transmitReceive = (data.array()[1] & 0x04) >> 2 == 1 ? 'T' : 'R';
      buffer.append(transmitReceive);
      buffer.append(String.format("%02d", (((short)(data.array()[1] & 0x0003)) << 3) +
                                          (((short)(data.array()[2] & 0x00E0)) >> 5)));
      String muxId = buffer.flip().toString();
      MessageNode receiveMessage = messageNodes.get(muxId);
      if (receiveMessage == null) {
         receiveMessage = new MessageNode(muxId);
         addNode(muxId, receiveMessage);
      }
      receiveMessage.setData(data);
   }
}
