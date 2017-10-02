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

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQDestination;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.ConsoleDebugSupport;
import org.eclipse.osee.framework.messaging.internal.ServiceUtility;

/**
 * @author Andrew M. Finkbeiner
 */
class ActiveMqMessageListenerWrapper implements MessageListener {

   private final OseeMessagingListener listener;
   private final MessageProducer producer;
   private final Session session;
   private final ActiveMqUtil activeMqUtil;
   private final ExecutorService executor;

   ActiveMqMessageListenerWrapper(ActiveMqUtil activeMqUtil, MessageProducer producer, Session session, OseeMessagingListener listener) {
      this.producer = producer;
      this.session = session;
      this.listener = listener;
      this.activeMqUtil = activeMqUtil;
      executor = Executors.newSingleThreadExecutor();
   }

   @Override
   public void onMessage(javax.jms.Message jmsMessage) {
      try {
         ConsoleDebugSupport support = ServiceUtility.getConsoleDebugSupport();
         if (support != null) {
            if (support.getPrintReceives()) {
               System.out.println(new Date() + " : " + jmsMessage.getJMSMessageID());
               System.out.println("MESSAGE:");
               System.out.println(jmsMessage.toString());
               System.out.println("-----------------------------------------------------------------------------");
            }
            support.addReceive(jmsMessage);
         }
         Destination destReply = jmsMessage.getJMSReplyTo();
         if (destReply != null) {
            ActiveMQDestination dest = (ActiveMQDestination) jmsMessage.getJMSDestination();
            String correlationId = dest.getPhysicalName();
            ReplyConnectionActiveMqImpl replyConnectionActiveMqImpl =
               new ReplyConnectionActiveMqImpl(activeMqUtil, session, producer, destReply, correlationId);
            process(jmsMessage, replyConnectionActiveMqImpl);
         } else {
            process(jmsMessage, new ReplyConnectionActiveMqImpl());
         }
      } catch (JMSException ex) {
         OseeLog.log(ActiveMqMessageListenerWrapper.class, Level.SEVERE, ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(ActiveMqMessageListenerWrapper.class, Level.SEVERE, ex);
      }
   }

   OseeMessagingListener getListener() {
      return listener;
   }

   private void process(javax.jms.Message message, ReplyConnection replyConnection) throws JMSException {
      executor.submit(new ListenerProcessRunnable(message, replyConnection));
   }

   class ListenerProcessRunnable implements Runnable {

      private final javax.jms.Message message;
      private final ReplyConnection replyConnection;

      public ListenerProcessRunnable(Message message, ReplyConnection replyConnection) {
         this.message = message;
         this.replyConnection = replyConnection;
      }

      @Override
      public void run() {
         try {
            Map<String, Object> headers = new HashMap<>();
            Enumeration<?> propertyNames = message.getPropertyNames();
            while (propertyNames.hasMoreElements()) {
               String name = (String) propertyNames.nextElement();
               Object element = message.getObjectProperty(name);
               headers.put(name, element);
            }
            listener.process(activeMqUtil.translateMessage(message, listener.getClazz()), headers, replyConnection);
            OseeLog.log(Activator.class, Level.FINE,
               String.format("recieved message %s - %s", message.getJMSDestination().toString(), message.toString()));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Exception ", ex);
         }
      }

   }

}
