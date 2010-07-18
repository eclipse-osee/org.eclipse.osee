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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQDestination;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.internal.Activator;

/**
 * @author Andrew M. Finkbeiner
 */
class ActiveMqMessageListenerWrapper implements MessageListener {

   private OseeMessagingListener listener;
   private MessageProducer producer;
   private Session session;
   private ActiveMqUtil activeMqUtil;
   
   ActiveMqMessageListenerWrapper(ActiveMqUtil activeMqUtil, MessageProducer producer, Session session, OseeMessagingListener listener){
      this.producer = producer;
      this.session = session;
      this.listener = listener;
      this.activeMqUtil = activeMqUtil;
   }

	public void onMessage(javax.jms.Message jmsMessage){
      try{
         Destination destReply = jmsMessage.getJMSReplyTo();
         if(destReply != null){
            ActiveMQDestination dest = (ActiveMQDestination)jmsMessage.getJMSDestination();
            String correlationId = dest.getPhysicalName();
            ReplyConnectionActiveMqImpl replyConnectionActiveMqImpl = new ReplyConnectionActiveMqImpl(activeMqUtil, session, producer, destReply, correlationId);
            process(jmsMessage, replyConnectionActiveMqImpl);
         } else {
            process(jmsMessage, new ReplyConnectionActiveMqImpl());
         } 
      } catch (JMSException ex){
         OseeLog.log(ActiveMqMessageListenerWrapper.class, Level.SEVERE, ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(ActiveMqMessageListenerWrapper.class, Level.SEVERE, ex);
      }
	}
	
	OseeMessagingListener getListener(){
	   return listener;
	}
	
	private void process(javax.jms.Message message, ReplyConnection replyConnection) throws JMSException, OseeCoreException{
	   Map<String, Object> headers = new HashMap<String, Object>();
	   listener.process(activeMqUtil.translateMessage(message, listener.getClazz()), headers, replyConnection);
	   OseeLog.log(Activator.class, Level.FINE, String.format("recieved message %s - %s", message.getJMSDestination().toString(), message.toString()));
	}

}
