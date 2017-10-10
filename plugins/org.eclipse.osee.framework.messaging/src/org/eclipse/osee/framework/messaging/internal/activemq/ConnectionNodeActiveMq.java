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
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNodeFailoverSupport;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.ConsoleDebugSupport;
import org.eclipse.osee.framework.messaging.internal.ServiceUtility;
import org.eclipse.osee.framework.messaging.services.internal.OseeMessagingStatusImpl;

/**
 * @author Andrew M. Finkbeiner
 */
class ConnectionNodeActiveMq implements ConnectionNodeFailoverSupport, MessageListener {

   private final NodeInfo nodeInfo;
   private Connection connection;
   private Session session;
   private TemporaryTopic temporaryTopic;
   private MessageConsumer replyToConsumer;
   private final Map<String, OseeMessagingListener> replyListeners;
   private final CompositeKeyHashMap<String, MessageConsumer, OseeMessagingListener> regularListeners;
   private boolean started = false;

   private final ConcurrentHashMap<String, Topic> topicCache;
   private final ConcurrentHashMap<Topic, MessageProducer> messageProducerCache;
   private final ExceptionListener exceptionListener;

   private MessageProducer replyProducer;
   private final ActiveMqUtil activeMqUtil;

   public ConnectionNodeActiveMq(String version, String sourceId, NodeInfo nodeInfo, ExecutorService executor, ExceptionListener exceptionListener) {
      this.nodeInfo = nodeInfo;
      this.exceptionListener = exceptionListener;
      activeMqUtil = new ActiveMqUtil();
      topicCache = new ConcurrentHashMap<>();
      messageProducerCache = new ConcurrentHashMap<>();
      regularListeners = new CompositeKeyHashMap<>(64, true);
      replyListeners = new ConcurrentHashMap<>();
   }

