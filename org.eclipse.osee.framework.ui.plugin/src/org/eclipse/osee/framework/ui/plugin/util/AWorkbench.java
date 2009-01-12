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
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AWorkbench {

   /**
    * Popup a workbench viewer eg: AWorkbench.popupView(IPageLayout.ID_PROBLEM_VIEW);
    * 
    * @param iPageLayoutView
    * @return success
    */
   public static boolean popupView(String iPageLayoutView) {
      IViewPart p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iPageLayoutView);
      if (p != null) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(p);
         return true;
      }
      return false;
   }

   public static IViewPart getView(String viewId) {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewId);
   }

   public static List<IEditorReference> getEditors(String editorId) {
      List<IEditorReference> editors = new ArrayList<IEditorReference>();
      for (IEditorReference editor : getEditors()) {
         if (editor.getId().equals(editorId)) {
            editors.add(editor);
         }
      }
      return editors;
   }

   public static List<IEditorReference> getEditors() {
      List<IEditorReference> editors = new ArrayList<IEditorReference>();
      for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {
         for (IEditorReference editor : page.getEditorReferences()) {
            editors.add(editor);
         }
      }
      return editors;
   }

   public static void popup(final String title, final String message) {
      if (!PlatformUI.isWorkbenchRunning()) {
         OseeLog.log(AWorkbench.class, Level.SEVERE, message);
      } else {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);
            }
         });
      }
   }

   public static void popup(Composite comp, String title, String message) {
      MessageDialog.openInformation(comp.getShell(), title, message);
   }

   public static IWorkbenchPage getActivePage() {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      return workbenchWindow != null ? workbenchWindow.getActivePage() : null;
   }
}