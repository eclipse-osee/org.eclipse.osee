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
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public enum BroadcastEventType {
   Message(EventType.RemoteOnly),
   Ping(EventType.RemoteOnly),
   Pong(EventType.RemoteOnly),
   Force_Shutdown(EventType.RemoteOnly);

   private final EventType eventType;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   /**
    * @param localOnly true if this event type is to be thrown only locally and not to other clients
    */
   private BroadcastEventType(EventType eventType) {
      this.eventType = eventType;
   }

   public boolean isPingOrPong() {
      return this == BroadcastEventType.Ping || this == BroadcastEventType.Pong;
   }
}
