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

import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.world.AtsXColumn;
import org.eclipse.osee.ats.world.WorldArtifactItem;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class TaskLabelProvider extends WorldLabelProvider {

   private final TaskXViewer taskXViewer;

   /**
    * @param treeViewer
    */
   public TaskLabelProvider(TaskXViewer treeViewer) {
      super(treeViewer);
      this.taskXViewer = treeViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.world.WorldLabelProvider#getColumnText(java.lang.Object, int)
    */
   @Override
   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      TaskArtifact taskArt = ((TaskArtifactItem) element).getTaskArtifact();
      if (taskArt == null || taskArt.isDeleted()) return "";
      XViewerColumn xCol = taskXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         if (!xCol.isShow()) return ""; // Since not shown, don't display
         if (aCol == AtsXColumn.Assignees_Col) {
            return (new SMAManager(taskArt)).getAssigneesWasIsStr();
         }
         return super.getColumnText(element, columnIndex, taskArt, xCol, aCol);
      }
      return "";
   }

   @Override
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      Artifact artifact = ((WorldArtifactItem) element).getArtifact();
      if (artifact == null || artifact.isDeleted()) return null;
      XViewerColumn xCol = taskXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         if (!xCol.isShow()) return null; // Since not shown, don't display
         if (aCol == AtsXColumn.Title_Col) return artifact.getImage();
         return super.getColumnImage(element, columnIndex, artifact, xCol, aCol);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.WorldLabelProvider#getForeground(java.lang.Object, int)
    */
   @Override
   public Color getForeground(Object element, int columnIndex) {
      if (!taskXViewer.isUsingTaskResolutionOptions()) return null;
      XViewerColumn xCol = taskXViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AtsXColumn aCol = AtsXColumn.getAtsXColumn(xCol);
         if (aCol != null && aCol == AtsXColumn.Resolution_Col) {
            TaskArtifact taskArt = (TaskArtifact) ((WorldArtifactItem) element).getArtifact();
            if (taskArt != null) {
               TaskResOptionDefinition def = taskXViewer.getTaskResOptionDefinition(taskArt.getWorldViewResolution());
               if (def != null) {
                  return Display.getCurrent().getSystemColor(def.getColorInt());
               }
            }
         }
      }
      return super.getForeground(element, columnIndex);
   }

}
