/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Roberto E. Escobar
 */
public class Options implements Cloneable {
   private static final int TRANSACTION_SENTINEL = -1;

   private DeletionFlag includeDeleted;
   private int transactionId;

   public Options() {
      super();
      reset();
   }

   public void reset() {
      includeDeleted = DeletionFlag.EXCLUDE_DELETED;
      transactionId = TRANSACTION_SENTINEL;
   }

   public boolean areDeletedIncluded() {
      return includeDeleted.areDeletedAllowed();
   }

   public void setIncludeDeleted(boolean enabled) {
      includeDeleted = DeletionFlag.allowDeleted(enabled);
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

   public boolean isHistorical() {
      return !isHeadTransaction();
   }

   @Override
   public Options clone() {
      Options clone = new Options();
      clone.includeDeleted = this.includeDeleted;
      clone.transactionId = this.transactionId;
      return clone;
   }

   @Override
   public String toString() {
      return "Options [includeDeleted=" + includeDeleted + ", transactionId=" + transactionId + "]";
   }

}
