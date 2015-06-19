/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column.ev;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.team.IAtsTeamWorkflowProvider;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.IActivityIdUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Provides for display of ActivityId and ActivityName from related Work Package
 *
 * @author Donald G. Dunne
 */
public class ActivityIdUtility implements IActivityIdUtility {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;

   public ActivityIdUtility(IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      StringBuilder sb = new StringBuilder();
      try {
         Collection<IAtsWorkPackage> workPackages = getWorkPackages(atsObject, new HashSet<IAtsWorkPackage>());
         if (!workPackages.isEmpty()) {
            for (IAtsWorkPackage workPackage : workPackages) {
               sb.append(workPackage.getActivityId());
               sb.append(" - ");
               sb.append(workPackage.getActivityName());
               sb.append(", ");
            }
            result = sb.toString().replaceFirst(", $", "");
         }
      } catch (OseeCoreException ex) {
         return AtsColumnUtilities.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   @Override
   public Collection<IAtsWorkPackage> getWorkPackages(IAtsObject atsObject, Set<IAtsWorkPackage> workPackages) throws OseeCoreException {
      // If object has children team workflows, roll-up results of all work packages
      if (atsObject instanceof IAtsTeamWorkflowProvider) {
         for (IAtsTeamWorkflow team : ((IAtsTeamWorkflowProvider) atsObject).getTeamWorkflows()) {
            getWorkPackages(team, workPackages);
         }
      }

      // Children work items inherit the work packages of their parent team workflow
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkPackage workPkg =
            earnedValueServiceProvider.getEarnedValueService().getWorkPackage((IAtsWorkItem) atsObject);
         if (workPkg != null) {
            workPackages.add(workPkg);
         }
      }
      return workPackages;
   }

   @Override
   public String getDescription() {
      return "Provides Activity Id and Name from the selected Work Package related to the selected workflow.";
   }
}
