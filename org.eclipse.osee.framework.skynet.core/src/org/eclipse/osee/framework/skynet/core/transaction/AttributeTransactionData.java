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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;

/**
 * @author Jeff C. Phillips
 */
public class AttributeTransactionData implements ITransactionData {
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO osee_define_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri, modification_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

   private static final String SET_PREVIOUS_TX_NOT_CURRENT =
         "UPDATE osee_define_txs txs1 SET tx_current = 0 WHERE (txs1.transaction_id, txs1.gamma_id) = (SELECT txs2.transaction_id, txs2.gamma_id from osee_define_tx_details txd1, osee_define_txs txs2, osee_define_attribute at3 WHERE txs2.transaction_id = txd1.transaction_id AND txs2.gamma_id = at3.gamma_id AND txd1.branch_id = ? AND at3.attr_id = ? AND txs2.tx_current = " + TxChange.CURRENT.getValue() + ")";

   private static final int PRIME_NUMBER = 5;

   private int artId;
   private int attrId;
   private int attrTypeId;
   private String value;
   private int gammaId;
   private TransactionId transactionId;
   private String uri;
   private ModificationType modificationType;
   private Branch branch;
   private List<Object> dataItems = new LinkedList<Object>();
   private List<Object> notCurrentDataItems = new LinkedList<Object>();

   public AttributeTransactionData(int artId, int attrId, int attrTypeId, String value, int gammaId, TransactionId transactionId, String uri, ModificationType modificationType, Branch branch) {
      super();
      this.artId = artId;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
      this.value = value;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.uri = uri;
      this.modificationType = modificationType;
      this.branch = branch;

      populateDataList();
   }

   /**
    * 
    */
   private void populateDataList() {
      dataItems.add(artId);
      dataItems.add(attrId);
      dataItems.add(attrTypeId);
      dataItems.add(value == null ? SQL3DataType.VARCHAR : value);
      dataItems.add(gammaId);
      dataItems.add(uri == null ? SQL3DataType.VARCHAR : uri);
      dataItems.add(modificationType.getValue());

      notCurrentDataItems.add(branch.getBranchId());
      notCurrentDataItems.add(attrId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.transaction.TransactionData#getTransactionChangeSql()
    */
   public String getTransactionChangeSql() {
      return INSERT_ATTRIBUTE;
   }

   /* (non-Javadoc)
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
      if (obj instanceof AttributeTransactionData) {
         return ((AttributeTransactionData) obj).attrId == attrId;
      }
      return false;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return attrId * PRIME_NUMBER;
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
    * @return the artId
    */
   public int getArtId() {
      return artId;
   }

   public String getUri() {
      return uri;
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
