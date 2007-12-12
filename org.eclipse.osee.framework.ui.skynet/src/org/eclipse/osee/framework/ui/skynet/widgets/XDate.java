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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.formattedtext.DateFormatter;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XDate extends XWidget {

   private DateChooserCombo dateCombo;
   private Composite parent;
   private Date date;
   public static String MMDDYY = "MM/dd/yyyy";
   public static String MMDDYYHHMM = "MM/dd/yyyy hh:mm a";
   public static String HHMMSS = "hh:mm:ss";
   public static String HHMM = "hh:mm";
   private String defaultFormat = MMDDYYHHMM;
   private ArrayList<ModifyListener> listeners = new ArrayList<ModifyListener>();
   private boolean requireFutureDate = false;

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

   public String getHHMM() {
      return get(HHMM);
   }

   public String getHHMMSS() {
      return get(HHMMSS);
   }

   public String getMMDDYY() {
      return get(MMDDYY);
   }

   public String getMMDDYYHHMM() {
      return get(MMDDYYHHMM);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return dateCombo;
   }

   /**
    * Set the default format for the date ADate.MMMDDDYY or ADate MMMDDDYYYHHHMM or use java.util.date format string
    * 
    * @param format
    */
   public void setFormat(String format) {
      defaultFormat = format;
      if (dateCombo != null) dateCombo.setFormatter(new DateFormatter(defaultFormat));
   }

   public void clearData() {
      date = null;
   }

   public static String getDateStr(Date date, String format) {
      if (date == null) return "";
      return (new SimpleDateFormat(format)).format(date);
   }

   public static String getDateNow() {
      return getDateNow(MMDDYY);
   }

   public static String getDateNow(String format) {
      XDate d = new XDate();
      d.setDateToNow();
      return d.get(format);
   }

   /**
    * Create Date Widgets Label/DatePickerCombo
    * 
    * @param parent
    * @param horizontalSpan - horizontalSpan takes up 4 columns, therefore horizontalSpan must be >=4
    */
   public void createWidgets(Composite parent, int horizontalSpan) {

      // composite = new Composite(parent, parent.getStyle());
      this.parent = parent;

      if (horizontalSpan < 2) horizontalSpan = 2;

      labelWidget = new Label(parent, SWT.NONE);
      labelWidget.setText(label + ": ");
      dateCombo = new DateChooserCombo(parent, SWT.BORDER);
      dateCombo.setFooterVisible(true);
      dateCombo.setFormatter(new DateFormatter("MM/dd/yyyy"));
      dateCombo.setEnabled(true);
      GridData gd = new GridData();
      gd.widthHint = 100;
      if (date != null) dateCombo.setValue(date);
      dateCombo.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            date = dateCombo.getValue();
            setLabelError();
            notifyXModifiedListeners();
            dateCombo.getParent().layout();
         }
      });

   }

   @Override
   public void dispose() {
      labelWidget.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public void setFromXml(String xml) {
      Matcher m =
            Pattern.compile("<" + xmlRoot + ">(\\d+)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
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
         if (dateCombo != null)
            dateCombo.setValue(date);
         else
            dateCombo.setValue(null);
      }
   }

   public void refresh() {
      setLabelError();
   }

   public boolean isValid() {
      if (isRequireFutureDate()) {
         if (getDate().before(new Date())) return false;
      }
      if (requiredEntry) {
         if (get().equals("")) return false;
      }
      return true;
   }

   public String getReportData() {
      return get();
   }

   public String get() {
      if (date == null) return "";
      return date.toString();
   }

   public String get(String pattern) {
      if (date == null) return "";
      String result = (new SimpleDateFormat(pattern)).format(date);
      return result;
   }

   public void setFocus() {
      if (dateCombo != null) dateCombo.setFocus();
   }

   /**
    * Don't need this since overriding toReport and toXml
    */
   public String getXmlData() {
      String dateStr = "";
      if (date != null) dateStr = date.getTime() + "";
      return dateStr;
   }

   /**
    * Don't need this since overriding setFromXml
    */
   public void setXmlData(String str) {
      if (str.equals(""))
         date = null;
      else {
         try {
            Long l = new Long(str);
            date = new Date(l.longValue());
         } catch (NumberFormatException e) {
            e.printStackTrace();
            date = null;
         }
      }
   }

   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, label + ": ") + get(defaultFormat);
   }

   public String toHTML(String labelFont, String pattern) {
      return AHTML.getLabelStr(labelFont, label + ": ") + get(pattern);
   }

   public boolean isRequireFutureDate() {
      return requireFutureDate;
   }

   public void setRequireFutureDate(boolean requireFutureDate) {
      this.requireFutureDate = requireFutureDate;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return getDate();
   }

}