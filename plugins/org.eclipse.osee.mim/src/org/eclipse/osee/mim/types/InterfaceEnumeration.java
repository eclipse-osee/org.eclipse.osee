/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumeration extends ArtifactAccessorResultWithGammas {
   public static final InterfaceEnumeration SENTINEL = new InterfaceEnumeration();
   private ApplicabilityToken applicability = ApplicabilityToken.SENTINEL;
   private AttributePojo<Long> ordinal =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceEnumOrdinal, GammaId.SENTINEL, 0L, "");
   private AttributePojo<String> ordinalType = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfaceEnumOrdinalType, GammaId.SENTINEL, InterfaceEnumOrdinalType.LONG.toString(), "");

   public InterfaceEnumeration(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceEnumeration(ArtifactReadable art) {
      super(art);
      this.setOrdinal(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceEnumOrdinal, 0L)));
      this.setOrdinalType(AttributePojo.valueOf(
         art.getSoleAttribute(CoreAttributeTypes.InterfaceEnumOrdinalType, InterfaceEnumOrdinalType.LONG.toString())));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceEnumeration(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, name));
   }

   public InterfaceEnumeration() {
      this(Id.SENTINEL, "");
   }

   @JsonIgnore
   public String getFormattedOrdinal() {
      if (InterfaceEnumOrdinalType.HEX.toString().equals(getOrdinalType().getValue())) {
         return "0x" + Long.toHexString(getOrdinal().getValue()).toUpperCase();
      } else {
         return getOrdinal().getValue().toString();
      }
   }

   /**
    * @return the applicability
    */
   public ApplicabilityToken getApplicability() {
      return applicability;
   }

   /**
    * @param applicability the applicability to set
    */
   public void setApplicability(ApplicabilityToken applicability) {
      this.applicability = applicability;
   }

   /**
    * @return the ordinal
    */
   public AttributePojo<Long> getOrdinal() {
      return ordinal;
   }

   /**
    * @param ordinal the ordinal to set
    */
   public void setOrdinal(Long ordinal) {
      AttributePojo<Long> oldOrdinal = getOrdinal();
      this.ordinal = AttributePojo.valueOf(oldOrdinal.getId(), oldOrdinal.getTypeId(), oldOrdinal.getGammaId(), ordinal,
         oldOrdinal.getDisplayableString());
   }

   @JsonProperty
   public void setOrdinal(AttributePojo<Long> ordinal) {
      this.ordinal = ordinal;
   }

   public AttributePojo<String> getOrdinalType() {
      return ordinalType;
   }

   @JsonProperty
   public void setOrdinalType(AttributePojo<String> ordinalType) {
      this.ordinalType = ordinalType;
   }

   public void setOrdinalType(InterfaceEnumOrdinalType ordinalType) {
      this.ordinalType = AttributePojo.valueOf(this.ordinalType.getId(), this.ordinalType.getTypeId(),
         this.ordinalType.getGammaId(), ordinalType.toString(), this.ordinalType.getDisplayableString());
   }

   @Override
   public String toString() {
      return getFormattedOrdinal() + " = " + getName();
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.InterfaceEnumOrdinal, this.getOrdinal().getValue().toString());
      values.put(CoreAttributeTypes.InterfaceEnumOrdinalType, this.getOrdinalType().toString());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfaceEnum.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceEnum.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

   @JsonIgnore
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (obj instanceof InterfaceEnumeration) {
         InterfaceEnumeration other = ((InterfaceEnumeration) obj);
         if (!this.getName().valueEquals(other.getName())) {
            return false;
         }
         if (!this.getOrdinal().valueEquals(other.getOrdinal())) {
            return false;
         }
         if (!this.getOrdinalType().valueEquals(other.getOrdinalType())) {
            return false;
         }
         return true;
      }
      return false;

   }

}
