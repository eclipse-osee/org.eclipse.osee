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
package org.eclipse.osee.ote.ui.define.viewers.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ote.ui.define.dialogs.ReportsDialog;
import org.eclipse.osee.ote.ui.define.utilities.EditorUtility;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

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
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            ReportsDialog dialog = new ReportsDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
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
