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

/**
 * @author Donald G. Dunne
 */
public class TopicEvent implements FrameworkEvent, HasNetworkSender {

   EventType eventType;
   private String topic;
   private final Map<String, String> properties;
   private NetworkSender networkSender;

   public TopicEvent(String topic) {
      this.topic = topic;
      properties = new HashMap<>();
   }

   public TopicEvent(AbstractTopicEvent topic, String key, String value) {
      this(topic.getTopic(), key, value, topic.getEventType());
   }

   public TopicEvent(String topic, String key, String value, EventType eventType) {
      this(topic);
      this.eventType = eventType;
      properties.put(key, value);
   }

   public String getTopic() {
      return topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public void put(String key, String value) {
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

}
