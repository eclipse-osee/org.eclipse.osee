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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Robert A. Fisher
 */
public final class Wizards {
   public static final void initAndOpen(IWorkbenchWizard wizard, IViewPart viewPart) {
      initAndOpen(wizard, viewPart.getViewSite().getWorkbenchWindow());
   }

   public static final void initAndOpen(IWorkbenchWizard wizard, IWorkbenchWindow workbenchWindow) {

      IStructuredSelection selectionToPass;
      // get the current workbench selection
      ISelection workbenchSelection = workbenchWindow.getSelectionService().getSelection();
      if (workbenchSelection instanceof IStructuredSelection) {
         selectionToPass = (IStructuredSelection) workbenchSelection;
      } else {
         selectionToPass = StructuredSelection.EMPTY;
      }

      wizard.init(workbenchWindow.getWorkbench(), selectionToPass);

      Shell parent = workbenchWindow.getShell();
      WizardDialog dialog = new WizardDialog(parent, wizard);
      dialog.create();
      dialog.open();
   }
}
