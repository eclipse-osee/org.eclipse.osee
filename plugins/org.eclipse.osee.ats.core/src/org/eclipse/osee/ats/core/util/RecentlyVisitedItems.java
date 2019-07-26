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
package org.eclipse.osee.ats.core.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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
      RecentlyVisistedItem item = RecentlyVisistedItem.valueOf(workItem.getArtifactToken(),
         ArtifactTypeToken.valueOf(workItem.getArtifactType().getId(), workItem.getArtifactTypeName()));
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

}
