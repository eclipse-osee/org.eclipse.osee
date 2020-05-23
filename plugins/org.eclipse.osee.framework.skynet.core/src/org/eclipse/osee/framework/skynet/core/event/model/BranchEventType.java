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
public enum BranchEventType {

   // Local and Remote events
   // justify branch refresh
   Added(EventType.LocalAndRemote, "AAn_QHDohywDoSTxwcQA", true),
   ArchiveStateUpdated(EventType.LocalAndRemote, "AAn_QHS7Zhr6OLhKl3gA", true),
   Renamed(EventType.LocalAndRemote, "AAn_QHGLIUsH2BdX2gwA", true),
   StateUpdated(EventType.LocalAndRemote, "AAn_QHQdKhxNLtWPchAA", true),
   TypeUpdated(EventType.LocalAndRemote, "AAn_QHLW4DKKbUkEZggA", true),
   // no need to refresh branch
   Purging(EventType.LocalAndRemote, "ATPHeMoAFyL543vrAyQA", false),
   Purged(EventType.LocalAndRemote, "AAn_QG7jRGZAqPE0UewA", false),
   Deleting(EventType.LocalAndRemote, "ATPHeNujxAkPZEkWUtQA", false),
   Deleted(EventType.LocalAndRemote, "AAn_QHBDvwtT5jjKaHgA", false),
   Committing(EventType.LocalAndRemote, "ATPHeN1du2GAbS3SQsAA", false),
   CommitFailed(EventType.LocalAndRemote, "ATPHeN3RaBnDmpoYXkQA", false),
   Committed(EventType.LocalAndRemote, "AAn_QHIu0mGZytQ11QwA", false),
   MergeConflictResolved(EventType.LocalAndRemote, "AAn_QHiJ53W5W_k8W7AA", false),
   FavoritesUpdated(EventType.LocalOnly, "AFRkIheIUn3Jpz4kNBgA", false);

   private final EventType eventType;
   private final String guid;
   private final boolean justifiesCacheRefresh;

   public boolean isRemoteEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.RemoteOnly;
   }

   public boolean isLocalEventType() {
      return eventType == EventType.LocalAndRemote || eventType == EventType.LocalOnly;
   }

   private BranchEventType(EventType eventType, String guid, boolean justifiesCacheRefresh) {
      this.eventType = eventType;
      this.guid = guid;
      this.justifiesCacheRefresh = justifiesCacheRefresh;
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

   public boolean justifiesCacheRefresh() {
      return justifiesCacheRefresh;
   }

   public boolean matches(BranchEventType... branchEventTypes) {
      for (BranchEventType branchEventType : branchEventTypes) {
         if (this == branchEventType) {
            return true;
         }
      }
      return false;
   }

}
