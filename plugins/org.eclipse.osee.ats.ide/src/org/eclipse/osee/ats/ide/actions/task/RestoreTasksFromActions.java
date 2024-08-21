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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.create.TasksFromAction;
import org.eclipse.osee.ats.api.util.AtsConstants;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Re-open Actions that were turned into Tasks using RestoreTasksFromActions
 *
 * @author Donald G. Dunne
 */
public class RestoreTasksFromActions extends AbstractAtsAction {

   private final Collection<TaskArtifact> tasks;
   private ISelectedAtsArtifacts selectedAtsArtifacts;

   public RestoreTasksFromActions(Collection<TaskArtifact> tasks) {
      super(AtsConstants.RestoreTasksFromActions.name());
      this.tasks = tasks;
   }

   public RestoreTasksFromActions(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super(AtsConstants.RestoreTasksFromActions.name());
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      tasks = null;
   }

   private Collection<TaskArtifact> getTasks() {
      if (tasks != null) {
         return tasks;
      }
      return selectedAtsArtifacts.getSelectedTaskArtifacts();
   }

   @Override
   public void run() {
      Collection<TaskArtifact> useTasks = getTasks();
      if (useTasks.isEmpty()) {
         AWorkbench.popup("Task(s) Must Be Selected");
         return;
      }

      AtsApi atsApi = AtsApiService.get();
      for (TaskArtifact task : useTasks) {
         if (task.isCompletedOrCancelled()) {
            AWorkbench.popup("Can't restore actions from off Completed/Cancelled tasks");
            return;
         }
      }
      String msg = "This operation will:\n\n" //
         + "1. Look for a Team Workflow that created each task.\n" //
         + "2. Transition the related Team Workflow to the state it was previously in.\n" //
         + "3. Auto-add to configured Goals if not already a member.\n" //
         + "4. Task will remain untouched.";

      if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(), msg)) {
         TasksFromAction tfa = new TasksFromAction();
         tfa.setCreatedBy(atsApi.getUserService().getCurrentUser().getArtifactId());
         for (IAtsTask task : useTasks) {
            tfa.getSourceTasks().add(task.getArtifactToken());
         }
         tfa = atsApi.getServerEndpoints().getTaskEp().restore(tfa);
         XResultDataUI.report(tfa.getRd(), getText());

         if (tfa.getRd().isSuccess()) {
            List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
            for (ArtifactToken task : tfa.getSourceTasks()) {
               Collection<ArtifactToken> related =
                  atsApi.getRelationResolver().getRelated(task, AtsRelationTypes.Derive_From);
               if (related.size() == 1 && related.iterator().next().isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  teamWfs.add(atsApi.getWorkItemService().getTeamWf(related.iterator().next()));
               }
            }
            atsApi.getStoreService().reload(Collections.castAll(teamWfs));
            atsApi.getStoreService().reload(Collections.castAll(useTasks));
            if (teamWfs.size() == 1) {
               WorkflowEditor.edit(teamWfs.iterator().next());
            } else {
               WorldEditor.open(getText(), Collections.castAll(teamWfs));
            }
         }
      }

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
