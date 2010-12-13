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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.CalendarListenerAdapter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XDate extends XWidget {

   private final ArrayList<ModifyListener> listeners = new ArrayList<ModifyListener>();
   private String defaultFormat = DateUtil.MMDDYYHHMM;
   private boolean requireFutureDate = false;
   private CalendarCombo dateCombo;
   private Composite parent;
   protected Date date;

   public XDate() {
      this("", "");
   }

   public XDate(Date date) {
      this("", "");
      this.date = date;
   }

   public XDate(String displayLabel) {
      this(displayLabel, "");
   }

   public XDate(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
      date = null;
   }

   @Override
   public Control getControl() {
      return dateCombo;
   }

   /**
    * Set the default format for the date ADate.MMMDDDYY or ADate MMMDDDYYYHHHMM or use java.util.date format string
    */
   public void setFormat(String format) {
      defaultFormat = format;
   }

   public void clearData() {
      date = null;
   }

   /**
    * Create Date Widgets Label/DatePickerCombo
    * 
    * @param horizontalSpan - horizontalSpan takes up 4 columns, therefore horizontalSpan must be >=4
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      // composite = new Composite(parent, parent.getStyle());
      this.parent = parent;

      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      labelWidget = new Label(parent, SWT.NONE);
      labelWidget.setText(getLabel() + ": ");

      int style = SWT.BORDER;
      if (!Lib.isWindows()) {
         style |= SWT.FLAT;
      }
      dateCombo = new CalendarCombo(parent, style);
      dateCombo.setEnabled(isEditable());
      GridData gd = new GridData();
      gd.widthHint = 100;
      if (date != null) {
         dateCombo.setDate(date);
      }
      dateCombo.addCalendarListener(new CalendarListenerAdapter() {
         @Override
         public void dateChanged(Calendar newDate) {
            super.dateChanged(newDate);
            if (newDate == null) {
               date = null;
            } else {
               date = newDate.getTime();
            }
            validate();
            notifyXModifiedListeners();
            dateCombo.getParent().layout();
         }
      });

   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      if (Widgets.isAccessible(parent)) {
         parent.layout();
      }
   }

   @Override
   public void setFromXml(String xml) {
      Matcher m =
         Pattern.compile("<" + getXmlRoot() + ">(\\d+)</" + getXmlRoot() + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
            xml);
      if (m.find()) {
         try {
            Long l = new Long(m.group(1));
            date = new Date(l.longValue());
         } catch (NumberFormatException e) {
            e.printStackTrace();
         }
      }
      refresh();
   }

   public void addModifyListener(ModifyListener listener) {
      listeners.add(listener);
      updateListeners();
   }

   public void updateListeners() {
      for (ModifyListener listener : listeners) {
         if (dateCombo != null) {
            dateCombo.removeModifyListener(listener);
            dateCombo.addModifyListener(listener);
         }
      }
   }

   public void clear() {
      date = new Date();
      refresh();
   }

   public Date getDate() {
      return date;
   }

   public void setDateToNow() {
      setDate(new java.util.Date());
   }

   public void setDate(Date date) {
      this.date = date;
      if (dateCombo != null && !dateCombo.isDisposed()) {
         dateCombo.setDate(date);
      }
   }

   @Override
   public void refresh() {
      validate();
   }

   @Override
   public IStatus isValid() {
      if (isRequireFutureDate()) {
         if (getDate().before(new Date())) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, getLabel() + " must be in future.");
         }
      }
      if (isRequiredEntry()) {
         if (get().equals("")) {
            return new Status(IStatus.ERROR, SkynetGuiPlugin.PLUGIN_ID, getLabel() + " must be selected.");
         }
      }
      return Status.OK_STATUS;
   }

   @Override
   public String getReportData() {
      return get();
   }

   public String get() {
      if (date == null) {
         return "";
      }
      return DateFormat.getDateInstance().format(date);
   }

   public String get(String pattern) {
      return get(new SimpleDateFormat(pattern));
   }

   public String get(DateFormat dateFormat) {
      if (date == null) {
         return "";
      }
      String result = dateFormat.format(date);
      return result;
   }

   @Override
   public void setFocus() {
      if (dateCombo != null) {
         dateCombo.setFocus();
      }
   }

   /**
    * Don't need this since overriding toReport and toXml
    */
   @Override
   public String getXmlData() {
      String dateStr = "";
      if (date != null) {
         dateStr = date.getTime() + "";
      }
      return dateStr;
   }

   /**
    * Don't need this since overriding setFromXml
    */
   @Override
   public void setXmlData(String str) {
      if (str.equals("")) {
         date = null;
      } else {
         try {
            Long l = new Long(str);
            date = new Date(l.longValue());
         } catch (NumberFormatException e) {
            e.printStackTrace();
            date = null;
         }
      }
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + get(defaultFormat);
   }

   public String toHTML(String labelFont, String pattern) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + get(pattern);
   }

   public boolean isRequireFutureDate() {
      return requireFutureDate;
   }

   public void setRequireFutureDate(boolean requireFutureDate) {
      this.requireFutureDate = requireFutureDate;
   }

   @Override
   public Object getData() {
      return getDate();
   }

   public int getDifference(Date date) {
      return DateUtil.getDifference(getDate(), date);
   }

}