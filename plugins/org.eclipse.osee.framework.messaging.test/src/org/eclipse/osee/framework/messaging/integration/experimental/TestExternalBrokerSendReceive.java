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

import static org.eclipse.osee.framework.messaging.data.DefaultNodeInfos.OSEE_JMS_NODE_INFO;
import static org.eclipse.osee.framework.messaging.data.TestMessages.JMS_TOPIC;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.data.TestMessage;
import org.junit.Test;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestExternalBrokerSendReceive extends BaseBrokerTesting {

   private final int messagesToSend = 10;
   private int messagesReceived = 0;

   @Test
   public void testSendingAndRecievingUsingJMS() throws Exception {
      startBroker();
      messagesReceived = 0;
      MessageStatusTest status1 = new MessageStatusTest(true);
      getConnectionNode().subscribe(JMS_TOPIC, new OseeMessagingListener(TestMessage.class) {
         @Override
         public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
            TestMessage msg = (TestMessage) message;
            System.out.println(msg.getMessage());
            messagesReceived++;
         }
      }, status1);
      status1.waitForStatus(500);

      Thread sending = new Thread(new Runnable() {
         @Override
         public void run() {
            for (int i = 0; i < messagesToSend; i++) {
               MessageStatusTest status2 = new MessageStatusTest(true);
               try {
                  TestMessage message = new TestMessage();
                  message.setMessage("TestMessage " + i);
                  getMessaging().get(OSEE_JMS_NODE_INFO).send(JMS_TOPIC, message, status2);
                  System.out.println("sending " + i);
               } catch (OseeCoreException ex) {
                  fail(ex.getMessage());
               }
               status2.waitForStatus(500);

            }
         }
      });
      sending.start();
      Thread.sleep(10000);

      assertTrue(String.format("sent[%d] != recieved[%d]", messagesToSend, messagesReceived),
         messagesToSend == messagesReceived);
      stopBroker();
   }
}
