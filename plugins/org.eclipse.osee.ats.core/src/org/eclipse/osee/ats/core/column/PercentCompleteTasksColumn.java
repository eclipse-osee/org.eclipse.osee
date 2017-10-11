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
package org.eclipse.osee.ats.core.column;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksColumn extends AbstractServicesColumn {

   public PercentCompleteTasksColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      if (IAtsAction.isOfType(atsObject) || IAtsTeamWorkflow.isOfType(atsObject)) {
         return String.valueOf(getPercentCompleteFromTasks(atsObject, atsApi));
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on tasks. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromTasks(IAtsObject atsObject, AtsApi atsApi) {
      if (IAtsAction.isOfType(atsObject)) {
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
      if (IAtsTeamWorkflow.isOfType(atsObject)) {
         return getPercentCompleteFromTasks((IAtsTeamWorkflow) atsObject, atsApi);
      }
      return 0;
   }

   public static int getPercentCompleteFromTasks(IAtsTeamWorkflow teamWf, AtsApi atsApi) {
      int spent = 0, result = 0;
      Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(teamWf);
      for (IAtsTask task : tasks) {
         spent += PercentCompleteTotalUtil.getPercentCompleteTotal(task, atsApi);
      }
      if (spent > 0) {
         result = spent / tasks.size();
      }
      return result;
   }

}
