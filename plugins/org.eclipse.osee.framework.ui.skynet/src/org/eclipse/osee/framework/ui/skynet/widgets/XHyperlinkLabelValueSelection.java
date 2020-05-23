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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public abstract class XHyperlinkLabelValueSelection extends GenericXWidget {

   Hyperlink valueHyperlinkLabel;
   protected Label valueLabel;

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

      GridData gd = new GridData();
      if (isFillHorizontally()) {
         gd.grabExcessHorizontalSpace = true;
      }

      if (isEditable()) {
         if (toolkit == null) {
            valueHyperlinkLabel = new Hyperlink(comp, SWT.NONE);
         } else {
            valueHyperlinkLabel = toolkit.createHyperlink(comp, " <edit>", SWT.NONE);
         }
         valueHyperlinkLabel.setToolTipText(Strings.isValid(getToolTip()) ? getToolTip() : "Select to Modify");
         valueHyperlinkLabel.setLayoutData(gd);
         valueHyperlinkLabel.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (handleSelection()) {
                  refresh();
                  notifyXModifiedListeners();
               }
            }
         });
      } else {
         if (toolkit == null) {
            valueLabel = new Label(comp, SWT.NONE);
         } else {
            valueLabel = toolkit.createLabel(comp, " Not Set", SWT.NONE);
         }
         valueLabel.setToolTipText(getToolTip());
         valueLabel.setLayoutData(gd);
      }

      refresh();
   }

   @Override
   public void refresh() {
      if (getControl() == null || getControl().isDisposed()) {
         return;
      }
      if (Widgets.isAccessible(valueHyperlinkLabel)) {
         if (getCurrentValue().equals(valueHyperlinkLabel.getText())) {
            return;
         }
         valueHyperlinkLabel.setText(getCurrentValue());
         valueHyperlinkLabel.update();
         valueHyperlinkLabel.getParent().update();
      } else if (Widgets.isAccessible(valueLabel)) {
         if (getCurrentValue().equals(valueLabel.getText())) {
            return;
         }
         valueLabel.setText(getCurrentValue());
         valueLabel.update();
         valueLabel.getParent().update();

      }
      validate();
   }

   public boolean handleSelection() {
      return false;
   }

   @Override
   public Control getControl() {
      if (Widgets.isAccessible(valueHyperlinkLabel)) {
         return valueHyperlinkLabel;
      }
      if (Widgets.isAccessible(valueLabel)) {
         return valueLabel;
      }
      return null;
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      if (Widgets.isAccessible(valueHyperlinkLabel)) {
         toolkit.adapt(valueHyperlinkLabel, true, true);
         valueHyperlinkLabel.update();
      }
      if (Widgets.isAccessible(valueLabel)) {
         toolkit.adapt(valueLabel, true, true);
         valueLabel.update();
      }
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getCurrentValue());
   }
}
