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
package org.eclipse.osee.framework.core.applicability;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.BaseId;

public class FeatureAttribute extends BaseId {

   private String value = "";
   private AttributeTypeId attributeType = AttributeTypeId.SENTINEL;
   private GammaId gammaId = GammaId.SENTINEL;

   public FeatureAttribute(Long id, String value, AttributeTypeId typeId, GammaId gammaId) {
      super(id);
      this.setValue(value);
      this.setAttributeType(typeId);
      this.setGammaId(gammaId);
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
    * @return the typeId
    */
   public AttributeTypeId getAttributeType() {
      return attributeType;
   }

   /**
    * @param typeId the typeId to set
    */
   public void setAttributeType(AttributeTypeId typeId) {
      this.attributeType = typeId;
   }

   /**
    * @return the gammaId
    */
   public GammaId getGammaId() {
      return gammaId;
   }

   /**
    * @param gammaId the gammaId to set
    */
   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
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

}
