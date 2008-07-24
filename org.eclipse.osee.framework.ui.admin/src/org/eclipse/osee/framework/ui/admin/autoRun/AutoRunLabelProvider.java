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

import java.sql.SQLException;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.autoRun.AutoRunStartup;
import org.eclipse.osee.framework.ui.skynet.autoRun.IAutoRunTask;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class AutoRunLabelProvider extends XViewerLabelProvider {
   Font font = null;

   private final AutoRunXViewer autoRunXViewer;

   public AutoRunLabelProvider(AutoRunXViewer autoRunXViewer) {
      super(autoRunXViewer);
      this.autoRunXViewer = autoRunXViewer;
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException, SQLException {
      IAutoRunTask autoRunTask = (IAutoRunTask) element;
      if (autoRunXViewer.getXAutoRunViewer().isLaunchNewWorkbench()) {
         if (aCol.equals(AutoRunXViewerFactory.Run_Col)) return autoRunXViewer.isRun(autoRunTask) ? SkynetGuiPlugin.getInstance().getImage(
               "chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage("chkbox_disabled.gif");
      } else {
         if (aCol.equals(AutoRunXViewerFactory.Run_Col)) {
            Result result = AutoRunStartup.validateAutoRunExecution(autoRunTask);
            if (result.isFalse()) return SkynetGuiPlugin.getInstance().getImage("chkbox_redslash.gif");
            return autoRunXViewer.isRun(autoRunTask) ? SkynetGuiPlugin.getInstance().getImage("chkbox_enabled.gif") : SkynetGuiPlugin.getInstance().getImage(
                  "chkbox_disabled.gif");
         }
      }
      if (aCol.equals(AutoRunXViewerFactory.Name_Col) && autoRunXViewer.getXAutoRunViewer().isScheduled(autoRunTask)) {
         return SkynetGuiPlugin.getInstance().getImage("clock.gif");
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn aCol, int columnIndex) throws OseeCoreException, SQLException {
      IAutoRunTask autoRunTask = (IAutoRunTask) element;
      if (aCol.equals(AutoRunXViewerFactory.Run_Col)) return "";
      if (aCol.equals(AutoRunXViewerFactory.Name_Col)) return autoRunTask.getAutoRunUniqueId();
      if (aCol.equals(AutoRunXViewerFactory.Schedule_Time)) return autoRunTask.get24HourStartTime();
      if (aCol.equals(AutoRunXViewerFactory.Run_Db)) return autoRunTask.getRunDb().name();
      if (aCol.equals(AutoRunXViewerFactory.Task_Type)) return autoRunTask.getTaskType().name();
      if (aCol.equals(AutoRunXViewerFactory.Description)) return autoRunTask.getDescription();
      if (aCol.equals(AutoRunXViewerFactory.Category)) return autoRunTask.getCategory();
      if (aCol.equals(AutoRunXViewerFactory.Notification)) return Lib.getCommaString(autoRunTask.getNotificationEmailAddresses());
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
      return autoRunXViewer;
   }

}
