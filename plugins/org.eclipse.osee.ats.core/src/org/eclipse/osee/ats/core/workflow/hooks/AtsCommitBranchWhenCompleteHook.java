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
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Contributed via AtsWorkItemServiceImpl
 *
 * @author David W. Miller
 */
public class AtsCommitBranchWhenCompleteHook implements IAtsTransitionHook {

   public String getName() {
      return AtsCommitBranchWhenCompleteHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "When the Workflow is transitioned to Completed, archive the branch, if it is not already archived.";
   }

   @Override
   public void transitioned(IAtsWorkItem workItem, IStateToken fromState, IStateToken toState,
      Collection<AtsUser> toAssignees, AtsUser asUser, IAtsChangeSet changes, AtsApi atsApi) {
      try {
         if (workItem instanceof IAtsTeamWorkflow && toState.isCompleted()) {
            BranchToken branch =
               AtsApiService.get().getBranchService().getWorkingBranch((IAtsTeamWorkflow) workItem, true);
            if (branch != null && branch.isValid() && !AtsApiService.get().getBranchService().isArchived(branch)) {
               AtsApiService.get().getBranchService().archiveBranch(branch);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsCommitBranchWhenCompleteHook.class, Level.SEVERE, "Error archiving branch in complete hook",
            ex);
      }
   }
}