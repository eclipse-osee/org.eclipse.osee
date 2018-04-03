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

import java.net.URL;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.Activator;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Roberto E. Escobar
 */
public class OpenAssociatedScript extends AbstractActionHandler {
   private static final SelectionHelper selectionHelper = SelectionHelper.getInstance();

   public OpenAssociatedScript(StructuredViewer viewer, String text) throws Exception {
      super(viewer, text);
   }

   public OpenAssociatedScript(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(viewer, text, image);
   }

   @Override
   public void updateState() {
      TestRunOperator operator = selectionHelper.getSelection(getViewer());
      setEnabled(operator != null);
   }

   @Override
   public void run() {
      try {
         ArtifactTestRunOperator operator = selectionHelper.getSelection(getViewer());
         if (operator.isScriptRevisionValid()) {
            openRemoteScript(operator.getScriptUrl(), operator.getScriptRevision());
         } else {
            handleException();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void handleException() {
      MessageDialog.openError(AWorkbench.getActiveShell(), "Open Script", "Unable to open script with invalid url.");
   }

   private void openRemoteScript(String scriptUrl, String revision) {
      try {
         URL urlToOpen = new URL(scriptUrl);

         IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
         IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR,
            "org.eclipse.ui.browser.editor", scriptUrl, "");
         browser.openURL(urlToOpen);

      } catch (Exception ex) {
         handleException();
      }
   }
}
