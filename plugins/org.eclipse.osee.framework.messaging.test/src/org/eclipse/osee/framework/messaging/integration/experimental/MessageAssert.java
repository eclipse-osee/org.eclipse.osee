/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging.integration.experimental;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.data.TestMessage;
import org.eclipse.osee.framework.messaging.data.TestMessages;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageAssert {

   private MessageAssert() {
      // Static Class
   }

   public static void verifyJMSSendShouldFail(ConnectionNode node) {
      MessageStatusTest status = new MessageStatusTest(false);
      try {
         node.send(TestMessages.test, "test", status);
      } catch (OseeCoreException ex) {
         OseeLog.log(MessageAssert.class, Level.SEVERE, ex);
      }
      status.waitForStatus(5000);
   }

   public static void verifyJMSSendShouldPass(ConnectionNode node) {
      MessageStatusTest status = new MessageStatusTest(true);
      try {
         node.send(TestMessages.test, "test", status);
      } catch (OseeCoreException ex) {
         OseeLog.log(MessageAssert.class, Level.SEVERE, ex);
      }
      status.waitForStatus(5000);
   }

   public static void verifyJMSSubscribeShouldFail(ConnectionNode node) {
      MessageStatusTest status = new MessageStatusTest(false);
      OseeMessagingListener listener = new OseeMessagingListener(TestMessage.class) {
         @Override
         public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
            TestMessage msg = (TestMessage) message;
            System.out.println(msg.getMessage());
         }
      };

      node.subscribe(TestMessages.test2, listener, status);
      status.waitForStatus(5000);
      node.unsubscribe(TestMessages.test2, listener, status);//we have to remove so we don't get a false fail later on
   }

   public static void verifyJMSSubscribeShouldPass(ConnectionNode node) {
      MessageStatusTest status = new MessageStatusTest(true);
      node.subscribe(TestMessages.test2, new OseeMessagingListener(TestMessage.class) {
         @Override
         public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
            TestMessage msg = (TestMessage) message;
            System.out.println(msg.getMessage());
         }
      }, status);
      status.waitForStatus(5000);
   }
}
