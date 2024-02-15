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

import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ConflictType;

public class AttributeMergeData {
   private AttributeTypeToken attrType;
   private AttributeId attrId;
   private String[] values;
   private String[] uris;
   private ConflictType conflictType;
   private ConflictStatus conflictStatus;
   private Long conflictId;
   public AttributeMergeData(Long conflictId, ConflictType conflictType, ConflictStatus conflictStatus, AttributeTypeToken attrType, AttributeId attrId, String[] values, String[] uris) {
      this.setConflictId(conflictId);
      this.setConflictType(conflictType);
      this.setConflictStatus(conflictStatus);
      this.setAttrType(attrType);
      this.setAttrId(attrId);
      this.setValues(values);
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public String[] getValues() {
      return values;
   }

   public void setValues(String[] values) {
      this.values = values;
   }

   public ConflictType getConflictType() {
      return conflictType;
   }

   public void setConflictType(ConflictType conflictType) {
      this.conflictType = conflictType;
   }

   public ConflictStatus getConflictStatus() {
      return conflictStatus;
   }

   public void setConflictStatus(ConflictStatus conflictStatus) {
      this.conflictStatus = conflictStatus;
   }

   public Long getConflictId() {
      return conflictId;
   }

   public void setConflictId(Long conflictId) {
      this.conflictId = conflictId;
   }

   public String[] getUris() {
      return uris;
   }

   public void setUris(String[] uris) {
      this.uris = uris;
   }
}
