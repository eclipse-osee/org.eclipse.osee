/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.condition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import org.eclipse.osee.ote.core.MethodFormatter;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.CheckPoint;
import org.eclipse.osee.ote.core.testPoint.Operation;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.MsgWaitResult;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

public class MultiMessageCondition {


   private static final class MessageCounter {
      private final Message message;
      private int count;
      /**
       * @param count
       * @param messageData
       */
      public MessageCounter(Message message, int initialCount) {
         this.count = initialCount;
         this.message = message;
      }

      public void incrementCount() {
         count++;
      }

      public int getCount() {
         return count;
      }

   }
   private static final class Listener implements IOSEEMessageListener, ITimeout {
      private volatile boolean isTimedOut = false;
      private final Message[] messages;
      private final HashSet<MessageData> messagesNotSeen = new HashSet<MessageData>();
      private final HashMap<MessageData, MessageCounter> hitCount = new HashMap<MessageData, MessageCounter>();


      private Listener(Message... messages) {
         this.messages = messages;
         for (Message message : messages) {
            MessageData data = message.getActiveDataSource();
            messagesNotSeen.add(data);
            hitCount.put(data, new MessageCounter(message, 0));
         }

      }

      synchronized void begin() {
         for (Message message : messages) {
            message.addListener(this);
         }
      }

      void end() {
         for (Message message : messages) {
            message.removeListener(this);
         }
      }

      @Override
      public synchronized void onDataAvailable(MessageData data, MemType type) throws MessageSystemException {
         MessageCounter count = hitCount.get(data);
         if (count != null) {
            if (count.getCount() == 0) {
               // remove this message from the list of messages not seen
               messagesNotSeen.remove(data);
            }
            count.incrementCount();
         }
         notify();
      }

      @Override
      public void onInitListener() throws MessageSystemException {
         // TODO Auto-generated method stub

      }

      @Override
      public boolean isTimedOut() {
         return isTimedOut;
      }

      @Override
      public void setTimeout(boolean timeout) {
         isTimedOut = timeout;
      }

      public synchronized boolean waitForTransmission() throws InterruptedException {
         wait();
         return !isTimedOut;

      }

      public void fillInMessagesReceived(Collection<MessageCounter> list) {
         for (Message msg : messages) {
            // items are removed from the hit list when they are found so if they are not
            // in the hit list then that means we received it

            MessageCounter counter = hitCount.get(msg.getActiveDataSource());
            if (counter != null) {
               list.add(counter);
            }
         }
      }

      public int getHitCount(Message msg) {
         MessageCounter count = hitCount.get(msg.getActiveDataSource());
         return count != null ? count.getCount() : 0;
      }

      public void fillInMessagesNotReceived(Collection<Message> list) {

         for (Message msg : messages) {
            // if we found it in the hit list then that means we didn't see it
            if (messagesNotSeen.contains(msg.getActiveDataSource())) {
               list.add(msg);
            }
         }
      }
   }

   public MultiMessageCondition() {
   }

   public MsgWaitResult waitForAllTransmissions(ITestEnvironmentAccessor accessor, int timeout, Collection<MessageCounter> msgsNotSeen, Message... messages) throws InterruptedException {
      long time = accessor.getEnvTime();
      boolean seenAllMessages = false;
      int count = 0;
      if (timeout > 0) {
         boolean done = false;
         Listener listener = new Listener(messages);
         listener.begin();
         try {
            final ICancelTimer cancelTimer = accessor.setTimerFor(listener, timeout);
            while (!done) {
               if (listener.waitForTransmission()) {
                  seenAllMessages = listener.messagesNotSeen.isEmpty();
                  count++;
               }
               done = seenAllMessages | listener.isTimedOut();
            }
            cancelTimer.cancelTimer();
         } finally {
            listener.end();
            if (msgsNotSeen != null) {
               listener.fillInMessagesReceived(msgsNotSeen);
            }
         }
      }
      time = accessor.getEnvTime() - time;
      return new MsgWaitResult(time, count, seenAllMessages);
   }



