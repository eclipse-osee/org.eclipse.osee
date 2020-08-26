/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class EstimatedHoursUtil {

   public static double getEstimatedHours(Object object) {
      if (object instanceof AbstractWorkflowArtifact) {
         return AtsApiService.get().getEarnedValueService().getEstimatedHoursTotal(
            (AbstractWorkflowArtifact) object);
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double total = 0;
         for (IAtsTeamWorkflow team : AtsApiService.get().getWorkItemService().getTeams(object)) {
            total += getEstimatedHours(team);
         }
         return total;
      }
      return 0.0;
   }

}
