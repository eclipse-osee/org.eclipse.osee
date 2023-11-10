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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Vaibhav Y Patel
 */
public class TaskRiskFactorsColumn extends AtsCoreCodeColumn {

   public TaskRiskFactorsColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.TaskRiskFactorsColumn, atsApi);
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
         List<String> riskFactors = new ArrayList<>();
         for (IAtsTask task : tasks) {
            String riskFactor =
               atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.RiskFactor, "");
            if (Strings.isValid(riskFactor) && !riskFactors.contains(riskFactor)) {
               riskFactors.add(riskFactor);
            }
         }
         if (riskFactors.size() > 0) {
            riskFactors.sort(Comparator.naturalOrder());
            return Collections.toString(", ", riskFactors);
         }
         return result;
      }
      if (atsObject instanceof IAtsTask) {
         IAtsTask task = (IAtsTask) atsObject;
         return atsApi.getAttributeResolver().getSoleAttributeValue(task, AtsAttributeTypes.RiskFactor, "");
      }
      return result;
   }
}
