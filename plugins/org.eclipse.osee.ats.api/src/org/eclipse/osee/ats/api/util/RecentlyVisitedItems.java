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

package org.eclipse.osee.ats.api.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedItems {

   public static final RecentlyVisitedItems EMPTY_ITEMS = new RecentlyVisitedItems();
   public List<RecentlyVisistedItem> visited = new ArrayList<>();
   // This number can not be increased without analyzing size of storage value
   private final int defaultRecentlyVisitedCount = 20;

   @JsonIgnore
   public List<RecentlyVisistedItem> getReverseVisited() {
      List<RecentlyVisistedItem> revItems = new ArrayList<>();
      for (int x = visited.size() - 1; x >= 0; x--) {
         RecentlyVisistedItem item = visited.get(x);
         revItems.add(item);
      }
      return revItems;
   }

   public void addVisited(IAtsWorkItem workItem) {
      RecentlyVisistedItem item = RecentlyVisistedItem.valueOf(workItem.getArtifactToken(), workItem.getArtifactType());
      visited.remove(item);
      visited.add(item);
      if (visited.size() > defaultRecentlyVisitedCount) {
         for (int x = defaultRecentlyVisitedCount - 1; x < defaultRecentlyVisitedCount; x++) {
            visited.remove(0);
         }
      }
   }

   public void clearVisited() {
      if (visited != null) {
         visited.clear();
      }
   }

   public void addVisitedItem(RecentlyVisistedItem item) {
      visited.add(item);
   }

   public List<RecentlyVisistedItem> getVisited() {
      return visited;
   }

   public void setVisited(List<RecentlyVisistedItem> visited) {
      this.visited = visited;
   }

}
