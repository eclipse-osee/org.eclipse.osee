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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public class XLabelAttribute extends XWidget {

   private Composite parent;
   private String data = "";
   private Label valueLabel;
   private Attribute attribute;

   // 
   public XLabelAttribute(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
   }

   public XLabelAttribute(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
   }

   public XLabelAttribute(String displayLabel) {
      super(displayLabel, "");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return valueLabel;
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   public void createWidgets(Composite parent, int horizontalSpan) {

      this.parent = parent;
      if (horizontalSpan < 2) horizontalSpan = 2;
      // Create Data Widgets
      if (!label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }
      valueLabel = new Label(parent, SWT.NONE);
      valueLabel.setText(data);

      refresh();
   }

   public void setFocus() {
   }

   public void setAttribute(Attribute attribute) {
      this.attribute = attribute;
      set(attribute.getStringData());
   }

   public void setFromXml(String xml) {
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
         set(str);
         break;
      }
      refresh();
   }

   public void refresh() {
      if (valueLabel != null && !valueLabel.isDisposed()) {
         if (attribute != null && data != attribute.getStringData()) data = attribute.getStringData();
         valueLabel.setText(data);
         valueLabel.getParent().layout();
      }
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
      return data;
   }

   public String getReportData() {
      return data;
   }

   public String getXmlData() {
      return getReportData();
   }

   public void setXmlData(String str) {
   }

   public void set(String data) {
      this.data = data;
      attribute.setStringData(data);
      refresh();
   }

   public boolean isValid() {
      if (requiredEntry && data.equals("")) return false;
      return true;
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

   public static void copy(XLabelAttribute from, XLabelAttribute to) {
      to.set(from.get());
   }

   public void dispose() {
      if (labelWidget != null) labelWidget.dispose();
      if (valueLabel != null) valueLabel.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return valueLabel.getText();
   }

}