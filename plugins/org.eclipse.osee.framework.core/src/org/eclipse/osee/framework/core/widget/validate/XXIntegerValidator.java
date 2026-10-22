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

/**
 * @author Donald G. Dunne
 */
public class XXIntegerValidator implements IValueValidator {

   private static final String INTEGER_REGEX = "^[+-]?(?:0|[1-9]\\d*)$";
   public static XXIntegerValidator instance = new XXIntegerValidator();
   private final Integer min;
   private final Integer max;
   private final String label;

   public XXIntegerValidator() {
      this("Integer", null, null);
   }

   public XXIntegerValidator(XWidgetData widData) {
      this(widData.getLabel(), //
         (widData.getMin() != null ? widData.getMin().intValue() : null), //
         (widData.getMax() != null ? widData.getMax().intValue() : null));
   }

   public XXIntegerValidator(String label, Integer min, Integer max) {
      this.label = label;
      this.min = min;
      this.max = max;
   }

   @Override
   public XResultData isValid(Object obj) {
      Integer value = null;
      if (obj instanceof String) {
         if (!((String) obj).matches(INTEGER_REGEX)) {
            return XWidgetStatus.NOT_INTEGER;
         }
         value = Integer.valueOf((String) obj);
      } else if (obj instanceof Integer) {
         value = (Integer) obj;
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
