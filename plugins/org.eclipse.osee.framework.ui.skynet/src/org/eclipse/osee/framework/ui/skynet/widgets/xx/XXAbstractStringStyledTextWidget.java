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
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * General StyledText where border is included and in-line editing is provided. <br>
 * For use-case where saving to artifact is done, use XXStringSTextHEditWidget.
 *
 * @author Donald G. Dunne
 */
public abstract class XXAbstractStringStyledTextWidget extends XXAbstractSTextWidget {

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractStringStyledTextWidget(WidgetId widgetId, String label) {
      this(widgetId, label, "");
   }

   public XXAbstractStringStyledTextWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label);
      if (Strings.isValid(value)) {
         setDefaultValue(value);
      }
      set(XOption.WRITE);
      set(XOption.COLON);
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setValue(String value) {
      setSelected(value);
      if (Widgets.isAccessible(getLabelWidget())) {
         getLabelWidget().setForeground(null);
      }
   }

   /**
    * @param swtColor like SWT.COLOR_RED or SWT.NONE to set back to default color
    */
   public void setValue(String value, int swtColor) {
      this.setValue(value);
      setValueFgColor(swtColor);
   }

}
