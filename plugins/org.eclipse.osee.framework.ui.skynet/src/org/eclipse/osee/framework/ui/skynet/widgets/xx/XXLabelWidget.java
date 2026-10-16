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
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * To only show a un-editable label with no value. Color is optional through XOption
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXLabelWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetId.XXLabelWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXLabelWidget() {
      this("", "");
   }

   public XXLabelWidget(String label) {
      this(ID, label, "");
   }

   public XXLabelWidget(String label, String value) {
      this(ID, label, value);
   }

   /**
    * @param value retained only for constructor/subclass compatibility; XXLabelWidget is label-only and does NOT render
    * a value. Use XXLabelValueWidget (XLabelDam) when a label + value is needed.
    */
   public XXLabelWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label);
      set(XOption.READ);
      set(XOption.NO_CLEAR);
      set(XOption.NO_COLON);
      set(XOption.NO_LABEL_BOLD);
      remove(XOption.VAL_SINGLE_SEL);
      set(XOption.VAL_NOT_APPLICABLE);
      set(XOption.NO_VALUE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   /**
    * XXLabelWidget is label-only. Use XXLabelValueWidget if value is always or sometimes desired.
    */
   @Override
   protected void createValueWidget() {
      Object defaultValue = getWidData().getDefaultValue();
      if (defaultValue != null && Strings.isValid(defaultValue.toString())) {
         throw new OseeArgumentException(
            "XXLabelWidget is label-only and cannot display a value [%s]; use XXLabelValueWidget (andLabelValueWidget) for a label + value.",
            defaultValue);
      }
   }

}
