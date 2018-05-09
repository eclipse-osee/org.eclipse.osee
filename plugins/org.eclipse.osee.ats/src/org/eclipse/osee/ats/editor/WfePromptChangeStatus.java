/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.workflow.transition.TransitionStatusData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.widgets.dialog.TransitionStatusDialog;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.workflow.task.TaskArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;

/**
 * @author Donald G. Dunne
 */
public class WfePromptChangeStatus {

   private final Collection<? extends AbstractWorkflowArtifact> awas;

   public WfePromptChangeStatus(AbstractWorkflowArtifact sma) {
      this(Arrays.asList(sma));
   }

   public WfePromptChangeStatus(final Collection<? extends AbstractWorkflowArtifact> awas) {
      this.awas = awas;
   }

   public static boolean promptChangeStatus(Collection<? extends AbstractWorkflowArtifact> awas, boolean persist) {
      WfePromptChangeStatus promptChangeStatus = new WfePromptChangeStatus(awas);
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Prompt Change Status");
      boolean result = promptChangeStatus.promptChangeStatus(changes).isTrue();
      if (result) {
         changes.execute();
      }
      return result;
   }

   public static Result isValidToChangeStatus(Collection<? extends AbstractWorkflowArtifact> awas) {
      // Don't allow statusing for any canceled tasks
      for (AbstractWorkflowArtifact awa : awas) {
         if (awa.isCancelled()) {
            String error =
               "Can not status a cancelled " + awa.getArtifactTypeName() + ".\n\nTransition out of cancelled first.";
            return new Result(error);
         }

         // If task status is being changed, make sure tasks belong to current state
         if (awa.isTypeEqual(AtsArtifactTypes.Task)) {
            TaskArtifact taskArt = (TaskArtifact) awa;
            if (taskArt.isRelatedToUsed() && !taskArt.isRelatedToParentWorkflowCurrentState()) {
               return new Result(String.format(
                  "Task work must be done in \"Related to State\" of parent workflow for Task titled: \"%s\".\n\n" +
                  //
                     "Task work configured to be done in parent's \"%s\" state.\nParent workflow is currently in \"%s\" state.\n\n" +
                     //
                     "Either transition parent workflow or change Task's \"Related to State\" to perform task work.",
                  taskArt.getName(), taskArt.getSoleAttributeValueAsString(AtsAttributeTypes.RelatedToState, "unknown"),
                  taskArt.getParentAWA().getStateMgr().getCurrentStateName()));
            }
         }

      }
      return Result.TrueResult;
   }

   public Result promptChangeStatus(IAtsChangeSet changes) {
      Result result = isValidToChangeStatus(awas);
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return result;
      }

      TransitionStatusData data = new TransitionStatusData(awas, true);
      TransitionStatusDialog dialog = new TransitionStatusDialog("Enter Hours Spent",
         "Enter percent complete and number of hours you spent since last status.", data);
      if (dialog.open() == 0) {
         performChangeStatus(awas, null, data.getAdditionalHours(), data.getPercent(), data.isSplitHoursBetweenItems(),
            changes);
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }

   public static void performChangeStatusAndPersist(Collection<? extends IAtsWorkItem> workItems, String selectedOption, double hours, int percent, boolean splitHours) {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("ATS Prompt Change Status");
      performChangeStatus(workItems, selectedOption, hours, percent, splitHours, changes);
      changes.execute();
   }

   public static void performChangeStatus(Collection<? extends IAtsWorkItem> workItems, String selectedOption, double hours, int percent, boolean splitHours, IAtsChangeSet changes) {
      if (splitHours) {
         hours = hours / workItems.size();
      }
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.getStateMgr().isUnAssigned()) {
            workItem.getStateMgr().removeAssignee(AtsCoreUsers.UNASSIGNED_USER);
            workItem.getStateMgr().addAssignee(AtsClientService.get().getUserService().getCurrentUser());
         }
         workItem.getStateMgr().updateMetrics(workItem.getStateDefinition(), hours, percent, true,
            AtsClientService.get().getUserService().getCurrentUser());
         changes.add(workItem);
      }
   }
}
