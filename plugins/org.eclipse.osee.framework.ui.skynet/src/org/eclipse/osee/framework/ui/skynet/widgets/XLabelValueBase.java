/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class XLabelValueBase extends GenericXWidget {

   String valueText = "";
   private Text valueTextWidget;
   private Composite comp;

   public Composite getComp() {
      return comp;
   }

   public XLabelValueBase(String label) {
      super(label);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      comp = new Composite(parent, SWT.NONE);
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

      valueTextWidget = new Text(comp, SWT.NONE);
      valueTextWidget.setEditable(false);
      if (Strings.isValid(getToolTip())) {
         valueTextWidget.setToolTipText(getToolTip());
      }
      if (isFillHorizontally()) {
         if (isFillHorizontally()) {
            valueTextWidget.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
         } else {
            valueTextWidget.setLayoutData(new GridData());
         }
      }
      refresh();
   }

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(valueTextWidget)) {
         return;
      }
      valueTextWidget.setText(getValueText());
      valueTextWidget.update();
      valueTextWidget.getParent().update();
      validate();
   }

   @Override
   public Control getControl() {
      return valueTextWidget;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      toolkit.adapt(valueTextWidget, true, true);
      valueTextWidget.update();
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getValueText());
   }

   public String getValueText() {
      return valueText == null ? "" : valueText;
   }

   public void setValueText(String text) {
      valueText = text;
      if (valueTextWidget != null && !valueTextWidget.isDisposed()) {
         valueTextWidget.setText(valueText);
         valueTextWidget.update();
         valueTextWidget.getParent().update();
      }
   }

   public Text getValueTextWidget() {
      return valueTextWidget;
   }

}
