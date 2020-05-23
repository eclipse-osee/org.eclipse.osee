/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.rest.client.internal;

import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Roberto E. Escobar
 */
public class Options implements Cloneable {
   private DeletionFlag includeDeleted;
   private TransactionId transactionId;

   public Options() {
      super();
      reset();
   }

   public void reset() {
      includeDeleted = DeletionFlag.EXCLUDE_DELETED;
      transactionId = TransactionId.SENTINEL;
   }

   public boolean areDeletedIncluded() {
      return includeDeleted.areDeletedAllowed();
   }

   public void setIncludeDeleted(boolean enabled) {
      includeDeleted = DeletionFlag.allowDeleted(enabled);
   }

   public DeletionFlag getIncludeDeleted() {
      return includeDeleted;
   }

   public void setFromTransaction(TransactionId transactionId) {
      this.transactionId = transactionId;
   }

   public TransactionId getFromTransaction() {
      return transactionId;
   }

   public void setHeadTransaction() {
      transactionId = TransactionId.SENTINEL;
   }

   public boolean isHeadTransaction() {
      return transactionId.equals(TransactionId.SENTINEL);
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