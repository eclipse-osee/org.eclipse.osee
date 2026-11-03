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

/**
 * @author Donald G. Dunne
 */
public class XXPercentValidator extends XXIntegerValidator {

   public static XXPercentValidator instance = new XXPercentValidator();

   public XXPercentValidator() {
      this("Percent", 0, 100);
   }

   public XXPercentValidator(XWidgetData widData) {
      this(widData.getLabel(), //
         (widData.getMin() != null ? widData.getMin().intValue() : 0), //
         (widData.getMax() != null ? widData.getMax().intValue() : 100));
   }

   public XXPercentValidator(String label, Integer min, Integer max) {
      super(label, min, max);
   }

}
