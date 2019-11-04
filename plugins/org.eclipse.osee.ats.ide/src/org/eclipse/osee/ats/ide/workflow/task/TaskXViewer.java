/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.task;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.actions.AddTaskAction;
import org.eclipse.osee.ats.ide.actions.EditAssigneeAction;
import org.eclipse.osee.ats.ide.actions.EditBlockedStatusAction;
import org.eclipse.osee.ats.ide.actions.EditStatusAction;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.workflow.transition.TransitionToMenu;
import org.eclipse.osee.ats.ide.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.ide.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   Action editAssigneeAction;
   Action addNewTaskAction;
   EditBlockedStatusAction editBlockedStatusAction;
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
      addNewTaskAction = new AddTaskAction(this);
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

      final Collection<TreeItem> selectedTreeItems = Arrays.asList(thisXViewer.getTree().getSelection());
      mm.insertBefore(MENU_GROUP_PRE,
         TransitionToMenu.createTransitionToMenuManager(thisXViewer, "Transition-To", selectedTreeItems));

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editStatusAction);
      editStatusAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editAssigneeAction);
      editAssigneeAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());

      mm.insertBefore(MENU_GROUP_PRE, editBlockedStatusAction);
      editBlockedStatusAction.setEnabled(getSelectedWorkflowArtifacts().size() >= 1);

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
}
