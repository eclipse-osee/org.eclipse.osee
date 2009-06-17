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
package org.eclipse.osee.ote.ui.test.manager.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PluginUtil {

   public static List<IViewReference> findAllViews(String viewID) {
      ArrayList<IViewReference> list = new ArrayList<IViewReference>();
      IWorkbenchWindow windows[] = PlatformUI.getWorkbench().getWorkbenchWindows();
      for (IWorkbenchWindow window : windows) {
         IWorkbenchPage pages[] = window.getPages();
         for (IWorkbenchPage page : pages) {
            IViewReference viewRefs[] = page.getViewReferences();
            for (IViewReference viewRef : viewRefs) {
               if (viewRef.getId().equals(viewID)) list.add(viewRef);
            }

         }
      }
      return list;
   }

   public static IViewPart findView(String viewID) {
      IWorkbenchWindow windows[] = PlatformUI.getWorkbench().getWorkbenchWindows();
      IWorkbenchPage pages[];
      for (int i = 0; i < windows.length; i++) {
         pages = windows[i].getPages();
         for (int j = 0; j < pages.length; j++)
            return pages[j].findView(viewID);
      }
      return null;
   }

   public static IViewPart findViews(String viewID) {
      IWorkbenchWindow windows[] = PlatformUI.getWorkbench().getWorkbenchWindows();
      IWorkbenchPage pages[];
      for (int i = 0; i < windows.length; i++) {
         pages = windows[i].getPages();
         for (int j = 0; j < pages.length; j++)
            return pages[j].findView(viewID);
      }
      return null;
   }

   public static boolean areTestManagersAvailable() {
      return getTestManagers().length > 0;
   }

   public static TestManagerEditor[] getTestManagers() {
      List<TestManagerEditor> tmes = new ArrayList<TestManagerEditor>();
      IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (window != null) {
         IWorkbenchPage page = window.getActivePage();
         if (page != null) {
            IEditorReference editors[] = page.getEditorReferences();
            if (editors != null) {
               for (int j = 0; j < editors.length; j++) {
                  IEditorReference editor = editors[j];
                  IWorkbenchPart part = editor.getPart(true);
                  if (part instanceof TestManagerEditor) {
                     tmes.add((TestManagerEditor) part);
                  }
               }
            }
         }
      }
      return tmes.toArray(new TestManagerEditor[tmes.size()]);
   }
}
