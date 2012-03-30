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

import org.eclipse.osee.ote.message.MessageDefinitionProvider;
import org.eclipse.osee.ote.message.MessageSink;

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
   private HashMap<String, ArrayList<String>> currentMsgs;
   private Collection<Map.Entry<String, ArrayList<String>>> allMessages = new ArrayList<Map.Entry<String, ArrayList<String>>>(200000);
   private final HashMap<MessageDefinitionProvider, HashMap<String, ArrayList<String>>> msgs = new HashMap<MessageDefinitionProvider, HashMap<String, ArrayList<String>>>(); 
   
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
      currentMsgs.put(messageName, lastAddedMsgNode);
      numMessages++;
   }

   @Override
   public void absorbProvider(String providerName) {
      // We don't care about absorbing this yet
      // In the future it would be nice to have this
      // information stored and related to the messages
      // that are from this provider for display
      // to the users
   }

   public int getNumElements() {
      return numElements;
   }

   public int getNumMessages() {
      return numMessages;
   }

   public Collection<Map.Entry<String, ArrayList<String>>> getMessages() {
      return allMessages;
   }

   private void buildAllMsgs(){
	   allMessages.clear();
	   for(HashMap<String, ArrayList<String>> val:msgs.values()){
		   allMessages.addAll(val.entrySet());
	   }
   }
   
   public void startProcessing(MessageDefinitionProvider provider) {
	   currentMsgs = new HashMap<String, ArrayList<String>>(200000);
	   msgs.put(provider, currentMsgs);
   }

   public void stopProcessing(MessageDefinitionProvider provider) {
	   buildAllMsgs();
	   currentMsgs = null;
   }

   public void removeProvider(MessageDefinitionProvider service) {
	   if(msgs.remove(service) == null){
		   System.out.println("didn't remove anything");
	   }
	   buildAllMsgs();
   }

   public int getNumProviders() {
	   return msgs.size();
   }

}
