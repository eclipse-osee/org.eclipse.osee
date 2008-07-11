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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XViewerTestLabelProvider implements ITableLabelProvider {
   Font font = null;

   private final XViewerTest xViewerTest;

   public XViewerTestLabelProvider(XViewerTest xViewerTest) {
      super();
      this.xViewerTest = xViewerTest;
   }

   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      IXViewerTestTask autoRunTask = ((IXViewerTestTask) element);
      if (autoRunTask == null) return "";
      XViewerColumn xCol = xViewerTest.getXTreeColumn(columnIndex);
      if (xCol != null) {
         XViewerTestColumns aCol = XViewerTestColumns.getAtsXColumn(xCol);
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
   public String getColumnText(Object element, int columnIndex, IXViewerTestTask task, XViewerColumn xCol, XViewerTestColumns aCol) {
      if (!xCol.isShow()) return ""; // Since not shown, don't display
      if (aCol == XViewerTestColumns.Run_Col) return String.valueOf(xViewerTest.isRun(task));
      if (aCol == XViewerTestColumns.Name_Col) return task.getId();
      if (aCol == XViewerTestColumns.Schedule_Time) return task.getStartTime();
      if (aCol == XViewerTestColumns.Run_Db) return task.getRunDb().name();
      if (aCol == XViewerTestColumns.Task_Type) return task.getTaskType().name();
      if (aCol == XViewerTestColumns.Description) return task.getDescription();
      if (aCol == XViewerTestColumns.Category) return task.getCategory();
      if (aCol == XViewerTestColumns.Notification) return task.getEmailAddress();
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

   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof String) return null;
      IXViewerTestTask task = (IXViewerTestTask) element;
      XViewerColumn xCol = xViewerTest.getXTreeColumn(columnIndex);
      if (xCol == null) return null;
      XViewerTestColumns aCol = XViewerTestColumns.getAtsXColumn(xCol);
      if (!xCol.isShow()) return null; // Since not shown, don't display
      if (aCol == XViewerTestColumns.Run_Col) {
         return xViewerTest.isRun(task) ? getSkynetImages("chkbox_enabled.gif") : getSkynetImages("chkbox_disabled.gif");
      }
      if (aCol == XViewerTestColumns.Name_Col && xViewerTest.isScheduled(task)) {
         return getSkynetImages("clock.gif");
      }
      return null;
   }

   /**
    * Allows test to be run as standalone without workbench kickoff.<br>
    * TODO Add ability to display images when XViewerTest kicked off as Java Application
    * 
    * @param imageName
    * @return
    */
   private Image getSkynetImages(String imageName) {
      if (SkynetGuiPlugin.getInstance() != null) return SkynetGuiPlugin.getInstance().getImage(imageName);
      return null;
   }
}
