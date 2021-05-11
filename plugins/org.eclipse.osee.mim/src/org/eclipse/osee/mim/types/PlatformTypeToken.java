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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * Class used to represent a platform type, as well as internal operations for a platform type.
 * 
 * @author Luciano T. Vaglienti
 */
public class PlatformTypeToken extends PLGenericDBObject {
   public static final PlatformTypeToken SENTINEL = new PlatformTypeToken();

   private String InterfacePlatformTypeEnumLiteral;
   private String InterfacePlatformTypeUnits;
   private String InterfacePlatformTypeValidRangeDescription;
   private String InterfacePlatformTypeMinval;
   private String InterfacePlatformTypeMaxval;
   private String InterfacePlatformTypeByteSize;
   private String InterfacePlatformTypeDefaultValue;
   private String InterfacePlatformTypeMsbValue;
   private String InterfacePlatformTypeBitsResolution;
   private String InterfacePlatformTypeCompRate;
   private String InterfacePlatformTypeAnalogAccuracy;
   private String InterfaceLogicalType;

   @JsonIgnore
   private static List<AttributeTypeId> attrType = Arrays.asList(new AttributeTypeId[] {
      CoreAttributeTypes.Name,
      CoreAttributeTypes.InterfacePlatformTypeEnumLiteral,
      CoreAttributeTypes.InterfacePlatformTypeUnits,
      CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription,
      CoreAttributeTypes.InterfacePlatformTypeMinval,
      CoreAttributeTypes.InterfacePlatformTypeMaxval,
      CoreAttributeTypes.InterfacePlatformTypeByteSize,
      CoreAttributeTypes.InterfacePlatform2sComplement,
      CoreAttributeTypes.InterfacePlatformTypeDefaultValue,
      CoreAttributeTypes.InterfacePlatformTypeMsbValue,
      CoreAttributeTypes.InterfacePlatformTypeBitsResolution,
      CoreAttributeTypes.InterfacePlatformTypeCompRate,
      CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy,
      CoreAttributeTypes.InterfaceLogicalType});

   public PlatformTypeToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public PlatformTypeToken(ArtifactReadable art) {
      this(art.getId(), art.getAttributes().iterator());
   }

   private PlatformTypeToken(Long id, Iterator<? extends AttributeReadable<Object>> iterator) {
      this();
      this.setId(id);
      while (iterator.hasNext()) {
         AttributeReadable<Object> next = iterator.next();
         String idString = next.getAttributeType().getIdString();
         if (idString.equals((PlatformTypeToken.getAttributeTypes().get(3).getIdString()))) {
            this.setInterfacePlatformTypeValidRangeDescription(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(6).getIdString()))) {
            this.setInterfacePlatformTypeByteSize(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(5).getIdString()))) {
            this.setInterfacePlatformTypeMaxval(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(4).getIdString()))) {
            this.setInterfacePlatformTypeMinval(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(0).getIdString()))) {
            this.setName(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(13).getIdString()))) {
            this.setInterfaceLogicalType(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(9).getIdString()))) {
            this.setInterfacePlatformTypeMsbValue(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(2).getIdString()))) {
            this.setInterfacePlatformTypeUnits(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(11).getIdString()))) {
            this.setInterfacePlatformTypeCompRate(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(10).getIdString()))) {
            this.setInterfacePlatformTypeBitsResolution(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(12).getIdString()))) {
            this.setInterfacePlatformTypeAnalogAccuracy(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(1).getIdString()))) {
            this.setInterfacePlatformTypeEnumLiteral(next.getDisplayableString());
         } else if (idString.equals((PlatformTypeToken.getAttributeTypes().get(8).getIdString()))) {
            this.setInterfacePlatformTypeDefaultValue(next.getDisplayableString());
         } else {
         }
      }
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, interfacePlatformTypeEnumLiteral);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeUnits, interfacePlatformTypeUnits);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription,
         interfacePlatformTypeValidRangeDescription);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeMinval, interfacePlatformTypeMinval);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeMaxval, interfacePlatformTypeMaxval);
   }

   /**
    * @return the interfacePlatformTypeByteSize
    */
   public String getInterfacePlatformTypeByteSize() {
      return InterfacePlatformTypeByteSize;
   }

   /**
    * @param interfacePlatformTypeByteSize the interfacePlatformTypeByteSize to set
    */
   public void setInterfacePlatformTypeByteSize(String interfacePlatformTypeByteSize) {
      InterfacePlatformTypeByteSize = interfacePlatformTypeByteSize;
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeByteSize, interfacePlatformTypeByteSize);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeDefaultValue, interfacePlatformTypeDefaultValue);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeMsbValue, interfacePlatformTypeMsbValue);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeBitsResolution,
         interfacePlatformTypeBitsResolution);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeCompRate, interfacePlatformTypeCompRate);
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
      this.attributeLookup.put(CoreAttributeTypes.InterfacePlatformTypeAnalogAccuracy,
         interfacePlatformTypeAnalogAccuracy);
   }

   @Override
   @JsonIgnore
   /**
    * Validates the platform type for missing required elements in a rest call
    *
    * @return errors that occurred, empty string = no errors.
    */
   public String getErrors() {
      String result = "";
      if (this.getInterfaceLogicalType() == "" || this.getInterfaceLogicalType() == null) {
         result = "PlatformType must have a valid logical type.";
      }
      if (this.getName() == "" || this.getName() == null) {
         result = "PlatformType must have a valid name.";
      }
      if (this.getInterfacePlatformTypeByteSize() == "" || this.getInterfacePlatformTypeByteSize() == null) {
         result = "PlatformType must have a valid byte size.";
      }
      return result;
   }

   public String getInterfaceLogicalType() {
      return InterfaceLogicalType;
   }

   public void setInterfaceLogicalType(String interfaceLogicalType) {
      InterfaceLogicalType = interfaceLogicalType;
   }

   @JsonIgnore
   public static List<AttributeTypeId> getAttributeTypes() {
      return attrType;
   }

}
