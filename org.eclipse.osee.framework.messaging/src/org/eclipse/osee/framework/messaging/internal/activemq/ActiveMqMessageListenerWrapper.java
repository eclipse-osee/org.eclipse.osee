/*
 * Created on Jul 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
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

/**
 * @author b1528444
 */
class ActiveMqMessageListenerWrapper implements MessageListener {

   private List<OseeMessagingListener> listeners;
   private MessageProducer producer;
   private Session session;
   private ActiveMqUtil activeMqUtil;
   
   ActiveMqMessageListenerWrapper(ActiveMqUtil activeMqUtil, MessageProducer producer, Session session){
      this.producer = producer;
      this.session = session;
      listeners = new CopyOnWriteArrayList<OseeMessagingListener>();
      this.activeMqUtil = activeMqUtil;
   }

   public void addListener(OseeMessagingListener listener){
      listeners.add(listener);
   }
   
   public void removeListener(OseeMessagingListener listener){
      listeners.remove(listener);
   }
   
   public boolean isEmpty(){
      return listeners.isEmpty();
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
	
	private void process(javax.jms.Message message, ReplyConnection replyConnection) throws JMSException, OseeCoreException{
	   Map<String, Object> headers = new HashMap<String, Object>();
	   for(OseeMessagingListener listener:listeners){
	      listener.process(activeMqUtil.translateMessage(message, listener.getClazz()), headers, replyConnection);
	   }
	}
}
