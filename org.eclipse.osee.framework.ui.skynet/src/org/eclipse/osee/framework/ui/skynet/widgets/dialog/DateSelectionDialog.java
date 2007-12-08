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

package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.datechooser.DateChooser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DateSelectionDialog extends MessageDialog {

   private Date selectedDate;

   private final String dialogMessage;
   private boolean noneSelected = false;

   public DateSelectionDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, Date selectedDate) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
            defaultIndex);
      this.selectedDate = selectedDate;
      this.dialogMessage = dialogMessage;
   }

   public DateSelectionDialog(String dialogTitle, String dialogMessage, Date selectedDate) {
      this(Display.getCurrent().getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE, new String[] {
            "Ok", "Cancel"}, 0, selectedDate);
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite filterComp = new Composite(container, SWT.NONE);

      filterComp.setLayout(new GridLayout(1, false));
      filterComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      (new Label(filterComp, SWT.None)).setText(dialogMessage);

      final DateChooser dp = new DateChooser(filterComp, SWT.SINGLE | SWT.FLAT);
      dp.setFooterVisible(true);
      dp.addClearListener(new DateChooser.ClearListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.nebula.widgets.datechooser.DateChooser.ClearListener#handleClearEvent()
          */
         public void handleClearEvent() {
            noneSelected = true;
         }
      });
      if (selectedDate != null) dp.setSelectedDate(selectedDate);
      dp.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            super.widgetSelected(e);
            noneSelected = false;
            selectedDate = dp.getSelectedDate();
         }
      });

      // set selected date if != null
      return filterComp;
   }

   /**
    * @return the selectedDate
    */
   public Date getSelectedDate() {
      return selectedDate;
   }

   /**
    * @param selectedDate the selectedDate to set
    */
   public void setSelectedDate(Date selectedDate) {
      this.selectedDate = selectedDate;
   }

   /**
    * @return the noneSelected
    */
   public boolean isNoneSelected() {
      return noneSelected;
   }

}
