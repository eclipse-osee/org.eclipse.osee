/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.search.ds.criteria;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.search.ds.Criteria;
import org.eclipse.osee.orcs.search.ds.Options;
import org.eclipse.osee.orcs.search.ds.OptionsUtil;

/**
 * @author Luciano Vaglienti
 */
public class CriteriaAttributeSort extends Criteria {

   private final AttributeTypeToken attributeType;

   public CriteriaAttributeSort(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   public CriteriaAttributeSort(long attributeType) {
      this.attributeType = AttributeTypeToken.valueOf(attributeType);
   }

   /**
    * @return the attributeTypeId
    */
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.assertTrue(OptionsUtil.getOrderByMechanism(options).contains("ATTRIBUTE"),
         "Order mechanism is not attribute");
      Conditions.assertTrue(attributeType.isValid(), "Attribute Type Id is not valid");
   }

   public boolean isValid() {
      return attributeType.isValid();
   }
}
