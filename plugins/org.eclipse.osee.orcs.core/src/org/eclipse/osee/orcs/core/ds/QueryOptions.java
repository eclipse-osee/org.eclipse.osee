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
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public class QueryOptions implements Cloneable {

   private static final int TRANSACTION_SENTINEL = -1;

   private boolean includeCache;
   private boolean includeDeleted;
   private boolean includeTypeInheritance;
   private int transactionId;

   public QueryOptions() {
      reset();
   }

   public void reset() {
      includeCache = true;
      includeDeleted = false;
      includeTypeInheritance = true;
      transactionId = TRANSACTION_SENTINEL;
   }

   public void setFromTransaction(int transactionId) {
      this.transactionId = transactionId;
      if (transactionId < -1) {
         this.transactionId = TRANSACTION_SENTINEL;
      }
   }

   public int getFromTransaction() {
      return transactionId;
   }

   public void setHeadTransaction() {
      transactionId = TRANSACTION_SENTINEL;
   }

   public boolean isHeadTransaction() {
      return TRANSACTION_SENTINEL == getFromTransaction();
   }

   public boolean isCacheIncluded() {
      return includeCache;
   }

   public boolean isTypeInheritanceIncluded() {
      return includeTypeInheritance;
   }

   public boolean areDeletedIncluded() {
      return includeDeleted;
   }

   public void setIncludeCache(boolean enabled) {
      includeCache = enabled;
   }

   public void setIncludeDeleted(boolean enabled) {
      includeDeleted = enabled;
   }

   public void setIncludeTypeInheritance(boolean enabled) {
      includeTypeInheritance = enabled;
   }

   public boolean isHistorical() {
      return !isHeadTransaction();
   }

   @Override
   public String toString() {
      return "QueryOptions [includeCache=" + includeCache + ", includeDeleted=" + includeDeleted + ", includeTypeInheritance=" + includeTypeInheritance + ", transactionId=" + transactionId + "]";
   }

   @Override
   public QueryOptions clone() {
      QueryOptions clone = new QueryOptions();
      clone.includeCache = this.includeCache;
      clone.includeDeleted = this.includeDeleted;
      clone.includeTypeInheritance = this.includeTypeInheritance;
      clone.transactionId = this.transactionId;
      return clone;
   }
}
