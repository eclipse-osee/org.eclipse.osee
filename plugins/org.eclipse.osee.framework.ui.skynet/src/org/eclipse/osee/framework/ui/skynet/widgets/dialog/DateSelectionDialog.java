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

import java.util.Calendar;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.util.CalendarWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.CalendarWidget.CalendarListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Donald G. Dunne
 */
public class DateSelectionDialog extends MessageDialog {

   private Date initialDate, selectedDate;

   private final String dialogMessage;

   public DateSelectionDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, Date selectedDate) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
         defaultIndex);
      this.initialDate = selectedDate;
      this.dialogMessage = dialogMessage;
   }

   public DateSelectionDialog(String dialogTitle, String dialogMessage, Date selectedDate) {
      this(Displays.getActiveShell(), dialogTitle, null, dialogMessage, MessageDialog.NONE,
         new String[] {"Ok", "Cancel"}, 0, selectedDate);
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite filterComp = new Composite(container, SWT.NONE);

      filterComp.setLayout(new GridLayout(1, false));
      filterComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      new Label(filterComp, SWT.None).setText(dialogMessage);

      final CalendarWidget dp = new CalendarWidget(filterComp, SWT.BORDER | SWT.SINGLE | SWT.FLAT);
      dp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (initialDate != null) {
         dp.setDate(initialDate);
      }
      dp.addCalendarListener(new CalendarListener() {
         @Override
         public void dateChanged(Calendar date) {
            if (date == null) {
               selectedDate = null;
            } else {
               selectedDate = date.getTime();
            }
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
   public void setSelectedDate(Date initialDate) {
      this.initialDate = initialDate;
   }

   /**
    * @return the noneSelected
    */
   public boolean isNoneSelected() {
      return selectedDate == null;
   }

}
