/*********************************************************************
 * Copyright (c) 2022 Boeing
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

/**
 * Class used to represent a platform type, as well as internal operations for a platform type.
 *
 * @author Luciano T. Vaglienti
 */
public class PlatformTypeImportToken extends PLGenericDBObject {
   public static final PlatformTypeImportToken SENTINEL = new PlatformTypeImportToken();

   private String InterfacePlatformTypeUnits;

   private String InterfacePlatformTypeMinval;

   private String InterfacePlatformTypeMaxval;

   private String InterfacePlatformTypeBitSize;

   private String InterfaceDefaultValue;

   private String Description;

   private String InterfaceLogicalType;

   private String InterfacePlatformTypeValidRangeDescription;

   private InterfaceEnumerationSet enumSet;

   public PlatformTypeImportToken(Long id, String name, InterfaceEnumerationSet enumSet) {
      super(id, name);
      setInterfaceDefaultValue("");
      this.enumSet = enumSet;
   }

   public PlatformTypeImportToken(Long id, String name, String logicalType, String bitSize, String minVal, String maxVal, String units, String description, String defaultValue, String validRange) {
      super(id, name);
      setInterfaceLogicalType(logicalType);
      setinterfacePlatformTypeBitSize(bitSize);
      setInterfacePlatformTypeMinval(minVal);
      setInterfacePlatformTypeMaxval(maxVal);
      setInterfacePlatformTypeUnits(units);
      setInterfaceDefaultValue(defaultValue);
      setDescription(description);
      setInterfacePlatformTypeValidRangeDescription(validRange);
   }

   public PlatformTypeImportToken() {
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
    * @return the InterfaceDefaultValue
    */
   public String getInterfaceDefaultValue() {
      return InterfaceDefaultValue;
   }

   /**
    * @param InterfaceDefaultValue the InterfaceDefaultValue to set
    */
   public void setInterfaceDefaultValue(String interfaceDefaultValue) {
      InterfaceDefaultValue = interfaceDefaultValue;
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

   @JsonIgnore
   public InterfaceEnumerationSet getEnumSet() {
      return enumSet;
   }

   public void setEnumSet(InterfaceEnumerationSet enumSet) {
      this.enumSet = enumSet;
   }

}
