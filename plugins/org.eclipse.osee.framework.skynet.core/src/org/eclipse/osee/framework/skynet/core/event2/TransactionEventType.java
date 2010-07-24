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
package org.eclipse.osee.framework.skynet.core.event2;

import org.eclipse.osee.framework.skynet.core.event.EventType;

/**
 * @author Donald G. Dunne
 */
public enum TransactionEventType {

   // Local and Remote events
   Purged(EventType.LocalAndRemote, "");

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
   private TransactionEventType(EventType eventType, String guid) {
      this.eventType = eventType;
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static TransactionEventType getByGuid(String guid) {
      for (TransactionEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

}
