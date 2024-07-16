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

package org.eclipse.osee.ats.core.column;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.Action;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksColumn extends AtsCoreCodeColumn {

   public PercentCompleteTasksColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.PercentCompleteTasksColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      if (atsObject.isOfType(TeamWorkflow, Action)) {
         return String.valueOf(getPercentCompleteFromTasks(atsObject, atsApi));
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromTasks(IAtsObject atsObject, AtsApi atsApi) {
      if (atsObject.isOfType(Action)) {
         IAtsAction action = (IAtsAction) atsObject;
         double percent = 0;
         for (IAtsTeamWorkflow teamWf : action.getTeamWorkflows()) {
            if (!teamWf.isCancelled()) {
               percent += getPercentCompleteFromTasks(teamWf, atsApi);
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / action.getTeamWorkflows().size();
         return rollPercent.intValue();
      }

      if (atsObject.isOfType(TeamWorkflow)) {
         return getPercentCompleteFromTasks((IAtsTeamWorkflow) atsObject, atsApi);
      }
      return 0;
   }

   public static int getPercentCompleteFromTasks(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      int spent = 0, result = 0;
      Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(teamWf);
      for (IAtsTask task : tasks) {
         spent += atsApi.getWorkItemMetricsService().getPercentCompleteTotal(task);
      }
      if (spent > 0) {
         result = spent / tasks.size();
      }
      return result;
   }

}
