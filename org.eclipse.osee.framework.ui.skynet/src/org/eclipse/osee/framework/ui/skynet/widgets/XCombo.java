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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XCombo extends XWidget {

   private Combo dataCombo;
   private Composite parent;
   protected String data = "";
   protected String[] inDataStrings; // Strings sent in for display
   // 
   private Map<String, Integer> displayDataStrings = new HashMap<String, Integer>();
   protected Map<String, String> dataStringToXmlString;
   private String displayArray[];

   public XCombo(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
   }

   public XCombo(String displayLabel, String xmlRoot) {
      this(displayLabel, xmlRoot, "");
   }

   public XCombo(String displayLabel) {
      this(displayLabel, "", "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return dataCombo;
   }

   public void setEnabled(boolean enabled) {
      dataCombo.setEnabled(enabled);
   }

   public void createWidgets(Composite composite, int horizontalSpan, String inDataStrings[]) {
      this.inDataStrings = inDataStrings;
      createWidgets(composite, horizontalSpan);
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   public void createWidgets(Composite parent, int horizontalSpan) {

      GridData gd;
      this.parent = parent;

      if (inDataStrings == null) {
         inDataStrings = new String[] {"DATA NOT FOUND"};
      }
      setDisplayDataStrings();

      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }

      // Create Data Widgets
      if (!label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      dataCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
      dataCombo.setItems(displayArray);
      if (displayArray.length < 15) dataCombo.setVisibleItemCount(displayArray.length);
      gd = new GridData();
      if (fillHorizontally) gd.grabExcessHorizontalSpace = true;
      if (fillVertically) gd.grabExcessVerticalSpace = true;
      gd.horizontalSpan = horizontalSpan - 1;
      dataCombo.setLayoutData(gd);
      ModifyListener dataComboListener = new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            data = dataCombo.getText();
            if (data.compareTo("--select--") == 0) {
               data = "";
            }
            setLabelError();
            notifyXModifiedListeners();
         }
      };
      dataCombo.addModifyListener(dataComboListener);

      refresh();
      dataCombo.setEnabled(editable);
   }

   public int getDisplayPosition(String str) {
      for (int i = 0; i < displayArray.length; i++) {
         if (str.equals(displayArray[i])) return i;
      }
      return 0;
   }

   public int getDisplayPosition() {
      for (int i = 0; i < displayArray.length; i++) {
         if (data.equals(displayArray[i])) return i;
      }
      return 0;
   }

   public void setDataStrings(String[] inDataStrings) {
      this.inDataStrings = inDataStrings;
      setDisplayDataStrings();
      if (dataCombo != null && !dataCombo.isDisposed()) {
         dataCombo.setItems(displayArray);
         if (displayArray.length < 15) dataCombo.setVisibleItemCount(displayArray.length);
      }
      updateComboWidget();
   }

   /**
    * Given the inDataStrings, create the mapping of all data strings including "--select--" and map them to their index
    * in the combo list.
    */
   private void setDisplayDataStrings() {
      displayDataStrings.clear();
      displayDataStrings.put("--select--", 0);
      displayArray = new String[inDataStrings.length + 1];
      displayArray[0] = "--select--";
      for (int i = 0; i < inDataStrings.length; i++) {
         displayDataStrings.put(inDataStrings[i], (i + 1));
         displayArray[i + 1] = inDataStrings[i];
      }
   }

   public void setFocus() {
      if (dataCombo != null) dataCombo.setFocus();
   }

   public void setDataStringToXmlTranslations(Map<String, String> dataStringToXmlString) {
      this.dataStringToXmlString = dataStringToXmlString;
   }

   @SuppressWarnings( {"unchecked", "unchecked"})
   @Override
   public void setFromXml(String xml) throws IllegalStateException, SQLException {
      Matcher m;
      if (xmlSubRoot.equals("")) {
         m =
               Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(
                     xml);
      } else {
         m =
               Pattern.compile("<" + xmlRoot + "><" + xmlSubRoot + ">(.*?)</" + xmlSubRoot + "></" + xmlRoot + ">",
                     Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      }
      while (m.find()) {
         String str = m.group(1);
         String transStr = null;
         // If translation given, translate back to display string
         // ie. bems number => full name
         if (dataStringToXmlString != null) {
            if (dataStringToXmlString.containsValue(str)) {
               for (Iterator iter = dataStringToXmlString.entrySet().iterator(); iter.hasNext();) {
                  Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
                  if (str.equals(entry.getValue())) {
                     transStr = (String) entry.getKey();
                     break;
                  }
               }
            }
         }
         if (transStr != null) {
            set(transStr);
         } else {
            set(str);
         }
         break;
      }
      refresh();
   }

   public void refresh() {
      updateComboWidget();
   }

   public void addModifyListener(ModifyListener modifyListener) {
      if (dataCombo != null) dataCombo.addModifyListener(modifyListener);
   }

   public Combo getComboBox() {
      return dataCombo;
   }

   /**
    * @return selected display value (eg. Donald Dunne)
    */
   public String get() {
      return data;
   }

   /**
    * @return returns translated xml value (eg. 727536)
    */
   public String getXml() {
      String s = "";
      if (dataStringToXmlString == null) {
         s = data;
      } else {
         s = (String) dataStringToXmlString.get(data);
         if (s == null) {
            s = data;
         }
      }
      return s;
   }

   public String getReportData() {
      return data;
   }

   public String getXmlData() {
      return getReportData();
   }

   public void setXmlData(String str) {
   }

   private void updateComboWidget() {
      if (dataCombo != null && !dataCombo.isDisposed()) {
         if (displayDataStrings.containsKey(data)) {
            if (data.compareTo("") == 0) {
               dataCombo.select(0);
            } else {
               Integer pos = displayDataStrings.get(data);
               dataCombo.select(pos.intValue());
            }
         } else if (data.compareTo("") != 0) {
            // if not found, add it and select it
            displayDataStrings.put(data, displayDataStrings.size());
            dataCombo.add(data);
            dataCombo.select(displayDataStrings.size() - 1);
         } else {
            dataCombo.select(0);
         }
         if (displayDataStrings.size() < 15) dataCombo.setVisibleItemCount(displayDataStrings.size());
      }
      setLabelError();
   }

   public void set(String data) throws IllegalStateException, SQLException {
      this.data = data;
      updateComboWidget();
   }

   public void set(int pos) {
      if (displayArray.length > pos) {
         this.data = displayArray[pos];
         updateComboWidget();
      }
   }

   public void remove(String data) {
      displayDataStrings.remove(data);
      if (dataCombo.indexOf(data) >= 0) dataCombo.remove(data);
   }

   public Result isValid() {
      if (requiredEntry && data.equals("")) {
         return new Result(getLabel() + " must be selected.");
      }
      return Result.TrueResult;
   }

   public String toXml() {
      return toXml(xmlRoot);
   }

   public String toXml(String xmlRoot) {
      String s;
      String dataStr = getXml();
      if (xmlSubRoot == null || xmlSubRoot.equals("")) {
         s = "<" + xmlRoot + ">" + dataStr + "</" + xmlRoot + ">\n";
      } else {
         s = "<" + xmlRoot + "><" + xmlSubRoot + ">" + dataStr + "</" + xmlSubRoot + "></" + xmlRoot + ">\n";
      }
      return s;
   }

   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, label + ": ") + data;
   }

   public static void copy(XCombo from, XCombo to) throws IllegalStateException, SQLException {
      to.set(from.get());
   }

   public void dispose() {
      if (labelWidget != null) labelWidget.dispose();
      if (dataCombo != null) dataCombo.dispose();
      if (labelWidget != null) labelWidget.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public String[] getDisplayArray() {
      return displayArray;
   }

   @Override
   public Object getData() {
      return dataCombo.getText();
   }
}