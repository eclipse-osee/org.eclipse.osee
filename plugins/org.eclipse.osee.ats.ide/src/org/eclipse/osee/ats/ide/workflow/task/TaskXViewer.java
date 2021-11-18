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

package org.eclipse.osee.ats.ide.workflow.task;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.EditAssigneeAction;
import org.eclipse.osee.ats.ide.actions.EditBlockedStatusAction;
import org.eclipse.osee.ats.ide.actions.EditHoldStatusAction;
import org.eclipse.osee.ats.ide.actions.EditStatusAction;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToMenu;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   Action editAssigneeAction;
   EditBlockedStatusAction editBlockedStatusAction;
   EditHoldStatusAction editHoldStatusAction;
   private boolean tasksEditable = true;
   private IAtsTeamWorkflow teamWf;

   public TaskXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor, IAtsTeamWorkflow teamWf) {
      super(parent, style, xViewerFactory, editor);
      this.teamWf = teamWf;
   }

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editStatusAction = new EditStatusAction(this, this, this);
      editAssigneeAction = new EditAssigneeAction(this, this);
      editBlockedStatusAction = new EditBlockedStatusAction(this);
      editHoldStatusAction = new EditHoldStatusAction(this);
   }

   @Override
   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
         item.updateTaskEditMenuActions(this);
      }

      // EDIT MENU BLOCK
      MenuManager editMenuManager = updateEditMenu(mm);
      mm.insertBefore(MENU_GROUP_ATS_WORLD_EDIT, editMenuManager);

      mm.insertBefore(MENU_GROUP_PRE,
         TransitionToMenu.createTransitionToMenuManager(thisXViewer, "Transition-To", getSelectedWorkflowArtifacts()));

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editStatusAction);
      editStatusAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editAssigneeAction);
      editAssigneeAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      mm.insertBefore(MENU_GROUP_PRE, editBlockedStatusAction);
      editBlockedStatusAction.setEnabled(getSelectedWorkflowArtifacts().size() >= 1);

      mm.insertBefore(MENU_GROUP_PRE, editHoldStatusAction);
      editHoldStatusAction.setEnabled(getSelectedWorkflowArtifacts().size() >= 1);

   }

   public boolean isTasksEditable() {
      return tasksEditable;
   }

   public void setTasksEditable(boolean tasksEditable) {
      this.tasksEditable = tasksEditable;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

   @Override
   public Collection<TeamWorkFlowArtifact> getSelectedTeamWorkflowArtifacts() {
      return Arrays.asList((TeamWorkFlowArtifact) this.teamWf);
   }

   public IAtsTeamWorkflow getTeamWf() {
      return teamWf;
   }
}
