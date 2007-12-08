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
package org.eclipse.osee.framework.ui.encrypter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class EncrypterAction implements IWorkbenchWindowActionDelegate {

   /**
    * The constructor.
    */
   public EncrypterAction() {
   }

   /**
    * The action has been activated. The argument of the method represents the 'real' action sitting in the workbench
    * UI.
    * 
    * @see IWorkbenchWindowActionDelegate#run
    */
   public void run(IAction action) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         page.showView(EncrypterView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch OSEE Encrypter View " + e1.getMessage());
      }
   }

   /**
    * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
    * this can only happen after the delegate has been created.
    * 
    * @see IWorkbenchWindowActionDelegate#selectionChanged
    */
   public void selectionChanged(IAction action, ISelection selection) {
   }

   /**
    * We can use this method to dispose of any system resources we previously allocated.
    * 
    * @see IWorkbenchWindowActionDelegate#dispose
    */
   public void dispose() {
   }

   /**
    * We will cache window object in order to be able to provide parent shell for the message dialog.
    * 
    * @see IWorkbenchWindowActionDelegate#init
    */
   public void init(IWorkbenchWindow window) {
   }
}