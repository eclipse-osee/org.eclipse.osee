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

}
