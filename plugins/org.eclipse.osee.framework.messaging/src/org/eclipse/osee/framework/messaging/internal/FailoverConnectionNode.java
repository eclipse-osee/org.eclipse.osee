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
package org.eclipse.osee.framework.messaging.internal;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.jms.JMSException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.ConnectionNodeFailoverSupport;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.internal.activemq.OseeExceptionListener;
import org.eclipse.osee.framework.messaging.services.internal.OseeMessagingStatusImpl;

/**
 * This is written using ActiveMQ as the use case. So it will only retry connection and it will keep all subscribes so
 * that when a valid connection is made it will do all of the requested subscriptions.
 * 
 * @author Andrew M. Finkbeiner
 */
public class FailoverConnectionNode implements ConnectionNode, Runnable {

   private final ConnectionNodeFailoverSupport connectionNode;
   private final List<SavedSubscribe> savedSubscribes;
   private final List<ConnectionListener> connectionListeners;
   private final ScheduledExecutorService scheduledExecutor;
   private boolean lastConnectedState = false;
   private final OseeExceptionListener exceptionListener;
   private final ScheduledFuture<?> itemToCancel;

   public FailoverConnectionNode(ConnectionNodeFailoverSupport connectionNode, ScheduledExecutorService scheduledExecutor, OseeExceptionListener exceptionListener) {
      this.connectionNode = connectionNode;
      this.exceptionListener = exceptionListener;
      exceptionListener.setListener(this);
      savedSubscribes = new CopyOnWriteArrayList<>();
      connectionListeners = new CopyOnWriteArrayList<>();
      this.scheduledExecutor = scheduledExecutor;
      itemToCancel = this.scheduledExecutor.scheduleAtFixedRate(this, 60, 15, TimeUnit.SECONDS);
   }

   @Override
   public void send(MessageID messageId, Object message, OseeMessagingStatusCallback statusCallback) {
      send(messageId, message, null, statusCallback);
   }

   @Override
   public void send(MessageID messageId, Object message, Properties properties, OseeMessagingStatusCallback statusCallback) {
      attemptSmartConnect();
      if (lastConnectedState) {
         try {
            connectionNode.send(messageId, message, properties, statusCallback);
         } catch (OseeCoreException ex) {
            stop();
            run();
            connectionNode.send(messageId, message, properties, statusCallback);
         }
      }
   }

   @Override
   public void send(MessageID messageId, Object message) {
      String errorMessage = String.format("Error sending message(%s)", messageId.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.send(messageId, message, defaultErrorHandler);
   }

   private void attemptSmartConnect() {
      if (!lastConnectedState) {
         run();
      }
   }

   @Override
   public void stop() {
      itemToCancel.cancel(false);
      connectionNode.stop();
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      savedSubscribes.add(new SavedSubscribe(messageId, listener, statusCallback));
      attemptSmartConnect();
      connectionNode.subscribe(messageId, listener, statusCallback);
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener, String selector, OseeMessagingStatusCallback statusCallback) {
      savedSubscribes.add(new SavedSubscribe(messageId, listener, statusCallback));
      attemptSmartConnect();
      connectionNode.subscribe(messageId, listener, selector, statusCallback);
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener) {
      String errorMessage = String.format("Error subscribing message(%s)", messageId.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.subscribe(messageId, listener, defaultErrorHandler);
   }

   @Override
   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener) {
      return connectionNode.subscribeToReply(messageId, listener);
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener) {
      String errorMessage = String.format("Error unsubscribing message(%s)", messageId.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.unsubscribe(messageId, listener, defaultErrorHandler);
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      savedSubscribes.remove(new SavedSubscribe(messageId, listener, statusCallback));
      connectionNode.unsubscribe(messageId, listener, statusCallback);
   }

   @Override
   public boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener) {
      connectionNode.unsubscribteToReply(messageId, listener);
      return false;
   }

   @Override
   public void addConnectionListener(ConnectionListener connectionListener) {
      connectionListeners.add(connectionListener);
      if (lastConnectedState) {
         connectionListener.connected(this);
      } else {
         connectionListener.notConnected(this);
      }
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
      connectionListeners.remove(connectionListener);
   }

   private void subscribeToMessages() {
      for (SavedSubscribe subscribe : savedSubscribes) {
         if (subscribe.selector == null) {
            connectionNode.subscribe(subscribe.messageId, subscribe.listener, subscribe.statusCallback);
         } else {
            connectionNode.subscribe(subscribe.messageId, subscribe.listener, subscribe.selector,
               subscribe.statusCallback);
         }
      }
   }

   private class SavedSubscribe {
      MessageID messageId;
      OseeMessagingListener listener;
      OseeMessagingStatusCallback statusCallback;
      String selector;

      public SavedSubscribe(MessageID messageId, OseeMessagingListener listener, String selector, OseeMessagingStatusCallback statusCallback) {
         this.messageId = messageId;
         this.listener = listener;
         this.statusCallback = statusCallback;
         this.selector = selector;
      }

      public SavedSubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
         this.messageId = messageId;
         this.listener = listener;
         this.statusCallback = statusCallback;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + getOuterType().hashCode();
         result = prime * result + (listener == null ? 0 : listener.hashCode());
         result = prime * result + (messageId == null ? 0 : messageId.hashCode());
         result = prime * result + (statusCallback == null ? 0 : statusCallback.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         SavedSubscribe other = (SavedSubscribe) obj;
         if (!getOuterType().equals(other.getOuterType())) {
            return false;
         }
         if (listener == null) {
            if (other.listener != null) {
               return false;
            }
         } else if (!listener.equals(other.listener)) {
            return false;
         }
         if (messageId == null) {
            if (other.messageId != null) {
               return false;
            }
         } else if (!messageId.equals(other.messageId)) {
            return false;
         }
         if (statusCallback == null) {
            if (other.statusCallback != null) {
               return false;
            }
         }
         return true;
      }

      private FailoverConnectionNode getOuterType() {
         return FailoverConnectionNode.this;
      }

   }

   @Override
   public void run() {
      if (connectionNode.isConnected()) {
         connected();
      } else {
         try {
            connectionNode.start();
            subscribeToMessages();
            if (connectionNode.isConnected()) {
               connected();
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(FailoverConnectionNode.class, Level.FINE, ex);
            notConnected();
         }
      }
   }

   private synchronized void connected() {
      if (!lastConnectedState) {
         lastConnectedState = true;
         notifyConnectionListenersConnected();
      }

   }

   private void notifyConnectionListenersConnected() {
      for (ConnectionListener listener : connectionListeners) {
         listener.connected(this);
      }
   }

   private synchronized void notConnected() {
      if (lastConnectedState) {
         notifyConnectionListenersNotConnected();
      }
      lastConnectedState = false;
   }

   private void notifyConnectionListenersNotConnected() {
      for (ConnectionListener listener : connectionListeners) {
         listener.notConnected(this);
      }
   }

   @Override
   public String getSenders() {
      return connectionNode.getSenders();
   }

   @Override
   public String getSubscribers() {
      return connectionNode.getSubscribers();
   }

   @Override
   public String getSummary() {
      return connectionNode.getSummary();
   }

   public void onException(JMSException ex) {
      connectionNode.stop();
   }
}
