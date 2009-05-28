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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.world.WorldContentProvider;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   private final TaskComposite xTaskViewer;
   private final IDirtiableEditor editor;
   private boolean tasksEditable = true;
   private boolean addDeleteTaskEnabled = false;
   private static String viewerId = GUID.generateGuidStr();

   /**
    * @param parent
    * @param style
    */
   public TaskXViewer(Composite parent, int style, IDirtiableEditor editor, TaskComposite xTaskViewer) {
      super(parent, style, new TaskXViewerFactory());
      this.editor = editor;
      this.xTaskViewer = xTaskViewer;
   }

   @Override
   public String toString() {
      if (xTaskViewer == null) return "TaskXViewer";
      try {
         if (xTaskViewer.getIXTaskViewer().getParentSmaMgr() != null) {
            return "TaskXViewer - id:" + viewerId + " - " + xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma().toString();
         }
         return "TaskXViewer - id:" + viewerId + " - " + xTaskViewer.getIXTaskViewer().toString();
      } catch (Exception ex) {
         return "TaskXViewer - id:" + viewerId;
      }
   }

   public boolean isUsingTaskResolutionOptions() {
      try {
         if (getSelectedTaskArtifact() == null) return false;
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
      for (TreeItem item : treeItems)
         items.add((TaskArtifact) item.getData());
      refresh();
      editor.onDirtied();
   }

   @Override
   public void set(Collection<? extends Artifact> artifacts) {
      for (Artifact art : artifacts)
         if (!(art instanceof TaskArtifact)) throw new IllegalArgumentException("set only allowed for TaskArtifact");
      ((WorldContentProvider) getContentProvider()).set(artifacts);
   }

   @Override
   public void add(final Artifact artifact) {
      if (!(artifact instanceof TaskArtifact)) throw new IllegalArgumentException("set only allowed for TaskArtifact");
      add(Arrays.asList(artifact));
   }

   @Override
   public void add(Collection<Artifact> artifacts) {
      for (Artifact art : artifacts)
         if (!(art instanceof TaskArtifact)) throw new IllegalArgumentException("add only allowed for TaskArtifact");
      ((WorldContentProvider) getContentProvider()).add(artifacts);
   }

   public void removeTask(final Collection<TaskArtifact> artifacts) {
      ((WorldContentProvider) getContentProvider()).removeAll(artifacts);
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
            if (obj instanceof TaskArtifact) if (!((TaskArtifact) obj).isInWork()) return false;
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

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editTaskTitleAction = new Action("Edit Task Title", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               SMAManager taskSmaMgr = new SMAManager(getSelectedTaskArtifact());
               boolean success = taskSmaMgr.promptChangeAttribute(ATSAttributes.TITLE_ATTRIBUTE, false, false);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskAssigneesAction = new Action("Edit Task Assignees", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (SMAManager.promptChangeAssignees(getSelectedTaskArtifacts(), false)) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskStatusAction = new Action("Edit Task Status", Action.AS_PUSH_BUTTON) {
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

      editTaskResolutionAction = new Action("Edit Task Resolution", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               handleChangeResolution();
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskEstimateAction = new Action("Edit Task Estimate", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                     ArtifactPromptChange.promptChangeFloatAttribute(
                           ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(),
                           ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName(), getSelectedTaskArtifacts(), false);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskRelatedStateAction = new Action("Edit Task Related to State", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                     SMAManager.promptChangeAttribute(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE,
                           getSelectedTaskArtifacts(), false, true);
               if (success) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifacts().toArray(), null);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      };

      editTaskNotesAction = new Action("Edit Task Notes", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               boolean success =
                     SMAManager.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, getSelectedTaskArtifacts(),
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

      addNewTaskAction = new Action("New Task", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            xTaskViewer.handleNewTask();
         }
      };

      deleteTasksAction = new Action("Delete Task", Action.AS_PUSH_BUTTON) {
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
   public void updateMenuActions() {
      super.updateMenuActions();
      MenuManager mm = getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, new Separator());
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, addNewTaskAction);
      addNewTaskAction.setEnabled(isTasksEditable() && addDeleteTaskEnabled);

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, deleteTasksAction);
      deleteTasksAction.setEnabled(isTasksEditable() && addDeleteTaskEnabled && getSelectedTaskArtifacts().size() > 0);

   }

   public boolean handleChangeResolution() throws OseeCoreException {
      // Ensure tasks are related to current state of workflow 
      SMAPromptChangeStatus promptChangeStatus = new SMAPromptChangeStatus(getSelectedTaskArtifacts());
      Result result = promptChangeStatus.isValidToChangeStatus();
      if (result.isFalse()) return false;

      if (isUsingTaskResolutionOptions()) {
         if (SMAPromptChangeStatus.promptChangeStatus(getSelectedTaskArtifacts(), false)) {
            editor.onDirtied();
            update(getSelectedTaskArtifacts().toArray(), null);
            return true;
         }
      } else if (SMAManager.promptChangeAttribute(ATSAttributes.RESOLUTION_ATTRIBUTE, getSelectedTaskArtifacts(),
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
      SMAManager taskSmaMgr = new SMAManager((TaskArtifact) treeItem.getData());
      boolean modified = false;
      try {
         if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Estimated_Hours_Col)) {
            modified = taskSmaMgr.promptChangeFloatAttribute(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Title_Col)) {
            modified = taskSmaMgr.promptChangeAttribute(ATSAttributes.TITLE_ATTRIBUTE, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Related_To_State_Col)) {
            modified = taskSmaMgr.promptChangeAttribute(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE, false, false);
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Assignees_Col)) {
            modified = taskSmaMgr.promptChangeAssignees(false);
         } else if (isUsingTaskResolutionOptions() && (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col))) {
            modified = handleChangeResolution();
         } else if (isSelectedTaskArtifactsAreInWork() && xCol.equals(WorldXViewerFactory.Resolution_Col)) {
            modified = handleChangeResolution();
         } else if (xCol.equals(WorldXViewerFactory.Hours_Spent_State_Col) || xCol.equals(WorldXViewerFactory.Hours_Spent_Total_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_State_Col) || xCol.equals(WorldXViewerFactory.Percent_Complete_Total_Col)) {
            modified = SMAPromptChangeStatus.promptChangeStatus(Arrays.asList(taskSmaMgr.getSma()), false);
         } else
            modified = super.handleAltLeftClick(treeColumn, treeItem, false);

         if (modified) {
            editor.onDirtied();
            update((treeItem.getData()), null);
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
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().size() == 0) return;
         // ContentProvider ensures in display thread
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  WorldContentProvider contentProvider =
                        (WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider();
                  if (contentProvider != null) {
                     contentProvider.removeAll(loadedArtifacts.getLoadedArtifacts());
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().size() == 0) return;
         // ContentProvider ensures in display thread
         ((WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider()).removeAll(loadedArtifacts.getLoadedArtifacts());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.WorldXViewer#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (xTaskViewer.getTaskXViewer().getContentProvider() == null) return;
            ((WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider()).removeAll(transData.cacheDeletedArtifacts);
            ((WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider()).updateAll(transData.cacheChangedArtifacts);

            try {
               if (xTaskViewer.getIXTaskViewer().getParentSmaMgr() == null) {
                  return;
               }
               Artifact parentSma = xTaskViewer.getIXTaskViewer().getParentSmaMgr().getSma();
               if (parentSma != null) {
                  // Add any new tasks related to parent sma
                  Collection<Artifact> artifacts =
                        transData.getRelatedArtifacts(parentSma.getArtId(),
                              AtsRelation.SmaToTask_Task.getRelationType().getRelationTypeId(),
                              AtsPlugin.getAtsBranch().getBranchId(), transData.cacheAddedRelations);
                  if (artifacts.size() > 0) {
                     ((WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider()).add(artifacts);
                  }

                  // Remove any tasks related to parent sma
                  artifacts =
                        transData.getRelatedArtifacts(parentSma.getArtId(),
                              AtsRelation.SmaToTask_Task.getRelationType().getRelationTypeId(),
                              AtsPlugin.getAtsBranch().getBranchId(), transData.cacheDeletedRelations);
                  if (artifacts.size() > 0) {
                     ((WorldContentProvider) xTaskViewer.getTaskXViewer().getContentProvider()).removeAll(artifacts);
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   /**
    * @return the addDeleteTaskEnabled
    */
   public boolean isAddDeleteTaskEnabled() {
      return addDeleteTaskEnabled;
   }

   /**
    * @param addDeleteTaskEnabled the addDeleteTaskEnabled to set
    */
   public void setAddDeleteTaskEnabled(boolean addDeleteTaskEnabled) {
      this.addDeleteTaskEnabled = addDeleteTaskEnabled;
   }

}
