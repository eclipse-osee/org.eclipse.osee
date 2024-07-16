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

package org.eclipse.osee.ats.core.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisitedItems {

   public List<RecentlyVisistedItem> visited = new ArrayList<>();
   private static String RECENTLY_VISITED_COUNT = "recentlyVisitedCount";
   private final int defaultRecentlyVisitedCount = 20;
   private Integer recentlyVisitedCount = null;

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
      if (visited.size() > getRecentlyVisitedCount()) {
         for (int x = getRecentlyVisitedCount() - 1; x < getRecentlyVisitedCount(); x++) {
            visited.remove(0);
         }
      }
   }

   public void clearVisited() {
      if (visited != null) {
         visited.clear();
      }
   }

   @JsonIgnore
   private int getRecentlyVisitedCount() {
      if (recentlyVisitedCount == null) {
         recentlyVisitedCount = defaultRecentlyVisitedCount;
         if (AtsApiService.get() != null) {
            String recentlyVisitedCountStr = AtsApiService.get().getConfigValue(RECENTLY_VISITED_COUNT);
            if (Strings.isNumeric(recentlyVisitedCountStr)) {
               recentlyVisitedCount = Integer.valueOf(recentlyVisitedCountStr);
            }
         }
      }
      return recentlyVisitedCount;
   }

   public void addVisitedItem(RecentlyVisistedItem item) {
      visited.add(item);
   }

}
