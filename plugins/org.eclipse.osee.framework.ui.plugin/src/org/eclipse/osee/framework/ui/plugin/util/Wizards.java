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
      initAndOpen(wizard, viewPart, viewPart.getViewSite().getWorkbenchWindow());
   }

   public static final void initAndOpen(IWorkbenchWizard wizard, IViewPart viewPart, IStructuredSelection selection) {
      initAndOpen(wizard, viewPart.getViewSite().getWorkbenchWindow(), selection);
   }

   public static final void initAndOpen(IWorkbenchWizard wizard, IViewPart viewPart, IWorkbenchWindow workbenchWindow) {

      IStructuredSelection selectionToPass;
      // get the current workbench selection
      ISelection workbenchSelection = workbenchWindow.getSelectionService().getSelection();
      if (workbenchSelection instanceof IStructuredSelection) {
         selectionToPass = (IStructuredSelection) workbenchSelection;
      } else {
         selectionToPass = StructuredSelection.EMPTY;
      }

      initAndOpen(wizard, viewPart.getViewSite().getWorkbenchWindow(), selectionToPass);

   }

   public static final void initAndOpen(IWorkbenchWizard wizard, IWorkbenchWindow workbenchWindow, IStructuredSelection selection) {

      wizard.init(workbenchWindow.getWorkbench(), selection);

      Shell parent = workbenchWindow.getShell();
      WizardDialog dialog = new WizardDialog(parent, wizard);
      dialog.create();
      dialog.open();

   }
}
