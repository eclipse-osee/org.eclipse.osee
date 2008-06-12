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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RelationTransactionData implements ITransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO " + RELATION_LINK_VERSION_TABLE + " (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order, b_order, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "UPDATE osee_define_txs txs1 set tx_current = 0 WHERE (txs1.transaction_id, txs1.gamma_id) = (SELECT txs2.transaction_id, txs2.gamma_id FROM osee_define_tx_details txd1, osee_Define_rel_link rl3, osee_define_txs txs2 WHERE txs2.transaction_id = txd1.transaction_id AND txd1.branch_id = ? AND txs2.gamma_id = rl3.gamma_id AND rl3.rel_link_id = ? AND txs2.tx_current = " + TxChange.CURRENT.getValue() + ")";

   private static final int PRIME_NUMBER = 7;

   private RelationLink link;
   private int gammaId;
   private int transactionId;
   private ModificationType modificationType;
   private Branch branch;
   private List<Object> dataItems = new LinkedList<Object>();
   private List<Object> notCurrentDataItems = new LinkedList<Object>();

   public RelationTransactionData(RelationLink link, int gammaId, int transactionId, ModificationType modificationType, Branch branch) {
      super();
      this.link = link;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.modificationType = modificationType;
      this.branch = branch;

      populateDataList();
   }

   /**
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   private void populateDataList() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getRelationId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getRelationType().getRelationTypeId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getAArtifactId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getBArtifactId());
      dataItems.add(SQL3DataType.VARCHAR);
      dataItems.add(link.getRationale());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getAOrder());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getBOrder());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(gammaId);
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(modificationType.getValue());

      notCurrentDataItems.add(SQL3DataType.INTEGER);
      notCurrentDataItems.add(branch.getBranchId());
      notCurrentDataItems.add(SQL3DataType.INTEGER);
      notCurrentDataItems.add(link.getRelationId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_INTO_RELATION_TABLE;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.ITransactionData#getTransactionChangeData()
    */
   public List<Object> getTransactionChangeData() {
      return dataItems;
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData#getPreviousTxNotCurrentData()
    */
   @Override
   public List<Object> getPreviousTxNotCurrentData() {
      return notCurrentDataItems;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.data.ITransactionData#setPreviousTxNotCurrentSql()
    */
   @Override
   public String setPreviousTxNotCurrentSql() {
      return SET_PREVIOUS_TX_NOT_CURRENT;
   }

}
