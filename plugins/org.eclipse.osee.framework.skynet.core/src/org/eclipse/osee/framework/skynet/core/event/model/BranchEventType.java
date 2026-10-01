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

import org.eclipse.osee.framework.core.event.BranchEventGuids;
import org.eclipse.osee.framework.core.event.EventType;

/**
 * @author Donald G. Dunne
 */
public enum BranchEventType {

   // Local and Remote events
   // justify branch refresh
   Added(EventType.LocalAndRemote, BranchEventGuids.ADDED, true),
   ArchiveStateUpdated(EventType.LocalAndRemote, BranchEventGuids.ARCHIVE_STATE_UPDATED, true),
   Renamed(EventType.LocalAndRemote, BranchEventGuids.RENAMED, true),
   StateUpdated(EventType.LocalAndRemote, BranchEventGuids.STATE_UPDATED, true),
   TypeUpdated(EventType.LocalAndRemote, BranchEventGuids.TYPE_UPDATED, true),
   // no need to refresh branch
   Purging(EventType.LocalAndRemote, BranchEventGuids.PURGING, false),
   Purged(EventType.LocalAndRemote, BranchEventGuids.PURGED, false),
   Deleting(EventType.LocalAndRemote, BranchEventGuids.DELETING, false),
   Deleted(EventType.LocalAndRemote, BranchEventGuids.DELETED, false),
   Committing(EventType.LocalAndRemote, BranchEventGuids.COMMITTING, false),
   CommitFailed(EventType.LocalAndRemote, BranchEventGuids.COMMIT_FAILED, false),
   Committed(EventType.LocalAndRemote, BranchEventGuids.COMMITTED, false),
   MergeConflictResolved(EventType.LocalAndRemote, BranchEventGuids.MERGE_CONFLICT_RESOLVED, false),
   FavoritesUpdated(EventType.LocalOnly, BranchEventGuids.FAVORITES_UPDATED, false);

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
