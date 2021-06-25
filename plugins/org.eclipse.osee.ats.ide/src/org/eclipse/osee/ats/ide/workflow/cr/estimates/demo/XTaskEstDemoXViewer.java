/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates.demo;

import java.util.Date;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstUtil;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemCheckboxStateDialog;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.XTaskEstViewer;
import org.eclipse.osee.ats.ide.workflow.cr.estimates.sibling.operation.CreateSiblingOffTaskEstOperation;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class XTaskEstDemoXViewer extends XTaskEstViewer {

   private final IAtsTeamWorkflow crTeamWf;
   private final AtsApi atsApi;

   public XTaskEstDemoXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf) {
      super(parent, style, xViewerFactory, editor, teamWf);
      atsApi = AtsApiService.get();
      crTeamWf = teamWf;
   }

   @Override
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      super.handleLeftClickInIconArea(treeColumn, treeItem);
      if ((treeItem.getData() instanceof IAtsTask)) {
         IAtsTask task = (IAtsTask) treeItem.getData();
         IAtsTeamWorkflow teamWf = TaskEstUtil.getWorkflow(crTeamWf, task, atsApi);
         if (teamWf != null) {
            WorkflowEditor.edit(teamWf);
         } else {
            String ptsStr = atsApi.getAgileService().getPointsStr(task);
            if (!Strings.isValid(ptsStr)) {
               AWorkbench.popupf("No estimated points for task %s\n", task.toStringWithId());
               return false;
            } else if (!Strings.isNumeric(ptsStr)) {
               AWorkbench.popupf("Estimated points non-numeric for task %s\n", task.toStringWithId());
               return false;
            } else {
               Double pts = Double.valueOf(ptsStr);
               if (pts > 0) {
                  return createWorkflow(task);
               } else {
                  AWorkbench.popupf("Estimated points must be > 0 for task %s\n", task.toStringWithId());
                  return false;
               }
            }
         }
      }
      return false;
   }

   private boolean createWorkflow(IAtsTask task) {
      XTaskEstDemoWidget estWidget = new XTaskEstDemoWidget();
      estWidget.setArtifact((Artifact) crTeamWf.getStoreObject());
      TaskEstDefinition tedMatch = null;
      for (TaskEstDefinition ted : estWidget.getTaskEstDefs()) {
         if (task.hasTag(ted.getIdString())) {
            tedMatch = ted;
            break;
         }
      }
      IAtsActionableItem useAi = IAtsActionableItem.SENTINEL;
      if (tedMatch != null) {
         ArtifactToken art = tedMatch.getActionableItem();
         if (art != null && art.isValid()) {
            useAi = atsApi.getActionableItemService().getActionableItemById(art);
         }
      }
      if (useAi.isInvalid()) {
         ActionableItemCheckboxStateDialog dialog = new ActionableItemCheckboxStateDialog(
            "Create Team Workflow off Task Estimate", "Select a single Actionable Item for new Team Workflow",
            atsApi.getActionableItemService().getTopLevelActionableItems(Active.Active));
         if (dialog.open() == Window.OK) {
            useAi = dialog.getSelectedActionableItems().iterator().next();
         } else {
            return false;
         }
      }

      IAtsTeamDefinition teamDef = atsApi.getActionableItemService().getTeamDefinitionInherited(useAi);
      List<AtsUser> assignees = TaskEstUtil.getAssignees(tedMatch, atsApi);
      if (assignees.isEmpty()) {
         assignees.add(AtsCoreUsers.UNASSIGNED_USER);
      }
      IAtsAction action = crTeamWf.getParentAction();

      // Create workflow with configured or selected AI
      XResultData rd = new XResultData();
      IAtsChangeSet changes = atsApi.createChangeSet("Create Task Est Workflow");
      CreateSiblingOffTaskEstOperation.createTaskEstSiblingWorkflow(rd, changes, new Date(), task, useAi, teamDef,
         assignees, action, atsApi);
      changes.execute();
      if (rd.isErrors()) {
         XResultDataUI.report(rd, "Create Task Est Workflow");
         return false;
      }
      return true;
   }

}
