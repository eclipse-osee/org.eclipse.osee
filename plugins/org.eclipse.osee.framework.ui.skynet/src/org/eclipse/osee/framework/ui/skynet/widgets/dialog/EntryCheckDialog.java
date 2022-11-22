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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class EntryCheckDialog extends EntryDialog {

   private final String checkBoxMessage;
   private boolean checked = false;
   protected Composite checkComp;

   public EntryCheckDialog(String dialogTitle, String dialogMessage, String checkBoxMessage) {
      super(dialogTitle, dialogMessage);
      this.checkBoxMessage = checkBoxMessage;
   }

   public EntryCheckDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, String checkBoxMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.checkBoxMessage = checkBoxMessage;
   }

   @Override
   protected void createExtendedArea(Composite parent) {

      (new org.eclipse.swt.widgets.Label(parent, SWT.NONE)).setText(" ");

      checkComp = new Composite(parent, SWT.NONE);
      checkComp.setLayoutData(new GridData());
      checkComp.setLayout(ALayout.getZeroMarginLayout(2, false));

      final XCheckBox checkbox = new XCheckBox(checkBoxMessage);
      checkbox.setFillHorizontally(true);
      checkbox.setFocus();
      checkbox.setDisplayLabel(false);
      checkbox.set(checked);
      checkbox.createWidgets(checkComp, 1);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleModified();
            checked = checkbox.isSelected();
         }
      };
      checkbox.addSelectionListener(selectionListener);

   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

}
