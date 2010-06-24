/*
 * Created on Feb 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.messaging.internal.JAXBUtil;

/**
 * @author Andrew M. Finkbeiner
 */
class ActiveMqUtil {

   ActiveMqUtil(){
      
   }
   
   Object translateMessage(Message message, Class<?> clazz) throws OseeCoreException, JMSException {
      Object messageBody = message;
      if (message instanceof TextMessage) {
         String text = ((TextMessage) message).getText();
         if (clazz != null) {
            try {
               messageBody = JAXBUtil.unmarshal(text, clazz);
            } catch (UnsupportedEncodingException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
         } else {
            messageBody = text;
         }
      } else if(message instanceof BytesMessage){
         int length = (int)((BytesMessage)message).getBodyLength();
         byte[] bytes = new byte[length];
         ((BytesMessage)message).readBytes(bytes);
         messageBody = bytes;
      } else if(message instanceof ObjectMessage){
    	 messageBody = ((ObjectMessage)message).getObject();  
      }
      return messageBody;
   }
   
   Message createMessage(Session session, Class<?> clazz, Object body) throws OseeCoreException, JMSException {
      body = tryToGetSerialized(clazz, body);
      if (body instanceof String) {
         return session.createTextMessage((String) body);
      } else if (body instanceof byte[]) {
         BytesMessage byteMessage = session.createBytesMessage();
         byteMessage.writeBytes((byte[]) body);
         return byteMessage;
      } else if (body instanceof Serializable){
    	 return session.createObjectMessage((Serializable)body);
      } else {
         throw new OseeCoreException(String.format("Unsupported java type [%s]", body.getClass().getName()));
      }
   }
   
   private Object tryToGetSerialized(Class<?> clazz, Object body) throws OseeCoreException {
      if (clazz != null) {
         try {
            return JAXBUtil.marshal(body);
         } catch (UnsupportedEncodingException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return body;
   }
}
