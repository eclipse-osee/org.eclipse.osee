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
package org.eclipse.osee.framework.messaging.event.res.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * @author Donald G. Dunne
 */
public class OseeCoreModelEventServiceImpl implements OseeMessagingStatusCallback, IOseeCoreModelEventService {

   private final Map<IFrameworkEventListener, HashCollectionSet<ResMessages, OseeMessagingListener>> subscriptions =
      new ConcurrentHashMap<IFrameworkEventListener, HashCollectionSet<ResMessages, OseeMessagingListener>>();

   private final Map<ResMessages, Boolean> messages;
   private final MessageService messageService;

   private ConnectionNode connectionNode;

   public OseeCoreModelEventServiceImpl(MessageService messageService, Map<ResMessages, Boolean> messages) {
      this.messageService = messageService;
      this.messages = messages;
   }

   private synchronized ConnectionNode getConnectionNode() {
      if (connectionNode == null) {
         try {
            connectionNode = messageService.getDefault();
         } catch (OseeCoreException ex) {
            OseeLog.log(OseeCoreModelEventServiceProxy.class, Level.SEVERE,
               "Error initializing OseeCoreModelEventServiceProxy");
         }
      }
      return connectionNode;
   }

   @Override
   public void success() {
      // do nothing
   }

   @Override
   public void fail(Throwable th) {
      OseeLog.log(OseeCoreModelEventServiceImpl.class, Level.SEVERE, th);
   }

   @Override
   public void sendRemoteEvent(RemoteEvent remoteEvent) {
      ResMessages resMessage = getResMessageType(remoteEvent);
      if (resMessage == null) {
         OseeLog.logf(OseeCoreModelEventServiceImpl.class, Level.INFO, "ResEventManager: Unhandled remote event [%s]",
            resMessage);
      } else if (getConnectionNode() == null) {
         OseeLog.logf(OseeCoreModelEventServiceImpl.class, Level.INFO,
            "ResEventManager: Connection node was null - unable to send remote event [%s]", resMessage);
      } else {
         getConnectionNode().send(resMessage, remoteEvent, this);
      }
   }

   private ResMessages getResMessageType(RemoteEvent remoteEvent) {
      ResMessages resMessage = null;
      if (remoteEvent != null) {
         for (ResMessages allowedMessage : messages.keySet()) {
            Class<?> messageClass = allowedMessage.getSerializationClass();
            if (messageClass.isAssignableFrom(remoteEvent.getClass())) {
               resMessage = allowedMessage;
               break;
            }
         }
      }
      return resMessage;
   }

   @Override
   public void addConnectionListener(ConnectionListener connectionListener) {
      getConnectionNode().addConnectionListener(connectionListener);
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
      getConnectionNode().removeConnectionListener(connectionListener);
   }

   @Override
   public void addFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      OseeLog.log(OseeCoreModelEventServiceImpl.class, Level.INFO, "Registering Client for Remote Events");

      for (Entry<ResMessages, Boolean> messageEntries : messages.entrySet()) {
         ResMessages resMessageID = messageEntries.getKey();
         boolean isVerbose = messageEntries.getValue();
         subscribe(resMessageID, resMessageID.getSerializationClass(), isVerbose, frameworkEventListener);
      }
   }

   @Override
   public void removeFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      OseeLog.log(OseeCoreModelEventServiceImpl.class, Level.INFO, "De-Registering Client for Remote Events");

      HashCollectionSet<ResMessages, OseeMessagingListener> listeners = subscriptions.get(frameworkEventListener);
      if (listeners != null) {
         for (ResMessages messageID : listeners.keySet()) {
            Collection<OseeMessagingListener> listernerList = listeners.getValues(messageID);
            if (listernerList != null) {
               for (OseeMessagingListener listener : listernerList) {
                  getConnectionNode().unsubscribe(messageID, listener, this);
               }
            }
         }
         subscriptions.remove(frameworkEventListener);
      }
   }

   private <T extends RemoteEvent> void subscribe(ResMessages messageId, Class<T> clazz, boolean isVerbose, IFrameworkEventListener frameworkEventListener) {
      OseeMessagingListener listener = new FrameworkRelayMessagingListener<>(clazz, frameworkEventListener, isVerbose);
      getConnectionNode().subscribe(messageId, listener, this);
      HashCollectionSet<ResMessages, OseeMessagingListener> listeners = subscriptions.get(frameworkEventListener);
      if (listeners == null) {
         listeners = new HashCollectionSet<>(true, HashSet::new);
         subscriptions.put(frameworkEventListener, listeners);
      }
      listeners.put(messageId, listener);
   }
}
