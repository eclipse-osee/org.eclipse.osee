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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.ats.core.util.PercentCompleteTotalUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteTasksColumn implements IAtsColumn {

   private static PercentCompleteTasksColumn instance;
   private final IAtsServices services;

   public PercentCompleteTasksColumn(IAtsServices services) {
      this.services = services;
   }

   public PercentCompleteTasksColumn getInstance(IAtsServices services) {
      if (instance == null) {
         instance = new PercentCompleteTasksColumn(services);
      }
      return instance;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         result = String.valueOf(getPercentCompleteFromTasks(atsObject, services));
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

   /**
    * Return Percent Complete ONLY on tasks. Total Percent / # Tasks
    */
   public static int getPercentCompleteFromTasks(IAtsObject atsObject, IAtsServices services) throws OseeCoreException {
      if (atsObject instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) atsObject;
         double percent = 0;
         for (IAtsTeamWorkflow teamWf : action.getTeamWorkflows()) {
            if (!teamWf.isCancelled()) {
               percent += getPercentCompleteFromTasks(teamWf, services);
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / action.getTeamWorkflows().size();
         return rollPercent.intValue();
      }
      if (atsObject instanceof IAtsTeamWorkflow) {
         return getPercentCompleteFromTasks((IAtsTeamWorkflow) atsObject, services);
      }
      return 0;
   }

   public static int getPercentCompleteFromTasks(IAtsTeamWorkflow teamWf, IAtsServices services) {
      int spent = 0, result = 0;
      Collection<IAtsTask> tasks = services.getTaskService().getTasks(teamWf);
      for (IAtsTask task : tasks) {
         spent += PercentCompleteTotalUtil.getPercentCompleteTotal(task, services);
      }
      if (spent > 0) {
         result = spent / tasks.size();
      }
      return result;
   }

}
