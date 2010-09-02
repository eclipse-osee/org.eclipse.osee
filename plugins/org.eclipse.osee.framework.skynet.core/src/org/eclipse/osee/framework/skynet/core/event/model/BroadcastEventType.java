/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

/**
 * @author Donald G. Dunne
 */
public enum BroadcastEventType {
   Message(EventType.RemoteOnly, "AAn_QHYAmR1zR6BMU8QA"),
   Ping(EventType.RemoteOnly, "AAn_QHaq1hnSs1du2twA"),
   Pong(EventType.RemoteOnly, "AAn_QHdMWETFTHYRkKQA"),
   Force_Shutdown(EventType.RemoteOnly, "AAn_QHfqlBSxbuANqXAA");

   private final EventType eventType;
   private final String guid;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   /**
    * @param localOnly true if this event type is to be thrown only locally and not to other clients
    */
   private BroadcastEventType(EventType eventType, String guid) {
      this.eventType = eventType;
      this.guid = guid;
   }

   public boolean isPingOrPong() {
      return this == BroadcastEventType.Ping || this == BroadcastEventType.Pong;
   }

   public static BroadcastEventType getByGuid(String guid) {
      for (BroadcastEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

   public String getGuid() {
      return guid;
   }

}
