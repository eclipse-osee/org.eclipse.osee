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

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.dialog.TaskResOptionDefinition;
import org.eclipse.osee.ats.world.WorldLabelProvider;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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

   @Override
   public String getColumnText(Object element, XViewerColumn col, int columnIndex) {
      TaskArtifact taskArt = (TaskArtifact) element;
      if (taskArt == null) return "";
      if (taskArt.isDeleted()) {
         if (col.equals(WorldXViewerFactory.ID_Col))
            return taskArt.getHumanReadableId();
         else if (col.equals(WorldXViewerFactory.Title_Col))
            return taskArt.getInternalDescriptiveName();
         else
            return "<deleted>";
      }
      if (col.equals(WorldXViewerFactory.Assignees_Col)) {
         return (new SMAManager(taskArt)).getAssigneesWasIsStr();
      }
      return super.getColumnText(element, col, columnIndex);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      if (!col.isShow()) return null; // Since not shown, don't display
      if (col.equals(WorldXViewerFactory.Title_Col)) return ((TaskArtifact) element).getImage();
      return super.getColumnImage(element, col, columnIndex);
   }

   @Override
   public Color getForeground(Object element, XViewerColumn col, int columnIndex) {
      try {
         if (col.equals(WorldXViewerFactory.Resolution_Col)) {
            TaskArtifact taskArt = (TaskArtifact) element;
            if (taskArt != null) {
               TaskResOptionDefinition def = taskXViewer.getTaskResOptionDefinition(taskArt.getWorldViewResolution());
               if (def != null) {
                  return Display.getCurrent().getSystemColor(def.getColorInt());
               }
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
      return null;
   }
}
