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
public class XXLongValidator implements IValueValidator {

   private static final String LONG_REGEX = "^-?\\d{1,19}$";
   public static XXLongValidator instance = new XXLongValidator();
   private final Long min;
   private final Long max;
   private final String label;

   public XXLongValidator() {
      this("Long", null, null);
   }

   public XXLongValidator(XWidgetData widData) {
      this(widData.getLabel(), //
         (widData.getMin() != null ? widData.getMin().longValue() : null), //
         (widData.getMax() != null ? widData.getMax().longValue() : null));
   }

   public XXLongValidator(String label, Long min, Long max) {
      this.label = label;
      this.min = min;
      this.max = max;
   }

   @Override
   public XResultData isValid(Object obj) {
      Long value = null;
      if (obj instanceof String) {
         if (!((String) obj).matches(LONG_REGEX)) {
            return XWidgetStatus.NOT_LONG;
         }
         value = Long.valueOf((String) obj);
      } else if (obj instanceof Long) {
         value = (Long) obj;
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
