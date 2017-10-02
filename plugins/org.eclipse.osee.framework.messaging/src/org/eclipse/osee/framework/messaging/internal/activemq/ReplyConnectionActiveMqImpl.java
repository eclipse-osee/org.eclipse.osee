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
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.logging.Level;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.internal.Activator;

/**
 * @author Andrew M. Finkbeiner
 */
class ReplyConnectionActiveMqImpl implements ReplyConnection {

   private final boolean isReplyRequested;
   private MessageProducer producer;
   private Destination destReply;
   private String correlationId;
   private Session session;
   private ActiveMqUtil activeMqUtil;

   ReplyConnectionActiveMqImpl(ActiveMqUtil activeMqUtil, Session session, MessageProducer producer, Destination destReply, String correlationId) {
      isReplyRequested = true;
      this.producer = producer;
      this.destReply = destReply;
      this.correlationId = correlationId;
      this.session = session;
      this.activeMqUtil = activeMqUtil;
   }

   ReplyConnectionActiveMqImpl() {
      isReplyRequested = false;
   }

   @Override
   public boolean isReplyRequested() {
      return isReplyRequested;
   }

   @Override
   public void send(Object body, Class<?> clazz, OseeMessagingStatusCallback statusCallback)  {
      try {
         Message message = activeMqUtil.createMessage(session, clazz, body);
         message.setJMSCorrelationID(correlationId);
         producer.send(destReply, message);
         OseeLog.logf(Activator.class, Level.INFO, "Sending Reply Message %s", message.toString());
      } catch (JMSException ex) {
         statusCallback.fail(ex);
         OseeCoreException.wrapAndThrow(ex);
      }
   }

}
