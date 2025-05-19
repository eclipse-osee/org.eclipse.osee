/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data.conditions;

import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Note: It enables the widget when at least one value matches the current attribute value.
 *
 * @author Donald G. Dunne
 */
public class EnableIfAttrValueCondition extends ConditionalRule {

   private AttributeTypeToken attrType;
   private Object value;

   public EnableIfAttrValueCondition(AttributeTypeToken attrType, Object... value) {
      this.attrType = attrType;
      this.value = value;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public Object getValue() {
      return value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   @Override
   public boolean isEnabled(List<String> currentValues) {
      if (getValue() instanceof Object[]) {
         Object[] matchValues = (Object[]) getValue();
         for (Object matchValue : matchValues) {
            if ((currentValues.size() == 0 && (matchValue instanceof String && Strings.isInvalid(
               (String) matchValue))) || currentValues.contains(matchValue)) {
               return true;
            }
         }
      } else {
         if (currentValues.contains(getValue())) {
            return true;
         }
      }
      return false;
   }

}
