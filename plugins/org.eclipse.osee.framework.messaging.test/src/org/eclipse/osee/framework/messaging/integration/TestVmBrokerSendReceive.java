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

package org.eclipse.osee.framework.messaging.integration;

import static junit.framework.Assert.assertEquals;
import static org.eclipse.osee.framework.messaging.data.DefaultNodeInfos.OSEE_VM_BROKER_URI;
import static org.eclipse.osee.framework.messaging.data.DefaultNodeInfos.OSEE_VM_NODE;
import static org.eclipse.osee.framework.messaging.data.TestMessages.VM_TOPIC;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.data.StatusCallback;
import org.eclipse.osee.framework.messaging.data.TestMessage;
import org.eclipse.osee.framework.messaging.data.TestMessageListener;
import org.eclipse.osee.framework.messaging.data.TestMessageListener.Data;
import org.eclipse.osee.framework.messaging.internal.MessageServiceController.BrokerType;
import org.eclipse.osee.framework.messaging.rules.MessageBroker;
import org.eclipse.osee.framework.messaging.rules.MessageConnection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestVmBrokerSendReceive {

   private static final int MESSAGE_COUNT = 10;

   @Rule
   public MessageBroker embeddedBroker = new MessageBroker(BrokerType.VM_BROKER, OSEE_VM_BROKER_URI, this);

   @MessageConnection(name = OSEE_VM_NODE, brokerUri = OSEE_VM_BROKER_URI)
   private ConnectionNode connection;

   @Mock
   private OseeMessagingStatusCallback sendCallback;

   private StatusCallback subscribeCallback;
   private TestMessageListener messageListener;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      subscribeCallback = new StatusCallback(1);
      messageListener = new TestMessageListener(MESSAGE_COUNT);
   }

   @SuppressWarnings("deprecation")
   @Test
   public void testSendingAndReceivingUsingVM() throws InterruptedException {
      synchronized (subscribeCallback) {
         connection.subscribe(VM_TOPIC, messageListener, subscribeCallback);
         subscribeCallback.wait(500);
      }

      assertEquals(false, subscribeCallback.failed());
      assertEquals(1, subscribeCallback.getTotalReceived());

      Thread sending = new Thread(new Runnable() {
         @Override
         public void run() {
            for (int index = 0; index < MESSAGE_COUNT; index++) {
               TestMessage message = new TestMessage();
               message.setMessage("TestMessage " + index);
               try {
                  connection.send(VM_TOPIC, message, sendCallback);
               } catch (OseeCoreException ex) {
                  fail(ex.getMessage());
               }
            }
         }
      });
      synchronized (messageListener) {
         sending.start();
         messageListener.wait(3000);
      }

      verify(sendCallback, times(MESSAGE_COUNT)).success();
      verify(sendCallback, times(0)).fail(any(Throwable.class));

      assertEquals(MESSAGE_COUNT, messageListener.getTotalReceived());

      List<Data> data = messageListener.getData();
      assertEquals(MESSAGE_COUNT, data.size());

      for (int index = 0; index < MESSAGE_COUNT; index++) {
         TestMessage message = data.get(index).getMessage();
         assertEquals("TestMessage " + index, message.getMessage());
      }
   }
}
