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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.commands.SubscribeToMessage;
import org.eclipse.osee.ote.message.commands.UnSubscribeToMessage;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
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
   private final Set<DataType> availableTypes = new HashSet<DataType>();
   private boolean supported = true;

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

   public Integer attachToService(IRemoteMessageService service, IMsgToolServiceClient client) throws Exception {
      SubscriptionDetails details =
         service.subscribeToMessage(new SubscribeToMessage(msg.getClass().getName(), type, mode, client));
      if (details == null) {
         supported = false;
         return null;
      }
      supported = true;
      msg.setData(details.getCurrentData());
      availableTypes.clear();
      availableTypes.addAll(details.getAvailableMemTypes());
      serverSubscriptionKey = details.getKey();
      return serverSubscriptionKey.getId();
   }

   public void detachService(IRemoteMessageService service, IMsgToolServiceClient client) throws Exception {
      if (service != null) {
         service.unsubscribeToMessage(new UnSubscribeToMessage(msg.getClass().getName(), mode, type,
            client.getAddressByType(msg.getClass().getName(), type)));
      }
      availableTypes.clear();
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
      return availableTypes;
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
