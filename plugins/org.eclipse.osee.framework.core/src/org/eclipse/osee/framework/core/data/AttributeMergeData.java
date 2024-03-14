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

package org.eclipse.osee.framework.core.data;

public class AttributeMergeData {
   private final AttributeTypeToken attrType;
   private final AttributeId attrId;
   private final String attrTypeName;
   private final String sourceValue;
   private final String mergeValue;
   private final String destValue;
   private final String sourceUri;
   private final String mergeUri;
   private final String destUri;
   private final String sourceGammaId;
   private final String destGammaId;

   public AttributeMergeData(AttributeTypeToken attrType, AttributeId attrId, String sourceValue, String mergeValue, String destValue, String sourceUri, String mergeUri, String destUri, String sourceGammaId, String destGammaId) {
      this.attrType = attrType;
      this.attrId = attrId;
      this.attrTypeName = attrType.getName();
      this.sourceValue = sourceValue;
      this.mergeValue = mergeValue;
      this.destValue = destValue;
      this.sourceUri = sourceUri;
      this.mergeUri = mergeUri;
      this.destUri = destUri;
      this.sourceGammaId = sourceGammaId;
      this.destGammaId = destGammaId;
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public String getAttrTypeName() {
      return attrTypeName;
   }

   public String getSourceValue() {
      return sourceValue;
   }

   public String getMergeValue() {
      return mergeValue;
   }

   public String getDestValue() {
      return destValue;
   }

   public String getSourceUri() {
      return sourceUri;
   }

   public String getMergeUri() {
      return mergeUri;
   }

   public String getDestUri() {
      return destUri;
   }

   public String getSourceGammaId() {
      return sourceGammaId;
   }

   public String getDestGammaId() {
      return destGammaId;
   }

   public String getStoreType() {
      return attrType.getStoreType();
   }

}
