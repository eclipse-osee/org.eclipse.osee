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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * @author Donald G. Dunne
 */
public class VisitedItemCache {

   public List<Long> visitedUuids = new ArrayList<>();
   public Map<Long, IAtsWorkItem> uuidtoWorkItem = new HashMap<>();

   public List<IAtsWorkItem> getReverseVisited() {
      // Search artifacts and hold on to references so don't get garbage collected
      List<IAtsWorkItem> revArts = new ArrayList<>();
      for (int x = visitedUuids.size() - 1; x >= 0; x--) {
         IAtsWorkItem workItem = uuidtoWorkItem.get(visitedUuids.get(x));
         if (workItem != null) {
            revArts.add(workItem);
         }
      }
      return revArts;
   }

   public void addVisited(IAtsWorkItem workItem) {
      if (!visitedUuids.contains(workItem.getId())) {
         visitedUuids.add(workItem.getId());
         uuidtoWorkItem.put(workItem.getId(), workItem);
      }
   }

   public void clearVisited() {
      if (visitedUuids != null) {
         visitedUuids.clear();
         uuidtoWorkItem.clear();
      }
   }

}
