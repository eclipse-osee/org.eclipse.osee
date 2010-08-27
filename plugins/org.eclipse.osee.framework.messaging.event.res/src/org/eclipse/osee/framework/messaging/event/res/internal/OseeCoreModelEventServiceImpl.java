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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.event.res.IFrameworkEventListener;
import org.eclipse.osee.framework.messaging.event.res.IOseeCoreModelEventService;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * @author Donald G. Dunne
 */
public class OseeCoreModelEventServiceImpl implements OseeMessagingStatusCallback, IOseeCoreModelEventService {

   private final Map<IFrameworkEventListener, HashCollection<ResMessages, OseeMessagingListener>> subscriptions =
      new HashMap<IFrameworkEventListener, HashCollection<ResMessages, OseeMessagingListener>>();
   private final Map<ResMessages, Boolean> messages;
   private final ConnectionNode connectionNode;

   public OseeCoreModelEventServiceImpl(ConnectionNode connectionNode, Map<ResMessages, Boolean> messages) {
      this.connectionNode = connectionNode;
      this.messages = messages;
   }

   @Override
   public void success() {
      // do nothing
   }

   @Override
   public void fail(Throwable th) {
      System.err.println(getClass().getSimpleName() + " - fail: " + th.getLocalizedMessage());
      th.printStackTrace();
   }

   @Override
   public void sendRemoteEvent(RemoteEvent remoteEvent) throws OseeCoreException {
      ResMessages resMessage = getResMessageType(remoteEvent);
      if (resMessage == null) {
         System.out.println(String.format("ResEventManager: Unhandled remote event [%s]", remoteEvent));
      } else if (connectionNode == null) {
         System.out.println(String.format(
            "ResEventManager: Connection node was null - unable to send remote event [%s] ", resMessage));
      } else {
         connectionNode.send(resMessage, remoteEvent, this);
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
      connectionNode.addConnectionListener(connectionListener);
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
      connectionNode.removeConnectionListener(connectionListener);
   }

   @Override
   public void addFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      System.out.println("Registering Client for Remote Events\n");

      for (Entry<ResMessages, Boolean> messageEntries : messages.entrySet()) {
         ResMessages resMessageID = messageEntries.getKey();
         boolean isVerbose = messageEntries.getValue();
         subscribe(resMessageID, resMessageID.getSerializationClass(), isVerbose, frameworkEventListener);
      }
   }

   @Override
   public void removeFrameworkListener(IFrameworkEventListener frameworkEventListener) {
      System.out.println("De-Registering Client for Remote Events\n");

      HashCollection<ResMessages, OseeMessagingListener> listeners = subscriptions.get(frameworkEventListener);
      if (listeners != null) {
         for (ResMessages messageID : listeners.keySet()) {
            Collection<OseeMessagingListener> listernerList = listeners.getValues(messageID);
            if (listernerList != null) {
               for (OseeMessagingListener listener : listernerList) {
                  connectionNode.unsubscribe(messageID, listener, this);
               }
            }
         }
         subscriptions.remove(frameworkEventListener);
      }
   }

   private <T extends RemoteEvent> void subscribe(ResMessages messageId, Class<T> clazz, boolean isVerbose, IFrameworkEventListener frameworkEventListener) {
      OseeMessagingListener listener = new FrameworkRelayMessagingListener<T>(clazz, frameworkEventListener, isVerbose);
      connectionNode.subscribe(messageId, listener, this);
      HashCollection<ResMessages, OseeMessagingListener> listeners = subscriptions.get(frameworkEventListener);
      if (listeners == null) {
         listeners = new HashCollection<ResMessages, OseeMessagingListener>(true, HashSet.class);
         subscriptions.put(frameworkEventListener, listeners);
      }
      listeners.put(messageId, listener);
   }
}
