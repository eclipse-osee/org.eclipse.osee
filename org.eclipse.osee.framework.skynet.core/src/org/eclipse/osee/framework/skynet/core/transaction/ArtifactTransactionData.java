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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData extends BaseTransactionData {
   private static final String INSERT_SQL =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + "(art_id, gamma_id, modification_id) VALUES (?,?,?)";

   private static final String SELECT_PREVIOUS_TX_NOT_CURRENT =
         "SELECT txs1.transaction_id, txs1.gamma_id FROM osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE arv1.art_id = ? AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();

   private final Artifact artifact;

   public ArtifactTransactionData(Artifact artifact, int gammaId, TransactionId transactionId, ModificationType modificationType, Branch branch) {
      super(artifact.getArtId(), gammaId, transactionId, modificationType);
      this.artifact = artifact;
   }

   /**
    * should not be called by applications. Should be called exactly once - when the transaction has been committed
    */
   void internalUpdate() {
      artifact.internalSetPersistenceData(getGammaId(), getTransactionId(), getModificationType(), false);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#internalClearDirtyState()
    */
   @Override
   void internalClearDirtyState() {
      super.internalClearDirtyState();
      artifact.setNotDirty();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertSql()
    */
   @Override
   public String getInsertSql() {
      return INSERT_SQL;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getSelectTxNotCurrentSql()
    */
   @Override
   public String getSelectTxNotCurrentSql() {
      return SELECT_PREVIOUS_TX_NOT_CURRENT;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.BaseTransactionData#getInsertData()
    */
   @Override
   public Object[] getInsertData() {
      return new Object[] {getItemId(), getGammaId(), getModificationType().getValue()};
   }
}