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
import org.eclipse.osee.framework.core.data.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * Class used to represent a platform type, as well as internal operations for a platform type.
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypeToken extends ArtifactAccessorResultWithGammas {
   public static final PlatformTypeToken SENTINEL = new PlatformTypeToken();

   private AttributePojo<String> InterfacePlatformTypeUnits =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeUnits, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeValidRangeDescription = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeMinval =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeMinval, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeMaxval =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeMaxval, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeBitSize =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeBitSize, GammaId.SENTINEL, "0", ""); //required

   private AttributePojo<String> InterfaceDefaultValue =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceDefaultValue, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeMsbValue =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeMsbValue, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeBitsResolution = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfacePlatformTypeBitsResolution, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeCompRate =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeCompRate, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfacePlatformTypeAnalogAccuracy = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy, GammaId.SENTINEL, "", "");
   private AttributePojo<String> Description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");

   private AttributePojo<String> InterfaceLogicalType =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceLogicalType, GammaId.SENTINEL, "", ""); //required

   private AttributePojo<Boolean> InterfacePlatformType2sComplement = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfacePlatformType2sComplement, GammaId.SENTINEL, false, ""); //required

   private InterfaceEnumerationSet enumSet = InterfaceEnumerationSet.SENTINEL;

   private ApplicabilityToken applicability = ApplicabilityToken.BASE;

   public PlatformTypeToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PlatformTypeToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Name, "")));
      this.setInterfaceLogicalType(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceLogicalType, "")));
      this.setInterfacePlatformType2sComplement(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformType2sComplement, false)));
      this.setInterfacePlatformTypeAnalogAccuracy(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy, "")));
      this.setinterfacePlatformTypeBitSize(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeBitSize, "")));
      this.setInterfacePlatformTypeBitsResolution(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeBitsResolution, "")));
      this.setInterfacePlatformTypeCompRate(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeCompRate, "")));
      this.setInterfaceDefaultValue(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceDefaultValue, "")));
      this.setInterfacePlatformTypeMaxval(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeMaxval, "")));
      this.setInterfacePlatformTypeMinval(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeMinval, "")));
      this.setInterfacePlatformTypeMsbValue(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeMsbValue, "")));
      this.setInterfacePlatformTypeUnits(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeUnits, "")));
      this.setInterfacePlatformTypeValidRangeDescription(AttributePojo.valueOf(
         art.getSoleAttribute(CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription, "")));
      this.setDescription(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Description, "")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      if (this.getInterfaceLogicalType().getValue().equals("enumeration") && art.getRelated(
         CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getOneOrDefault(
            ArtifactReadable.SENTINEL).isValid() && !art.getRelated(
               CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getOneOrDefault(
                  ArtifactReadable.SENTINEL).getExistingAttributeTypes().isEmpty()) {
         this.setEnumSet(new InterfaceEnumerationSet(
            art.getRelated(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getExactlyOne()));
      }
   }

   public PlatformTypeToken(Long id, String name, String logicalType, String bitSize, String minVal, String maxVal, String units) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
      this.setInterfaceLogicalType(logicalType);
      this.setinterfacePlatformTypeBitSize(bitSize);
      this.setInterfacePlatformTypeMinval(minVal);
      this.setInterfacePlatformTypeMaxval(maxVal);
      this.setInterfacePlatformTypeUnits(units);

      if (minVal.equals(Strings.EMPTY_STRING) || maxVal.equals(Strings.EMPTY_STRING)) {
         this.setInterfacePlatformTypeValidRangeDescription("Calculated");
      } else if (minVal.equals(maxVal)) {
         this.setInterfacePlatformTypeValidRangeDescription(minVal);
      } else {
         this.setInterfacePlatformTypeValidRangeDescription(minVal + "-" + maxVal);
      }
   }

   public PlatformTypeToken(Long id, String name, String logicalType, String bitSize, String minVal, String maxVal, String units, String description, String defaultValue, String validRange) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
      this.setInterfaceLogicalType(logicalType);
      this.setinterfacePlatformTypeBitSize(bitSize);
      this.setInterfacePlatformTypeMinval(minVal);
      this.setInterfacePlatformTypeMaxval(maxVal);
      this.setInterfacePlatformTypeUnits(units);
      this.setDescription(description);
      this.setInterfaceDefaultValue(defaultValue);
      this.setInterfacePlatformTypeValidRangeDescription(validRange);
      this.setApplicability(ApplicabilityToken.BASE);

   }

   public PlatformTypeToken() {
      super(ArtifactId.SENTINEL.getId(),
         AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, "", ""));
      // Not doing anything
   }

   public void setInterfacePlatformTypeValidRangeDescription(String desc) {
      this.InterfacePlatformTypeValidRangeDescription = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription, GammaId.SENTINEL, desc, "");
   }

   void setInterfacePlatformTypeUnits(String units) {
      this.InterfacePlatformTypeUnits =
         AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfacePlatformTypeUnits, GammaId.SENTINEL, units, "");
   }

   public void setInterfacePlatformTypeMaxval(String maxVal) {
      this.InterfacePlatformTypeMaxval = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeMaxval, GammaId.SENTINEL, maxVal, "");
   }

   public void setInterfacePlatformTypeMinval(String minVal) {
      this.InterfacePlatformTypeMinval = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeMinval, GammaId.SENTINEL, minVal, "");
   }

   public void setinterfacePlatformTypeBitSize(String bitSize) {
      this.InterfacePlatformTypeBitSize = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeBitSize, GammaId.SENTINEL, bitSize, "");
   }

   void setInterfaceLogicalType(String logicalType) {
      this.InterfaceLogicalType =
         AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceLogicalType, GammaId.SENTINEL, logicalType, "");
   }

   public void setInterfacePlatformTypeMsbValue(String msb) {
      this.InterfacePlatformTypeMsbValue = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeMsbValue, GammaId.SENTINEL, msb, "");
   }

   public void setInterfacePlatformTypeCompRate(String compRate) {
      this.InterfacePlatformTypeCompRate = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeCompRate, GammaId.SENTINEL, compRate, "");
   }

   public void setInterfacePlatformTypeBitsResolution(String resolution) {
      this.InterfacePlatformTypeBitsResolution = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeBitsResolution, GammaId.SENTINEL, resolution, "");
   }

   public void setInterfacePlatformTypeAnalogAccuracy(String accuracy) {
      this.InterfacePlatformTypeAnalogAccuracy = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy, GammaId.SENTINEL, accuracy, "");
   }

   public void setInterfacePlatformType2sComplement(boolean complement) {
      this.InterfacePlatformType2sComplement = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfacePlatformType2sComplement, GammaId.SENTINEL, complement, "");
   }

   public void setDescription(String desc) {
      this.Description = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, desc, "");
   }

   public void setInterfaceDefaultValue(String defaultVal) {
      this.InterfaceDefaultValue =
         AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceDefaultValue, GammaId.SENTINEL, defaultVal, "");
   }

   /**
    * @return the interfacePlatformTypeUnits
    */
   public AttributePojo<String> getInterfacePlatformTypeUnits() {
      return InterfacePlatformTypeUnits;
   }

   /**
    * @param interfacePlatformTypeUnits the interfacePlatformTypeUnits to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeUnits(AttributePojo<String> interfacePlatformTypeUnits) {
      InterfacePlatformTypeUnits = interfacePlatformTypeUnits;
   }

   /**
    * @return the interfacePlatformTypeValidRangeDescription
    */
   public AttributePojo<String> getInterfacePlatformTypeValidRangeDescription() {
      return InterfacePlatformTypeValidRangeDescription;
   }

   /**
    * @param interfacePlatformTypeValidRangeDescription the interfacePlatformTypeValidRangeDescription to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeValidRangeDescription(
      AttributePojo<String> interfacePlatformTypeValidRangeDescription) {
      InterfacePlatformTypeValidRangeDescription = interfacePlatformTypeValidRangeDescription;
   }

   /**
    * @return the interfacePlatformTypeMinval
    */
   public AttributePojo<String> getInterfacePlatformTypeMinval() {
      return InterfacePlatformTypeMinval;
   }

   /**
    * @param interfacePlatformTypeMinval the interfacePlatformTypeMinval to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeMinval(AttributePojo<String> interfacePlatformTypeMinval) {
      InterfacePlatformTypeMinval = interfacePlatformTypeMinval;
   }

   /**
    * @return the interfacePlatformTypeMaxval
    */
   public AttributePojo<String> getInterfacePlatformTypeMaxval() {
      return InterfacePlatformTypeMaxval;
   }

   /**
    * @param interfacePlatformTypeMaxval the interfacePlatformTypeMaxval to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeMaxval(AttributePojo<String> interfacePlatformTypeMaxval) {
      InterfacePlatformTypeMaxval = interfacePlatformTypeMaxval;
   }

   /**
    * @return the interfacePlatformTypeBitSize
    */
   public AttributePojo<String> getInterfacePlatformTypeBitSize() {
      return InterfacePlatformTypeBitSize;
   }

   /**
    * @param interfacePlatformTypeBitSize the interfacePlatformTypeBitSize to set
    */
   @JsonProperty
   public void setinterfacePlatformTypeBitSize(AttributePojo<String> interfacePlatformTypeBitSize) {
      InterfacePlatformTypeBitSize = interfacePlatformTypeBitSize;
   }

   /**
    * @return the InterfaceDefaultValue
    */
   public AttributePojo<String> getInterfaceDefaultValue() {
      return this.InterfaceDefaultValue;
   }

   /**
    * @param InterfaceDefaultValue the InterfaceDefaultValue to set
    */
   @JsonProperty
   public void setInterfaceDefaultValue(AttributePojo<String> interfaceDefaultValue) {
      this.InterfaceDefaultValue = interfaceDefaultValue;
   }

   /**
    * @return the interfacePlatformTypeMsbValue
    */
   public AttributePojo<String> getInterfacePlatformTypeMsbValue() {
      return InterfacePlatformTypeMsbValue;
   }

   /**
    * @param interfacePlatformTypeMsbValue the interfacePlatformTypeMsbValue to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeMsbValue(AttributePojo<String> interfacePlatformTypeMsbValue) {
      InterfacePlatformTypeMsbValue = interfacePlatformTypeMsbValue;
   }

   /**
    * @return the interfacePlatformTypeBitsResolution
    */
   public AttributePojo<String> getInterfacePlatformTypeBitsResolution() {
      return InterfacePlatformTypeBitsResolution;
   }

   /**
    * @param interfacePlatformTypeBitsResolution the interfacePlatformTypeBitsResolution to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeBitsResolution(AttributePojo<String> interfacePlatformTypeBitsResolution) {
      InterfacePlatformTypeBitsResolution = interfacePlatformTypeBitsResolution;
   }

   /**
    * @return the interfacePlatformTypeCompRate
    */
   public AttributePojo<String> getInterfacePlatformTypeCompRate() {
      return InterfacePlatformTypeCompRate;
   }

   /**
    * @param interfacePlatformTypeCompRate the interfacePlatformTypeCompRate to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeCompRate(AttributePojo<String> interfacePlatformTypeCompRate) {
      InterfacePlatformTypeCompRate = interfacePlatformTypeCompRate;
   }

   /**
    * @return the interfacePlatformTypeAnalogAccuracy
    */
   public AttributePojo<String> getInterfacePlatformTypeAnalogAccuracy() {
      return InterfacePlatformTypeAnalogAccuracy;
   }

   /**
    * @param interfacePlatformTypeAnalogAccuracy the interfacePlatformTypeAnalogAccuracy to set
    */
   @JsonProperty
   public void setInterfacePlatformTypeAnalogAccuracy(AttributePojo<String> interfacePlatformTypeAnalogAccuracy) {
      InterfacePlatformTypeAnalogAccuracy = interfacePlatformTypeAnalogAccuracy;
   }

   /**
    * @return the interfacePlatformType2sComplement
    */
   public AttributePojo<Boolean> getInterfacePlatformType2sComplement() {
      return InterfacePlatformType2sComplement;
   }

   /**
    * @param interfacePlatformType2sComplement the interfacePlatformType2sComplement to set
    */
   @JsonProperty
   public void setInterfacePlatformType2sComplement(AttributePojo<Boolean> interfacePlatformType2sComplement) {
      InterfacePlatformType2sComplement = interfacePlatformType2sComplement;
   }

   /**
    * @return the interfaceLogicalType
    */
   public AttributePojo<String> getInterfaceLogicalType() {
      return InterfaceLogicalType;
   }

   /**
    * @param interfaceLogicalType the InterfaceLogicalType to set
    */
   @JsonProperty
   public void setInterfaceLogicalType(AttributePojo<String> interfaceLogicalType) {
      InterfaceLogicalType = interfaceLogicalType;

   }

   /**
    * @return the description
    */
   public AttributePojo<String> getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   @JsonProperty
   public void setDescription(AttributePojo<String> description) {
      Description = description;
   }

   /**
    * @return the enumSet
    */
   public InterfaceEnumerationSet getEnumSet() {
      return enumSet;
   }

   /**
    * @param enumSet the enumSet to set
    */
   public void setEnumSet(InterfaceEnumerationSet enumSet) {
      this.enumSet = enumSet;
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

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription().getValue());
      values.put(CoreAttributeTypes.InterfaceLogicalType, this.getInterfaceLogicalType().getValue());
      values.put(CoreAttributeTypes.InterfacePlatformTypeBitSize, this.getInterfacePlatformTypeBitSize().getValue());
      values.put(CoreAttributeTypes.InterfacePlatformTypeMinval, this.getInterfacePlatformTypeMinval().getValue());
      values.put(CoreAttributeTypes.InterfacePlatformTypeMaxval, this.getInterfacePlatformTypeMaxval().getValue());
      values.put(CoreAttributeTypes.InterfacePlatformTypeUnits, this.getInterfacePlatformTypeUnits().getValue());
      values.put(CoreAttributeTypes.InterfaceDefaultValue, this.getInterfaceDefaultValue().getValue());
      values.put(CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription,
         this.getInterfacePlatformTypeValidRangeDescription().getValue());

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfacePlatformType.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfacePlatformType.getValidAttributeTypes()) {
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
      if (obj instanceof PlatformTypeToken) {
         PlatformTypeToken other = ((PlatformTypeToken) obj);
         if (!this.getName().valueEquals(other.getName())) {
            return false;
         }
         if (!this.getDescription().valueEquals(other.getDescription())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeUnits().valueEquals(other.getInterfacePlatformTypeUnits())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeValidRangeDescription().valueEquals(
            other.getInterfacePlatformTypeValidRangeDescription())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeMinval().valueEquals(other.getInterfacePlatformTypeMinval())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeMaxval().valueEquals(other.getInterfacePlatformTypeMaxval())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeMsbValue().valueEquals(other.getInterfacePlatformTypeMsbValue())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeBitSize().valueEquals(other.getInterfacePlatformTypeBitSize())) {
            return false;
         }
         if (!this.getInterfaceDefaultValue().valueEquals(other.getInterfaceDefaultValue())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeBitsResolution().valueEquals(
            other.getInterfacePlatformTypeBitsResolution())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeCompRate().valueEquals(other.getInterfacePlatformTypeCompRate())) {
            return false;
         }
         if (!this.getInterfacePlatformTypeAnalogAccuracy().valueEquals(
            other.getInterfacePlatformTypeAnalogAccuracy())) {
            return false;
         }
         if (!this.getInterfaceLogicalType().valueEquals(other.getInterfaceLogicalType())) {
            return false;
         }
         if (!this.getInterfacePlatformType2sComplement().valueEquals(other.getInterfacePlatformType2sComplement())) {
            return false;
         }
         if (!this.getEnumSet().equals(other.getEnumSet())) {
            return false;
         }
         return true;
      }
      return false;

   }
}
