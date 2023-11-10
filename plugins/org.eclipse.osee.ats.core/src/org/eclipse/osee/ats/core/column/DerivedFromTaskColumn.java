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
public class DerivedFromTaskColumn extends AtsCoreCodeColumn {

   private static DerivedFromTaskColumn instance;

   public static DerivedFromTaskColumn getInstance() {
      return instance;
   }

   public DerivedFromTaskColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.DerivedFromTaskColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      String result = "";
      if (atsObject instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) atsObject;
         IAtsTask relatedTask = TaskEstUtil.getTask(teamWf, atsApi);
         if (relatedTask != null) {
            result = relatedTask.toStringWithId();
         }
      }
      return result;
   }

}
