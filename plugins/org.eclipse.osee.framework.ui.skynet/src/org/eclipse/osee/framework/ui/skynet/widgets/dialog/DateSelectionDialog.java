/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Single dialog combining a text entry field and calendar picker for date selection. User can type a date (MM/dd/yyyy)
 * or click in the calendar. Double-clicking a date closes the dialog. Pressing Enter after a valid date entry closes
 * the dialog.
 *
 * @author Donald G. Dunne
 */
public class DateSelectionDialog extends TitleAreaDialog {

   private static final String DATE_FORMAT = "MM/dd/yyyy";
   private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);

   private final String dialogTitle;
   private Date selectedDate;
   private DateTime dateTime;
   private Text dateText;

   public DateSelectionDialog(String title, Date initialDate) {
      super(Displays.getActiveShell());
      this.dialogTitle = title;
      this.selectedDate = initialDate;
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   @Override
   protected void configureShell(Shell newShell) {
      super.configureShell(newShell);
      newShell.setText(dialogTitle);
   }

   @Override
   protected org.eclipse.swt.graphics.Point getInitialSize() {
      org.eclipse.swt.graphics.Point size = super.getInitialSize();
      return new org.eclipse.swt.graphics.Point(size.x * 2 / 3, size.y);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      setTitle(dialogTitle);
      setMessage("Enter date as " + DATE_FORMAT + "or\nSelect from calendar (double-click to accept)");

      Composite area = (Composite) super.createDialogArea(parent);
      Composite container = new Composite(area, SWT.NONE);
      container.setLayoutData(new GridData(GridData.FILL_BOTH));
      container.setLayout(new GridLayout(1, false));

      // Date text entry
      Composite textComp = new Composite(container, SWT.NONE);
      textComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      textComp.setLayout(new GridLayout(2, false));

      Label dateLabel = new Label(textComp, SWT.NONE);
      dateLabel.setText("Date:");

      dateText = new Text(textComp, SWT.BORDER | SWT.SINGLE);
      dateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      dateText.setMessage(DATE_FORMAT);
      if (selectedDate != null) {
         dateText.setText(SDF.format(selectedDate));
      }

      // Calendar picker
      dateTime = new DateTime(container, SWT.CALENDAR);
      dateTime.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
      if (selectedDate != null) {
         Calendar cal = Calendar.getInstance();
         cal.setTime(selectedDate);
         dateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
      }

      // When calendar selection changes, update the text field
      dateTime.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Calendar cal = Calendar.getInstance();
            cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            selectedDate = cal.getTime();
            dateText.setText(SDF.format(selectedDate));
            setErrorMessage(null);
            Button okButton = getButton(IDialogConstants.OK_ID);
            if (okButton != null) {
               okButton.setEnabled(true);
            }
         }
      });

      // Double-click on calendar closes dialog
      dateTime.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseDoubleClick(MouseEvent e) {
            Calendar cal = Calendar.getInstance();
            cal.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay(), 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            selectedDate = cal.getTime();
            setReturnCode(OK);
            close();
         }
      });

      // When user types in text field, validate and update calendar
      dateText.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            String text = dateText.getText().trim();
            Date parsed = parseDate(text);
            if (parsed != null) {
               selectedDate = parsed;
               Calendar cal = Calendar.getInstance();
               cal.setTime(parsed);
               dateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
               setErrorMessage(null);
               Button okButton = getButton(IDialogConstants.OK_ID);
               if (okButton != null) {
                  okButton.setEnabled(true);
               }
               // Enter key closes dialog with valid date
               if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                  setReturnCode(OK);
                  close();
               }
            } else {
               if (text.isEmpty()) {
                  setErrorMessage(null);
                  selectedDate = null;
               } else {
                  setErrorMessage("Invalid date. Use format " + DATE_FORMAT);
               }
               Button okButton = getButton(IDialogConstants.OK_ID);
               if (okButton != null) {
                  okButton.setEnabled(text.isEmpty() || parsed != null);
               }
            }
         }
      });

      return area;
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
      createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
   }

   @Override
   protected void okPressed() {
      // Final validation before accepting
      String text = dateText.getText().trim();
      if (!text.isEmpty()) {
         Date parsed = parseDate(text);
         if (parsed == null) {
            setErrorMessage("Invalid date. Use format " + DATE_FORMAT);
            return;
         }
         selectedDate = parsed;
      } else {
         selectedDate = null;
      }
      super.okPressed();
   }

   private Date parseDate(String text) {
      if (text == null || text.trim().isEmpty()) {
         return null;
      }
      try {
         SDF.setLenient(false);
         Date date = SDF.parse(text.trim());
         // Verify the parsed date formats back to the same string (catches invalid dates like 02/30/2025)
         String formatted = SDF.format(date);
         if (formatted.equals(text.trim())) {
            return date;
         }
         return null;
      } catch (ParseException e) {
         return null;
      }
   }

   public Date getSelectedDate() {
      return selectedDate;
   }

   public boolean isNoneSelected() {
      return selectedDate == null;
   }

}
