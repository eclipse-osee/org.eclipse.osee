/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.event;

import org.osgi.service.event.Event;

/**
 * @author Donald G. Dunne
 */
public class AbstractTopicEvent {

   private EventType eventType;
   private String topic;

   protected AbstractTopicEvent(EventType eventType, String topic) {
      this.eventType = eventType;
      this.topic = topic;
   }

   public EventType getEventType() {
      return eventType;
   }

   public void setEventType(EventType eventType) {
      this.eventType = eventType;
   }

   public String getTopic() {
      return topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   @Override
   public String toString() {
      return "EventTopic [eventType=" + eventType + ", topic=" + topic + "]";
   }

   /**
    * @return true of this topic matches the event topic
    */
   public boolean matches(Event event) {
      return getTopic().equals(event.getTopic());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((topic == null) ? 0 : topic.hashCode());
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
      AbstractTopicEvent other = (AbstractTopicEvent) obj;
      if (topic == null) {
         if (other.topic != null) {
            return false;
         }
      } else if (!topic.equals(other.topic)) {
         return false;
      }
      return true;
   }

}
