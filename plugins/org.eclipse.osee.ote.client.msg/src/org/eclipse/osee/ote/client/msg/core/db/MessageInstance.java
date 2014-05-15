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
package org.eclipse.osee.ote.client.msg.core.db;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.ote.client.msg.core.internal.MessageServiceSupport;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.message.tool.SubscriptionDetails;
import org.eclipse.osee.ote.message.tool.SubscriptionKey;

/**
 * @author Ken J. Aguilar
 */
public class MessageInstance {

   private final DataType type;
   private final MessageMode mode;
   private final Message msg;
   private SubscriptionKey serverSubscriptionKey = null;
   private int refcount = 0;
   private boolean supported = true;
   private volatile boolean connected = false;
   
   public MessageInstance(Message msg, MessageMode mode, DataType type) {
      this.msg = msg;
      this.mode = mode;
      this.type = type;
   }

   public Message getMessage() {
      return msg;
   }

   public SubscriptionKey getServerSubscriptionKey() {
      return serverSubscriptionKey;
   }

   public void setServerSubscriptionKey(SubscriptionKey serverSubscriptionKey) {
      this.serverSubscriptionKey = serverSubscriptionKey;
   }

   public boolean isAttached() {
      return serverSubscriptionKey != null;
   }

   public Integer attachToService(IMsgToolServiceClient client) throws Exception {
      InetSocketAddress address = client.getAddressByType(msg.getClass().getName(), type);
      SubscriptionDetails details;
      if(address == null){
         details = null;
      } else {
         details = MessageServiceSupport.subscribeToMessage(new SubscribeToMessage(msg.getClass().getName(), type, mode,
               client.getAddressByType(msg.getClass().getName(), type), client.getTestSessionKey()));
      }
      if (details == null) {
         supported = false;
         return null;
      }
      supported = true;
      msg.setData(details.getCurrentData());
      connected = true;
      serverSubscriptionKey = details.getKey();
      return serverSubscriptionKey.getId();
   }

   public void detachService(IMsgToolServiceClient client) throws Exception {
      if (supported) {
         MessageServiceSupport.unsubscribeToMessage(new UnSubscribeToMessage(msg.getClass().getName(), mode, type,
            client.getAddressByType(msg.getClass().getName(), type)));
      }
      connected = false;
      serverSubscriptionKey = null;
   }

   public Integer getId() {
      return serverSubscriptionKey != null ? serverSubscriptionKey.getId() : null;
   }

   public void incrementReferenceCount() {
      refcount++;
   }

   public void decrementReferenceCount() {
      refcount--;
   }

   public boolean hasReferences() {
      return refcount > 0;
   }

   public DataType getType() {
      return type;
   }

   public MessageMode getMode() {
      return mode;
   }

   public Set<DataType> getAvailableTypes() {
	  HashSet<DataType> set = new HashSet<DataType>();
	  if(connected){
	     Set<? extends DataType> envSet = MessageServiceSupport.getAvailablePhysicalTypes();
	     Set<DataType> available = msg.getAssociatedMessages().keySet();
	     for(DataType type : available.toArray(new DataType[available.size()])){
	        if(envSet.contains(type)){
	           set.add(type);
	        }
	     }
	  }
     return set;
   }

   public boolean isSupported() {
      return supported;
   }

   @Override
   public String toString() {
      return String.format("Message Instance(type=%s, mode=%s, ref=%d, supported=%b)", type.name(), mode.name(),
         refcount, supported);
   }
}
