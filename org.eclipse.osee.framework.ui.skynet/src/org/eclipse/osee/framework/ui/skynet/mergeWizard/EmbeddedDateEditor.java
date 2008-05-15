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
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.CalendarListenerAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class EmbeddedDateEditor {

	private Date selectedDate;

	private final String dialogMessage;
	private CalendarCombo dp;
   private boolean noneSelected = false;

	public EmbeddedDateEditor(String dialogMessage, Date selectedDate) {
		this.selectedDate = selectedDate;
		this.dialogMessage = dialogMessage;
	}

	public void createEditor(Composite container) {
	   (new Label(container, SWT.None)).setText(dialogMessage);

		dp = new CalendarCombo(container, SWT.SINGLE | SWT.FLAT);
      if (selectedDate != null) dp.setDate(selectedDate);
		dp.addCalendarListener(new CalendarListenerAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.nebula.widgets.datechooser.DateChooser.ClearListener#handleClearEvent()
			 */
         public void dateChanged(Calendar date) {
            if (date == null) {
               noneSelected = true;
            } else {
               noneSelected = false;
               selectedDate = dp.getDate().getTime();
            }
         }
		});
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
		if (selectedDate != null){
			dp.setDate(this.selectedDate);
		}
	}

	public Date getSelectedDate() {
	   if (noneSelected) return null;
		return selectedDate == null ? null : (Date)selectedDate.clone();
	}
}
