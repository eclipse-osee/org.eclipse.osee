/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ote.service.MessageSink;

/**
 * Builds a simple tree out of messages. All nodes under the root are messages. Each node under each message is a
 * message element
 * 
 * @author Ken J. Aguilar
 */
public class MessageTreeBuilder implements MessageSink {

   private ArrayList<String> lastAddedMsgNode;
   private int numElements = 0;
   private int numMessages = 0;
   private final HashMap<String, ArrayList<String>> msgs = new HashMap<String, ArrayList<String>>(200000);

   public void clear() {
      numMessages = numElements = 0;
      msgs.clear();
      lastAddedMsgNode = null;
   }

   @Override
   public void absorbElement(final String elementName) {
      if (lastAddedMsgNode == null) {
         throw new IllegalStateException("no message exists for " + elementName);
      }
      lastAddedMsgNode.add(elementName);
      numElements++;
   }

   @Override
   public void absorbMessage(String messageName) {
      lastAddedMsgNode = new ArrayList<String>(64);
      msgs.put(messageName, lastAddedMsgNode);
      numMessages++;
   }

   public int getNumElements() {
      return numElements;
   }

   public int getNumMessages() {
      return numMessages;
   }

   public Collection<Map.Entry<String, ArrayList<String>>> getMessages() {
      return msgs.entrySet();
   }

}
