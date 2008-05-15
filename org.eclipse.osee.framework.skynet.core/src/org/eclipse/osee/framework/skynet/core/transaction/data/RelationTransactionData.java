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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Jeff C. Phillips
 */
public class RelationTransactionData implements ITransactionData {
   private static final String INSERT_INTO_RELATION_TABLE =
         "INSERT INTO " + RELATION_LINK_VERSION_TABLE + " (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, a_order_value, b_order_value, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "UPDATE osee_Define_txs tx1 set tx_current = 0 WHERE EXISTS (SELECT 'x' from osee_define_tx_details td2, osee_Define_rel_link rl3 WHERE tx1.transaction_id = td2.transaction_id AND td2.branch_id = ? AND tx1.gamma_id = rl3.gamma_id AND rl3.rel_link_id = ? AND tx1.tx_current = 1)";

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
    * 
    */
   private void populateDataList() {
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getPersistenceMemo().getLinkId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getRelationType().getRelationTypeId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getArtifactA().getArtId());
      dataItems.add(SQL3DataType.INTEGER);
      dataItems.add(link.getArtifactB().getArtId());
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
      notCurrentDataItems.add(link.getPersistenceMemo().getLinkId());
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
         return ((RelationTransactionData) obj).link.getPersistenceMemo().getLinkId() == link.getPersistenceMemo().getLinkId();
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return link.getPersistenceMemo().getLinkId() * PRIME_NUMBER;
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
