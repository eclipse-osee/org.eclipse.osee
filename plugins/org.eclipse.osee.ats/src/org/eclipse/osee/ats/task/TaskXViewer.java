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
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAPromptChangeHoursSpent;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.xviewer.RelatedToStateColumn;
import org.eclipse.osee.ats.world.AtsWorldEditorItems;
import org.eclipse.osee.ats.world.IAtsWorldEditorItem;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   private final TaskComposite taskComposite;
   private final IDirtiableEditor editor;
   private boolean tasksEditable = true;
   private boolean newTaskSelectionEnabled = false;
   private static String viewerId = GUID.create();

   public TaskXViewer(TaskComposite taskComposite, int style, IDirtiableEditor editor) {
      super(taskComposite, style, new TaskXViewerFactory());
      this.editor = editor;
      this.taskComposite = taskComposite;
   }

   @Override
   public String toString() {
      if (taskComposite == null) {
         return "TaskXViewer";
      }
      try {
         if (taskComposite.getIXTaskViewer().getSma() != null) {
            return "TaskXViewer - id:" + viewerId + " - " + taskComposite.getIXTaskViewer().getSma().toString();
         }
         return "TaskXViewer - id:" + viewerId + " - " + taskComposite.getIXTaskViewer().toString();
      } catch (Exception ex) {
         return "TaskXViewer - id:" + viewerId;
      }
   }

   public boolean isUsingTaskResolutionOptions() {
      try {
         if (getSelectedTaskArtifact() == null) {
            return false;
         }
         return getSelectedTaskArtifact().isUsingTaskResolutionOptions();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
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

   public boolean isSelectedTaskArtifactsAreInWork() {
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifact && !((TaskArtifact) obj).isInWork()) {
            return false;
         }
      }
      return true;
   }

   Action editTaskTitleAction, editTaskAssigneesAction, editTaskStatusAction, editTaskHoursSpentAction,
      editTaskResolutionAction, editTaskEstimateAction, editTaskRelatedStateAction;
   Action addNewTaskAction, deleteTasksAction;

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editTaskTitleAction = new Action("Edit Task Title", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                  PromptChangeUtil.promptChangeAttribute(getSelectedTaskArtifact(), AtsAttributeTypes.Title, false,
                     false);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskAssigneesAction = new Action("Edit Task Assignees", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (PromptChangeUtil.promptChangeAssignees(getSelectedTaskArtifacts(), false)) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskStatusAction = new Action("Edit Task Status", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAPromptChangeStatus.promptChangeStatus(getSelectedTaskArtifacts(), false)) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskHoursSpentAction = new Action("Edit Task Hours Spent", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAPromptChangeHoursSpent.promptChangeStatus(getSelectedTaskArtifacts(), false)) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskResolutionAction = new Action("Edit Task Resolution", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               handleChangeResolution();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskEstimateAction = new Action("Edit Task Estimate", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                  ArtifactPromptChange.promptChangeAttribute(AtsAttributeTypes.EstimatedHours,
                     getSelectedTaskArtifacts(), false);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskRelatedStateAction = new Action("Edit Task Related to State", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success = RelatedToStateColumn.promptChangeRelatedToState(getSelectedTaskArtifacts(), false);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      addNewTaskAction = new Action("New Task", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            taskComposite.handleNewTask();
         }
      };

      deleteTasksAction = new Action("Delete Task", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               taskComposite.handleDeleteTask();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

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

      // EDIT MENU BLOCK
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskTitleAction);
      editTaskTitleAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() == 1 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskAssigneesAction);
      editTaskAssigneesAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskStatusAction);
      editTaskStatusAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskHoursSpentAction);
      editTaskHoursSpentAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      if (!isUsingTaskResolutionOptions()) {
         mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskResolutionAction);
         editTaskResolutionAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());
      }

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskEstimateAction);
      editTaskEstimateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskRelatedStateAction);
      editTaskRelatedStateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

   }

   @Override
   public void updateMenuActionsForTable() {
      super.updateMenuActionsForTable();
      MenuManager mm = getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, new Separator());
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, addNewTaskAction);
      addNewTaskAction.setEnabled(isTasksEditable() && newTaskSelectionEnabled);

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, deleteTasksAction);
      deleteTasksAction.setEnabled(isTasksEditable() && getSelectedTaskArtifacts().size() > 0);

   }

   public boolean handleChangeResolution() throws OseeCoreException {
      // Ensure tasks are related to current state of workflow
      Result result = SMAPromptChangeStatus.isValidToChangeStatus(getSelectedTaskArtifacts());
      if (result.isFalse()) {
         return false;
      }

      if (isUsingTaskResolutionOptions()) {
         if (SMAPromptChangeStatus.promptChangeStatus(getSelectedTaskArtifacts(), false)) {
            editor.onDirtied();
            update(getSelectedTaskArtifacts().toArray(), null);
            return true;
         }
      } else if (PromptChangeUtil.promptChangeAttribute(getSelectedTaskArtifacts(), AtsAttributeTypes.Resolution,
         false, false)) {
         editor.onDirtied();
         update(getSelectedTaskArtifacts().toArray(), null);
         return true;
      }
      return false;
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

         if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Estimated_Hours_Col)) {
            modified = PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.EstimatedHours, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Title_Col)) {
            modified = PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.Title, false, false);
         } else if (xCol.equals(WorldXViewerFactory.Related_To_State_Col)) {
            modified = RelatedToStateColumn.promptChangeRelatedToState(taskArt, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Related_To_State_Col)) {
            modified = PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.RelatedToState, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Assignees_Col)) {
            modified = PromptChangeUtil.promptChangeAssignees(taskArt, false);
         } else if (isUsingTaskResolutionOptions() && (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col))) {
            modified = handleChangeResolution();
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Resolution_Col)) {
            modified = handleChangeResolution();
         } else if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col)) {
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
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isTasksEditable() {
      return tasksEditable;
   }

   /**
    * @param tasksEditable the tasksEditable to set
    */
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
