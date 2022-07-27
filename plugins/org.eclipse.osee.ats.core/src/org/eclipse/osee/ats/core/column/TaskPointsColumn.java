/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Vaibhav Y Patel
 */
public class TaskPointsColumn extends AbstractServicesColumn {

   public TaskPointsColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsApi.getStoreService().isDeleted(atsObject)) {
         return "<deleted>";
      }
      if (atsObject instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsObject;
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks(teamWf);
         double value = 0;
         for (IAtsTask task : tasks) {
            String points = atsApi.getAgileService().getPointsStr(task);
            if (Strings.isNumeric(points)) {
               value = value + Double.parseDouble(points);
            }
         }
         return value > 0 ? Double.toString(value) : result;
      }
      if (atsObject instanceof IAtsTask) {
         IAtsTask task = (IAtsTask) atsObject;
         return atsApi.getAgileService().getPointsStr(task);
      }
      return result;
   }
}
