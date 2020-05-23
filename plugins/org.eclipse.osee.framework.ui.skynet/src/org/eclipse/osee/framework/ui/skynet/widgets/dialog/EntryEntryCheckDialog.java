/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Provides a dialog with 2 entry fields and a checkbox.
 *
 * @author Donald G. Dunne
 */
public class EntryEntryCheckDialog extends EntryEntryDialog {
   private boolean checked = false;
   private final String checkBoxMessage;

   public EntryEntryCheckDialog(String dialogTitle, String dialogMessage, String text1Label, String text2Label, String checkBoxMessage) {
      super(dialogTitle, dialogMessage, text1Label, text2Label);
      this.checkBoxMessage = checkBoxMessage;
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      super.createExtendedArea(parent);

      Composite composite = new Composite(customAreaParent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      composite.setLayoutData(gd);

      final XCheckBox checkbox = new XCheckBox(checkBoxMessage);
      checkbox.setFillHorizontally(true);
      checkbox.setVerticalLabel(false);
      checkbox.set(checked);
      checkbox.createWidgets(composite, 1);

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
