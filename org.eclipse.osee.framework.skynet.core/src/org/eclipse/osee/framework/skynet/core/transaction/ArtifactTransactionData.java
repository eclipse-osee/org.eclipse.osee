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
import java.sql.SQLException;
import java.sql.Timestamp;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData implements ITransactionData {
   private static final String INSERT_INTO_ARTIFACT_VERSION_TABLE =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + "(art_id, gamma_id, modification_id) VALUES (?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "INSERT INTO osee_join_transaction(query_id, transaction_id, gamma_id, insert_time) SELECT ?, txs1.transaction_id, txs1.gamma_id, ? FROM osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE arv1.art_id = ? AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();
   private static final int PRIME_NUMBER = 3;

   private int gammaId;
   private TransactionId transactionId;
   private ModificationType modificationType;
   private Artifact artifact;
   private Branch branch;

   public ArtifactTransactionData(Artifact artifact, int gammaId, TransactionId transactionId, ModificationType modificationType, Branch branch) {
      super();
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.artifact = artifact;
      this.branch = branch;
   }

   /**
    * should not be called by applications. Should be called exactly once - when the transaction has been committed
    * 
    * @throws SQLException
    * @throws BranchDoesNotExist
    * @throws TransactionDoesNotExist
    */
   void updateArtifact() {
      artifact.internalSetPersistenceData(gammaId, transactionId, modificationType, false);
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ArtifactTransactionData) {
         return ((ArtifactTransactionData) obj).artifact.getArtId() == artifact.getArtId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return artifact.getArtId() * PRIME_NUMBER;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getGammaId()
    */
   public int getGammaId() {
      return gammaId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionId()
    */
   public TransactionId getTransactionId() {
      return transactionId;
   }

   /**
    * @return Returns the modificationType.
    */
   public ModificationType getModificationType() {
      return modificationType;
   }

   /**
    * @param modificationType The modificationType to set.
    */
   public void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   /**
    * @return the artifact
    */
   public Artifact getArtifact() {
      return artifact;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#setPreviousTxNotCurrent()
    */
   @Override
   public void setPreviousTxNotCurrent(Timestamp insertTime, int queryId) throws SQLException {
      ConnectionHandler.runPreparedUpdate(SET_PREVIOUS_TX_NOT_CURRENT, queryId, insertTime, artifact.getArtId(),
            branch.getBranchId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#insertTransactionChange()
    */
   @Override
   public void insertTransactionChange() throws SQLException {
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_ARTIFACT_VERSION_TABLE, artifact.getArtId(), gammaId,
            modificationType.getValue());
   }
}