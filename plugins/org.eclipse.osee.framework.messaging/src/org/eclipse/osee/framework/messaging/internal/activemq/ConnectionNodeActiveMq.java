/*
 * Created on Feb 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNodeFailoverSupport;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.internal.Activator;

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
   private CompositeKeyHashMap<String, MessageConsumer, OseeMessagingListener> regularListeners;
   private boolean started = false;

   private ConcurrentHashMap<String, Topic> topicCache;
   private ConcurrentHashMap<Topic, MessageProducer> messageProducerCache;
   private final ExceptionListener exceptionListener;

   private MessageProducer replyProducer;
   private ActiveMqUtil activeMqUtil;

   public ConnectionNodeActiveMq(String version, String sourceId, NodeInfo nodeInfo, ExecutorService executor, ExceptionListener exceptionListener) {
      this.nodeInfo = nodeInfo;
      this.exceptionListener = exceptionListener;
      activeMqUtil = new ActiveMqUtil();
      topicCache = new ConcurrentHashMap<String, Topic>();
      messageProducerCache = new ConcurrentHashMap<Topic, MessageProducer>();
      regularListeners = new CompositeKeyHashMap<String, MessageConsumer, OseeMessagingListener>(64, true);
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
         connection.setExceptionListener(exceptionListener);
         session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
         temporaryTopic = session.createTemporaryTopic();
         replyToConsumer = session.createConsumer(temporaryTopic);
         replyToConsumer.setMessageListener(this);
         replyProducer = session.createProducer(null);
         connection.start();
         started = true;
      } catch (Throwable ex) {
         throw new OseeWrappedException(ex);
      }
   }

   @Override
   public synchronized void send(MessageID topic, Object body, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      send(topic, body, null, statusCallback);
   }
   
   @Override
   public synchronized void send(MessageID topic, Object body, Properties properties, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
      try {
         if (topic.isTopic()) {
            try{
               sendInternal(topic, body, properties, statusCallback);
            } catch (JMSException ex){
               removeProducerFromCache(topic);
               sendInternal(topic, body, properties, statusCallback);
            }
         //   OseeLog.log(Activator.class, Level.FINE, String.format("Sending message %s - %s", topic.getName(), topic.getGuid()));
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
   
	private synchronized void sendInternal(MessageID topic, Object body, Properties properties, OseeMessagingStatusCallback statusCallback) throws JMSException, OseeCoreException {
		Topic destination = getOrCreateTopic(topic);
		MessageProducer producer = getOrCreateProducer(destination);
		Message msg = activeMqUtil.createMessage(session, topic.getSerializationClass(), body);
		if (topic.isReplyRequired()) {
			msg.setJMSReplyTo(temporaryTopic);
		}
		if(properties != null){
		   for(Entry<Object, Object> entry:properties.entrySet()){
		      if(entry.getValue() instanceof Integer){
		         msg.setIntProperty(entry.getKey().toString(), (Integer)entry.getValue());
		      } if(entry.getValue() instanceof Boolean){
		         msg.setBooleanProperty(entry.getKey().toString(), (Boolean)entry.getValue());
            } if(entry.getValue() instanceof Byte){
               msg.setByteProperty(entry.getKey().toString(), (Byte)entry.getValue());
            } if(entry.getValue() instanceof Double){
               msg.setDoubleProperty(entry.getKey().toString(), (Double)entry.getValue());
            } if(entry.getValue() instanceof Float){
               msg.setFloatProperty(entry.getKey().toString(), (Float)entry.getValue());
            } if(entry.getValue() instanceof Long){
               msg.setLongProperty(entry.getKey().toString(), (Long)entry.getValue());
            } if(entry.getValue() instanceof String){
               msg.setStringProperty(entry.getKey().toString(), (String)entry.getValue());
            } if(entry.getValue() instanceof Short){
               msg.setShortProperty(entry.getKey().toString(), (Short)entry.getValue());
            } else {
		         msg.setObjectProperty(entry.getKey().toString(), entry.getValue());
		      }
		   }
		}
		producer.send(msg);
//		OseeLog.log(Activator.class, Level.FINE, String.format("Sending message %s - %s", topic.getName(), topic.getGuid()));
		statusCallback.success();
	}

   @Override
   public synchronized void subscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Topic destination;
      try {
         if (isConnectedThrow()) {
            destination = getOrCreateTopic(messageId);
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(new ActiveMqMessageListenerWrapper(activeMqUtil, replyProducer, session, listener));
            regularListeners.put(messageId.getGuid(), consumer, listener);
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
            consumer.setMessageListener(new ActiveMqMessageListenerWrapper(activeMqUtil, replyProducer, session, listener));
            regularListeners.put(messageId.getGuid(), consumer, listener);
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
   
   private void removeProducerFromCache(MessageID topic) throws JMSException{
	   Topic destination = getOrCreateTopic(topic);
	   messageProducerCache.remove(destination);
   }

   @Override
   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener) {
      replyListeners.put(messageId.getGuid(), listener);
      return true;
   }

   @Override
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
      Map<MessageConsumer, OseeMessagingListener> listeners = regularListeners.getKeyedValues(messageId.getGuid());
      List<MessageConsumer> consumersToRemove = new ArrayList<MessageConsumer>();
      if (listeners != null) {
         try{ 
            for(Entry<MessageConsumer, OseeMessagingListener> entry:listeners.entrySet()){
               if(entry.getValue().equals(listener)){
                  entry.getKey().setMessageListener(null);
                  entry.getKey().close();
                  consumersToRemove.add(entry.getKey());
               }
            }
            for(MessageConsumer messageConsumer: consumersToRemove){
               messageConsumer.setMessageListener(null);
               messageConsumer.close();
            }
         }catch (JMSException ex) {
            statusCallback.fail(ex);
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
      OseeLog.log(Activator.class, Level.FINE, String.format("recieved reply message %s", jmsMessage.toString()));
   }

   @Override
   public synchronized void stop() {
	  topicCache.clear();
	  messageProducerCache.clear();
	  regularListeners.clear();
      try {
         if (session != null) {
            session.close();
            session = null;
         }
      } catch (JMSException ex) {
         OseeLog.log(ConnectionNodeActiveMq.class, Level.SEVERE, ex);
      }
      try {
         if (connection != null) {
            connection.close();
            connection = null;
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
      session.createProducer(session.createTopic("mytest"));
      return true;
   }

   @Override
   public void addConnectionListener(ConnectionListener connectionListener) {
   }

   @Override
   public void removeConnectionListener(ConnectionListener connectionListener) {
   }
   
   
   @Override
   public String getSenders() {
      StringBuilder sb = new StringBuilder();
      for(Entry<Topic, MessageProducer> entry:this.messageProducerCache.entrySet()){
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
      for(Pair<String, MessageConsumer> entry:this.regularListeners.keySet()){
         try {
            sb.append(String.format("Topic [%s] \n", entry.getFirst()));
            sb.append(String.format("\tConsumer Selector [%s]\n", entry.getSecond().getMessageSelector()));
            MessageListener listener = entry.getSecond().getMessageListener();
            if(listener instanceof ActiveMqMessageListenerWrapper){
               sb.append("\tConsumer Listeners:\n");   
               sb.append(String.format("\t\t%s\n", ((ActiveMqMessageListenerWrapper)listener).getListener().toString()));   
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
