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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ListSelectionDialog extends MessageDialog {

   private List selections;
   private int selectionIndex;
   private final Object[] choose;
   private Button saveSelection;
   private boolean isChecked = true;
   private boolean showSaveSelection = true;

   /**
    * @return Returns the isChecked.
    */
   public boolean saveSelection() {
      return isChecked;
   }

   public ListSelectionDialog(Object[] choose, Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.choose = choose;
   }

   @Override
   protected Control createCustomArea(Composite parent) {
      selections = new List(parent, SWT.SINGLE);

      if (showSaveSelection) {
         saveSelection = new Button(parent, SWT.CHECK);
         saveSelection.setText("Remember Selection");
         saveSelection.setSelection(true);

         saveSelection.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               isChecked = saveSelection.getSelection();
            }

         });
      }

      for (int i = 0; i < choose.length; i++) {
         selections.add(choose[i].toString());
      }

      selections.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            selectionIndex = selections.getSelectionIndex();
            System.out.println("selected index " + selectionIndex);
         }

      });

      selections.select(0);

      return parent;
   }

   public int getSelection() {
      return selectionIndex;
   }

   public boolean isShowSaveSelection() {
      return showSaveSelection;
   }

   public void setShowSaveSelection(boolean showSaveSelection) {
      this.showSaveSelection = showSaveSelection;
   }
}
