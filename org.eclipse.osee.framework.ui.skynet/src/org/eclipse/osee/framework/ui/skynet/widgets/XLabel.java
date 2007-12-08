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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Simply shows the label name and nothing else. No storage or value associated with this widget.
 * 
 * @author Donald G. Dunne
 */
public class XLabel extends XWidget {

   public XLabel(String displayLabel) {
      super(displayLabel, "");
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   public void createWidgets(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) horizontalSpan = 2;
      // Create Data Widgets
      if (!label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label);
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }
   }

   public void setFocus() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return labelWidget;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#isValid()
    */
   @Override
   public boolean isValid() {
      return true;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return "";
   }

}