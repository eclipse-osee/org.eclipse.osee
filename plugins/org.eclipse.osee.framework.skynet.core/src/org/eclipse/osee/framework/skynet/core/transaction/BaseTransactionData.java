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

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public abstract class BaseTransactionData {

   public static interface InsertDataCollector {

      TransactionId getTransaction();

      BranchId getBranch();

      void internalAddInsertToBatch(int insertPriority, String insertSql, Object... data);
   }

   private static final String GAMMA_ID_SEQ = "SKYNET_GAMMA_ID_SEQ";
   private static final String INSERT_INTO_TRANSACTION_TABLE =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) VALUES (?, ?, ?, ?, ?, ?)";

   private final Id itemId;
   private ModificationType modificationType;
   private Integer gammaId;
   private ApplicabilityId applicabilityId;

   public BaseTransactionData(Id itemId, ModificationType modificationType) {
      this.modificationType = modificationType;
      this.itemId = itemId;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BaseTransactionData) {
         return itemId.equals(((BaseTransactionData) obj).itemId);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return itemId.hashCode();
   }

   protected void addInsertToBatch(InsertDataCollector collector) {
      ModificationType modTypeToStore = getAdjustedModificationType();

      internalAddInsertToBatch(collector, Integer.MAX_VALUE, INSERT_INTO_TRANSACTION_TABLE, collector.getTransaction(),
         getGammaId(), modTypeToStore, TxChange.getCurrent(modTypeToStore), collector.getBranch(), getApplicId());
      //TODO: remove hack defaulting to 1
   }

   //Replaces the replace_with_version modtype with modification for storage.
   private ModificationType getAdjustedModificationType() {
      ModificationType modtypeToReturn = getModificationType();
      if (modtypeToReturn == ModificationType.REPLACED_WITH_VERSION) {
         modtypeToReturn = ModificationType.MODIFIED;
      }
      return modtypeToReturn;
   }

   protected final Id getItemId() {
      return itemId;
   }

   protected final ModificationType getModificationType() {
      return modificationType;
   }

   protected final ApplicabilityId getApplicId() {
      if (applicabilityId == null) {
         applicabilityId = getApplicabilityId();
      }
      return applicabilityId;
   }

   protected final int getGammaId() {
      if (gammaId == null) {
         gammaId = createGammaId();
      }
      return gammaId;
   }

   final void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   protected abstract OseeSql getSelectTxNotCurrentSql();

   /**
    * Should be called by child classes during their implementation of addInsertToBatch.
    */
   protected final void internalAddInsertToBatch(InsertDataCollector collector, int insertPriority, String insertSql, Object... data) {
      collector.internalAddInsertToBatch(insertPriority, insertSql, data);
   }

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   protected abstract void internalUpdate(TransactionRecord transactionId);

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   protected abstract void internalClearDirtyState();

   /**
    * Should not be called by application. This should only be called once if there was an error committing the
    * transaction.
    */
   protected abstract void internalOnRollBack();

   /**
    * Should not be called by application. This method will be called by the base class when required;
    */
   protected abstract int createGammaId();

   /**
    * Should not be called by application. This method will be called by the base class when required;
    */
   protected abstract ApplicabilityId getApplicabilityId();

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    *
    * @param artifactEvent TODO
    */
   protected abstract void internalAddToEvents(ArtifactEvent artifactEvent);

   protected int getNextGammaIdFromSequence() {
      return (int) ConnectionHandler.getNextSequence(GAMMA_ID_SEQ, true);
   }
}
