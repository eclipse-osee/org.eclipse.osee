/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.core.task.TaskArtifact;
import org.eclipse.osee.ats.core.task.TaskResOptionDefinition;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.editor.SMAPromptChangeStatus;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinitionToSwtColor;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class ResolutionColumn extends XViewerAtsAttributeValueColumn {

   public static ResolutionColumn instance = new ResolutionColumn();

   public static ResolutionColumn getInstance() {
      return instance;
   }

   private ResolutionColumn() {
      super(AtsAttributeTypes.Resolution, 150, SWT.LEFT, false, SortDataType.String, true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ResolutionColumn copy() {
      ResolutionColumn newXCol = new ResolutionColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public boolean isMultiLineStringAttribute() {
      return true;
   }

   public static boolean promptChangeResolutionOfTasks(XViewer xViewer, TaskArtifact taskArt, boolean persist) {
      return promptChangeResolutionOfTasks(xViewer, Arrays.asList(taskArt), persist);
   }

   public static boolean promptChangeResolutionOfTasks(XViewer xViewer, final Collection<? extends TaskArtifact> tasks, boolean persist) {
      try {
         // Ensure tasks are related to current state of workflow
         Result result = SMAPromptChangeStatus.isValidToChangeStatus(tasks);
         if (result.isFalse()) {
            AWorkbench.popup(result);
            return false;
         }

         if (tasks.iterator().next().isUsingTaskResolutionOptions()) {
            if (SMAPromptChangeStatus.promptChangeStatus(tasks, false)) {
               //            editor.onDirtied();
               xViewer.update(tasks.toArray(), null);
               return true;
            }
         } else if (PromptChangeUtil.promptChangeAttribute(tasks, AtsAttributeTypes.Resolution, false, true)) {
            //         editor.onDirtied();
            xViewer.update(tasks.toArray(), null);
            return true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public boolean isSelectedTaskArtifactsAreInWork(final Collection<? extends TaskArtifact> tasks) throws OseeCoreException {
      for (TaskArtifact taskArt : tasks) {
         if (!taskArt.isInWork()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (Artifacts.isOfType(treeItem, AtsArtifactTypes.Task)) {
         return promptChangeResolutionOfTasks(getXViewer(), (TaskArtifact) treeItem.getData(), isPersistAltLeftClick());
      }
      return super.handleAltLeftClick(treeColumn, treeItem);
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      try {
         TaskArtifact taskArt = (TaskArtifact) element;
         TaskResOptionDefinition def =
            taskArt.getTaskResolutionOptionDefinition((String) taskArt.getSoleAttributeValue(
               AtsAttributeTypes.Resolution, null));
         if (def != null) {
            return Displays.getSystemColor(TaskResOptionDefinitionToSwtColor.getColorInt(def));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return null;
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      List<TaskArtifact> tasksUsingResOptions = new ArrayList<TaskArtifact>();
      List<TaskArtifact> allTasks = new ArrayList<TaskArtifact>();
      List<TreeItem> otherTreeItems = new ArrayList<TreeItem>();
      for (TreeItem item : treeItems) {
         try {
            if (Artifacts.isOfType(item, AtsArtifactTypes.Task)) {
               allTasks.add((TaskArtifact) item.getData());
            }
            if (Artifacts.isOfType(item, AtsArtifactTypes.Task) && ((TaskArtifact) item.getData()).isUsingTaskResolutionOptions()) {
               tasksUsingResOptions.add((TaskArtifact) item.getData());
            } else {
               otherTreeItems.add(item);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
      // Ensure tasks are related to current state of workflow
      try {
         Result result = SMAPromptChangeStatus.isValidToChangeStatus(allTasks);
         if (result.isFalse()) {
            AWorkbench.popup(result);
            return;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      if (!tasksUsingResOptions.isEmpty()) {
         promptChangeResolutionOfTasks(getXViewer(), tasksUsingResOptions, isPersistAltLeftClick());
      }
      if (!otherTreeItems.isEmpty()) {
         super.handleColumnMultiEdit(treeColumn, otherTreeItems);
      }
   }

}
