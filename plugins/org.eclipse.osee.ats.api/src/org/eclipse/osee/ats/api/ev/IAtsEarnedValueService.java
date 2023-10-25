/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.ev;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public interface IAtsEarnedValueService {

   double getEstimatedHoursFromArtifact(IAtsWorkItem workItem);

   double getEstimatedHoursFromTasks(IAtsWorkItem workItem, IStateToken relatedToState);

   double getEstimatedHoursFromTasks(IAtsWorkItem workItem);

   double getEstimatedHoursFromReviews(IAtsWorkItem workItem);

   double getEstimatedHoursFromReviews(IAtsWorkItem workItem, IStateToken relatedToState);

   double getEstimatedHoursTotal(IAtsWorkItem workItem, IStateToken relatedToState);

   double getEstimatedHoursTotal(IAtsWorkItem workItem);

   double getRemainHoursFromArtifact(IAtsWorkItem workItem);

   double getRemainHoursTotal(IAtsWorkItem workItem);

   double getRemainFromTasks(IAtsWorkItem workItem);

   double getRemainFromReviews(IAtsWorkItem workItem);

   double getManHrsPerDayPreference();

   int getPercentCompleteFromTasks(IAtsWorkItem workItem, IStateToken relatedToState);

   int getPercentCompleteFromTasks(IAtsWorkItem workItem);

   int getPercentCompleteFromReviews(IAtsWorkItem workItem, IStateToken state);

   default boolean isUseTextWorkPackages(Collection<IAtsTeamWorkflow> teamWfs) {
      boolean useTextWorkPackages = false;
      if (!teamWfs.isEmpty()) {
         if (teamWfs.iterator().next().getTeamDefinition().getTags() != null) {
            useTextWorkPackages =
               teamWfs.iterator().next().getTeamDefinition().getTags().contains(WorkPackageUtil.USE_TEXT_WORK_PACKAGES);
         }
      }
      return useTextWorkPackages;
   }

}
