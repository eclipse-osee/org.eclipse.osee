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
import org.eclipse.osee.framework.core.widget.validate.XXLongValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXLongSTextWidget extends XXAbstractSTextWidget {

   public static final WidgetId ID = WidgetId.XXLongSTextWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXLongSTextWidget() {
      this("", "");
   }

   public XXLongSTextWidget(String label) {
      this(ID, label, "");
   }

   public XXLongSTextWidget(String label, String value) {
      this(ID, label, value);
   }

   public XXLongSTextWidget(WidgetId widgetId, String label, String value) {
      super(widgetId, label);
      if (Strings.isValid(value)) {
         setDefaultValue(value);
      }
      set(XOption.VAL_SINGLE_TEXT);
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   public IValueValidator getValidator() {
      // Allow widData to override default validator
      if (widData.getValidator().isNoOp()) {
         IValueValidator validator = new XXLongValidator(widData);
         widData.setValidator(validator);
         return validator;
      }
      return widData.getValidator();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setValue(String value) {
      setSelected(value);
      setLabelFgColor(SWT.NONE);
   }

   /**
    * @param swtColor like SWT.COLOR_RED or SWT.NONE to set back to default color
    */
   public void setValue(String value, int swtColor) {
      this.setValue(value);
      setValueFgColor(swtColor);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         setSelected(getArtifact().getAttributesToStringList(getAttributeType()), false);
      }
   }

}
