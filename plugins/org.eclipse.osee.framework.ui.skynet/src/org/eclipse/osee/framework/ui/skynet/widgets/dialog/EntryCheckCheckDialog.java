/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class EntryCheckCheckDialog extends EntryCheckDialog {

   private final String checkBoxMessage2;
   private boolean checked2 = false;

   public EntryCheckCheckDialog(String dialogTitle, String dialogMessage, String checkBoxMessage1, String checkBoxMessage2) {
      super(dialogTitle, dialogMessage, checkBoxMessage1);
      this.checkBoxMessage2 = checkBoxMessage2;
   }

   public EntryCheckCheckDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, String checkBoxMessage1, String checkBoxMessage2, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, checkBoxMessage1, dialogImageType,
         dialogButtonLabels, defaultIndex);
      this.checkBoxMessage2 = checkBoxMessage2;
   }

   @Override
   protected void createExtendedArea(Composite parent) {
      super.createExtendedArea(parent);

      final XCheckBox checkbox = new XCheckBox(checkBoxMessage2);
      checkbox.setFillHorizontally(true);
      checkbox.setFocus();
      checkbox.setDisplayLabel(false);
      checkbox.set(checked2);
      checkbox.createWidgets(checkComp, 1);

      SelectionListener selectionListener = new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            handleModified();
            checked2 = checkbox.isSelected();
         }
      };
      checkbox.addSelectionListener(selectionListener);

   }

   public boolean isChecked2() {
      return checked2;
   }

   public void setChecked2(boolean checked2) {
      this.checked2 = checked2;
   }

}
