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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public abstract class BaseTransactionData {

   private static final int PRIME_NUMBER = 37;
   private final int gammaId;
   private final TransactionId transactionId;
   private final int itemId;
   private ModificationType modificationType;

   public BaseTransactionData(int itemId, int gammaId, TransactionId transactionId, ModificationType modificationType) {
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.itemId = itemId;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BaseTransactionData) {
         BaseTransactionData data = (BaseTransactionData) obj;
         return data.itemId == this.itemId && data.getClass().equals(this.getClass());
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return itemId * PRIME_NUMBER * this.getClass().hashCode();
   }

   public abstract Object[] getInsertData();

   public abstract String getInsertSql();

   public Object[] getSelectData() {
      return new Object[] {itemId, transactionId.getBranchId()};
   }

   public abstract String getSelectTxNotCurrentSql();

   final int getItemId() {
      return itemId;
   }

   final int getGammaId() {
      return gammaId;
   }

   final TransactionId getTransactionId() {
      return transactionId;
   }

   final ModificationType getModificationType() {
      return modificationType;
   }

   public final void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   void internalUpdate() {
      // Used to update backing data - client must override to get this functionality
   }

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   void internalClearDirtyState() {
      // Used to clear dirty flags from backing data - client must override to get this functionality
   }

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   void internalOnRollBack() throws OseeCoreException {
      // Used to perform rollback operations on backing data - client must override to get this functionality
   }
}