   @Override
   public synchronized void start() {
      if (started) {
         return;
      }
      try {
         String uri = nodeInfo.getUri().toASCIIString();
         ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
            ActiveMQConnectionFactory.DEFAULT_PASSWORD, uri);
         connection = factory.createConnection();
         connection.setExceptionListener(exceptionListener);
         session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
         temporaryTopic = session.createTemporaryTopic();
         replyToConsumer = session.createConsumer(temporaryTopic);
         replyToConsumer.setMessageListener(this);
         replyProducer = session.createProducer(null);
         connection.start();
         started = true;
      } catch (Throwable ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public void send(MessageID topic, Object body) {
      String errorMessage = String.format("Error sending message(%s)", topic.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.send(topic, body, defaultErrorHandler);
   }

   @Override
   public synchronized void send(MessageID messageId, Object message, OseeMessagingStatusCallback statusCallback) {
      send(messageId, message, null, statusCallback);
   }

   @Override
   public synchronized void send(MessageID messageId, Object message, Properties properties, OseeMessagingStatusCallback statusCallback) {
      try {
         if (messageId.isTopic()) {
            try {
               sendInternal(messageId, message, properties);
               statusCallback.success();
            } catch (JMSException ex) {
               removeProducerFromCache(messageId);
               sendInternal(messageId, message, properties);
               statusCallback.success();
            }
         }
      } catch (Exception ex) {
         statusCallback.fail(ex);
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private synchronized void sendInternal(MessageID messageId, Object message, Properties properties) throws JMSException {
      ConsoleDebugSupport support = ServiceUtility.getConsoleDebugSupport();
      if (support != null) {
         if (support.getPrintSends()) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(messageId.getName() + " ==> " + new Date());
            if (properties != null) {
               System.out.println("PROPERTIES:");
               System.out.println(properties.toString());
            }
            System.out.println("MESSAGE:");
            System.out.println(message.toString());
            System.out.println("STACK:");
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement el : stack) {
               System.out.println("   " + el.toString());
            }
            System.out.println("-----------------------------------------------------------------------------");
         }
         support.addSend(messageId);
      }
      Topic destination = getOrCreateTopic(messageId);
      MessageProducer producer = getOrCreateProducer(destination);
      Message msg = activeMqUtil.createMessage(session, messageId.getSerializationClass(), message);
      if (messageId.isReplyRequired()) {
         msg.setJMSReplyTo(temporaryTopic);
      }
      if (properties != null) {
         for (Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() instanceof Integer) {
               msg.setIntProperty(entry.getKey().toString(), (Integer) entry.getValue());
            }
            if (entry.getValue() instanceof Boolean) {
               msg.setBooleanProperty(entry.getKey().toString(), (Boolean) entry.getValue());
            }
            if (entry.getValue() instanceof Byte) {
               msg.setByteProperty(entry.getKey().toString(), (Byte) entry.getValue());
            }
            if (entry.getValue() instanceof Double) {
               msg.setDoubleProperty(entry.getKey().toString(), (Double) entry.getValue());
            }
            if (entry.getValue() instanceof Float) {
               msg.setFloatProperty(entry.getKey().toString(), (Float) entry.getValue());
            }
            if (entry.getValue() instanceof Long) {
               msg.setLongProperty(entry.getKey().toString(), (Long) entry.getValue());
            }
            if (entry.getValue() instanceof String) {
               msg.setStringProperty(entry.getKey().toString(), (String) entry.getValue());
            }
            if (entry.getValue() instanceof Short) {
               msg.setShortProperty(entry.getKey().toString(), (Short) entry.getValue());
            } else {
               msg.setObjectProperty(entry.getKey().toString(), entry.getValue());
            }
         }
      }
      producer.send(msg);
   }

   @Override
   public synchronized void subscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Topic destination;
      try {
         if (isConnectedThrow()) {
            destination = getOrCreateTopic(messageId);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(
               new ActiveMqMessageListenerWrapper(activeMqUtil, replyProducer, session, listener));
            regularListeners.put(messageId.getId(), consumer, listener);
            statusCallback.success();
         } else {
            statusCallback.fail(new OseeCoreException("This connection is not started."));
         }
      } catch (JMSException ex) {
         statusCallback.fail(ex);
      } catch (NullPointerException ex) {
         statusCallback.fail(ex);
      }
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener, String selector, OseeMessagingStatusCallback statusCallback) {
      Topic destination;
      try {
         if (isConnectedThrow()) {
            destination = getOrCreateTopic(messageId);
            MessageConsumer consumer = session.createConsumer(destination, selector);
            consumer.setMessageListener(
               new ActiveMqMessageListenerWrapper(activeMqUtil, replyProducer, session, listener));
            regularListeners.put(messageId.getId(), consumer, listener);
            statusCallback.success();
         } else {
            statusCallback.fail(new OseeCoreException("This connection is not started."));
         }
      } catch (JMSException ex) {
         statusCallback.fail(ex);
      } catch (NullPointerException ex) {
         statusCallback.fail(ex);
      }
   }

   @Override
   public void subscribe(MessageID messageId, OseeMessagingListener listener) {
      String errorMessage = String.format("Error subscribing message(%s)", messageId.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.subscribe(messageId, listener, defaultErrorHandler);
   }

   private Topic getOrCreateTopic(MessageID messageId) throws JMSException {
      Topic topic = topicCache.get(messageId.getId());
      if (topic == null) {
         topic = session.createTopic(messageId.getId());
         topicCache.put(messageId.getId(), topic);
      }
      return topic;
   }

   private MessageProducer getOrCreateProducer(Topic destination) throws JMSException {
      MessageProducer producer = messageProducerCache.get(destination);
      if (producer == null) {
         producer = session.createProducer(destination);
         producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
         messageProducerCache.put(destination, producer);
      }
      return producer;
   }

   private void removeProducerFromCache(MessageID topic) throws JMSException {
      Topic destination = getOrCreateTopic(topic);
      messageProducerCache.remove(destination);
   }

   @Override
   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.put(messageId.getId(), listener);
      return true;
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener) {
      String errorMessage = String.format("Error unsubscribing message(%s)", messageId.getId());
      OseeMessagingStatusImpl defaultErrorHandler = new OseeMessagingStatusImpl(errorMessage, getClass());
      this.unsubscribe(messageId, listener, defaultErrorHandler);
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Map<MessageConsumer, OseeMessagingListener> listeners = regularListeners.getKeyedValues(messageId.getId());
      List<MessageConsumer> consumersToRemove = new ArrayList<>();
      if (listeners != null) {
         try {
            for (Entry<MessageConsumer, OseeMessagingListener> entry : listeners.entrySet()) {
               if (entry.getValue().equals(listener)) {
                  consumersToRemove.add(entry.getKey());
               }
            }
            for (MessageConsumer messageConsumer : consumersToRemove) {
               listeners.remove(messageConsumer);
               messageConsumer.close();
            }
         } catch (JMSException ex) {
            statusCallback.fail(ex);
         }
      }
      statusCallback.success();
   }

