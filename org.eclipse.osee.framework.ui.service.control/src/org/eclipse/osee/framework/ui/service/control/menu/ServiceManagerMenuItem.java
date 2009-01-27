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
package org.eclipse.osee.framework.ui.service.control.menu;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.view.ServiceManagerView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ServiceManagerMenuItem implements IWorkbenchWindowActionDelegate {

   public void dispose() {
   }

   public void init(IWorkbenchWindow window) {
   }

   public void run(IAction action) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         page.showView(ServiceManagerView.VIEW_ID);
      } catch (PartInitException ex) {
         OseeLog.log(ControlPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void selectionChanged(IAction action, ISelection selection) {
   }

}
