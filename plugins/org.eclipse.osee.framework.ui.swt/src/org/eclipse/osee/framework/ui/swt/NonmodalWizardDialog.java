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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * Nonmodal Wizard Dialog
 * 
 * @author Jeff C. Phillips
 */

public class NonmodalWizardDialog extends WizardDialog {
   private Button backButton;
   private Button nextButton;
   private Button finishButton;
   @SuppressWarnings("unused")
   private Button cancelButton;
   @SuppressWarnings("unused")
   private Button helpButton;
   private IWizard wizard;
   private SelectionAdapter cancelListener;
   private IWizardPage currentPage = null;

   public NonmodalWizardDialog(Shell shell, Wizard wizard) {
      super(new Shell(), wizard);
      setShellStyle(SWT.MODELESS | SWT.SHELL_TRIM | SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.RESIZE | SWT.ON_TOP | getDefaultOrientation());

      cancelListener = new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            cancelPressed();
         }
      };

      this.wizard = getWizard();
   }

   protected void createButtonsForButtonBar(Composite parent) {
      if (wizard.isHelpAvailable()) {
         helpButton = createButton(parent, IDialogConstants.HELP_ID, IDialogConstants.HELP_LABEL, false);
      }
      if (wizard.needsPreviousAndNextButtons()) createPreviousAndNextButtons(parent);
      finishButton = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.FINISH_LABEL, true);
      cancelButton = createCancelButton(parent);
   }

   private Button createCancelButton(Composite parent) {
      // increment the number of columns in the button bar
      ((GridLayout) parent.getLayout()).numColumns++;
      Button button = new Button(parent, SWT.PUSH);
      button.setText(IDialogConstants.CANCEL_LABEL);
      setButtonLayoutData(button);
      button.setFont(parent.getFont());
      button.setData(new Integer(IDialogConstants.CANCEL_ID));
      button.addSelectionListener(cancelListener);
      return button;
   }

   private Composite createPreviousAndNextButtons(Composite parent) {
      // increment the number of columns in the button bar
      ((GridLayout) parent.getLayout()).numColumns++;
      Composite composite = new Composite(parent, SWT.NONE);
      // create a layout with spacing and margins appropriate for the font size.
      GridLayout layout = new GridLayout();
      layout.numColumns = 0; // will be incremented by createButton
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      layout.horizontalSpacing = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_CENTER);
      composite.setLayoutData(data);
      composite.setFont(parent.getFont());
      backButton = createButton(composite, IDialogConstants.BACK_ID, IDialogConstants.BACK_LABEL, false);
      nextButton = createButton(composite, IDialogConstants.NEXT_ID, IDialogConstants.NEXT_LABEL, false);
      nextButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            //            System.out.println("the next button was pressed");
         }
      });
      return composite;
   }

   public void updateButtons() {
      boolean canFlipToNextPage = false;
      boolean canFinish = wizard.canFinish();

      this.currentPage = getCurrentPage();

      if (backButton != null) backButton.setEnabled(currentPage.getPreviousPage() != null);
      if (nextButton != null) {
         canFlipToNextPage = currentPage.canFlipToNextPage();
         nextButton.setEnabled(canFlipToNextPage);
      }
      finishButton.setEnabled(canFinish);
      // finish is default unless it is diabled and next is enabled
      if (canFlipToNextPage && !canFinish)
         getShell().setDefaultButton(nextButton);
      else
         getShell().setDefaultButton(finishButton);
   }
}