   @Override
   public boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.remove(messageId.getId());
      return true;
   }

   @Override
   public void onMessage(Message jmsMessage) {
      try {
         String correlationId = jmsMessage.getJMSCorrelationID();
         if (correlationId != null) {
            OseeMessagingListener listener = replyListeners.get(correlationId);
            if (listener != null) {
               listener.process(activeMqUtil.translateMessage(jmsMessage, listener.getClazz()),
                  new HashMap<String, Object>(), new ReplyConnectionActiveMqImpl());
            }
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      }
      OseeLog.logf(Activator.class, Level.FINE, "recieved reply message %s", jmsMessage);
   }

   @Override
   public synchronized void stop() {
      topicCache.clear();
      messageProducerCache.clear();
      regularListeners.clear();
      started = false;
      try {
         if (session != null) {
            session.close();
            session = null;
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.FINEST, ex);
      }
      try {
         if (connection != null) {
            connection.setExceptionListener(null);
            connection.close();
            connection = null;
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.FINEST, ex);
      }
   }

   @Override
   public synchronized boolean isConnected() {
      try {
         return isConnectedThrow();
      } catch (JMSException ex) {
         started = false;
         return false;
      }
   }

   private synchronized boolean isConnectedThrow() throws JMSException {
      if (connection == null || started == false) {
         return false;
      }
      connection.getMetaData();
      return true;
   }

   @Override
   public void addConnectionListener(ConnectionListener connectionListener) {
      // do nothing
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
      // do nothing
   }

   @Override
   public String getSenders() {
      StringBuilder sb = new StringBuilder();
      for (Entry<Topic, MessageProducer> entry : this.messageProducerCache.entrySet()) {
         try {
            sb.append(String.format("Topic [%s] \n", entry.getKey().getTopicName()));
            sb.append(String.format("\tProducer Destination [%s]\n", entry.getValue().getDestination().toString()));
         } catch (JMSException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return sb.toString();
   }

   @Override
   public String getSubscribers() {
      StringBuilder sb = new StringBuilder();
      for (Pair<String, MessageConsumer> entry : this.regularListeners.keySet()) {
         try {
            sb.append(String.format("Topic [%s] \n", entry.getFirst()));
            sb.append(String.format("\tConsumer Selector [%s]\n", entry.getSecond().getMessageSelector()));
            MessageListener listener = entry.getSecond().getMessageListener();
            if (listener instanceof ActiveMqMessageListenerWrapper) {
               sb.append("\tConsumer Listeners:\n");
               sb.append(
                  String.format("\t\t%s\n", ((ActiveMqMessageListenerWrapper) listener).getListener().toString()));
            }
         } catch (JMSException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return sb.toString();
   }

   @Override
   public String getSummary() {
      StringBuilder sb = new StringBuilder();
      sb.append(nodeInfo.toString());
      sb.append("\n");
      sb.append(String.format("\tisStarted[%b]\n", started));
      sb.append(getSenders());
      sb.append(getSubscribers());
      return sb.toString();
   }

}
