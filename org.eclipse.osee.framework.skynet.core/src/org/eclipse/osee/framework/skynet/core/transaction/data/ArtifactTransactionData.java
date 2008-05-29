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
package org.eclipse.osee.framework.skynet.core.transaction.data;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData implements ITransactionData {
   private static final String INSERT_INTO_ARTIFACT_VERSION_TABLE =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + "(art_id, gamma_id, modification_id) VALUES (?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "UPDATE osee_Define_txs txs1 set tx_current = 0 WHERE (txs1.transaction_id, txs1.gamma_id) = (SELECT txs2.transaction_id, txs2.gamma_id from osee_define_tx_details txd1, osee_Define_artifact_version av3, osee_define_txs txs2 WHERE txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs2.gamma_id = av3.gamma_id AND av3.art_id = ? AND txs2.tx_current = " + TxChange.CURRENT.getValue() + ")";

   private static final int PRIME_NUMBER = 3;

   private List<Object> dataItems = new LinkedList<Object>();
   private List<Object> notCurrentDataItems = new LinkedList<Object>();
   private int gammaId;
   private int transactionId;
   private ModificationType modificationType;
   private Artifact artifact;
   private Branch branch;

   public ArtifactTransactionData(Artifact artifact, int gammaId, int transactionId, ModificationType modificationType, Branch branch) {
      super();
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.artifact = artifact;
      this.branch = branch;

      populateDataList();
   }

   /**
    * 
    */
   private void populateDataList() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artifact.getArtId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

      notCurrentDataItems.add(SQL3DataType.INTEGER);
      notCurrentDataItems.add(branch.getBranchId());
      notCurrentDataItems.add(SQL3DataType.INTEGER);
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
   public int getTransactionId() {
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
