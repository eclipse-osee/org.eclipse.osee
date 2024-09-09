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

import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class EnableIfCondition extends ConditionalRule {

   private AttributeTypeToken attrType;
   private Object value;

   public EnableIfCondition(AttributeTypeToken attrType, Object... value) {
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

   //   @Override
   //   public boolean isEnabled(ArtifactToken artifact) {
   //      //      List<Boolean> enabledAll = new ArrayList<Boolean>();
   //      //      for (ConditionalRule rule : getConditions()) {
   //      //         boolean enabled = false;
   //      //         if (rule instanceof EnableIfCondition) {
   //      //            EnableIfCondition condition = (EnableIfCondition) rule;
   //      //            Object currValue =
   //      //               ((ArtifactStoredWidget) this).getArtifact().getSoleAttributeValueAsString(condition.getAttrType(), "");
   //      //            Object value = condition.getValue();
   //      //            if (value instanceof Object[]) {
   //      //               Object[] values = (Object[]) value;
   //      //               for (Object val : values) {
   //      //                  if (currValue.equals(val)) {
   //      //                     enabled = true;
   //      //                     break;
   //      //                  }
   //      //               }
   //      //            } else {
   //      //               if (currValue.equals(value)) {
   //      //                  enabled = true;
   //      //               }
   //      //            }
   //      //            enabledAll.add(enabled);
   //      //         }
   //      //      }
   //      return true;
   //   }

}
