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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultiTextDialog extends Dialog {

   private String title;
   private String message;

   private Text[] texts;
   private String[] prompt;
   private boolean[] displayable;
   private String[] value;

   public MultiTextDialog(Shell parentShell, String dialogTitle, String dialogMessage, String[] prompt, boolean[] displayable) {
      super(parentShell);
      this.title = dialogTitle;
      this.message = dialogMessage;
      this.prompt = prompt != null ? prompt : new String[0];
      this.displayable = displayable != null ? displayable : new boolean[0];
      this.value = null;
   }

   protected void buttonPressed(int buttonId) {
      if (buttonId == IDialogConstants.OK_ID) {
         value = new String[prompt.length];
         for (int i = 0; i < prompt.length; i++) {
            value[i] = texts[i].getText();
         }
      } else {
         value = null;
      }
      super.buttonPressed(buttonId);
   }

   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      if (title != null) {
         shell.setText(title);
      }
   }

   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
      for (int i = 0; i < prompt.length; i++) {
         if (value != null && value[i] != null) {
            texts[i].setFocus();
            texts[i].setText(value[i]);
            texts[i].selectAll();
         }
      }
   }

   protected Control createDialogArea(Composite parent) {
      Composite composite = (Composite) super.createDialogArea(parent);
      if (message != null) {
         Label label = new Label(composite, SWT.NONE);
         label.setText(message);
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
         data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
         label.setLayoutData(data);
         label.setFont(parent.getFont());
      }
      texts = new Text[prompt.length];
      for (int i = 0; i < prompt.length; i++) {
         if (displayable[i] != false) {
            texts[i] = new Text(composite, SWT.SINGLE | SWT.BORDER);
         } else {
            texts[i] = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
         }
         texts[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      }
      applyDialogFont(composite);
      return composite;
   }

   public String[] getValue() {
      return value;
   }
}
