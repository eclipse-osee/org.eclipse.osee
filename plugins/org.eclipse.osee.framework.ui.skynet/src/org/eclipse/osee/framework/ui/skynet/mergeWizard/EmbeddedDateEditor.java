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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

public class EmbeddedDateEditor {
   private Date selectedDate;
   private final String dialogMessage;
   private DateTime datePicker;

   public EmbeddedDateEditor(String dialogMessage, Date selectedDate) {
      this.selectedDate = selectedDate;
      this.dialogMessage = dialogMessage;
   }

   public void createEditor(Composite container) {
      (new Label(container, SWT.None)).setText(dialogMessage);
      datePicker = new DateTime(container, SWT.DATE | SWT.DROP_DOWN);
      setSelectedDate(selectedDate);
   }

   public void setSelectedDate(Date selectedDate) {
      this.selectedDate = selectedDate;
      if (selectedDate != null) {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(selectedDate);
         datePicker.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
               calendar.get(Calendar.DAY_OF_MONTH));
      }
   }

   public Date getSelectedDate() {
      Calendar calendar = Calendar.getInstance();
      calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDay());
      return calendar.getTime();
   }
}
