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

package org.eclipse.osee.ats.core.workflow.hooks;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author Stephen J. Molaro
 */
public class AtsWriteDiffWhenCompleteHook implements IAtsTransitionHook {

   public String getName() {
      return AtsWriteDiffWhenCompleteHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "When the Workflow is transitioned to Completed, write an attribute with the diff information";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      try {
         if (workItem instanceof IAtsTeamWorkflow && workItem.isWorkType(
            WorkType.Requirements) && toState.isCompleted()) {
            BranchToken branch =
               AtsApiService.get().getBranchService().getWorkingBranch((IAtsTeamWorkflow) workItem, true);
            if (AtsApiService.get().getBranchService().branchExists(branch) && branch != null && branch.isValid()) {
               IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
               List<ChangeItem> changeItems = AtsApiService.get().getBranchService().getChangeData(branch);
               if (!changeItems.isEmpty()) {
                  String changeItemJson = JsonUtil.toJson(changeItems);
                  if (AtsApiService.get().getAttributeResolver().getAttributeCount(teamWf,
                     CoreAttributeTypes.BranchDiffData) == 0) {
                     changes.addAttribute(workItem, CoreAttributeTypes.BranchDiffData, changeItemJson);
                  } else if (!AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
                     CoreAttributeTypes.BranchDiffData, "").equals(changeItemJson)) {
                     changes.setSoleAttributeFromString(workItem, CoreAttributeTypes.BranchDiffData, changeItemJson);
                     AtsApiService.get().getAttributeResolver().setSoleAttributeValue(workItem,
                        CoreAttributeTypes.BranchDiffData, changeItemJson);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsWriteDiffWhenCompleteHook.class, Level.WARNING, "Error setting Branch Diff Data JSON", ex);
      }
   }
}