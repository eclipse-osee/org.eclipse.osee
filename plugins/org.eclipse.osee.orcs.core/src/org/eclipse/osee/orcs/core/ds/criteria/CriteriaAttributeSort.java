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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;

/**
 * @author Luciano Vaglienti
 */
public class CriteriaAttributeSort extends Criteria {

   private final long attributeTypeId;

   public CriteriaAttributeSort(long attributeTypeId) {
      this.attributeTypeId = attributeTypeId;
   }

   public CriteriaAttributeSort(AttributeTypeId attributeTypeId) {
      this.attributeTypeId = attributeTypeId.getId();
   }

   /**
    * @return the attributeTypeId
    */
   public long getAttributeTypeId() {
      return attributeTypeId;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.assertTrue(OptionsUtil.getOrderByMechanism(options).contains("ATTRIBUTE"),
         "Order mechanism is not attribute");
      Conditions.assertTrue(AttributeTypeId.valueOf(attributeTypeId).isValid(), "Attribute Type Id is not valid");
   }

}
