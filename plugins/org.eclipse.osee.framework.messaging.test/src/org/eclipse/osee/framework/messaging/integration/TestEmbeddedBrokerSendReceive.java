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
package org.eclipse.osee.framework.messaging.integration;

import static org.eclipse.osee.framework.messaging.data.DefaultNodeInfos.OSEE_JMS_BROKER_URI;
import static org.eclipse.osee.framework.messaging.data.DefaultNodeInfos.OSEE_JMS_NODE;
import static org.eclipse.osee.framework.messaging.data.TestMessages.JMS_TOPIC;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.data.StatusCallback;
import org.eclipse.osee.framework.messaging.data.TestMessage;
import org.eclipse.osee.framework.messaging.data.TestMessageListener;
import org.eclipse.osee.framework.messaging.data.TestMessageListener.Data;
import org.eclipse.osee.framework.messaging.data.TestMessages;
import org.eclipse.osee.framework.messaging.internal.MessageServiceController.BrokerType;
import org.eclipse.osee.framework.messaging.rules.MessageBroker;
import org.eclipse.osee.framework.messaging.rules.MessageConnection;
import org.eclipse.osee.framework.messaging.services.internal.OseeMessagingStatusImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Andrew M. Finkbeiner
 */
public class TestEmbeddedBrokerSendReceive {

   private static final int TOTAL_SUBSCRIBERS = 20;

   @Rule
   public MessageBroker embeddedBroker = new MessageBroker(BrokerType.EMBEDDED_BROKER, OSEE_JMS_BROKER_URI, this);

   @MessageConnection(name = OSEE_JMS_NODE, brokerUri = OSEE_JMS_BROKER_URI)
   private ConnectionNode connection;

   @Mock
   private OseeMessagingStatusCallback sendCallback;

   private StatusCallback subscribeCallback;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      subscribeCallback = new StatusCallback(TOTAL_SUBSCRIBERS);
   }

   @Ignore("Intermittent failures")
   @Test
   public void testMultipleConsumers() throws Exception {
      List<TestMessageListener> listeners = new ArrayList<>();
      for (int index = 0; index < TOTAL_SUBSCRIBERS; index++) {
         TestMessageListener messageListener = new TestMessageListener(1);
         listeners.add(messageListener);

         synchronized (subscribeCallback) {
            connection.subscribe(JMS_TOPIC, messageListener, subscribeCallback);
            if (index + 1 > TOTAL_SUBSCRIBERS) {
               subscribeCallback.wait(500);
            }
         }
      }

      TestMessage message = new TestMessage();
      message.setMessage("TestMessage 1");

      TestMessageListener listener = listeners.get(0);
      synchronized (listener) {
         connection.send(JMS_TOPIC, message, sendCallback);
         listener.wait(500);
      }
      assertEquals(1, listener.getTotalReceived());

      verify(sendCallback).success();
      verify(sendCallback, times(0)).fail(any(Throwable.class));
      assertEquals(TOTAL_SUBSCRIBERS, subscribeCallback.getTotalReceived());

      for (int index = 0; index < TOTAL_SUBSCRIBERS; index++) {
         TestMessageListener messageListener = listeners.get(index);
         assertEquals(1, messageListener.getTotalReceived());

         List<Data> data = messageListener.getData();
         assertEquals(1, data.size());

         TestMessage testMessage = data.iterator().next().getMessage();
         assertEquals("TestMessage 1", testMessage.getMessage());
      }
   }

   @Ignore("Intermittent failures")
   @Test
   public void testMultipleConsumersWithSelector() throws Exception {
      List<TestMessageListener> listeners = new ArrayList<>();
      for (int index = 0; index < TOTAL_SUBSCRIBERS; index++) {
         TestMessageListener messageListener = new TestMessageListener(1);
         listeners.add(messageListener);

         synchronized (subscribeCallback) {
            connection.subscribe(JMS_TOPIC, messageListener, String.format("id = %d", index), subscribeCallback);
            if (index + 1 > TOTAL_SUBSCRIBERS) {
               subscribeCallback.wait(500);
            }
         }
      }

      assertEquals(false, subscribeCallback.failed());
      assertEquals(TOTAL_SUBSCRIBERS, subscribeCallback.getTotalReceived());

      TestMessage message = new TestMessage();
      message.setMessage("TestMessage 1");
      Properties properties = new Properties();
      properties.put("id", 1);

      TestMessageListener messageListener = listeners.get(1);
      synchronized (messageListener) {
         connection.send(JMS_TOPIC, message, properties, sendCallback);
         messageListener.wait(500);
      }
      assertEquals(1, messageListener.getTotalReceived());

      verify(sendCallback).success();
      verify(sendCallback, times(0)).fail(any(Throwable.class));

      List<Data> data = messageListener.getData();
      assertEquals(1, data.size());
      Data value = data.iterator().next();
      assertEquals("TestMessage 1", value.getMessage().getMessage());
      assertEquals(1, value.getHeaders().get("id"));

      for (int index = 0; index < TOTAL_SUBSCRIBERS; index++) {
         TestMessageListener listener = listeners.get(index);
         if (index != 1) {
            assertEquals(0, listener.getTotalReceived());
         } else {
            assertEquals(1, listener.getTotalReceived());
         }
      }
   }

   @Test
   public void testReply() throws Exception {
      String initialMessage = "send a message";
      String replyMessage = "replying...";

      TestReplyListener relay = new TestReplyListener(replyMessage);
      connection.subscribe(TestMessages.replyTopic, relay, getCallback("failed to subscribe"));

      TestReplyMessageListener messageListener = new TestReplyMessageListener();
      connection.subscribeToReply(TestMessages.replyTopic, messageListener);

      connection.send(TestMessages.replyTopic, initialMessage, sendCallback);
      synchronized (relay) {
         relay.wait(toMillis(2));
      }
      verify(sendCallback).success();
      verify(sendCallback, times(0)).fail(any(Throwable.class));

      assertEquals(1, relay.sentReply);
      assertEquals(initialMessage, relay.getMessage());

      assertEquals(1, messageListener.getCount());
      assertEquals(replyMessage, messageListener.getMessage());

      connection.unsubscribe(TestMessages.replyTopic, relay, getCallback("failed to subscribe"));
   }

   private static class TestReplyMessageListener extends OseeMessagingListener {

      private int count;
      private String message;

      @Override
      public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         synchronized (this) {
            count++;
            this.message = (String) message;
            notify();
         }
      }

      public int getCount() {
         return count;
      }

      public String getMessage() {
         return message;
      }
   }

   private static class TestReplyListener extends OseeMessagingListener {
      private int sentReply;
      private final String msg;
      private String message;

      public TestReplyListener(String replyMsg) {
         super();
         msg = replyMsg;
      }

      @Override
      public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         this.message = (String) message;
         if (replyConnection.isReplyRequested()) {
            try {
               replyConnection.send(msg, null, getCallback("Error"));
               sentReply++;
            } catch (OseeCoreException ex) {
               // Do Nothing
            }
         }
         synchronized (this) {
            notify();
         }
      }

      public String getMessage() {
         return message;
      }
   }

   private static OseeMessagingStatusCallback getCallback(String message) {
      return new OseeMessagingStatusImpl(message, TestEmbeddedBrokerSendReceive.class);
   }

   private static long toMillis(long value) {
      return value * 1000;
   }

}
