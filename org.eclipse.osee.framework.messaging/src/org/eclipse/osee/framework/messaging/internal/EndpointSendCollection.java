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
package org.eclipse.osee.framework.messaging.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.messaging.EndpointSend;
import org.eclipse.osee.framework.messaging.id.MessageId;
import org.eclipse.osee.framework.messaging.id.ProtocolId;

/**
 * @author Andrew M. Finkbeiner
 */
public class EndpointSendCollection {

   private final List<EndpointSend> endpoints;
   private Map<ProtocolId, EndpointSend> protocolMapping;
   private Map<MessageId, ProtocolId> messageIdMapping;

   public EndpointSendCollection() {
      endpoints = new CopyOnWriteArrayList<EndpointSend>();
      protocolMapping = new ConcurrentHashMap<ProtocolId, EndpointSend>();
      messageIdMapping = new ConcurrentHashMap<MessageId, ProtocolId>();
   }

   public synchronized boolean add(EndpointSend endpoint) {
      if (endpoints.contains(endpoint)) {
         return false;
      } else {
         return endpoints.add(endpoint);
      }
   }

   public synchronized boolean remove(EndpointSend endpoint) {
      if (endpoints.remove(endpoint)) {
         List<ProtocolId> protocolIdsToRemove = new ArrayList<ProtocolId>();
         Iterator<ProtocolId> it = protocolMapping.keySet().iterator();
         while (it.hasNext()) {
            ProtocolId id = it.next();
            if (protocolMapping.get(id).equals(endpoint)) {
               it.remove();
               protocolIdsToRemove.add(id);
            }
         }
         for (ProtocolId id : protocolIdsToRemove) {
            removeProtocolId(id);
         }
         return true;
      }
      return false;
   }

   public synchronized boolean bind(ProtocolId protocolId, EndpointSend endpoint) {
      if (protocolMapping.containsKey(protocolId)) {
         return false;
      } else {
         protocolMapping.put(protocolId, endpoint);
         return true;
      }
   }

   public synchronized boolean unbind(ProtocolId protocolId, EndpointSend endpoint) {
      if (protocolMapping.containsKey(protocolId)) {
         protocolMapping.remove(protocolId);

         removeProtocolId(protocolId);

         return true;
      } else {
         return false;
      }
   }

   private void removeProtocolId(ProtocolId protocolId) {
      Iterator<MessageId> it = messageIdMapping.keySet().iterator();
      while (it.hasNext()) {
         MessageId id = it.next();
         if (messageIdMapping.get(id).equals(protocolId)) {
            it.remove();
         }
      }
   }

   public synchronized boolean bind(MessageId messageId, ProtocolId protocolId) {
      if (messageIdMapping.containsKey(messageId)) {
         return false;
      } else {
         messageIdMapping.put(messageId, protocolId);
         return true;
      }
   }

   public synchronized boolean unbind(MessageId messageId, ProtocolId protocolId) {
      if (messageIdMapping.containsKey(messageId)) {
         messageIdMapping.remove(messageId);
         return true;
      } else {
         return false;
      }
   }

   public synchronized Collection<EndpointSend> getAll() {
      return endpoints;
   }

   public synchronized void dispose() {
      endpoints.clear();
      protocolMapping.clear();
      messageIdMapping.clear();
   }

   public synchronized EndpointSend get(MessageId id) {
      ProtocolId protocolId = messageIdMapping.get(id);
      if (protocolId != null) {
         EndpointSend send = protocolMapping.get(protocolId);
         if (send != null) {
            return send;
         }
      }
      return null;
   }
}