   public MsgWaitResult waitForAnyTransmission(ITestEnvironmentAccessor accessor, int timeout, Collection<MessageCounter> msgsSeen, Message... messages) throws InterruptedException {
      long time = accessor.getEnvTime();
      boolean anyTransmissions = false;
      int count = 0;
      if (timeout > 0) {
         boolean done = false;
         Listener listener = new Listener(messages);
         listener.begin();
         try {
            final ICancelTimer cancelTimer = accessor.setTimerFor(listener, timeout);
            while (!done) {
               if (listener.waitForTransmission()) {
                  count++;
                  anyTransmissions = true;
               }
               done = anyTransmissions | listener.isTimedOut();
            }
            cancelTimer.cancelTimer();
         } finally {
            listener.end();
            if (msgsSeen != null) {
               listener.fillInMessagesReceived(msgsSeen);
            }
         }
      }
      time = accessor.getEnvTime() - time;
      return new MsgWaitResult(time, count, anyTransmissions);
   }

   public void checkNoTransmissions(ITestAccessor accessor, int timeout, Message... messages) throws InterruptedException {
      MethodFormatter mf = new MethodFormatter();
      mf.add(timeout);
      for (Message msg : messages) {
         mf.add(msg.getName());
      }
      accessor.getLogger().methodCalledOnObject(accessor, "MultMessage", mf);
      LinkedList<MessageCounter> msgsReceived = new LinkedList<MessageCounter>();
      MsgWaitResult result = waitForAnyTransmission(accessor, timeout, msgsReceived, messages);
      if (!result.isPassed()) {
         CheckPoint cp =
            new CheckPoint("MESSAGE_TRANSMISSION.NONE", "NONE", result.isPassed() ? "AT LEAST ONE" : "NONE",
                  !result.isPassed(), result.getXmitCount(), result.getElapsedTime());
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), cp);
      } else {
         CheckGroup group = new CheckGroup(Operation.AND, "MESSAGE_TRANSMISSION.NONE");
         for (MessageCounter counter : msgsReceived) {
            group.add(new CheckPoint("TRANSMISSIONS OF " + counter.message.getName(), "0", Integer.toString(counter.count), counter.count == 0, counter.count, result.getElapsedTime()));
         }
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), group);
      }
      accessor.getLogger().methodEnded(accessor);
   }

   public void checkAnyTransmissions(ITestAccessor accessor, int timeout, Message... messages) throws InterruptedException {
      MethodFormatter mf = new MethodFormatter();
      mf.add(timeout);
      for (Message msg : messages) {
         mf.add(msg.getName());
      }
      accessor.getLogger().methodCalledOnObject(accessor, "MultMessage", mf);
      LinkedList<MessageCounter> msgsReceived = new LinkedList<MessageCounter>();
      MsgWaitResult result = waitForAnyTransmission(accessor, timeout, msgsReceived, messages);
      CheckPoint cp =
         new CheckPoint("MESSAGE_TRANSMISSION.ANY", "AT LEAST ONE", result.isPassed() ? "AT LEAST ONE" : "NONE",
               result.isPassed(), result.getXmitCount(), result.getElapsedTime());
      accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), cp);
      accessor.getLogger().methodEnded(accessor);
   }

   public void checkAllTransmissions(ITestAccessor accessor, int timeout, Message... messages) throws InterruptedException {
      MethodFormatter mf = new MethodFormatter();
      mf.add(timeout);
      for (Message msg : messages) {
         mf.add(msg.getName());
      }
      accessor.getLogger().methodCalledOnObject(accessor, "MultMessage", mf);
      LinkedList<MessageCounter> msgsReceived = new LinkedList<MessageCounter>();
      MsgWaitResult result = waitForAllTransmissions(accessor, timeout, msgsReceived, messages);
      if (result.isPassed()) {
         CheckPoint cp =
            new CheckPoint("MESSAGE_TRANSMISSION.ALL", "ALL", result.isPassed() ? "ALL" : "NOT ALL",
                  result.isPassed(), result.getXmitCount(), result.getElapsedTime());
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), cp);
      } else {
         CheckGroup group = new CheckGroup(Operation.AND, "MESSAGE_TRANSMISSION.ALL");
         for (MessageCounter counter : msgsReceived) {
            group.add(new CheckPoint("TRANSMISSIONS OF " + counter.message.getName(), "GREATER THAN 0", Integer.toString(counter.count), counter.count > 0, counter.count, result.getElapsedTime()));
         }
         accessor.getLogger().testpoint(accessor, accessor.getTestScript(), accessor.getTestCase(), group);
      }


      accessor.getLogger().methodEnded(accessor);
   }

   public static void main(String[] args) {
      MultiMessageCondition mmc = new MultiMessageCondition();

   }
}
