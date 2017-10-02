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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.internal.JAXBUtil;

/**
 * @author Andrew M. Finkbeiner
 */
class ActiveMqUtil {

   ActiveMqUtil() {

   }

   Object translateMessage(Message message, Class<?> clazz) throws JMSException {
      Object messageBody = message;
      if (message instanceof TextMessage) {
         String text = ((TextMessage) message).getText();
         if (clazz != null) {
            try {
               messageBody = JAXBUtil.unmarshal(text, clazz);
            } catch (Exception ex) {
               throw new OseeCoreException(ex.getCause(), "Unmarshal exception for text [%s] and class [%s]", text,
                  clazz);
            }
         } else {
            messageBody = text;
         }
      } else if (message instanceof BytesMessage) {
         int length = (int) ((BytesMessage) message).getBodyLength();
         byte[] bytes = new byte[length];
         ((BytesMessage) message).readBytes(bytes);
         messageBody = bytes;
      } else if (message instanceof ObjectMessage) {
         messageBody = ((ObjectMessage) message).getObject();
      }
      return messageBody;
   }

   Message createMessage(Session session, Class<?> clazz, Object body) throws JMSException {
      body = tryToGetSerialized(clazz, body);
      if (body instanceof String) {
         return session.createTextMessage((String) body);
      } else if (body instanceof byte[]) {
         BytesMessage byteMessage = session.createBytesMessage();
         byteMessage.writeBytes((byte[]) body);
         return byteMessage;
      } else if (body instanceof Serializable) {
         return session.createObjectMessage((Serializable) body);
      } else {
         throw new OseeCoreException("Unsupported java type [%s]", body.getClass().getName());
      }
   }

   private Object tryToGetSerialized(Class<?> clazz, Object body) {
      if (clazz != null) {
         try {
            return JAXBUtil.marshal(body);
         } catch (UnsupportedEncodingException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return body;
   }
}
