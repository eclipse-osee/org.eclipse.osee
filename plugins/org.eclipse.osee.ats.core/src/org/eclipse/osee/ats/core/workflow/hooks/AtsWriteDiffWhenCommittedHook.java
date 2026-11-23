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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsTransitionHook;
import org.eclipse.osee.ats.api.workflow.hooks.IAtsWorkItemHook;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Writes Branch Diff Data Contributed via AtsWorkItemServiceImpl
 *
 * @author Stephen J. Molaro
 */
public class AtsWriteDiffWhenCommittedHook implements IAtsTransitionHook, IAtsWorkItemHook {

   public String getName() {
      return AtsWriteDiffWhenCommittedHook.class.getSimpleName();
   }

   @Override
   public String getDescription() {
      return "When the Workflow is transitioned to Completed, write an attribute with the requirements diff information if there were changes";
   }

   @Override
   public boolean isBackgroundTask(Collection<IAtsWorkItem> workItems, Map<IAtsWorkItem, String> workItemFromStateMap,
      String toStateName, AtsUser transitionUser, AtsApi atsApi) {
      for (IAtsWorkItem workItem : workItems) {
         if (needsBranchDiffDataCheck(toStateName, atsApi, workItem)) {
            // Short circuit if at least one needs check
            return true;
         }
      }
      return false;
   }

   private boolean needsBranchDiffDataCheck(String toStateName, AtsApi atsApi, IAtsWorkItem workItem) {
      if (!workItem.isTeamWorkflow()) {
         return false;
      }
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
      if (!teamWf.isWorkType(WorkType.Requirements)) {
         return false;
      }
      if (atsApi.getAttributeResolver().hasAttribute(teamWf, CoreAttributeTypes.BranchDiffData)) {
         return false;
      }
      StateDefinition toStateDef = workItem.getCurrentStateDef();
      if (Strings.isValid(toStateName)) {
         toStateDef = workItem.getWorkDefinition().getStateByName(toStateName);
      } else {
         toStateDef = workItem.getCurrentStateDef();
      }
      if (toStateDef == null) {
         return false;
      }
      if (atsApi.getBranchService().isWorkingBranchEverCommitted(teamWf) || toStateDef.isCompleted()) {
         return true;
      }
      return false;
   }

   @Override
   public void committed(IAtsTeamWorkflow teamWf, XResultData rd) {
      if (needsBranchDiffDataCheck(null, AtsApiService.get(), teamWf)) {
         Thread backgroundTask = new Thread(AtsWriteDiffWhenCommittedHook.class.getSimpleName()) {

            @Override
            public void run() {
               computeAndStoreDiffData(null, Arrays.asList(teamWf));
            }

         };
         backgroundTask.run();
      }
   }

   @Override
   public void transitionPersistedBackground(Collection<? extends IAtsWorkItem> workItems,
      Map<IAtsWorkItem, String> workItemFromStateMap, String toStateName, AtsUser asUser, AtsApi atsApi) {
      computeAndStoreDiffData(toStateName, workItems);
   }

   private void computeAndStoreDiffData(String toStateNameOrNull, Collection<? extends IAtsWorkItem> workItems) {
      try {
         AtsApi atsApi = AtsApiService.get();
         List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
         for (IAtsWorkItem workItem : workItems) {
            // Have to re-check cause at least on workItem needs check
            if (needsBranchDiffDataCheck(toStateNameOrNull, atsApi, workItem)) {
               teamWfs.add((IAtsTeamWorkflow) workItem);
            }
         }
         atsApi.getBranchService().setBranchDiffData(teamWfs);
      } catch (Exception ex) {
         OseeLog.log(AtsWriteDiffWhenCommittedHook.class, Level.WARNING, "Error setting Branch Diff Data JSON", ex);
      }
   }
}