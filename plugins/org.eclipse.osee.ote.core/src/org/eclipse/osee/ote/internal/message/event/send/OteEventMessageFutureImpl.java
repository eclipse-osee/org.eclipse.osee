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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.send.OteEventMessageCallable;
import org.eclipse.osee.ote.message.event.send.OteEventMessageFuture;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class OteEventMessageFutureImpl<T extends OteEventMessage, R extends OteEventMessage> implements OteEventMessageFuture<T, R>, EventHandler{

   private final ServiceRegistration<EventHandler> reg;
   private final OteEventMessageCallable<T, R> callable;
   private final Class<R> recieveClasstype;
   private final int responseId;
   private final T sentMessage;
   private final ScheduledExecutorService ex;
   private final ReentrantLock lock;
   private final Condition condition;
   private final ScheduledFuture<?> wakeup;
   private TimeoutRunnable<T, R> timeoutRunnable;
   private volatile boolean gotResponse = false;

   public OteEventMessageFutureImpl(Class<R> recieveClasstype, OteEventMessageCallable<T, R> callable, T sentMessage, String responseTopic, int responseId, long timeout) {
      this.callable = callable;
      this.responseId = responseId;
      this.sentMessage = sentMessage;
      this.recieveClasstype = recieveClasstype;
      reg = OteEventMessageUtil.subscribe(responseTopic, this);
      lock = new ReentrantLock();
      condition = lock.newCondition();
      ex = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OteEventMessage Timeout");
            return th;
         }
      });
      timeoutRunnable = new TimeoutRunnable<T, R>(lock, condition, sentMessage, callable, this);
      wakeup = ex.schedule(timeoutRunnable, timeout, TimeUnit.MILLISECONDS);
   }

   @Override
   public void handleEvent(Event event) {
      try {
         R msg = recieveClasstype.newInstance();
         OteEventMessageUtil.putBytes(event, msg);
         if(msg.getHeader().RESPONSE_ID.getValue() == responseId){
            cancel();
            gotResponse = true;
            callable.call(sentMessage, msg);
            lock.lock();
            try{
               condition.signal();
            } finally {
               lock.unlock();
            }
         }
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (IllegalAccessException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void cancel(){
      dispose();
   }
   
   @Override
   public void waitForCompletion(){
      lock.lock();
      try{
         while(!timeoutRunnable.isTimedOut() && !this.wakeup.isDone()){
            try {
               condition.await();
            } catch (InterruptedException e) {
               OseeLog.log(getClass(), Level.SEVERE, e);
            }
         }
      } finally {
         lock.unlock();
      }
   }
   
   @Override
   public boolean isTimedOut(){
      return timeoutRunnable.isTimedOut();
   }
   
   @Override
   public boolean gotResponse(){
      return gotResponse ;
   }
   
   private void dispose(){
      reg.unregister();
      wakeup.cancel(false);
      this.ex.shutdown();
   }
}
