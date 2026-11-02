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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXLabelValueWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetId.XXLabelValueWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXLabelValueWidget() {
      this("", "");
   }

   public XXLabelValueWidget(String label, String value) {
      this(ID, label, value);
   }

   public XXLabelValueWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label);
      if (Strings.isValid(value)) {
         setDefaultValue(value);
      }
      set(XOption.READ);
      set(XOption.NO_CLEAR);
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setValue(String value) {
      this.setValue(value, SWT.NONE);
   }

   /**
    * @param swtColor like SWT.COLOR_RED or SWT.NONE to set back to default color
    */
   public void setValue(String value, int swtColor) {
      if (Strings.isInvalid(value)) {
         clear();
      } else {
         super.setSelected(value);
      }
      setValueFgColor(swtColor);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (widData.getDefaultValue() instanceof String && Strings.isValid((String) widData.getDefaultValue())) {
         setSelected((String) widData.getDefaultValue(), false);
      }
   }

}
