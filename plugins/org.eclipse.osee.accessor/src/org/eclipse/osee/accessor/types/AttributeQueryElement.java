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
package org.eclipse.osee.accessor.types;

import org.eclipse.osee.framework.core.data.AttributeTypeId;

public class AttributeQueryElement {
   private AttributeTypeId attributeId;
   private String value;

   public AttributeQueryElement() {
   }

   /**
    * @return the value
    */
   public String getValue() {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(String value) {
      this.value = value;
   }

   /**
    * @return the attributeId
    */
   public AttributeTypeId getAttributeId() {
      return attributeId;
   }

   /**
    * @param attributeId the attributeId to set
    */
   public void setAttributeId(AttributeTypeId attributeId) {
      this.attributeId = attributeId;
   }

}
