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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemListDialog;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
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
                     CustomizeData data = new CustomizeData();
                     data.setGuid(GUID.create());
                     data.setName("Orphaned Task View");
                     data.setPersonal(true);
                     data.setNameSpace(WorldXViewerFactory.NAMESPACE);
                     List<String> visibleColIds = Arrays.asList(AtsColumnTokens.TypeColumn.getId(),
                        AtsColumnTokens.TitleColumn.getId(), AtsColumnTokens.CreatedDateColumn.getId(),
                        LastModifiedByColumn.FRAMEWORK_LAST_MOD_BY, LastModifiedDateColumn.FRAMEWORK_LAST_MOD_DATE);
                     for (String visibleId : visibleColIds) {
                        XViewerColumn visCol = null;
                        for (XViewerColumn col : WorldXViewerFactory.getWorldViewColumns()) {
                           if (col.getId().equals(visibleId)) {
                              visCol = col.copy();
                              visCol.setShow(true);
                              break;
                           }
                        }
                        if (visCol != null) {
                           data.getColumnData().getColumns().add(visCol);
                        }
                     }
                     for (XViewerColumn col : WorldXViewerFactory.getWorldViewColumns()) {
                        if (!visibleColIds.contains(col.getId())) {
                           XViewerColumn cCol = col.copy();
                           cCol.setShow(false);
                           data.getColumnData().getColumns().add(cCol);
                        }
                     }
                     if (fAi == null) {
                        AtsEditors.openInAtsWorldEditor("Orphaned Tasks", artifacts, data);
                     } else {
                        AtsUser asUser = AtsApiService.get().getUserService().getCurrentUser();
                        IAtsChangeSet changes =
                           AtsApiService.get().getStoreService().createAtsChangeSet(getName(), asUser);
                        ActionResult results = AtsApiService.get().getActionService().createAction(asUser, getName(),
                           getName(), ChangeTypes.Support, "3", false, null, Arrays.asList(fAi), new Date(), asUser,
                           null, changes);
                        IAtsTeamWorkflow teamWf = results.getFirstTeam();
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
