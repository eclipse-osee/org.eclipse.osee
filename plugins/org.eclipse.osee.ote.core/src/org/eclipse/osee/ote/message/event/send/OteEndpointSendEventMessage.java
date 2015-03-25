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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.osee.ote.OTEException;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.internal.message.event.send.NotifyOnResponse;
import org.eclipse.osee.ote.internal.message.event.send.OteEventMessageFutureImpl;
import org.eclipse.osee.ote.internal.message.event.send.OteEventMessageFutureMultipleResponseImpl;
import org.eclipse.osee.ote.internal.message.event.send.OteEventMessageResponseFutureImpl;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class OteEndpointSendEventMessage {

   private final OteUdpEndpoint endpoint;
   private final Lock lock;
   private final Condition responseReceived;
   private final InetSocketAddress destination;
   
   public OteEndpointSendEventMessage(OteUdpEndpoint eventAdmin, InetSocketAddress destination){
      this.endpoint = eventAdmin;
      this.destination = destination;
      lock = new ReentrantLock();
      responseReceived = lock.newCondition();
   }
   
   /**
    * sends a message and returns immediately
    */
   public void asynchSend(OteEventMessage message) {
      updateHeaderInfo(message);
      endpoint.getOteEndpointSender(destination).send(message);
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
      int responseId = updateHeaderInfo(message);
      String responseTopic = message.getHeader().RESPONSE_TOPIC.getValue();
      OteEventMessageFutureImpl<T, R> response = new OteEventMessageFutureImpl<T, R>(clazz, callable, message, responseTopic, responseId, timeout);
      endpoint.getOteEndpointSender(destination).send(message);
      return response;
   }
   
   /**
    * Registers for a callback of the given message type as specified by the RESPONSE_TOPIC element in the sent message 
    * and the class type passed in, then sends the given message and returns immediately.  The returned value can be used to 
    * wait for the response using waitForCompletion().  The callback expects you to handle both the response and the timeout case and to determine
    * when the appropriate number of responses has been received, by specifying it is complete. 
    * 
    * @param clazz - Type of OteEventMessage for the response
    * @param message - message to send
    * @param callable - callback executed when the response is recieved or if a timeout occurs or called immediately after the send if 
    *                   no response is expected
    * @param timeout - amount of time in milliseconds to wait for response before calling timeout on the passed in OteEventMessageCallable
    * @return   <T extends OteEventMessage> Future<T> - a future that contains the response message
    */
   public <T extends OteEventMessage, R extends OteEventMessage> OteEventMessageFuture<T, R> asynchSendAndMultipleResponse(Class<R> clazz, T message, OteEventMessageCallable<T, R> callable, long timeout){
      int responseId = updateHeaderInfo(message);
      String responseTopic = message.getHeader().RESPONSE_TOPIC.getValue();
      OteEventMessageFutureImpl<T, R> response = new OteEventMessageFutureMultipleResponseImpl<T,R>(clazz, callable, message, responseTopic, responseId, timeout);
      endpoint.getOteEndpointSender(destination).send(message);
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
      OteEventMessageResponseFutureImpl<R> response = new OteEventMessageResponseFutureImpl<R>(clazz, callable, topic);
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
         int responseId = updateHeaderInfo(message);
         message.getHeader().RESPONSE_TOPIC.setValue(responseTopic);
         NotifyOnResponse<T> response = new NotifyOnResponse<T>(clazz, responseTopic, responseId, lock, responseReceived);
         try{
            endpoint.getOteEndpointSender(destination).send(message);
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
         int responseId = updateHeaderInfo(sendMessage);
         sendMessage.getHeader().RESPONSE_TOPIC.setValue(responseMessage.getHeader().TOPIC.getValue());
         NotifyOnResponse<T> response = new NotifyOnResponse<T>(responseMessage, responseId, lock, responseReceived);
         try{
            endpoint.getOteEndpointSender(destination).send(sendMessage);
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
    * Increment the sequence number and set the source InetSocketAddress
    * 
    * @param message
    * @return returns the sequence number that was set
    */
   private int updateHeaderInfo(OteEventMessage message){
      int responseId = message.getHeader().MESSAGE_SEQUENCE_NUMBER.getValue();
      if(responseId >= Integer.MAX_VALUE){
         responseId = 1;
      } else {
         responseId++;
      }
      message.getHeader().MESSAGE_SEQUENCE_NUMBER.setValue(responseId);
      
      message.getHeader().ADDRESS.setAddress(endpoint.getLocalEndpoint().getAddress());
      message.getHeader().ADDRESS.setPort(endpoint.getLocalEndpoint().getPort());
      return responseId;
   }
   
}
