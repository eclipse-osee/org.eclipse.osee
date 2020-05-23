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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Simply shows the label name and nothing else. No storage or value associated with this widget.
 * 
 * @author Donald G. Dunne
 */
public class XLabel extends GenericXWidget {

   private final String showString;

   public XLabel(String displayLabel) {
      this(displayLabel, displayLabel);
   }

   public XLabel(String displayLabel, String showString) {
      super(displayLabel);
      this.showString = showString;
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
         labelWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         labelWidget.setText(showString);
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