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
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
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
      return "When the Workflow is transitioned to Completed, write an attribute with the requirements diff information if there were changes";
   }

   @Override
   public boolean isBackgroundTask(Collection<IAtsWorkItem> workItems, Map<IAtsWorkItem, String> workItemFromStateMap,
      String toStateName, AtsUser transitionUser, AtsApi atsApi) {
      for (IAtsWorkItem workItem : workItems) {
         StateDefinition toStateDef = workItem.getStateDefinition();
         if (workItem.isTeamWorkflow() && workItem.isWorkType(WorkType.Requirements) && toStateDef.isCompleted()) {
            return true;
         }
      }
      return false;
   }

   @Override
   public void transitionPersistedBackground(Collection<? extends IAtsWorkItem> workItems,
      Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName, AtsUser asUser, AtsApi atsApi) {
      try {
         IAtsChangeSet changes = null;
         for (IAtsWorkItem workItem : workItems) {
            StateDefinition toStateDef = workItem.getStateDefinition();
            if (workItem.isTeamWorkflow() && workItem.isWorkType(WorkType.Requirements) && toStateDef.isCompleted()) {
               BranchToken branch = atsApi.getBranchService().getWorkingBranch((IAtsTeamWorkflow) workItem, true);
               if (atsApi.getBranchService().branchExists(branch) && branch != null && branch.isValid()) {
                  IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
                  List<ChangeItem> changeItems = atsApi.getBranchService().getChangeData(branch);
                  if (!changeItems.isEmpty()) {
                     String changeItemJson = JsonUtil.toJson(changeItems);
                     if (atsApi.getAttributeResolver().getAttributeCount(teamWf,
                        CoreAttributeTypes.BranchDiffData) == 0) {
                        changes = getOrCreateChanges(changes, atsApi);
                        changes.addAttribute(workItem, CoreAttributeTypes.BranchDiffData, changeItemJson);
                     } else if (!atsApi.getAttributeResolver().getSoleAttributeValue(teamWf,
                        CoreAttributeTypes.BranchDiffData, "").equals(changeItemJson)) {
                        changes = getOrCreateChanges(changes, atsApi);
                        changes.setSoleAttributeFromString(workItem, CoreAttributeTypes.BranchDiffData, changeItemJson);
                        atsApi.getAttributeResolver().setSoleAttributeValue(workItem, CoreAttributeTypes.BranchDiffData,
                           changeItemJson);
                     }
                  }
               }
            }
         }
         if (changes != null) {
            changes.executeIfNeeded();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsWriteDiffWhenCompleteHook.class, Level.WARNING, "Error setting Branch Diff Data JSON", ex);
      }
   }

   private IAtsChangeSet getOrCreateChanges(IAtsChangeSet changes, AtsApi atsApi) {
      if (changes != null) {
         return changes;
      }
      return atsApi.createChangeSet(getClass().getSimpleName());
   }
}