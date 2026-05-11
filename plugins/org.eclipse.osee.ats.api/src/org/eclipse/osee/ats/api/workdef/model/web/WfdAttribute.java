/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.Multiplicity.MultiplicityToken;

/**
 * @author Donald G. Dunne
 */
public class WfdAttribute {

   @JsonSerialize(using = ToStringSerializer.class)
   public Long attrId;
   public AttributeTypeToken attrType;
   public GammaId gammaId = GammaId.SENTINEL;
   public String value;
   private MultiplicityToken multiplicity;
   private List<String> enumOptions = new ArrayList<>();

   public WfdAttribute() {
      // for jax-rs
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public Long getAttrId() {
      return attrId;
   }

   public void setAttrId(Long attrId) {
      this.attrId = attrId;
   }

   public List<String> getEnumOptions() {
      return enumOptions;
   }

   public void setEnumOptions(List<String> enumOptions) {
      this.enumOptions = enumOptions;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public MultiplicityToken getMultiplicity() {
      return multiplicity;
   }

   public void setMultiplicity(MultiplicityToken multiplicity) {
      this.multiplicity = multiplicity;
   }

}
