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
package org.eclipse.osee.ote.ui.test.manager.actions;

import java.util.Iterator;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.ote.ui.test.manager.operations.AddIFileToTestManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AddToTestManagerPopupAction implements IWorkbenchWindowActionDelegate {

   public static String getSelection() {
      StructuredSelection sel = AWorkspace.getSelection();
      Iterator<?> i = sel.iterator();
      String selection = "";
      while (i.hasNext()) {
         Object obj = i.next();
         if (obj instanceof IResource) {
            IResource resource = (IResource) obj;
            if (resource != null) selection = resource.getLocation().toOSString();
         } else if (obj instanceof ICompilationUnit) {
            ICompilationUnit resource = (ICompilationUnit) obj;
            if (resource != null) selection = resource.getResource().getLocation().toOSString();
         }
      }
      return selection;
   }

   IWorkbenchWindow activeWindow = null;

   // IWorkbenchWindowActionDelegate method
   public void dispose() {
      // nothing to do
   }

   // IWorkbenchWindowActionDelegate method
   public void init(IWorkbenchWindow window) {
      activeWindow = window;
   }

   public void run(IAction proxyAction) {
      String file = getSelection();
      if (file == null || file.equals("")) {
         AWorkbench.popup("ERROR", "Can't retrieve file");
         return;
      }
      AddIFileToTestManager.getOperation().addIFileToScriptsPage(file);
   }

   // IActionDelegate method
   public void selectionChanged(IAction proxyAction, ISelection selection) {

   }
}