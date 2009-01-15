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
package org.eclipse.nebula.widgets.xviewer.test;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class XViewerTestLabelProvider extends XViewerLabelProvider {
   Font font = null;
   private final XViewerTest xViewerTest;

   public XViewerTestLabelProvider(XViewerTest xViewerTest) {
      super(xViewerTest);
      this.xViewerTest = xViewerTest;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1)
            return (String) element;
         else
            return "";
      }
      IXViewerTestTask task = ((IXViewerTestTask) element);
      if (task == null) return "";
      if (xCol.equals(XViewerTestFactory.Run_Col)) return String.valueOf(xViewerTest.isRun(task));
      if (xCol.equals(XViewerTestFactory.Name_Col)) return task.getId();
      if (xCol.equals(XViewerTestFactory.Schedule_Time)) return task.getStartTime();
      if (xCol.equals(XViewerTestFactory.Run_Db)) return task.getRunDb().name();
      if (xCol.equals(XViewerTestFactory.Task_Type)) return task.getTaskType().name();
      if (xCol.equals(XViewerTestFactory.Description)) return task.getDescription();
      if (xCol.equals(XViewerTestFactory.Category)) return task.getCategory();
      if (xCol.equals(XViewerTestFactory.Notification)) return task.getEmailAddress();
      return "unhandled column";
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

   /**
    * Allows test to be run as standalone without workbench kickoff.<br>
    * TODO Add ability to display images when XViewerTest kicked off as Java Application
    * 
    * @param imageName
    * @return
    */
   private Image getSkynetImages(String imageName) {
      if (Activator.getInstance() != null) return Activator.getInstance().getImage(imageName);
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider#getColumnImage(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      if (xCol.equals(XViewerTestFactory.Run_Col)) {
         return xViewerTest.isRun((IXViewerTestTask) element) ? getSkynetImages("chkbox_enabled.gif") : getSkynetImages("chkbox_disabled.gif");
      }
      if (xCol.equals(XViewerTestFactory.Name_Col) && xViewerTest.isScheduled((IXViewerTestTask) element)) {
         return getSkynetImages("clock.gif");
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
    */
   @Override
   public Color getBackground(Object element, int columnIndex) {
      return null;
   }

}
