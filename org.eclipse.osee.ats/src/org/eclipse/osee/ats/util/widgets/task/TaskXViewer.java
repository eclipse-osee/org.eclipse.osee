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
package org.eclipse.osee.ats.util.widgets.task;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.world.AtsXColumn;
import org.eclipse.osee.ats.world.WorldArtifactItem;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class TaskXViewer extends WorldXViewer {

   private static String NAMESPACE = "org.eclipse.osee.ats.TaskXViewer";
   private final XTaskViewer xTaskViewer;
   private final IDirtiableEditor editor;
   private final boolean isUsingTaskResolutionOptions;
   private final List<TaskResOptionDefinition> taskResOptionDefinitions;
   private Map<String, TaskResOptionDefinition> nameToResOptionDef = null;
   private boolean tasksEditable = true;

   /**
    * @param parent
    * @param style
    */
   public TaskXViewer(Composite parent, int style, IDirtiableEditor editor, boolean isUsingTaskResolutionOptions, List<TaskResOptionDefinition> taskResOptionDefinition, XTaskViewer xTaskViewer) {
      super(parent, style, NAMESPACE, new TaskXViewerFactory());
      this.editor = editor;
      this.isUsingTaskResolutionOptions = isUsingTaskResolutionOptions;
      this.taskResOptionDefinitions = taskResOptionDefinition;
      this.xTaskViewer = xTaskViewer;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      handleColumnMultiEdit(treeColumn, treeItems, false);
      Set<TaskArtifactItem> items = new HashSet<TaskArtifactItem>();
      for (TreeItem item : treeItems)
         items.add((TaskArtifactItem) item.getData());
      refresh();
      editor.onDirtied();
   }

   @Override
   public void set(Collection<? extends Artifact> artifacts) {
      for (Artifact art : artifacts)
         if (!(art instanceof TaskArtifact)) throw new IllegalArgumentException("set only allowed for TaskArtifact");
      Set<TaskArtifactItem> items = new HashSet<TaskArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new TaskArtifactItem(this, art, null));
      ((TaskContentProvider) getContentProvider()).set(items);
   }

   @Override
   public void add(final Artifact artifact) {
      if (!(artifact instanceof TaskArtifact)) throw new IllegalArgumentException("set only allowed for TaskArtifact");
      add(Arrays.asList(new Artifact[] {artifact}));
   }

   @Override
   public void add(Collection<Artifact> artifacts) {
      for (Artifact art : artifacts)
         if (!(art instanceof TaskArtifact)) throw new IllegalArgumentException("set only allowed for TaskArtifact");
      Set<TaskArtifactItem> items = new HashSet<TaskArtifactItem>();
      for (Artifact art : artifacts)
         items.add(new TaskArtifactItem(this, art, null));
      ((TaskContentProvider) getContentProvider()).add(items);
   }

   public void removeTask(final Collection<TaskArtifact> artifacts) {
      ((TaskContentProvider) getContentProvider()).remove(artifacts);
   }

   public TaskArtifact getSelectedTaskArtifact() {
      Collection<TaskArtifact> arts = getSelectedTaskArtifacts();
      if (arts.size() > 0) return arts.iterator().next();
      return null;
   }

   public Collection<TaskArtifact> getSelectedTaskArtifacts() {
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      ArrayList<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifactItem) taskArts.add(((TaskArtifactItem) obj).getTaskArtifact());
      }
      return taskArts;
   }

   public boolean isSelectedTaskArtifactsAreInWork() {
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifactItem) if (!((TaskArtifactItem) obj).getTaskArtifact().isInWork()) return false;
      }
      return true;
   }

   public Collection<TaskArtifactItem> getSelectedTaskArtifactItems() {
      Iterator<?> i = ((IStructuredSelection) getSelection()).iterator();
      ArrayList<TaskArtifactItem> items = new ArrayList<TaskArtifactItem>();
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof TaskArtifactItem) items.add((TaskArtifactItem) obj);
      }
      return items;
   }

   public Object[] getSelectedTaskArtifactItemsArray() {
      return getSelectedArtifactItems().toArray(new TaskArtifactItem[getSelectedArtifactItems().size()]);
   }

   Action editTaskTitleAction;
   Action editTaskAssigneesAction;
   Action editTaskStatusAction;
   Action editTaskResolutionAction;
   Action editTaskEstimateAction;
   Action editTaskRelatedStateAction;
   Action editTaskNotesAction;
   Action addNewTaskAction;
   Action deleteTasksAction;

   @Override
   public void createMenuActions() {
      super.createMenuActions();

      editTaskTitleAction = new Action("Edit Task Title", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            SMAManager taskSmaMgr = new SMAManager(getSelectedTaskArtifact());
            if (taskSmaMgr.promptChangeAttribute(ATSAttributes.TITLE_ATTRIBUTE, false)) {
               editor.onDirtied();
               update(getSelectedTaskArtifactItemsArray(), null);
            }
         }
      };

      editTaskAssigneesAction = new Action("Edit Task Assignees", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangeAssignees(getSelectedTaskArtifacts())) {
               editor.onDirtied();
               update(getSelectedTaskArtifactItemsArray(), null);
            }
         }
      };

      editTaskStatusAction = new Action("Edit Task Status", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangeStatus((isUsingTaskResolutionOptions ? taskResOptionDefinitions : null),
                  getSelectedTaskArtifacts(), false)) {
               editor.onDirtied();
               update(getSelectedTaskArtifactItemsArray(), null);
            }
         }
      };

      editTaskResolutionAction = new Action("Edit Task Resolution", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            handleChangeResolution();
         }
      };

      editTaskEstimateAction = new Action("Edit Task Estimate", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            try {
               if (ArtifactPromptChange.promptChangeFloatAttribute(
                     ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(),
                     ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName(), getSelectedTaskArtifacts(), false)) {
                  editor.onDirtied();
                  update(getSelectedTaskArtifactItemsArray(), null);
               }
            } catch (SQLException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      };

      editTaskRelatedStateAction = new Action("Edit Task Related to State", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangeAttribute(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE, getSelectedTaskArtifacts(),
                  false)) {
               editor.onDirtied();
               update(getSelectedTaskArtifactItemsArray(), null);
            }
         }
      };

      editTaskNotesAction = new Action("Edit Task Notes", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            if (SMAManager.promptChangeAttribute(ATSAttributes.SMA_NOTE_ATTRIBUTE, getSelectedTaskArtifacts(), false)) {
               editor.onDirtied();
               update(getSelectedTaskArtifactItemsArray(), null);
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
            xTaskViewer.handleDeleteTask();
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

      if (!isUsingTaskResolutionOptions) {
         mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskResolutionAction);
         editTaskResolutionAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());
      }

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskEstimateAction);
      editTaskEstimateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskRelatedStateAction);
      editTaskRelatedStateAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() > 0 && isSelectedTaskArtifactsAreInWork());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_EDIT, editTaskNotesAction);
      editTaskNotesAction.setEnabled(isTasksEditable() && getSelectedArtifacts().size() == 1 && isSelectedTaskArtifactsAreInWork());

   }

   @Override
   public void updateMenuActions() {
      super.updateMenuActions();
      MenuManager mm = getMenuManager();

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, new Separator());
      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, addNewTaskAction);
      addNewTaskAction.setEnabled(isTasksEditable());

      mm.insertBefore(WorldXViewer.MENU_GROUP_ATS_WORLD_OPEN, deleteTasksAction);
      deleteTasksAction.setEnabled(isTasksEditable() && getSelectedTaskArtifacts().size() > 0);

   }

   public boolean handleChangeResolution() {
      if (isUsingTaskResolutionOptions) {
         if (SMAManager.promptChangeStatus(taskResOptionDefinitions, getSelectedTaskArtifacts(), false)) {
            editor.onDirtied();
            update(getSelectedTaskArtifactItemsArray(), null);
            return true;
         }
      } else if (SMAManager.promptChangeAttribute(ATSAttributes.RESOLUTION_ATTRIBUTE, getSelectedTaskArtifacts(), false)) {
         editor.onDirtied();
         update(getSelectedTaskArtifactItemsArray(), null);
         return true;
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.viewer.XViewer#handleAltLeftClick(org.eclipse.swt.widgets.TreeColumn,
    *      org.eclipse.swt.widgets.TreeItem)
    */
   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (!isTasksEditable()) {
         AWorkbench.popup("ERROR", "Editing disabled for current state.");
         return false;
      }
      XViewerColumn xCol = (XViewerColumn) treeColumn.getData();
      AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
      SMAManager taskSmaMgr = new SMAManager(((TaskArtifactItem) treeItem.getData()).getTaskArtifact());
      boolean modified = false;
      try {
         if (isSelectedTaskArtifactsAreInWork() && aCol == AtsXColumn.Estimated_Hours_Col) {
            modified = taskSmaMgr.promptChangeFloatAttribute(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, false);
         } else if (isSelectedTaskArtifactsAreInWork() && aCol == AtsXColumn.Title_Col) {
            modified = taskSmaMgr.promptChangeAttribute(ATSAttributes.TITLE_ATTRIBUTE, false);
         } else if (isSelectedTaskArtifactsAreInWork() && aCol == AtsXColumn.Related_To_State_Col) {
            modified = taskSmaMgr.promptChangeAttribute(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE, false);
         } else if (isSelectedTaskArtifactsAreInWork() && aCol == AtsXColumn.Assignees_Col) {
            modified = taskSmaMgr.promptChangeAssignees();
         } else if (isUsingTaskResolutionOptions && (aCol == AtsXColumn.Total_Hours_Spent_Col || aCol == AtsXColumn.Total_Percent_Complete_Col)) {
            modified = handleChangeResolution();
         } else if (isSelectedTaskArtifactsAreInWork() && aCol == AtsXColumn.Resolution_Col) {
            modified = handleChangeResolution();
         } else if ((aCol == AtsXColumn.Total_Hours_Spent_Col || aCol == AtsXColumn.Total_Percent_Complete_Col)) {
            modified = taskSmaMgr.promptChangeStatus(false);
         } else
            modified = super.handleAltLeftClick(treeColumn, treeItem, false);

         if (modified) {
            editor.onDirtied();
            update(((TaskArtifactItem) treeItem.getData()), null);
            return true;
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
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

   public Collection<WorldArtifactItem> getRootSet() {
      return xTaskViewer.getXViewer().getRootSet();
   }

   /**
    * @return the isUsingTaskResolutionOptions
    */
   public boolean isUsingTaskResolutionOptions() {
      return isUsingTaskResolutionOptions;
   }

   /**
    * @return the TaskResOptionDefinition
    */
   public TaskResOptionDefinition getTaskResOptionDefinition(String optionName) {
      if (nameToResOptionDef == null) {
         nameToResOptionDef = new HashMap<String, TaskResOptionDefinition>();
         for (TaskResOptionDefinition def : taskResOptionDefinitions) {
            nameToResOptionDef.put(def.getName(), def);
         }
      }
      return nameToResOptionDef.get(optionName);
   }

}
