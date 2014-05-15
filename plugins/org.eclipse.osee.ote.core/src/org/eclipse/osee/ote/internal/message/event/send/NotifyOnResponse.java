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
package org.eclipse.osee.ote.internal.message.event.send;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class NotifyOnResponse<T extends OteEventMessage> implements EventHandler {

   private final int responseId;
   private final Condition responseReceived;
   private final Lock lock;
   private final Class<T> clazz;
   private T recievedMessage;
   private volatile boolean responded = false;
   private final ServiceRegistration<EventHandler> reg;
   private T responseMessage;

   public NotifyOnResponse(Class<T> clazz, String responseTopic, int responseId, Lock lock, Condition responseReceived) {
      this.responseId = responseId;
      this.clazz = clazz;
      this.responseReceived = responseReceived;
      this.lock = lock;
      reg = OteEventMessageUtil.subscribe(responseTopic, this);
   }

   public NotifyOnResponse(T responseMessage, int responseId2, Lock lock2, Condition responseReceived2) {
      this((Class<T>)responseMessage.getClass(), responseMessage.getHeader().TOPIC.getValue(), responseId2, lock2, responseReceived2);
      this.responseMessage = responseMessage;
   }

   @Override
   public void handleEvent(Event event) {
      try {
         if(responseMessage == null){
            recievedMessage = clazz.newInstance();
         } else {
            recievedMessage = responseMessage;
         }
         OteEventMessageUtil.putBytes(event, recievedMessage);
         if(recievedMessage.getHeader().RESPONSE_ID.getValue() == responseId){
            lock.lock();
            try{
               responded = true;
               responseReceived.signal();
            }finally{
               lock.unlock();
            }
         } else {
            recievedMessage = null;
         }
      } catch (InstantiationException e) {
         recievedMessage = null;
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         recievedMessage = null;
         e.printStackTrace();
      }
   }

   public boolean hasResponse(){
      return responded;
   }
   
   public T getMessage(){
      return recievedMessage;
   }
   
   public void dispose(){
      reg.unregister();
   }
}
