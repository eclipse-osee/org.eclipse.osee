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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelValueSelection extends XWidget {

   Hyperlink valueHyperlinkLabel;

   public XHyperlinkLabelValueSelection(String label) {
      super(label);
   }

   /**
    * Override this method to provide changing value
    */
   public abstract String getCurrentValue();

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(5, false));
      if (isFillHorizontally()) {
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      } else {
         comp.setLayoutData(new GridData());
      }

      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(comp, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      if (toolkit == null) {
         valueHyperlinkLabel = new Hyperlink(comp, SWT.NONE);
      } else {
         valueHyperlinkLabel = toolkit.createHyperlink(comp, " <edit>", SWT.NONE);
      }
      valueHyperlinkLabel.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : "Select to Modify");
      GridData gd = new GridData();
      if (isFillHorizontally()) {
         gd.grabExcessHorizontalSpace = true;
      }
      valueHyperlinkLabel.setLayoutData(gd);
      valueHyperlinkLabel.addListener(SWT.MouseUp, new Listener() {
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
      refresh();
   }

   @Override
   public void refresh() {
      if (getControl() == null || getControl().isDisposed() || getCurrentValue().equals(valueHyperlinkLabel.getText())) return;
      valueHyperlinkLabel.setText(getCurrentValue());
      valueHyperlinkLabel.update();
      valueHyperlinkLabel.getParent().update();
      validate();
   }

   public boolean handleSelection() {
      return false;
   }

   @Override
   public Control getControl() {
      return valueHyperlinkLabel;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      toolkit.adapt(valueHyperlinkLabel, true, true);
      valueHyperlinkLabel.update();
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getCurrentValue());
   }

   @Override
   public void dispose() {
   }

   @Override
   public Object getData() {
      return null;
   }

   @Override
   public String getReportData() {
      return null;
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   @Override
   public void setFocus() {
   }

   @Override
   public void setXmlData(String str) {
   }

}
