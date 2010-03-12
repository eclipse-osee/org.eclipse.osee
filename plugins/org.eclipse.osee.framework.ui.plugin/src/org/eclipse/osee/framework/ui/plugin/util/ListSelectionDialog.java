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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class ListSelectionDialog extends MessageDialog {

   private Button okButton;
   private Button cancelButton;
   private List selections;
   private int selectionIndex;
   private Object[] choose;
   private Button saveSelection;
   private boolean isChecked = true;

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
      saveSelection = new Button(parent, SWT.CHECK);//.setText("Checkbox 1");
      saveSelection.setText("Remember Selection");
      saveSelection.setSelection(true);

      saveSelection.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            isChecked = saveSelection.getSelection();
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });
      for (int i = 0; i < choose.length; i++) {
         selections.add(choose[i].toString());
      }

      selections.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {
            selectionIndex = selections.getSelectionIndex();
            System.out.println("selected index " + selectionIndex);
         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });

      selections.select(0);

      return parent;
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Control c = super.createButtonBar(parent);
      okButton = getButton(0);
      cancelButton = getButton(1);

      okButton.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {

         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });

      cancelButton.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent e) {

         }

         public void widgetDefaultSelected(SelectionEvent e) {
         }

      });

      return c;
   }

   public int getSelection() {
      return selectionIndex;
   }
}
