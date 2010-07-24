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
package org.eclipse.osee.framework.messaging;

import java.util.Properties;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ConnectionNode {

   /**
    * Subscribes listener to updates to message/s with the given id.
    * 
    * @param messageId
    * @param listener
    * @param statusCallback Used to relay the status of the subscribe command ( success, failure, etc). Can not be null.
    */
   void subscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);

   /**
    * Subscribes listener to updates to message/s with the given id.
    * 
    * @param messageId
    * @param listener
    * @param selector A string conforming to the SQL 92 syntax used for extra filtering of subscriptions
    * @param statusCallback Used to relay the status of the subscribe command ( success, failure, etc). Can not be null.
    */
   void subscribe(MessageID messageId, OseeMessagingListener listener, String selector, final OseeMessagingStatusCallback statusCallback);

   /**
    * Subscribes listener to updates to message/s with the given id. A default status handler will be used to log
    * failures.
    * 
    * @param messageId
    * @param listener
    */
   void subscribe(MessageID messageId, OseeMessagingListener listener);

   /**
    * Unsubscribes listener from updates for message with id equal to messageId.
    * 
    * @param messageId
    * @param listener
    * @param statusCallback Used to relay the status of the unsubscribe ( success, failure, etc). Can not be null.
    */
   void unsubscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);

   /**
    * Unsubscribes listener from updates for message with id equal to messageId. A default status handler will be used
    * to log failures.
    * 
    * @param messageId
    * @param listener
    */
   void unsubscribe(MessageID messageId, OseeMessagingListener listener);

   boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener);

   boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener);

   /**
    * Sends given message.
    * 
    * @param messageId
    * @param message
    * @param statusCallback Used to relay the status of the sending of the message ( success, failure, etc). Can not be
    * null.
    * @throws OseeCoreException
    */
   void send(MessageID messageId, Object message, final OseeMessagingStatusCallback statusCallback) throws OseeCoreException;

   /**
    * Attaches the properties provided to the message before sending.
    * 
    * @param messageId
    * @param message
    * @param properties Set of properties to be attached to the message.
    * @param statusCallback Used to relay the status of the sending of the message ( success, failure, etc). Can not be
    * null.
    * @throws OseeCoreException
    */
   void send(MessageID messageId, Object message, Properties properties, OseeMessagingStatusCallback statusCallback) throws OseeCoreException;

   /**
    * Sends given message. Uses default status handler to log failures in sending given message.
    * 
    * @param messageId
    * @param message
    * @throws OseeCoreException
    */
   void send(MessageID messageId, Object message) throws OseeCoreException;

   void addConnectionListener(ConnectionListener connectionListener);

   void removeConnectionListener(ConnectionListener connectionListener);

   void stop();

   String getSummary();

   String getSubscribers();

   String getSenders();

}
