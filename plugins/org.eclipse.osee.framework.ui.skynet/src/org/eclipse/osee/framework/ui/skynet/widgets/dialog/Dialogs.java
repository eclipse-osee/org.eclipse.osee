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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.HtmlExportTable;
import org.eclipse.osee.framework.ui.skynet.util.email.EmailWizard;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class Dialogs {

   /**
    * Allows user to save html from a file selection dialog
    *
    * @param openInSystem true if desire to open resulting file in system browser after saving
    */
   public static Result saveHtmlDialog(String htmlText, boolean openInSystem) {
      if (!Strings.isValid(htmlText)) {
         AWorkbench.popup("ERROR", "Save data is empty.  Nothing to save.");
         return Result.FalseResult;
      }
      final FileDialog dialog = new FileDialog(Displays.getActiveShell().getShell(), SWT.SAVE);
      dialog.setFilterExtensions(new String[] {"*.html"});
      String filename = dialog.open();
      if (!Strings.isValid(filename)) {
         return Result.FalseResult;
      }
      try {
         Lib.writeStringToFile(htmlText, new File(filename));
      } catch (IOException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return Result.FalseResult;
      }
      if (openInSystem) {
         Program.launch(filename);
      }
      return Result.TrueResult;
   }

   public static Result emailDialog(String title, String text) {
      if (!Strings.isValid(text)) {
         AWorkbench.popup("ERROR", "Save data is empty.  Nothing to email.");
         return Result.FalseResult;
      }
      EmailWizard ew = new EmailWizard(text, title, null, null);
      WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), ew);
      dialog.create();
      if (dialog.open() == 0) {
         return Result.TrueResult;
      }
      return Result.FalseResult;
   }

   public static Result exportHtmlTableDialog(String title, String htmlText, boolean openInSystem) {
      if (!Strings.isValid(htmlText)) {
         AWorkbench.popup("ERROR", "Save data is empty.  Nothing to export.");
         return Result.FalseResult;
      }
      return new HtmlExportTable(title.equals("") ? "Exported Text" : title, htmlText, openInSystem).exportCsv();
   }

   public static Result exportHtmlExcelTableDialog(String title, String htmlText, boolean openInSystem) {
      if (!Strings.isValid(htmlText)) {
         AWorkbench.popup("ERROR", "Save data is empty.  Nothing to export.");
         return Result.FalseResult;
      }
      return new HtmlExportTable(title.equals("") ? "Exported Text" : title, htmlText, openInSystem).exportExcelXml();
   }

   public static IStructuredSelection getCurrentSelection() throws Exception {
      IStructuredSelection structuredSelection = null;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isClosing() || !workbench.isStarting()) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  ISelectionProvider selectionProvider = site.getSelectionProvider();
                  if (selectionProvider != null) {
                     ISelection selection = selectionProvider.getSelection();
                     if (selection instanceof IStructuredSelection) {
                        structuredSelection = (IStructuredSelection) selection;
                     }
                  }
               }
            }
         }
      }
      return structuredSelection;
   }
}
