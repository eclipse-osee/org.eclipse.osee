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

import java.sql.SQLException;
import java.sql.Timestamp;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RelationTransactionData implements ITransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO osee_define_rel_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order, b_order, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "INSERT INTO osee_join_transaction(query_id, transaction_id, gamma_id, insert_time) SELECT ?, txs1.transaction_id, txs1.gamma_id, ? FROM osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE rel1.rel_link_id = ? AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs1.tx_current = " + TxChange.CURRENT.getValue();

   private static final int PRIME_NUMBER = 7;

   private RelationLink link;
   private int gammaId;
   private TransactionId transactionId;
   private ModificationType modificationType;
   private Branch branch;

   public RelationTransactionData(RelationLink link, int gammaId, TransactionId transactionId, ModificationType modificationType, Branch branch) {
      super();
      this.link = link;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof RelationTransactionData) {
         return ((RelationTransactionData) obj).link.getRelationId() == link.getRelationId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return link.getRelationId() * PRIME_NUMBER;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#setPreviousTxNotCurrent()
    */
   @Override
   public void setPreviousTxNotCurrent(Timestamp insertTime, int queryId) throws SQLException {
      ConnectionHandler.runPreparedUpdate(SET_PREVIOUS_TX_NOT_CURRENT, queryId, insertTime, link.getRelationId(),
            branch.getBranchId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#insertTransactionChange()
    */
   @Override
   public void insertTransactionChange() throws SQLException {
      ConnectionHandler.runPreparedUpdate(INSERT_INTO_RELATION_TABLE, link.getRelationId(),
            link.getRelationType().getRelationTypeId(), link.getAArtifactId(), link.getBArtifactId(),
            link.getRationale(), link.getAOrder(), link.getBOrder(), gammaId, modificationType.getValue());
   }
}