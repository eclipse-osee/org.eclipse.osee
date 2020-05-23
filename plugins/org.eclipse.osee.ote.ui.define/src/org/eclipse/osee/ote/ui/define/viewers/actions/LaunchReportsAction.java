/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.ui.define.viewers.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.ui.define.dialogs.ReportsDialog;
import org.eclipse.osee.ote.ui.define.utilities.EditorUtility;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;

/**
 * @author Roberto E. Escobar
 */
public class LaunchReportsAction extends AbstractActionHandler {

   public LaunchReportsAction(StructuredViewer viewer, String text) throws Exception {
      super(viewer, text);
   }

   public LaunchReportsAction(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(viewer, text, image);
   }

   @Override
   public void run() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            ReportsDialog dialog = new ReportsDialog(AWorkbench.getActiveShell());
            int result = dialog.open();
            if (result == Window.OK) {
               String reportId = dialog.getReportSelected();
               String format = dialog.getReportFormat();
               EditorUtility.openEditor(reportId, format);
            }
         }
      });
   }

   @Override
   public void updateState() {
      if (getViewer() != null) {
         setEnabled(SelectionHelper.getInstance().getSelections(getViewer()).size() > 0);
      }
   }
}
