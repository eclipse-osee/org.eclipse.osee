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
public enum BranchEventType {

   // Local and Remote events
   Purged(EventType.LocalAndRemote, "AAn_QG7jRGZAqPE0UewA"),
   Deleted(EventType.LocalAndRemote, "AAn_QHBDvwtT5jjKaHgA"),
   Added(EventType.LocalAndRemote, "AAn_QHDohywDoSTxwcQA"),
   Renamed(EventType.LocalAndRemote, "AAn_QHGLIUsH2BdX2gwA"),
   Committed(EventType.LocalAndRemote, "AAn_QHIu0mGZytQ11QwA"),
   TypeUpdated(EventType.LocalAndRemote, "AAn_QHLW4DKKbUkEZggA"),
   StateUpdated(EventType.LocalAndRemote, "AAn_QHQdKhxNLtWPchAA"),
   ArchiveStateUpdated(EventType.LocalAndRemote, "AAn_QHS7Zhr6OLhKl3gA"),
   MergeConflictResolved(EventType.LocalAndRemote, "AAn_QHiJ53W5W_k8W7AA"),
   FavoritesUpdated(EventType.LocalOnly, "AFRkIheIUn3Jpz4kNBgA");

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
   private BranchEventType(EventType eventType, String guid) {
      this.eventType = eventType;
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public static BranchEventType getByGuid(String guid) {
      for (BranchEventType type : values()) {
         if (type.guid.equals(guid)) {
            return type;
         }
      }
      return null;
   }

}
