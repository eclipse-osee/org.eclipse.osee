/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.actions.task;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.task.create.TasksFromAction;
import org.eclipse.osee.ats.api.util.AtsConstants;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryEntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksFromActions extends AbstractAtsAction {

   private final Collection<TeamWorkFlowArtifact> teamWfs;
   private ISelectedAtsArtifacts selectedAtsArtifacts;

   public CreateTasksFromActions(Collection<TeamWorkFlowArtifact> teamWfs) {
      super(AtsConstants.CreateTasksFromActions.name());
      this.teamWfs = teamWfs;
   }

   public CreateTasksFromActions(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(AtsConstants.CreateTasksFromActions.name());
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      teamWfs = null;
   }

   private Collection<TeamWorkFlowArtifact> getTeamWfs() {
      if (teamWfs != null) {
         return teamWfs;
      }
      return selectedAtsArtifacts.getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {
      Collection<TeamWorkFlowArtifact> useTeamWfs = getTeamWfs();
      if (useTeamWfs.isEmpty()) {
         AWorkbench.popup("Team Workflow(s) Must Be Selected");
         return;
      }

      AtsApi atsApi = AtsApiService.get();
      for (TeamWorkFlowArtifact teamWf : useTeamWfs) {
         if (teamWf.isCompletedOrCancelled()) {
            AWorkbench.popup("Can't create tasks off Completed/Cancelled workflows");
            return;
         }
      }
      String msg = "This operation will:\n\n" //
         + "1. Validate that the Team Workflow for the given ATS Id is in a Working state.\n" //
         + "2. Create an ATS Task for each selected Team Workflow.\n" //
         + "3. Relate the new Task to the corresponding Team Workflow.\n" //
         + "4. Cancel the related Team Workflow with a comment about related Task.";

      EntryEntryDialog diag = new EntryEntryDialog(getText(), msg, "Destination Team Workflow ATS Id", "Enter Reason");
      diag.setEntry2("Moved to Task - See Related");
      if (diag.open() == Window.OK) {
         String atsId = diag.getEntry();
         String reason = diag.getEntry2();
         if (!Strings.isValid(atsId) || !Strings.isValid(reason)) {
            AWorkbench.popup("Must enter Destination ATS Id and Reason");
            return;
         }
         IAtsWorkItem destTeamWf = atsApi.getWorkItemService().getWorkItemByAtsId(atsId);
         if (destTeamWf == null) {
            AWorkbench.popup("Destination Team Workflow [%s] does not exist", atsId);
            return;
         }
         if (!destTeamWf.isTeamWorkflow()) {
            AWorkbench.popup("Destination Workflow Item [%s] is not a Team Workflow", atsId);
            return;
         }
         if (destTeamWf.isCompletedOrCancelled()) {
            AWorkbench.popupf("Destination Team Workflow in [%s] state: %s", destTeamWf.getCurrentStateType(),
               destTeamWf.toStringWithAtsId());
            return;
         }
         TasksFromAction tfa = new TasksFromAction();
         tfa.setCreatedBy(atsApi.getUserService().getCurrentUser().getArtifactId());
         tfa.setReason(reason);
         tfa.setDestTeamWf(destTeamWf.getArtifactToken());
         for (IAtsTeamWorkflow sourceWf : useTeamWfs) {
            tfa.getSourceTeamWfs().add(sourceWf.getArtifactToken());
         }
         tfa = atsApi.getServerEndpoints().getTaskEp().create(tfa);
         XResultDataUI.report(tfa.getRd(), getText());

         if (tfa.getRd().isSuccess()) {
            atsApi.getStoreService().reload(Arrays.asList(destTeamWf));
            atsApi.getStoreService().reload(Collections.castAll(useTeamWfs));
            WorkflowEditor.edit(destTeamWf);
         }
      }

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
