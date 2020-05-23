/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;

/**
 * @author Roberto E. Escobar
 */
public class TestMessageListener extends OseeMessagingListener {

   private final int expectedCount;
   private final List<Data> data = new ArrayList<>();
   private volatile int currentCount;
   private volatile boolean wasUpdateReceived;

   public TestMessageListener(int expectedCount) {
      super(TestMessage.class);
      this.expectedCount = expectedCount;
      reset();
   }

   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      currentCount++;
      data.add(new Data((TestMessage) message, headers, replyConnection));
      if (currentCount == expectedCount) {
         synchronized (this) {
            wasUpdateReceived = true;
            notify();
         }
      }
   }

   public void reset() {
      data.clear();
      currentCount = 0;
      wasUpdateReceived = false;
   }

   public int getTotalReceived() {
      return wasUpdateReceived() ? currentCount : 0;
   }

   private boolean wasUpdateReceived() {
      return wasUpdateReceived;
   }

   public List<Data> getData() {
      return data;
   }

   public static class Data {
      private final TestMessage message;
      private final Map<String, Object> headers;
      private final ReplyConnection replyConnection;

      public Data(TestMessage message, Map<String, Object> headers, ReplyConnection replyConnection) {
         super();
         this.message = message;
         this.headers = headers;
         this.replyConnection = replyConnection;
      }

      public TestMessage getMessage() {
         return message;
      }

      public Map<String, Object> getHeaders() {
         return headers;
      }

      public ReplyConnection getReplyConnection() {
         return replyConnection;
      }

   }

}
