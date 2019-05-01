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
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class SkynetViews {

   public static void closeView(final String viewId, final String secondaryId) {
      if (Strings.isValid(viewId)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               IWorkbench workbench = PlatformUI.getWorkbench();
               if (workbench != null) {
                  IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
                  if (workbenchWindow != null) {
                     IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
                     if (workbenchPage != null) {
                        IViewReference viewReference = workbenchPage.findViewReference(viewId, secondaryId);
                        if (viewReference != null) {
                           if (viewReference.getPart(false) != null) {
                              workbenchPage.hideView(viewReference);
                           }
                        }
                     }
                  }
               }
            }
         });
      }
   }
}