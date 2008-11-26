/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.handler;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Roberto E. Escobar
 */
public final class BranchData implements Cloneable {
   public static final String BRANCH_NAME = "branch_name";
   public static final String BRANCH_ID = "branch_id";
   private static final String BRANCH_TYPE = "branch_type";
   public static final String COMMIT_ART_ID = "associated_art_id";
   private static final String IS_ARCHIVED_BRANCH = "archived";
   private static final String BRANCH_SHORT_NAME = "short_name";
   public static final String PARENT_BRANCH_ID = "parent_branch_id";
   public static final String PARENT_TRANSACTION_ID = "parent_transaction_id";

   private final Map<String, Object> backingData;

   BranchData() {
      this.backingData = new HashMap<String, Object>();
   }

   void setData(String key, Object object) {
      this.backingData.put(key, object);
   }

   public String getBranchName() {
      return (String) backingData.get(BRANCH_NAME);
   }

   public Integer getBranchType() {
      return (Integer) backingData.get(BRANCH_TYPE);
   }

   public int getAssociatedArtId() {
      return (Integer) backingData.get(COMMIT_ART_ID);
   }

   public int getIsArchived() {
      return (Integer) backingData.get(IS_ARCHIVED_BRANCH);
   }

   public int getBranchId() {
      return (Integer) backingData.get(BRANCH_ID);
   }

   public String getShortName() {
      return (String) backingData.get(BRANCH_SHORT_NAME);
   }

   public int getParentBranchId() {
      return (Integer) backingData.get(PARENT_BRANCH_ID);
   }

   public int getParentTransactionId() {
      return (Integer) backingData.get(PARENT_TRANSACTION_ID);
   }

   public Object[] toArray(MetaData metadata) {
      return DataToSql.toDataArray(metadata, backingData);
   }

   public BranchData clone() {
      BranchData clone = new BranchData();
      for (String key : this.backingData.keySet()) {
         clone.setData(key, this.backingData.get(key));
      }
      return clone;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public String toString() {
      return String.format("(name[%s] id[%s])", getBranchName(), getBranchId());
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj == this) return true;
      if (!(obj instanceof BranchData)) return false;
      BranchData other = (BranchData) obj;
      boolean keysMatch = Collections.setComplement(this.backingData.keySet(), other.backingData.keySet()).isEmpty();
      if (!keysMatch) return false;
      boolean valuesMatch = true;
      for (String key : this.backingData.keySet()) {
         Object obj1 = this.backingData.get(key);
         Object obj2 = other.backingData.get(key);
         if (obj1 == null && obj2 != null || obj1 != null && obj2 == null || (obj1 != null && obj2 != null && !obj1.equals(obj2))) {
            valuesMatch = false;
            break;
         }
      }
      return valuesMatch;
   }

   public void setBranchId(int nextSeqVal) {
      this.backingData.put(BRANCH_ID, nextSeqVal);
   }

   public void setParentBranchId(int nextSeqVal) {
      this.backingData.put(PARENT_BRANCH_ID, nextSeqVal);
   }

   public void setParentTransactionId(int nextSeqVal) {
      this.backingData.put(PARENT_TRANSACTION_ID, nextSeqVal);
   }

   public void setAssociatedBranchId(int nextSeqVal) {
      this.backingData.put(COMMIT_ART_ID, nextSeqVal);
   }

   public void setBranchType(BranchType branchType) {
      this.backingData.put(BRANCH_TYPE, branchType.ordinal());
   }
}