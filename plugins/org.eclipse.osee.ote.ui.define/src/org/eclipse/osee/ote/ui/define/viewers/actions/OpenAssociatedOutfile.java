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
import org.eclipse.osee.ote.ui.define.internal.Activator;
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