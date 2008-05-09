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
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Generic label and radiobutton field object for use by single entry artifact attributes
 * 
 * @author Donald G. Dunne
 */
public class XRadioButton extends XWidget {

   private Composite parent;
   private boolean selected = false;
   private final String xmlRoot;
   private Button button;
   public static enum ButtonType {
      Check, Radio
   };
   private ButtonType buttonType = ButtonType.Radio;
   private boolean labelAfter;

   public XRadioButton(String displayLabel) {
      this(displayLabel, "");
   }

   public XRadioButton(String displayLabel, String xmlRoot) {
      super(displayLabel, xmlRoot);
      this.xmlRoot = xmlRoot;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return button;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
      if (button != null) button.setSelection(selected);
   }

   public String toString() {
      return getLabel() + ": " + selected;
   }

   public void setFromXml(String xml) {
      Matcher m;
      m = Pattern.compile("<" + xmlRoot + ">(.*?)</" + xmlRoot + ">", Pattern.MULTILINE | Pattern.DOTALL).matcher(xml);
      if (m.find()) {
         String str = m.group(1);
         if (str.equals("true"))
            setSelected(true);
         else if (str.equals("false"))
            setSelected(false);
         else
            System.err.println("Unexpected radiobutton value " + str);
      }
      refresh();
   }

   public String getXmlData() {
      return "" + selected;
   }

   public String getDisplayStr() {
      return getXmlData();
   }

   /**
    * Don't need this since overriding setFromXml
    */
   public void setXmlData(String str) {
   }

   /**
    * Create radio Widgets. Widgets Created: Label: "text entry" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2
    */
   public void createWidgets(Composite parent, int horizontalSpan) {
      this.parent = parent;
      // Create Text Widgets
      if (!isLabelAfter() && isDisplayLabel()) createLabel(parent);

      button = new Button(parent, (buttonType == ButtonType.Check) ? SWT.CHECK : SWT.RADIO);
      if (toolTip != null && !toolTip.equals("")) button.setToolTipText(toolTip);
      if (getToolTip() != null && !getToolTip().equals("")) button.setToolTipText(getToolTip());
      GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
      button.setLayoutData(gd);
      button.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent event) {
            Button b = (Button) event.getSource();
            setSelected(b.getSelection());
            notifyXModifiedListeners();
         }
      });
      if (isLabelAfter()) createLabel(parent);
      refresh();
   }

   public void createLabel(Composite parent) {
      labelWidget = new Label(parent, SWT.NONE);
      String str = label;
      if (!isLabelAfter()) str += ":";
      labelWidget.setText(str);
      if (toolTip != null && !toolTip.equals("")) labelWidget.setToolTipText(toolTip);
   }

   public void dispose() {
      button.dispose();
      if (labelWidget != null) labelWidget.dispose();
      if (parent != null && !parent.isDisposed()) parent.layout();
   }

   public void addSelectionListener(SelectionListener selectionListener) {
      if (button != null) button.addSelectionListener(selectionListener);
   }

   public void removeSelectionListener(SelectionListener selectionListener) {
      button.removeSelectionListener(selectionListener);
   }

   public boolean isSelected() {
      return selected;
   }

   public void refresh() {
      if (button != null) {
         button.setSelection(selected);
      }
      setLabelError();
   }

   public Result isValid() {
      return Result.TrueResult;
   }

   public String getReportData() {
      return getXmlData();
   }

   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, label + ": ") + getDisplayStr();
   }

   public boolean isLabelAfter() {
      return labelAfter;
   }

   /**
    * @return the buttonType
    */
   public ButtonType getButtonType() {
      return buttonType;
   }

   /**
    * @param buttonType the buttonType to set
    */
   public void setButtonType(ButtonType buttonType) {
      this.buttonType = buttonType;
   }

   /**
    * @param labelAfter the labelAfter to set
    */
   public void setLabelAfter(boolean labelAfter) {
      this.labelAfter = labelAfter;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return selected;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
      button.setFocus();
   }
}