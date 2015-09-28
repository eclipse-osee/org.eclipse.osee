/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.event.send;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.internal.message.event.send.NotifyOnResponse;
import org.eclipse.osee.ote.internal.message.event.send.OteEventMessageFutureImpl;
import org.eclipse.osee.ote.internal.message.event.send.OteEventMessageResponseFutureImpl;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.service.event.EventAdmin;

public class OteSendEventMessage {

   private final EventAdmin eventAdmin;
   private final Lock lock;
   private final Condition responseReceived;
   
   public OteSendEventMessage(EventAdmin eventAdmin){
      this.eventAdmin = eventAdmin;
      lock = new ReentrantLock();
      responseReceived = lock.newCondition();
   }
   
   /**
    * sends a message and returns immediately
    */
   public void asynchSend(OteEventMessage message) {
      incrementSequenceNumber(message);
      OteEventMessageUtil.postEvent(message, eventAdmin);
   }
   
   /**
    * Registers for a callback of the given message type as specified by the RESPONSE_TOPIC element in the sent message 
    * and the class type passed in, then sends the given message and returns immediately.  The returned value can be used to 
    * wait for the response using waitForCompletion().  The callback expects you to handle both the response and the timeout case. 
    * 
    * @param clazz - Type of OteEventMessage for the response
    * @param message - message to send
    * @param callable - callback executed when the response is recieved or if a timeout occurs or called immediately after the send if 
    *                   no response is expected
    * @param timeout - amount of time in milliseconds to wait for response before calling timeout on the passed in OteEventMessageCallable
    * @return   <T extends OteEventMessage> Future<T> - a future that contains the response message
    */
   public <T extends OteEventMessage, R extends OteEventMessage> OteEventMessageFuture<T, R> asynchSendAndResponse(Class<R> clazz, T message, OteEventMessageCallable<T, R> callable, long timeout){
      int responseId = incrementSequenceNumber(message);
      String responseTopic = message.getHeader().RESPONSE_TOPIC.getValue();
      OteEventMessageFutureImpl<T, R> response = new OteEventMessageFutureImpl<>(clazz, callable, message, responseTopic, responseId, timeout);
      OteEventMessageUtil.postEvent(message, eventAdmin);
      return response;
   }
   
   /**
    * Registers for a callback of the given message type and topic. 
    * 
    * @param clazz - Type of OteEventMessage for the response
    * @param callable - callback executed when the response is recieved
    * @return   a future that you should cancel when done listening so resources can be cleaned up.
    */
   public <R extends OteEventMessage> OteEventMessageResponseFuture<R> asynchResponse(Class<R> clazz, String topic, OteEventMessageResponseCallable<R> callable){
      OteEventMessageResponseFutureImpl<R> response = new OteEventMessageResponseFutureImpl<>(clazz, callable, topic);
      return response;
   }
   
   /**
    * Sends a message and waits for a response.
    * 
    * @param class - return type
    * @param message - message to send 
    * @param timeout - timeout in milliseconds
    * @return <T extends OteEventMessage> T - NULL if the timeout occurs before a response, otherwise returns the 
    *          message specified by the RESPONSE_TOPIC field in the passed in message header.
    * @throws Exception 
    */
   public <T extends OteEventMessage> T synchSendAndResponse(Class<T> clazz, String responseTopic, OteEventMessage message, long timeout) throws OTEException {
      lock.lock();
      try{
         int responseId = incrementSequenceNumber(message);
         message.getHeader().RESPONSE_TOPIC.setValue(responseTopic);
         NotifyOnResponse<T> response = new NotifyOnResponse<>(clazz, responseTopic, responseId, lock, responseReceived);
         try{
            OteEventMessageUtil.postEvent(message, eventAdmin);
            long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
            while(nanos > 0 && !response.hasResponse()) {
               try {
                  nanos = responseReceived.awaitNanos(nanos);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         } finally {
            response.dispose();
         }
         return response.getMessage();
      } finally {
         lock.unlock();
      }
   }
   
   /**
    * Sends a message and waits for a response.
    * 
    * @param T - response Message to populate type
    * @param message - message to send 
    * @param timeout - timeout in milliseconds
    * @return <T extends OteEventMessage> T - NULL if the timeout occurs before a response, otherwise returns the 
    *          message specified by the RESPONSE_TOPIC field in the passed in message header.
    * @throws Exception 
    */
   public <T extends OteEventMessage> T synchSendAndResponse(T responseMessage, OteEventMessage sendMessage, long timeout) throws OTEException {
      lock.lock();
      try{
         int responseId = incrementSequenceNumber(sendMessage);
         sendMessage.getHeader().RESPONSE_TOPIC.setValue(responseMessage.getHeader().TOPIC.getValue());
         NotifyOnResponse<T> response = new NotifyOnResponse<>(responseMessage, responseId, lock, responseReceived);
         try{
            OteEventMessageUtil.postEvent(sendMessage, eventAdmin);
            long nanos = TimeUnit.MILLISECONDS.toNanos(timeout);
            while(nanos > 0 && !response.hasResponse()) {
               try {
                  nanos = responseReceived.awaitNanos(nanos);
               } catch (InterruptedException e) {
                  e.printStackTrace();
               }
            }
         } finally {
            response.dispose();
         }
         return response.getMessage();
      } finally {
         lock.unlock();
      }
   }

   
   
   private int incrementSequenceNumber(OteEventMessage message){
      int responseId = message.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue();
      if(responseId >= Integer.MAX_VALUE){
         responseId = 1;
      } else {
         responseId++;
      }
      message.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(responseId);
      return responseId;
   }
   
}
