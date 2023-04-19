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
package org.eclipse.osee.mim.types;

/**
 * @author Ryan T. Baldwin
 */
public class MimAttributeChange {
   private final Long attributeId;
   private final String attributeName;
   private final String oldValue;
   private final String newValue;

   public MimAttributeChange(Long attributeId, String attributeName, String oldValue, String newValue) {
      this.attributeId = attributeId;
      this.attributeName = attributeName;
      this.oldValue = oldValue;
      this.newValue = newValue;
   }

   public Long getAttributeId() {
      return attributeId;
   }

   public String getAttributeName() {
      return attributeName;
   }

   public String getOldValue() {
      return oldValue;
   }

   public String getNewValue() {
      return newValue;
   }

}