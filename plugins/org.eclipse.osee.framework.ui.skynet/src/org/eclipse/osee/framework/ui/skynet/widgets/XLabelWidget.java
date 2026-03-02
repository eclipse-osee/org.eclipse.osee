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

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.component.annotations.Component;

/**
 * Simply shows the label name and nothing else. No storage or value associated with this widget.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XLabelWidget extends XWidget {

   public static final WidgetId ID = WidgetId.XLabelWidget;

   public XLabelWidget() {
      this("");
   }

   public XLabelWidget(String displayLabel) {
      super(ID, displayLabel);
   }

   /**
    * Create Data Widgets. Widgets Created: Data: "--select--" horizonatalSpan takes up 2 columns; horizontalSpan must
    * be >=2 the string "--select--" will be added to the sent in dataStrings array
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      // Create Data Widgets
      if (!getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         if (isFillHorizontally()) {
            labelWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         } else {
            labelWidget.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
         }
         if (Strings.isValid(getLabel())) {
            labelWidget.setText(getLabel());
         }
         if (Strings.isValid(widData.getDefaultValueStr())) {
            labelWidget.setText(widData.getDefaultValueStr());
         }
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }
   }

   @Override
   public Control getControl() {
      return labelWidget;
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

}