/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.orcs.core.ds.Criteria;

/**
 * @author Vaibhav Patel
 */
public class CriteriaAttributeValueRange extends Criteria {

   private final AttributeTypeToken attributeType;
   private final String fromValue;
   private final String toValue;

   public CriteriaAttributeValueRange(AttributeTypeToken attributeType, String fromValue, String toValue) {
      this.attributeType = attributeType;
      this.fromValue = fromValue;
      this.toValue = toValue;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public String getFromValue() {
      return fromValue;
   }

   public String getToValue() {
      return toValue;
   }

   @Override
   public String toString() {
      return "CriteriaAttributeValueRange [attributeType=" + attributeType.toString() + ", fromValue=" + fromValue + ", toValue=" + toValue + "]";
   }
}
