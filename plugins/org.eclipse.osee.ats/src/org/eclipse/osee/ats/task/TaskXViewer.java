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
package org.eclipse.osee.ats.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.EditAssigneeAction;
import org.eclipse.osee.ats.actions.EditStatusAction;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskDataFactory;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.column.RelatedToStateColumn;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsTaskCache;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.TransitionToMenu;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryComboDialog;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

public class TaskXViewer extends WorldXViewer {

   Action editAssigneeAction;
   Action addNewTaskAction;
   private boolean newTaskSelectionEnabled = false;
   private boolean tasksEditable = true;
   private IAtsTeamWorkflow teamWf;

   public TaskXViewer(Composite parent, int style, IXViewerFactory xViewerFactory, IDirtiableEditor editor) {
      super(parent, style, xViewerFactory, editor);
   }

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editStatusAction = new EditStatusAction(this, this, this);
      editAssigneeAction = new EditAssigneeAction(this, this);

      addNewTaskAction = new Action("New Task", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            handleNewTask();
         }
      };
      addNewTaskAction.setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.NEW_TASK));
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

   }

   @Override
   public void updateMenuActionsForTable() {
      super.updateMenuActionsForTable();
      MenuManager mm = getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, new Separator());
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, addNewTaskAction);
      addNewTaskAction.setEnabled(isTasksEditable() && newTaskSelectionEnabled);

   }

   public boolean isTasksEditable() {
      return tasksEditable;
   }

   public void setTasksEditable(boolean tasksEditable) {
      this.tasksEditable = tasksEditable;
   }

   public void setNewTaskSelectionEnabled(boolean newTaskSelectionEnabled) {
      this.newTaskSelectionEnabled = newTaskSelectionEnabled;
   }

   public TaskArtifact handleNewTask() {
      TaskArtifact taskArt = null;
      try {
         EntryComboDialog ed = new EntryComboDialog("Create New Task", "Enter Task Title",
            RelatedToStateColumn.RELATED_TO_STATE_SELECTION);
         List<String> validStates =
            RelatedToStateColumn.getValidInWorkStates((TeamWorkFlowArtifact) teamWf.getStoreObject());
         ed.setOptions(validStates);
         if (ed.open() == 0) {
            NewTaskData newTaskData = NewTaskDataFactory.get("Create New Task",
               AtsClientService.get().getUserService().getCurrentUser().getUserId(), teamWf.getId());
            JaxAtsTask task = JaxAtsTaskFactory.get(newTaskData, ed.getEntry(),
               AtsClientService.get().getUserService().getCurrentUser(), new Date());
            task.setId(Lib.generateArtifactIdAsInt());
            if (Strings.isValid(ed.getSelection())) {
               task.setRelatedToState(ed.getSelection());
            }
            AtsClientService.get().getTaskService().createTasks(new NewTaskDatas(newTaskData));

            taskArt = (TaskArtifact) AtsClientService.get().getQueryService().getArtifact(task.getId());
            AtsTaskCache.decache((TeamWorkFlowArtifact) teamWf.getStoreObject());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return taskArt;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

}
