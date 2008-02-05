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
package org.eclipse.osee.framework.ui.admin.autoRun;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class AutoRunLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final AutoRunXViewer treeViewer;

   public AutoRunLabelProvider(AutoRunXViewer treeViewer) {
      super();
      this.treeViewer = treeViewer;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      IAutoRunTask autoRunTask = ((IAutoRunTask) element);
      if (autoRunTask == null) return "";
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol != null) {
         AutoRunColumn aCol = AutoRunColumn.getAtsXColumn(xCol);
         return getColumnText(element, columnIndex, autoRunTask, xCol, aCol);
      }
      return "";
   }

   /**
    * Provided as optimization of subclassed classes so provider doesn't have to retrieve the same information that has
    * already been retrieved
    * 
    * @param element
    * @param columnIndex
    * @param defectItem
    * @param xCol
    * @param aCol
    * @return column string
    */
   public String getColumnText(Object element, int columnIndex, IAutoRunTask autoRunTask, XViewerColumn xCol, AutoRunColumn aCol) {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == AutoRunColumn.Run_Col) return "";
      if (aCol == AutoRunColumn.Name_Col) return autoRunTask.getAutoRunUniqueId();
      if (aCol == AutoRunColumn.Hour_Scheduled) return autoRunTask.getHourStartTime() + "";
      if (aCol == AutoRunColumn.Minute_Scheduled) return autoRunTask.getMinuteStartTime() + "";
      if (aCol == AutoRunColumn.Run_Db) return autoRunTask.getRunDb().name();
      if (aCol == AutoRunColumn.Task_Type) return autoRunTask.getTaskType().name();
      if (aCol == AutoRunColumn.Description) return autoRunTask.getDescription();
      if (aCol == AutoRunColumn.Category) return autoRunTask.getCategory();
      return "Unhandled Column";
   }

   public void dispose() {
      if (font != null) font.dispose();
      font = null;
   }

   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   public void addListener(ILabelProviderListener listener) {
   }

   public void removeListener(ILabelProviderListener listener) {
   }

   public AutoRunXViewer getTreeViewer() {
      return treeViewer;
   }

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      IAutoRunTask autoRunTask = (IAutoRunTask) element;
      XViewerColumn xCol = treeViewer.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      AutoRunColumn aCol = AutoRunColumn.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (aCol == AutoRunColumn.Run_Col) return treeViewer.isRun(autoRunTask) ? SkynetGuiPlugin.getInstance().getImage(
            "chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage("chkbox_disabled.gif");
      return null;
   }

}
