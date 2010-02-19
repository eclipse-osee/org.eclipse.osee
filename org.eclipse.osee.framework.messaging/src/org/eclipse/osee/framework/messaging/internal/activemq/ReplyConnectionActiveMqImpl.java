/*
 * Created on Feb 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;


/**
 * @author b1528444
 *
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
   public void send(Object body, Class<?> clazz, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      try {
         Message message = activeMqUtil.createMessage(session, clazz, body);
         message.setJMSCorrelationID(correlationId);
         producer.send(destReply, message);
      } catch (JMSException ex) {
         statusCallback.fail(ex);
         throw new OseeWrappedException(ex);
      }
   }

}
