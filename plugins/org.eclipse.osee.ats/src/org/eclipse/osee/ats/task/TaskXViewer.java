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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   public static final String MENU_GROUP_ATS_TASK_SHOW = "ATS TASK SHOW";
   private final TaskComposite xTaskViewer;
   private final IDirtiableEditor editor;
   private boolean tasksEditable = true;
   private boolean newTaskSelectionEnabled = false;
   private static String viewerId = GUID.create();
   private final TaskComposite taskComposite;
   private ITaskAction showRelatedTasksAction = null;

   public TaskXViewer(TaskComposite taskComposite, int style, IDirtiableEditor editor, TaskComposite xTaskViewer) {
      super(taskComposite, style, new TaskXViewerFactory());
      this.taskComposite = taskComposite;
      this.editor = editor;
      this.xTaskViewer = xTaskViewer;
      registerAdvancedSectionsFromExtensionPoints();
   }

   private void registerAdvancedSectionsFromExtensionPoints() {

      ExtensionDefinedObjects<ITaskAction> extensions =
         new ExtensionDefinedObjects<ITaskAction>(AtsPlugin.PLUGIN_ID + ".AtsMenuAction", "AtsMenuAction", "classname");
      for (ITaskAction item : extensions.getObjects()) {
         try {
            showRelatedTasksAction = item;
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public String toString() {
      if (xTaskViewer == null) {
         return "TaskXViewer";
      }
      try {
         if (xTaskViewer.getIXTaskViewer().getSma() != null) {
            return "TaskXViewer - id:" + viewerId + " - " + xTaskViewer.getIXTaskViewer().getSma().toString();
         }
         return "TaskXViewer - id:" + viewerId + " - " + xTaskViewer.getIXTaskViewer().toString();
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
      Set<TaskArtifact> items = new HashSet<TaskArtifact>();
      for (TreeItem item : treeItems) {
         items.add((TaskArtifact) item.getData());
      }
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
      try {
         Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
         while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof TaskArtifact) {
               if (!((TaskArtifact) obj).isInWork()) {
                  return false;
               }
            }
         }
         return true;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

   Action editTaskTitleAction, editTaskAssigneesAction, editTaskStatusAction, editTaskResolutionAction,
      editTaskEstimateAction, editTaskRelatedStateAction, editTaskNotesAction;
   Action addNewTaskAction, deleteTasksAction;
   Action showRelatedAction;

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
               boolean success =
                  PromptChangeUtil.promptChangeAttribute(getSelectedTaskArtifacts(),
                     AtsAttributeTypes.RelatedToState, false, true);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskNotesAction = new Action("Edit Task Notes", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                  PromptChangeUtil.promptChangeAttribute(getSelectedTaskArtifacts(), AtsAttributeTypes.SmaNote,
                     false, true);
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
            xTaskViewer.handleNewTask();
         }
      };

      deleteTasksAction = new Action("Delete Task", IAction.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               xTaskViewer.handleDeleteTask();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

   }

   @Override
   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      if (showRelatedTasksAction == null) {
         registerAdvancedSectionsFromExtensionPoints();
      }
      if (showRelatedTasksAction != null && showRelatedTasksAction.isValid(getSelectedTaskArtifacts())) {
         showRelatedTasksAction.setXViewer(this);
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new GroupMarker(MENU_GROUP_ATS_TASK_SHOW));
         mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, (Action) showRelatedTasksAction);
         mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, new Separator());
         editTaskNotesAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);
      }

      // EDIT MENU BLOCK
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskTitleAction);
      editTaskTitleAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() == 1 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskAssigneesAction);
      editTaskAssigneesAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskStatusAction);
      editTaskStatusAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

      if (!isUsingTaskResolutionOptions()) {
         mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskResolutionAction);
         editTaskResolutionAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());
      }

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskEstimateAction);
      editTaskEstimateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskRelatedStateAction);
      editTaskRelatedStateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskNotesAction);
      editTaskNotesAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0);

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
      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(getSelectedTaskArtifacts());
      Result result = promptChangeStatus.isValidToChangeStatus();
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
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem, boolean persist) {
      if (!isTasksEditable()) {
         AWorkbench.popup("ERROR", "Editing disabled for current state.");
         return false;
      }
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      try {
         TaskArtifact taskArt = (TaskArtifact) treeItem.getData();
         boolean modified = false;

         if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Estimated_Hours_Col)) {
            modified =
               PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.EstimatedHours, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Title_Col)) {
            modified = PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.Title, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Related_To_State_Col)) {
            modified =
               PromptChangeUtil.promptChangeAttribute(taskArt, AtsAttributeTypes.RelatedToState, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Assignees_Col)) {
            modified = PromptChangeUtil.promptChangeAssignees(taskArt, false);
         } else if (isUsingTaskResolutionOptions() && (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col))) {
            modified = handleChangeResolution();
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Resolution_Col)) {
            modified = handleChangeResolution();
         } else if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col)) {
            modified = SMAPromptChangeStatus.promptChangeStatus(Arrays.asList(taskArt), false);
         } else {
            modified = super.handleAltLeftClick(treeColumn, treeItem, false);
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

   /**
    * @return the tasksEditable
    */
   public boolean isTasksEditable() {
      return tasksEditable;
   }

   /**
    * @param tasksEditable the tasksEditable to set
    */
   public void setTasksEditable(boolean tasksEditable) {
      this.tasksEditable = tasksEditable;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != AtsUtil.getAtsBranch().getId()) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xTaskViewer.getTaskXViewer().getContentProvider() == null) {
               return;
            }
            remove(transData.cacheDeletedArtifacts.toArray(new Object[transData.cacheDeletedArtifacts.size()]));
            update(transData.cacheChangedArtifacts.toArray(new Object[transData.cacheChangedArtifacts.size()]), null);
            try {
               if (xTaskViewer.getIXTaskViewer().getSma() == null) {
                  return;
               }
               Artifact parentSma = xTaskViewer.getIXTaskViewer().getSma();
               if (parentSma != null) {
                  // Add any new tasks related to parent sma
                  Collection<TaskArtifact> artifacts =
                     Collections.castMatching(TaskArtifact.class, transData.getRelatedArtifacts(parentSma.getArtId(),
                        RelationTypeManager.getTypeId(AtsRelationTypes.SmaToTask_Task), AtsUtil.getAtsBranch().getId(),
                        transData.cacheAddedRelations));
                  if (artifacts.size() > 0) {
                     taskComposite.add(artifacts);
                  }

                  // Remove any tasks related to parent sma
                  artifacts =
                     Collections.castMatching(TaskArtifact.class, transData.getRelatedArtifacts(parentSma.getArtId(),
                        RelationTypeManager.getTypeId(AtsRelationTypes.SmaToTask_Task), AtsUtil.getAtsBranch().getId(),
                        transData.cacheDeletedRelations));
                  if (artifacts.size() > 0) {
                     remove(artifacts.toArray(new Object[artifacts.size()]));
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   public boolean isNewTaskSelectionEnabled() {
      return newTaskSelectionEnabled;
   }

   public void setNewTaskSelectionEnabled(boolean newTaskSelectionEnabled) {
      this.newTaskSelectionEnabled = newTaskSelectionEnabled;
   }

}
