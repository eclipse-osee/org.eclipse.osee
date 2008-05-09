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

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelSelection extends XWidget {

   Label valueLabel;
   Label hyperLinkLabel;

   /**
    * @param label
    */
   public XHyperlinkLabelSelection(String label) {
      super(label);
   }

   public String getCurrentValue() {
      return "";
   }

   public String getHyperlinkLabelString() {
      return " (modify) ";
   }

   public boolean handleSelection() {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(3, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      // Create Text Widgets
      if (displayLabel && !label.equals("")) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      hyperLinkLabel = new HyperLinkLabel(comp, SWT.NONE);
      hyperLinkLabel.setToolTipText("Select to modify");
      hyperLinkLabel.addListener(SWT.MouseUp, new Listener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         public void handleEvent(Event event) {
            if (handleSelection()) {
               refresh();
               notifyXModifiedListeners();
            }
         }
      });
      valueLabel = new Label(comp, SWT.NONE);
      valueLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
   }

   public void createWidgets(FormToolkit toolkit, Composite parent, int horizontalSpan) {
      this.createWidgets(parent, horizontalSpan);
      toolkit.adapt(valueLabel, false, false);
      valueLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
      refresh();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#refresh()
    */
   @Override
   public void refresh() {
      hyperLinkLabel.setText(getHyperlinkLabelString());
      valueLabel.setText(getCurrentValue());
      valueLabel.getParent().layout();
      setLabelError();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getControl()
    */
   @Override
   public Control getControl() {
      return valueLabel;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#toHTML(java.lang.String)
    */
   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getHyperlinkLabelString(), getCurrentValue());
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#dispose()
    */
   @Override
   public void dispose() {
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
   public Result isValid() {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setFocus()
    */
   @Override
   public void setFocus() {
   }

   /* (non-Javadoc)
    * @see osee.skynet.gui.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

}
