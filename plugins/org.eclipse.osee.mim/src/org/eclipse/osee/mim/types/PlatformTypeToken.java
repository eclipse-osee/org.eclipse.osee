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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * Class used to represent a platform type, as well as internal operations for a platform type.
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypeToken extends PLGenericDBObject {
   public static final PlatformTypeToken SENTINEL = new PlatformTypeToken();

   @OseeArtifactRequiredAttribute
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name; //required

   @OseeArtifactAttribute(attributeId = 2455059983007225803L)
   private String InterfacePlatformTypeEnumLiteral;

   @OseeArtifactAttribute(attributeId = 4026643196432874344L)
   private String InterfacePlatformTypeUnits;

   @OseeArtifactAttribute(attributeId = 2121416901992068417L)
   private String InterfacePlatformTypeValidRangeDescription;

   @OseeArtifactAttribute(attributeId = 3899709087455064782L)
   private String InterfacePlatformTypeMinval;

   @OseeArtifactAttribute(attributeId = 3899709087455064783L)
   private String InterfacePlatformTypeMaxval;

   @OseeArtifactRequiredAttribute
   @OseeArtifactAttribute(attributeId = 2455059983007225786L)
   private String InterfacePlatformTypeBitSize; //required

   @OseeArtifactAttribute(attributeId = 2886273464685805413L)
   private String InterfacePlatformTypeDefaultValue;

   @OseeArtifactAttribute(attributeId = 3899709087455064785L)
   private String InterfacePlatformTypeMsbValue;

   @OseeArtifactAttribute(attributeId = 3899709087455064786L)
   private String InterfacePlatformTypeBitsResolution;

   @OseeArtifactAttribute(attributeId = 3899709087455064787L)
   private String InterfacePlatformTypeCompRate;

   @OseeArtifactAttribute(attributeId = 3899709087455064788L)
   private String InterfacePlatformTypeAnalogAccuracy;

   @OseeArtifactRequiredAttribute
   @OseeArtifactAttribute(attributeId = 2455059983007225762L)
   private String InterfaceLogicalType; //required

   @OseeArtifactRequiredAttribute
   @OseeArtifactAttribute(attributeId = 3899709087455064784L)
   private String InterfacePlatformType2sComplement; //required

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
      this.setInterfacePlatformTypeEnumLiteral(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, ""));
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
   }

   public PlatformTypeToken() {
      super(ArtifactId.SENTINEL.getId(), "");
      // Not doing anything
   }

   /**
    * @return the interfacePlatformTypeEnumLiteral
    */
   public String getInterfacePlatformTypeEnumLiteral() {
      return InterfacePlatformTypeEnumLiteral;
   }

   /**
    * @param interfacePlatformTypeEnumLiteral the interfacePlatformTypeEnumLiteral to set
    */
   public void setInterfacePlatformTypeEnumLiteral(String interfacePlatformTypeEnumLiteral) {
      InterfacePlatformTypeEnumLiteral = interfacePlatformTypeEnumLiteral;
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

}
