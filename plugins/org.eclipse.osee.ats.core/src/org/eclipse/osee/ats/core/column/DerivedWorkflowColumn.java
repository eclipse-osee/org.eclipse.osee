/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;

/**
 * @author Donald G. Dunne
 */
public class DerivedWorkflowColumn extends AtsCoreCodeColumn {

   private static DerivedWorkflowColumn instance;

   public static DerivedWorkflowColumn getInstance() {
      return instance;
   }

   public DerivedWorkflowColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.DerivedWorkflowColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String result = "";
      if (atsObject instanceof IAtsTask) {
         IAtsTask task = (IAtsTask) atsObject;
         IAtsTeamWorkflow teamWf = TaskEstUtil.getWorkflow(task.getParentTeamWorkflow(), task, atsApi);
         if (teamWf != null) {
            result = teamWf.toStringWithId();
         }
      }
      return result;
   }

}
