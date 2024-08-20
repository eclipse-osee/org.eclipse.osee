/*******************************************************************************
 * Copyright (c) 2022 Boeing.
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
package org.eclipse.osee.ats.api.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.util.AttributeValues.AttrValueType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class AttributeValue {

   private AttributeTypeToken attrType = AttributeTypeToken.SENTINEL;
   private List<String> values = new ArrayList<>();
   private AttrValueType attrValueType = AttrValueType.Value;

   public AttributeValue() {
      // for jax-rs
   }

   public AttributeValue(AttributeTypeToken attrType, String... values) {
      this.attrType = attrType;
      for (String value : values) {
         this.values.add(value);
      }
   }

   public AttributeValue(AttributeTypeToken attrType, AttrValueType attrValueType) {
      this.attrType = attrType;
      this.attrValueType = attrValueType;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public List<String> getValues() {
      return values;
   }

   public void setValues(List<String> values) {
      this.values = values;
   }

   @Override
   public int hashCode() {
      return attrType.getIdIntValue();
   }

   @Override
   public boolean equals(Object obj) {
      AttributeValue other = (AttributeValue) obj;
      return attrType.equals(other.getAttrType());
   }

   public boolean hasValues() {
      return !values.isEmpty();
   }

   public boolean isNotExists() {
      return attrValueType == AttrValueType.AttrNotExists;
   }

   public boolean isExists() {
      return attrValueType == AttrValueType.AttrExists;
   }

   public boolean isExistsValue() {
      return attrValueType == AttrValueType.AttrExistsValue;
   }

   public final AttrValueType getAttrValueType() {
      return attrValueType;
   }

   public final void setAttrValueType(AttrValueType attrValueType) {
      this.attrValueType = attrValueType;
   }

}
