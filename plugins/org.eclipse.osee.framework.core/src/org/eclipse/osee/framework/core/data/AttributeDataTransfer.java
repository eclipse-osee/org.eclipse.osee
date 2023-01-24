/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core.data;

/**
 * @author David W. Miller
 */
public final class AttributeDataTransfer {
   private AttributeId attrId;
   private AttributeTypeId attrTypeId;
   private String attributeName;
   private String baseType;
   private String displayHint;
   private String description;
   private String extension;
   private GammaId gammaId;
   private String mediaType;
   private String multiplicity;
   private String[] validEnums;
   private String value;

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public AttributeTypeId getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(AttributeTypeId attrTypeId) {
      this.attrTypeId = attrTypeId;
   }

   public String getAttributeName() {
      return attributeName;
   }

   public void setAttributeName(String attributeName) {
      this.attributeName = attributeName;
   }

   public String getBaseType() {
      return baseType;
   }

   public void setBaseType(String baseType) {
      this.baseType = baseType;
   }

   public String getDisplayHint() {
      return displayHint;
   }

   public void setDisplayHint(String displayHint) {
      this.displayHint = displayHint;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getExtension() {
      return extension;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public String getMediaType() {
      return mediaType;
   }

   public void setMediaType(String mediaType) {
      this.mediaType = mediaType;
   }

   public String getMultiplicity() {
      return multiplicity;
   }

   public void setMultiplicity(String multiplicity) {
      this.multiplicity = multiplicity;
   }

   public String[] getValidEnums() {
      return validEnums;
   }

   public void setValidEnums(String[] validEnums) {
      this.validEnums = validEnums;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}