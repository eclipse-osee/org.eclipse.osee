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
package org.eclipse.osee.ats.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.actions.EditAssigneeAction;
import org.eclipse.osee.ats.actions.EditStatusAction;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.column.HoursSpentSMAStateColumn;
import org.eclipse.osee.ats.column.HoursSpentStateTotalColumn;
import org.eclipse.osee.ats.column.PercentCompleteSMAStateColumn;
import org.eclipse.osee.ats.column.PercentCompleteTotalColumn;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workflow.TransitionToMenu;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   private final TaskComposite taskComposite;
   private boolean tasksEditable = true;
   private boolean newTaskSelectionEnabled = false;
   private static String viewerId = GUID.create();

   public TaskXViewer(TaskComposite taskComposite, int style, IDirtiableEditor editor) {
      super(taskComposite, style, new TaskXViewerFactory(), editor);
      this.taskComposite = taskComposite;
   }

   @Override
   public String toString() {
      if (taskComposite == null) {
         return "TaskXViewer";
      }
      try {
         if (taskComposite.getIXTaskViewer().getAwa() != null) {
            return "TaskXViewer - id:" + viewerId + " - " + taskComposite.getIXTaskViewer().getAwa().toString();
         }
         return "TaskXViewer - id:" + viewerId + " - " + taskComposite.getIXTaskViewer().toString();
      } catch (Exception ex) {
         return "TaskXViewer - id:" + viewerId;
      }
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      super.handleColumnMultiEdit(treeColumn, treeItems);
      handleColumnMultiEdit(treeColumn, treeItems, false);
      refresh();
      editor.onDirtied();
   }

   public TaskArtifact getSelectedTaskArtifact() {
      Collection<TaskArtifact> arts = getSelectedTaskArtifacts();
      if (arts.size() > 0) {
         return arts.iterator().next();
      }
      return null;
   }

   public boolean isSelectedTaskArtifactsAreInWork() throws OseeCoreException {
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (Artifacts.isOfType(obj, AtsArtifactTypes.Task) && !((TaskArtifact) obj).isInWork()) {
            return false;
         }
      }
      return true;
   }

   Action editAssigneeAction;
   Action addNewTaskAction;

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editStatusAction = new EditStatusAction(this, this, this);
      editAssigneeAction = new EditAssigneeAction(this, this);

      addNewTaskAction = new Action("New Task", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            taskComposite.handleNewTask();
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

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!isTasksEditable()) {
         AWorkbench.popup("ERROR", "Editing disabled for current state.");
         return false;
      }
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      try {
         TaskArtifact taskArt = (TaskArtifact) treeItem.getData();
         boolean modified = false;

         if (xCol.equals(HoursSpentSMAStateColumn.getInstance()) || xCol.equals(
            HoursSpentStateTotalColumn.getInstance()) || xCol.equals(
               PercentCompleteSMAStateColumn.getInstance()) || xCol.equals(PercentCompleteTotalColumn.getInstance())) {
            modified = SMAPromptChangeStatus.promptChangeStatus(Arrays.asList(taskArt), false);
         } else {
            modified = super.handleAltLeftClick(treeColumn, treeItem);
         }

         if (modified) {
            editor.onDirtied();
            update(treeItem.getData(), null);
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isTasksEditable() {
      return tasksEditable;
   }

   public void setTasksEditable(boolean tasksEditable) {
      this.tasksEditable = tasksEditable;
   }

   public boolean isNewTaskSelectionEnabled() {
      return newTaskSelectionEnabled;
   }

   public void setNewTaskSelectionEnabled(boolean newTaskSelectionEnabled) {
      this.newTaskSelectionEnabled = newTaskSelectionEnabled;
   }

   public TaskComposite getTaskComposite() {
      return taskComposite;
   }

   @Override
   public boolean isAltLeftClickPersist() {
      return false;
   }

}
