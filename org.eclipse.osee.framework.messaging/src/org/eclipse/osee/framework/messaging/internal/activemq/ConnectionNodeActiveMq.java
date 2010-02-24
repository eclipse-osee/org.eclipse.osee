/*
 * Created on Feb 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNodeFailoverSupport;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author b1528444
 */
class ConnectionNodeActiveMq implements ConnectionNodeFailoverSupport, MessageListener {

   //   private String version;
   //   private String sourceId;
   private NodeInfo nodeInfo;
   //   private ExecutorService executor;
   private Connection connection;
   private Session session;
   private TemporaryTopic temporaryTopic;
   private MessageConsumer replyToConsumer;
   private Map<String, OseeMessagingListener> replyListeners;
   private Map<String, ActiveMqMessageListenerWrapper> regularListeners;
   private boolean started = false;

   private ConcurrentHashMap<String, Topic> topicCache;
   private ConcurrentHashMap<Topic, MessageProducer> messageProducerCache;
   private ConcurrentHashMap<Topic, MessageConsumer> messageConsumerCache;

   private MessageProducer replyProducer;
   private ActiveMqUtil activeMqUtil;

   public ConnectionNodeActiveMq(String version, String sourceId, NodeInfo nodeInfo, ExecutorService executor) {
      this.nodeInfo = nodeInfo;
      activeMqUtil = new ActiveMqUtil();
      topicCache = new ConcurrentHashMap<String, Topic>();
      messageConsumerCache = new ConcurrentHashMap<Topic, MessageConsumer>();
      messageProducerCache = new ConcurrentHashMap<Topic, MessageProducer>();
      regularListeners = new ConcurrentHashMap<String, ActiveMqMessageListenerWrapper>();
      replyListeners = new ConcurrentHashMap<String, OseeMessagingListener>();
   }

   public synchronized void start() throws OseeCoreException {
      if (started) {
         return;
      }
      try {
         String uri = nodeInfo.getUri().toASCIIString();
         ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, ActiveMQConnectionFactory.DEFAULT_PASSWORD, uri);
         connection = factory.createConnection();
         session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
         temporaryTopic = session.createTemporaryTopic();
         replyToConsumer = session.createConsumer(temporaryTopic);
         replyToConsumer.setMessageListener(this);
         replyProducer = session.createProducer(null);
         connection.start();
         started = true;
      } catch (JMSException ex) {
         ex.printStackTrace();
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public synchronized void send(MessageID topic, Object body, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      try {
         if (topic.isTopic()) {
            Topic destination = getOrCreateTopic(topic);
            MessageProducer producer = getOrCreateProducer(destination);
            Message msg = activeMqUtil.createMessage(session, topic.getSerializationClass(), body);
            if (topic.isReplyRequired()) {
               msg.setJMSReplyTo(temporaryTopic);
            }
            producer.send(msg);
            statusCallback.success();
         }
      } catch (JMSException ex) {
         statusCallback.fail(ex);
         throw new OseeWrappedException(ex);
      } catch (NullPointerException ex) {
         statusCallback.fail(ex);
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public synchronized void subscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Topic destination;
      try {
         if (isConnectedThrow()) {
            ActiveMqMessageListenerWrapper wrapperListener = regularListeners.get(messageId.getGuid());
            if (wrapperListener == null) {
               wrapperListener = new ActiveMqMessageListenerWrapper(activeMqUtil, replyProducer, session);
               regularListeners.put(messageId.getGuid(), wrapperListener);
               destination = getOrCreateTopic(messageId);
               MessageConsumer consumer = getOrCreateConsumer(destination);
               consumer.setMessageListener(wrapperListener);
            }
            wrapperListener.addListener(listener);
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

   private Topic getOrCreateTopic(MessageID messageId) throws JMSException {
      Topic topic = topicCache.get(messageId.getGuid());
      if (topic == null) {
         topic = session.createTopic(messageId.getGuid());
         topicCache.put(messageId.getGuid(), topic);
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

   private MessageConsumer getOrCreateConsumer(Topic topic) throws JMSException {
      MessageConsumer consumer = messageConsumerCache.get(topic);
      if (consumer == null) {
         consumer = session.createConsumer(topic);
         messageConsumerCache.put(topic, consumer);
      }
      return consumer;
   }

   @Override
   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.put(messageId.getGuid(), listener);
      return true;
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      ActiveMqMessageListenerWrapper wrapperListener = regularListeners.get(messageId.getGuid());
      if (wrapperListener != null) {
         wrapperListener.removeListener(listener);
         if (wrapperListener.isEmpty()) {
            try {
               Topic topic = getOrCreateTopic(messageId);
               MessageConsumer consumer = getOrCreateConsumer(topic);
               consumer.setMessageListener(null);
               consumer.close();
               messageConsumerCache.remove(topic);
            } catch (JMSException ex) {
               statusCallback.fail(ex);
            }
         }
      } 
      statusCallback.success();
   }

   @Override
   public boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.remove(messageId.getGuid());
      return true;
   }

   @Override
   public void onMessage(Message jmsMessage) {
      try {
         String correlationId = jmsMessage.getJMSCorrelationID();
         if (correlationId != null) {
            OseeMessagingListener listener = replyListeners.get(correlationId);
            if (listener != null) {
               listener.process(activeMqUtil.translateMessage(jmsMessage, listener.getClazz()), new HashMap<String, Object>(), new ReplyConnectionActiveMqImpl());
            }
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      } catch (OseeCoreException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      }
   }

   @Override
   public synchronized void stop() {
      try {
         if (session != null) {
            session.close();
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      }
      try {
         if (connection != null) {
            connection.close();
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
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
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
   }

}
