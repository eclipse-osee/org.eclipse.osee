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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Class used to represent a platform type, as well as internal operations for a platform type.
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypeToken extends PLGenericDBObject {
   public static final PlatformTypeToken SENTINEL = new PlatformTypeToken();

   private String Name; //required

   private String InterfacePlatformTypeUnits;

   private String InterfacePlatformTypeValidRangeDescription;

   private String InterfacePlatformTypeMinval;

   private String InterfacePlatformTypeMaxval;

   private String InterfacePlatformTypeBitSize; //required

   private String InterfacePlatformTypeDefaultValue;

   private String InterfacePlatformTypeMsbValue;

   private String InterfacePlatformTypeBitsResolution;

   private String InterfacePlatformTypeCompRate;

   private String InterfacePlatformTypeAnalogAccuracy;
   private String Description;

   private String InterfaceLogicalType; //required

   private String InterfacePlatformType2sComplement; //required

   private ArtifactReadable artifactReadable;
   private InterfaceEnumerationSet enumSet = InterfaceEnumerationSet.SENTINEL;

   public PlatformTypeToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PlatformTypeToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setInterfaceLogicalType(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceLogicalType, ""));
      this.setInterfacePlatformType2sComplement(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformType2sComplement, ""));
      this.setInterfacePlatformTypeAnalogAccuracy(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy, ""));
      this.setinterfacePlatformTypeBitSize(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeBitSize, ""));
      this.setInterfacePlatformTypeBitsResolution(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeBitsResolution, ""));
      this.setInterfacePlatformTypeCompRate(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeCompRate, ""));
      this.setInterfacePlatformTypeDefaultValue(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeDefaultValue, ""));
      this.setInterfacePlatformTypeMaxval(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeMaxval, ""));
      this.setInterfacePlatformTypeMinval(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeMinval, ""));
      this.setInterfacePlatformTypeMsbValue(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeMsbValue, ""));
      this.setInterfacePlatformTypeUnits(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeUnits, ""));
      this.setInterfacePlatformTypeValidRangeDescription(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription, ""));
      this.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));

      if (this.getInterfaceLogicalType().equals("enumeration") && art.getRelated(
         CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getOneOrDefault(
            ArtifactReadable.SENTINEL).isValid() && !art.getRelated(
               CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getOneOrDefault(
                  ArtifactReadable.SENTINEL).getExistingAttributeTypes().isEmpty()) {
         this.setEnumSet(new InterfaceEnumerationSet(
            art.getRelated(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getExactlyOne()));
      }
      this.artifactReadable = art;
   }

   public PlatformTypeToken(Long id, String name, String logicalType, String bitSize, String minVal, String maxVal, String units) {
      super(id, name);
      this.setInterfaceLogicalType(logicalType);
      this.setinterfacePlatformTypeBitSize(bitSize);
      this.setInterfacePlatformTypeMinval(minVal);
      this.setInterfacePlatformTypeMaxval(maxVal);
      this.setInterfacePlatformTypeUnits(units);

      if (minVal == Strings.EMPTY_STRING || maxVal == Strings.EMPTY_STRING) {
         this.setInterfacePlatformTypeValidRangeDescription("Calculated");
      } else if (minVal == maxVal) {
         this.setInterfacePlatformTypeValidRangeDescription(minVal);
      } else {
         this.setInterfacePlatformTypeValidRangeDescription(minVal + "-" + maxVal);
      }
   }

   public PlatformTypeToken() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   /**
    * @return the interfacePlatformTypeUnits
    */
   public String getInterfacePlatformTypeUnits() {
      return InterfacePlatformTypeUnits;
   }

   /**
    * @param interfacePlatformTypeUnits the interfacePlatformTypeUnits to set
    */
   public void setInterfacePlatformTypeUnits(String interfacePlatformTypeUnits) {
      InterfacePlatformTypeUnits = interfacePlatformTypeUnits;
   }

   /**
    * @return the interfacePlatformTypeValidRangeDescription
    */
   public String getInterfacePlatformTypeValidRangeDescription() {
      return InterfacePlatformTypeValidRangeDescription;
   }

   /**
    * @param interfacePlatformTypeValidRangeDescription the interfacePlatformTypeValidRangeDescription to set
    */
   public void setInterfacePlatformTypeValidRangeDescription(String interfacePlatformTypeValidRangeDescription) {
      InterfacePlatformTypeValidRangeDescription = interfacePlatformTypeValidRangeDescription;
   }

   /**
    * @return the interfacePlatformTypeMinval
    */
   public String getInterfacePlatformTypeMinval() {
      return InterfacePlatformTypeMinval;
   }

   /**
    * @param interfacePlatformTypeMinval the interfacePlatformTypeMinval to set
    */
   public void setInterfacePlatformTypeMinval(String interfacePlatformTypeMinval) {
      InterfacePlatformTypeMinval = interfacePlatformTypeMinval;
   }

   /**
    * @return the interfacePlatformTypeMaxval
    */
   public String getInterfacePlatformTypeMaxval() {
      return InterfacePlatformTypeMaxval;
   }

   /**
    * @param interfacePlatformTypeMaxval the interfacePlatformTypeMaxval to set
    */
   public void setInterfacePlatformTypeMaxval(String interfacePlatformTypeMaxval) {
      InterfacePlatformTypeMaxval = interfacePlatformTypeMaxval;
   }

   /**
    * @return the interfacePlatformTypeBitSize
    */
   public String getInterfacePlatformTypeBitSize() {
      return InterfacePlatformTypeBitSize;
   }

   /**
    * @param interfacePlatformTypeBitSize the interfacePlatformTypeBitSize to set
    */
   public void setinterfacePlatformTypeBitSize(String interfacePlatformTypeBitSize) {
      InterfacePlatformTypeBitSize = interfacePlatformTypeBitSize;
   }

   /**
    * @return the interfacePlatformTypeDefaultValue
    */
   public String getInterfacePlatformTypeDefaultValue() {
      return InterfacePlatformTypeDefaultValue;
   }

   /**
    * @param interfacePlatformTypeDefaultValue the interfacePlatformTypeDefaultValue to set
    */
   public void setInterfacePlatformTypeDefaultValue(String interfacePlatformTypeDefaultValue) {
      InterfacePlatformTypeDefaultValue = interfacePlatformTypeDefaultValue;
   }

   /**
    * @return the interfacePlatformTypeMsbValue
    */
   public String getInterfacePlatformTypeMsbValue() {
      return InterfacePlatformTypeMsbValue;
   }

   /**
    * @param interfacePlatformTypeMsbValue the interfacePlatformTypeMsbValue to set
    */
   public void setInterfacePlatformTypeMsbValue(String interfacePlatformTypeMsbValue) {
      InterfacePlatformTypeMsbValue = interfacePlatformTypeMsbValue;
   }

   /**
    * @return the interfacePlatformTypeBitsResolution
    */
   public String getInterfacePlatformTypeBitsResolution() {
      return InterfacePlatformTypeBitsResolution;
   }

   /**
    * @param interfacePlatformTypeBitsResolution the interfacePlatformTypeBitsResolution to set
    */
   public void setInterfacePlatformTypeBitsResolution(String interfacePlatformTypeBitsResolution) {
      InterfacePlatformTypeBitsResolution = interfacePlatformTypeBitsResolution;
   }

   /**
    * @return the interfacePlatformTypeCompRate
    */
   public String getInterfacePlatformTypeCompRate() {
      return InterfacePlatformTypeCompRate;
   }

   /**
    * @param interfacePlatformTypeCompRate the interfacePlatformTypeCompRate to set
    */
   public void setInterfacePlatformTypeCompRate(String interfacePlatformTypeCompRate) {
      InterfacePlatformTypeCompRate = interfacePlatformTypeCompRate;
   }

   /**
    * @return the interfacePlatformTypeAnalogAccuracy
    */
   public String getInterfacePlatformTypeAnalogAccuracy() {
      return InterfacePlatformTypeAnalogAccuracy;
   }

   /**
    * @param interfacePlatformTypeAnalogAccuracy the interfacePlatformTypeAnalogAccuracy to set
    */
   public void setInterfacePlatformTypeAnalogAccuracy(String interfacePlatformTypeAnalogAccuracy) {
      InterfacePlatformTypeAnalogAccuracy = interfacePlatformTypeAnalogAccuracy;
   }

   /**
    * @return the interfacePlatformType2sComplement
    */
   public String getInterfacePlatformType2sComplement() {
      return InterfacePlatformType2sComplement;
   }

   /**
    * @param interfacePlatformType2sComplement the interfacePlatformType2sComplement to set
    */
   public void setInterfacePlatformType2sComplement(String interfacePlatformType2sComplement) {
      InterfacePlatformType2sComplement = interfacePlatformType2sComplement;
   }

   /**
    * @return the interfaceLogicalType
    */
   public String getInterfaceLogicalType() {
      return InterfaceLogicalType;
   }

   /**
    * @param interfaceLogicalType the InterfaceLogicalType to set
    */
   public void setInterfaceLogicalType(String interfaceLogicalType) {
      InterfaceLogicalType = interfaceLogicalType;

   }

   /**
    * @return the description
    */
   public String getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      Description = description;
   }

   @JsonIgnore
   public ArtifactReadable getArtifactReadable() {
      return artifactReadable;
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

}
