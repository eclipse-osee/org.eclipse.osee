/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xx.flat;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXTextInternalWidget extends XXTextWidget {

   public static final WidgetId ID = WidgetId.XXTextInternalWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXTextInternalWidget() {
      this("");
   }

   public XXTextInternalWidget(String label) {
      super(ID, label);
      set(XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   public void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
      gd.widthHint = 200;
      valueSText.setLayoutData(gd);
      valueSText.setWordWrap(true);
      valueSText.setEditable(isEditable());
      if (!isEditable()) {
         valueSText.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
      }
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected int getTextStyle() {
      int styleBase = SWT.NONE;
      if (isEditable()) {
         styleBase |= SWT.READ_ONLY;
      }
      return styleBase | (isFillVertically() ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
   }

}
