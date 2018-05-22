/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class EstimatedHoursUtil {

   public static double getEstimatedHours(Object object) {
      if (object instanceof AbstractWorkflowArtifact) {
         return AtsClientService.get().getEarnedValueService().getEstimatedHoursTotal(
            (AbstractWorkflowArtifact) object);
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double total = 0;
         for (IAtsTeamWorkflow team : AtsClientService.get().getWorkItemService().getTeams(object)) {
            total += getEstimatedHours(team);
         }
         return total;
      }
      return 0.0;
   }

}
