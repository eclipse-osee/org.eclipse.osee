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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Donald G. Dunne
 */
public final class AWorkbench {

   public static void openPerspective(String perspId) {
      IAdaptable input = ResourcesPlugin.getWorkspace();
      // Get "Open Behavior" preference.
      @SuppressWarnings("deprecation")
      AbstractUIPlugin plugin = (AbstractUIPlugin) Platform.getPlugin(PlatformUI.PLUGIN_ID);
      IPreferenceStore store = plugin.getPreferenceStore();
      String pref = store.getString(IWorkbenchPreferenceConstants.OPEN_NEW_PERSPECTIVE);

      // Implement open behavior.
      try {
         if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_WINDOW)) {
            PlatformUI.getWorkbench().openWorkbenchWindow(perspId, input);
         } else if (pref.equals(IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE)) {
            IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(
               reg.findPerspectiveWithId(perspId));
         }
      } catch (WorkbenchException ex) {
         OseeLog.log(OseeUiActivator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Popup a workbench viewer eg: AWorkbench.popupView(IPageLayout.ID_PROBLEM_VIEW);
    */
   public static boolean popupView(String iPageLayoutView) {
      IViewPart p = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(iPageLayoutView);
      if (p != null) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(p);
         return true;
      }
      return false;
   }

   public static Display getDisplay() {
      return PlatformUI.getWorkbench().getDisplay();
   }

   public static Shell getActiveShell() {
      return getDisplay().getActiveShell();
   }

   public static Color getSystemColor(int id) {
      return getDisplay().getSystemColor(id);
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

   public static void popup(String title, Result result) {
      AWorkbench.popup(
         Strings.isValid(title) ? title : ((result.isTrue() ? "Success" : "ERROR")),
         Strings.isValid(result.getText()) ? result.getText() : result.isTrue() ? "Success" : "Error Encountered.  See Error Log View");

   }

   public static void popup(Result result) {
      popup((String) null, result);
   }

   public static void popup(final String message) {
      popup(message, message);
   }

   public static enum MessageType {
      Informational,
      Error,
      Confirm;
   }

   public static void popup(final String title, final String message) {
      popup(MessageType.Informational, title, message);
   }

   public static void popup(final MessageType messageType, final String title, final String message) {
      if (!PlatformUI.isWorkbenchRunning()) {
         OseeLog.log(AWorkbench.class, Level.SEVERE, message);
      } else {
         getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
               if (messageType == MessageType.Informational) {
                  MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);
               } else if (messageType == MessageType.Error) {
                  MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);

               } else if (messageType == MessageType.Confirm) {
                  MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title,
                     message);

               }
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