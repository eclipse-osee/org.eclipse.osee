/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.BaseId;

public class AttributePojo<T> extends BaseId {

   private final T value;
   private final GammaId gammaId;
   private final String displayableString;
   private final AttributeTypeToken typeId;

   public static <T> AttributePojo<T> valueOf(Long id, AttributeTypeToken type, GammaId gamma, T value,
      String displayableString) {
      return new AttributePojo<T>(id, type, gamma, value, displayableString);
   }

   public static <T> AttributePojo<T> valueOf(IAttribute<T> attribute) {
      return new AttributePojo<T>(attribute);
   }

   public AttributePojo(Long id, AttributeTypeToken typeId, GammaId gammaId, T value, String displayableString) {
      super(id);
      this.typeId = typeId;
      this.gammaId = gammaId;
      this.value = value;
      this.displayableString = displayableString;
   }

   public AttributePojo(IAttribute<T> attribute) {
      super(attribute.getId());
      this.value = attribute.getValue();
      this.gammaId = attribute.getGammaId();
      this.displayableString = attribute.getDisplayableString();
      this.typeId = attribute.getAttributeType();
   }

   public AttributePojo() {
      super(-1L);
      this.value = null;
      this.gammaId = GammaId.SENTINEL;
      this.displayableString = "";
      this.typeId = AttributeTypeToken.SENTINEL;
   }

   public T getValue() {
      return this.value;
   }

   public GammaId getGammaId() {
      return this.gammaId;
   }

   public AttributeTypeToken getTypeId() {
      return this.typeId;
   }

   @JsonIgnore
   public String getDisplayableString() {
      return this.displayableString;
   }

   @Override
   @JsonIgnore
   public String getIdString() {
      return super.getIdString();
   }

   @Override
   @JsonIgnore
   public int getIdIntValue() {
      return super.getIdIntValue();
   }

   @Override
   public String toString() {
      if (this.value == null) {
         return "";
      }
      return this.value.toString();
   }

   @JsonIgnore()
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof AttributePojo<?>) {
         AttributePojo<?> other = (AttributePojo<?>) obj;
         if (this.getId().equals(other.getId()) && this.getTypeId().equals(
            other.getTypeId()) && this.getGammaId().equals(other.getGammaId())) {
            return true;
         }
      }
      return false;
   }

   @JsonIgnore()
   public boolean valueEquals(Object obj) {
      if (obj instanceof AttributePojo<?>) {
         AttributePojo<?> other = (AttributePojo<?>) obj;
         if (this.getValue().equals(other.getValue())) {
            return true;
         }
      }
      return false;
   }
}
