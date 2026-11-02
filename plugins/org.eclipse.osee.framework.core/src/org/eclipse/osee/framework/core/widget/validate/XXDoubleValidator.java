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

package org.eclipse.osee.framework.core.widget.validate;

import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class XXDoubleValidator implements IValueValidator {

   public static XXDoubleValidator instance = new XXDoubleValidator();
   private final Double min;
   private final Double max;
   private final String label;
   private String regexValidate;

   public XXDoubleValidator() {
      this("Double", null, null);
   }

   public XXDoubleValidator(XWidgetData widData) {
      this(widData.getLabel(), //
         (widData.getMin() != null ? widData.getMin() : null), //
         (widData.getMax() != null ? widData.getMax() : null));
   }

   public XXDoubleValidator(XWidgetData widData, String regexValidate) {
      this(widData.getLabel(), //
         (widData.getMin() != null ? widData.getMin() : null), //
         (widData.getMax() != null ? widData.getMax() : null));
      this.regexValidate = regexValidate;
   }

   public XXDoubleValidator(String label, Double min, Double max) {
      this.label = label;
      this.min = min;
      this.max = max;
   }

   @Override
   public XResultData isValid(Object obj) {
      Double value = null;
      if (obj instanceof String) {
         String val = (String) obj;
         if (Strings.isValid(regexValidate) && !val.matches(regexValidate)) {
            return XWidgetStatus.NOT_VALID_DOUBLE;
         }
         if (Strings.isNotNumeric((String) obj)) {
            return XWidgetStatus.NOT_DOUBLE;
         }
         value = Double.valueOf((String) obj);
      } else if (obj instanceof Double) {
         value = (Double) obj;
         return XWidgetStatus.OK;
      }
      if (value != null) {
         if (min != null && value < min) {
            return new XResultData(XResultType.Error, //
               "%s value must be minimum [%s] not [%s]", //
               label, min, value);
         }
         if (max != null && value > max) {
            return new XResultData(XResultType.Error, //
               "%s value must be max [%s] not [%s]", //
               label, max, value);
         }
      }
      return XWidgetStatus.UNHANDLED_OBJECT_TYPE;
   }

}
