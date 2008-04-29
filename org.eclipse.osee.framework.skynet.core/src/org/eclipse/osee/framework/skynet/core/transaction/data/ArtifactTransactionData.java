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

/**
 * @author Jeff C. Phillips
 */
public class ArtifactTransactionData implements ITransactionData {
   private static final String INSERT_INTO_ARTIFACT_VERSION_TABLE =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + "(art_id, gamma_id, modification_id) VALUES (?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "UPDATE osee_Define_txs tx1 set tx_current = 0 WHERE EXISTS (SELECT 'x' from osee_define_tx_details td2, osee_Define_artifact_version av3 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = av3.gamma_id AND av3.art_id = ? AND tx1.tx_current = 1)";

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
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(artifact.getArtId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

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
      notCurrentDataItems.add(SQL3DataType.INTEGER);
      notCurrentDataItems.add(branch.getBranchId());
      notCurrentDataItems.add(SQL3DataType.INTEGER);
      notCurrentDataItems.add(artifact.getArtId());

      return notCurrentDataItems;
   }
}
