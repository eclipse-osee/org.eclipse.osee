/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.event.EventType;

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
