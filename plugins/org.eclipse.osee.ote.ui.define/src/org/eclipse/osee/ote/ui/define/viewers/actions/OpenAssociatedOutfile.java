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

import java.net.URI;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Roberto E. Escobar
 */
public class OpenAssociatedOutfile extends AbstractActionHandler {

   public OpenAssociatedOutfile(StructuredViewer viewer, String text) throws Exception {
      super(viewer, text);
   }

   public OpenAssociatedOutfile(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(viewer, text, image);
   }

   @Override
   public void updateState() {
      TestRunOperator artifact = SelectionHelper.getInstance().getSelection(getViewer());
      setEnabled(artifact != null);
   }

   @Override
   public void run() {
      TestRunOperator operator = SelectionHelper.getInstance().getSelection(getViewer());
      try {
         String targetFile = operator.getOutfileUrl();
         openEditorUtility(new URI(targetFile));
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   private void openEditorUtility(final URI resource) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
               IDE.openEditorOnFileStore(page, EFS.getStore(resource));
            } catch (Exception ex) {
               handleException(ex);
            }
         }
      });
   }

   private void handleException(Exception ex) {
      OseeLog.log(Activator.class, Level.WARNING, "Unable to open outfile.", ex);
      Shell shell = AWorkbench.getActiveShell();
      MessageDialog.openError(shell, "Open Outfile", "Unable to open outfile.");
   }
}