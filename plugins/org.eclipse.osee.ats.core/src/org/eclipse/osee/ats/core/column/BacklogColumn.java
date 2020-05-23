/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.core.column;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;

/**
 * @author Donald G. Dunne
 */
public class BacklogColumn {

   public static String getColumnText(Object element, AtsApi atsApi, boolean forBacklog) {
      if (element instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) element;
         Collection<IAtsWorkItem> related =
            atsApi.getRelationResolver().getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class);
         if (!related.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (IAtsWorkItem relatedWorkItem : related) {
               if (forBacklog) {
                  if (isBacklog(relatedWorkItem, atsApi)) {
                     sb.append(relatedWorkItem.getName());
                     sb.append("; ");
                  }
               } else {
                  if (!isBacklog(relatedWorkItem, atsApi)) {
                     sb.append(relatedWorkItem.getName());
                     sb.append("; ");
                  }
               }
            }
            return sb.toString().replaceFirst("; $", "");
         }
      }
      return "";
   }

   public static boolean isBacklog(IAtsWorkItem workItem, AtsApi atsApi) {
      return workItem.getStoreObject().isOfType(
         AtsArtifactTypes.AgileBacklog) || atsApi.getRelationResolver().getRelatedCount(workItem,
            AtsRelationTypes.AgileTeamToBacklog_AgileTeam) == 1;
   }
}
