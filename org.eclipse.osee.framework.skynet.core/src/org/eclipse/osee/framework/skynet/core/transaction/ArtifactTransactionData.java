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
import java.util.LinkedList;
import java.util.List;
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
         "UPDATE osee_define_txs txs1 SET tx_current = 0 WHERE (txs1.transaction_id, txs1.gamma_id) = " + "(SELECT txs2.transaction_id, txs2.gamma_id from osee_define_tx_details txd1, osee_define_txs txs2, osee_Define_artifact_version at3 " + "WHERE txs2.transaction_id = txd1.transaction_id AND txs2.gamma_id = at3.gamma_id " + "AND txd1.branch_id = ? AND at3.art_id = ? AND txs2.tx_current = " + TxChange.CURRENT.getValue() + ")";

   private static final int PRIME_NUMBER = 3;

   private List<Object> dataItems = new LinkedList<Object>();
   private List<Object> notCurrentDataItems = new LinkedList<Object>();
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

      populateDataList();
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

   /**
    * 
    */
   private void populateDataList() {
      dataItems.add(artifact.getArtId());
      dataItems.add(gammaId);
      dataItems.add(modificationType.getValue());

      notCurrentDataItems.add(branch.getBranchId());
      notCurrentDataItems.add(artifact.getArtId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_INTO_ARTIFACT_VERSION_TABLE;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      return dataItems;
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
    * @see org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData#getUpdatePreviousCurrentSql()
    */
   @Override
   public String setPreviousTxNotCurrentSql() {
      return SET_PREVIOUS_TX_NOT_CURRENT;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData#getPreviousTx_NotCurrentData()
    */
   @Override
   public List<Object> getPreviousTxNotCurrentData() {
      return notCurrentDataItems;
   }
}
