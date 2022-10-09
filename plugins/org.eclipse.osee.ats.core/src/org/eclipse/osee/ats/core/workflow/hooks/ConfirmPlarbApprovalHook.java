/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.core.workflow.hooks;

import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Audrey Denk
 */
public class ConfirmPlarbApprovalHook implements IAtsWorkItemHook {

   @Override
   public XResultData committing(IAtsTeamWorkflow teamWf, XResultData rd) {
      if (teamWf.getWorkTypes() != null) {
         if (teamWf.getWorkTypes().contains(WorkType.ARB)) {
            String performedBy = String.valueOf(AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
               AtsAttributeTypes.ProductLineApprovedBy, ""));
            if (Strings.isInValid(performedBy)) {
               rd.error("Branch can not be committed without PLARB Approval");
            }
         }
      }
      return rd;
   }

   @Override
   public String getDescription() {
      return "Check working branch has been approved by PLARB before allowing commit";
   }

}
