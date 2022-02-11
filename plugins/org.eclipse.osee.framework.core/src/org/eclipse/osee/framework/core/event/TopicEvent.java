/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.core.event;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Donald G. Dunne
 */
public class TopicEvent implements FrameworkEvent, HasNetworkSender {

   EventType eventType;
   private String topic;
   private final Map<String, String> properties;
   private NetworkSender networkSender;
   private TransactionId transaction;

   public TopicEvent(String topic) {
      this.topic = topic;
      properties = new HashMap<>();
   }

   public TopicEvent(AbstractTopicEvent topic, String key, String value) {
      this(topic.getTopic(), key, value, topic.getTransaction(), topic.getEventType());
   }

   public TopicEvent(String topic, String key, String value, TransactionId transaction, EventType eventType) {
      this(topic);
      this.transaction = transaction;
      this.eventType = eventType;
      properties.put(key, value);
   }

   public String getTopic() {
      return topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public void addProperty(String key, String value) {
      properties.put(key, value);
   }

   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   @Override
   public void setNetworkSender(NetworkSender networkSender) {
      this.networkSender = networkSender;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public EventType getEventType() {
      return eventType;
   }

   public void setEventType(EventType eventType) {
      this.eventType = eventType;
   }

   public TransactionId getTransaction() {
      return transaction;
   }

   public void setTransaction(TransactionId transaction) {
      this.transaction = transaction;
   }

   @Override
   public String toString() {
      return "TopicEvnt [type=" + eventType + ", topic=" + topic + ",\nprops=" + properties + ", tx=" + transaction + ", sender=" + networkSender + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
      result = prime * result + ((properties == null) ? 0 : properties.hashCode());
      result = prime * result + ((topic == null) ? 0 : topic.hashCode());
      result = prime * result + ((transaction == null) ? 0 : transaction.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      TopicEvent other = (TopicEvent) obj;
      if (eventType != other.eventType) {
         return false;
      }
      if (properties == null) {
         if (other.properties != null) {
            return false;
         }
      } else if (!properties.equals(other.properties)) {
         return false;
      }
      if (topic == null) {
         if (other.topic != null) {
            return false;
         }
      } else if (!topic.equals(other.topic)) {
         return false;
      }
      if (transaction == null) {
         if (other.transaction != null) {
            return false;
         }
      } else if (!transaction.getId().equals(other.transaction.getId())) {
         return false;
      }
      return true;
   }

}
