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

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author David W. Miller
 */
public class AtsArchiveBranchWhenCompleteHook implements IAtsTransitionHook {

   public String getName() {
      return AtsArchiveBranchWhenCompleteHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "When the Workflow is transitioned to Completed, archive the branch, if it is not already archived.";
   }

   @Override
   public boolean isBackgroundTask(Collection<IAtsWorkItem> workItems, Map<IAtsWorkItem, String> workItemFromStateMap,
      String toStateName, AtsUser transitionUser, AtsApi atsApi) {
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.isTeamWorkflow()) {
            StateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(toStateName);
            if (toStateDef.isCompleted()) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public void transitionPersistedBackground(Collection<? extends IAtsWorkItem> workItems,
      Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName, AtsUser asUser, AtsApi atsApi) {
      try {
         for (IAtsWorkItem workItem : workItems) {
            if (workItem.isTeamWorkflow()) {
               StateDefinition toStateDef = workItem.getWorkDefinition().getStateByName(toStateName);
               if (toStateDef.isCompleted()) {
                  BranchToken branch = atsApi.getBranchService().getWorkingBranch((IAtsTeamWorkflow) workItem, true);
                  if (branch != null && branch.isValid() && !atsApi.getBranchService().isArchived(branch)) {
                     atsApi.getBranchService().archiveBranch(branch);
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsArchiveBranchWhenCompleteHook.class, Level.SEVERE, "Error archiving branch in complete hook",
            ex);
      }
   }
}