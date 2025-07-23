/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemListDialog;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenOrphanedTasks extends Action {

   public OpenOrphanedTasks() {
      this("Open Orphaned Tasks");
   }

   public OpenOrphanedTasks(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      final String title = "Search Orphaned Tasks";
      EntryCheckDialog dialog = new EntryCheckDialog(title, title, "Add to New Team Workflow");
      if (dialog.open() == Window.OK) {
         IAtsActionableItem ai = null;
         if (dialog.isChecked()) {
            ActionableItemListDialog dialog2 = new ActionableItemListDialog(Active.Active, "Select AI for Action");
            if (dialog2.open() == 0) {
               ai = dialog2.getSelected().iterator().next();
            } else {
               return;
            }
         }
         IAtsActionableItem fAi = ai;

         AbstractOperation operation =
            new org.eclipse.osee.framework.core.operation.AbstractOperation(title, Activator.PLUGIN_ID) {

               @Override
               protected void doWork(IProgressMonitor monitor) throws Exception {
                  List<ArtifactId> ids =
                     ArtifactQuery.createQueryBuilder(AtsApiService.get().getAtsBranch()).andIsOfType(
                        AtsArtifactTypes.Task).andNotExists(AtsRelationTypes.TeamWfToTask_Task).getIds();
                  if (ids.isEmpty()) {
                     AWorkbench.popup("No Orphaned Tasks Found");
                  } else {
                     List<Artifact> artifacts =
                        ArtifactQuery.getArtifactListFrom(ids, AtsApiService.get().getAtsBranch());
                     if (fAi == null) {
                        MassArtifactEditor.editArtifacts("Orphaned Tasks", artifacts);
                     } else {
                        AtsApi atsApi = AtsApiService.get();

                        NewActionData newActionData =
                           atsApi.getActionService().createActionData(getName(), title, title) //
                              .andAi(fAi).andChangeType(ChangeTypes.Support).andPriority(Priorities.Three);

                        newActionData = atsApi.getActionService().createAction(newActionData);
                        if (newActionData.getRd().isErrors()) {
                           XResultDataUI.report(newActionData.getRd(), getName());
                        }

                        IAtsTeamWorkflow teamWf = newActionData.getActResult().getAtsTeamWfs().iterator().next();

                        IAtsChangeSet changes =
                           AtsApiService.get().getStoreService().createAtsChangeSet(getName(), atsApi.user());
                        for (Artifact taskArt : artifacts) {
                           changes.relate(teamWf, AtsRelationTypes.TeamWfToTask_Task, taskArt);
                        }
                        changes.execute();

                        AtsEditors.openATSAction(teamWf.getStoreObject(), AtsOpenOption.OpenOneOrPopupSelect);
                     }
                  }
               }
            };
         Operations.executeAsJob(operation, true);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.TASK);
   }

}
