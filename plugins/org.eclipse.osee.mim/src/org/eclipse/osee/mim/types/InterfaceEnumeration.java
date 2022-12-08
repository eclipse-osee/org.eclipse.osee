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
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceEnumeration extends PLGenericDBObject {
   public static final InterfaceEnumeration SENTINEL = new InterfaceEnumeration();
   private ApplicabilityToken applicability = ApplicabilityToken.SENTINEL;
   private Long ordinal = 0L;
   private InterfaceEnumOrdinalType ordinalType;
   private ArtifactReadable artifactReadable = ArtifactReadable.SENTINEL;

   public InterfaceEnumeration(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceEnumeration(ArtifactReadable art) {
      super(art);
      this.setOrdinal(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceEnumOrdinal, 0L));
      this.setOrdinalType(InterfaceEnumOrdinalType.valueOf(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinalType, "LONG")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      this.artifactReadable = art;
   }

   public InterfaceEnumeration(Long id, String name) {
      super(id, name);
   }

   public InterfaceEnumeration() {
      this(Id.SENTINEL, "");
   }

   @JsonIgnore
   public String getFormattedOrdinal() {
      if (InterfaceEnumOrdinalType.HEX.equals(getOrdinalType())) {
         return "0x" + Long.toHexString(getOrdinal()).toUpperCase();
      } else {
         return getOrdinal().toString();
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
   public Long getOrdinal() {
      return ordinal;
   }

   /**
    * @param ordinal the ordinal to set
    */
   public void setOrdinal(Long ordinal) {
      this.ordinal = ordinal;
   }

   public InterfaceEnumOrdinalType getOrdinalType() {
      return ordinalType;
   }

   public void setOrdinalType(InterfaceEnumOrdinalType ordinalType) {
      this.ordinalType = ordinalType;
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
   }

   @Override
   public String toString() {
      return getFormattedOrdinal() + " = " + getName();
   }

}
