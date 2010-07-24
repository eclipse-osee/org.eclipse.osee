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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Robert A. Fisher
 */
public class RelationInTransactionSearch implements ISearchPrimitive {
   private final Integer fromTransactionNumber;
   private final Integer toTransactionNumber;
   private final Integer branchId;
   private static final String sql = "";
   private static final String TOKEN = ";";

   public RelationInTransactionSearch(TransactionRecord transactionId) {
      this(transactionId, transactionId);
   }

   public RelationInTransactionSearch(TransactionRecord fromTransactionId, TransactionRecord toTransactionId) {
      if (fromTransactionId.getBranchId() != toTransactionId.getBranchId()) {
         throw new IllegalArgumentException("The fromTransactionId and toTransactionId must be on the same branch");
      }
      if (fromTransactionId.getId() > toTransactionId.getId()) {
         throw new IllegalArgumentException("The fromTransactionId can not be greater than the toTransactionId.");
      }

      this.fromTransactionNumber = fromTransactionId.getId();
      this.toTransactionNumber = toTransactionId.getId();
      this.branchId = fromTransactionId.getBranchId();
   }

   @Override
   public String getArtIdColName() {
      return "t1.art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return sql;
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      String transactionCheck =
         fromTransactionNumber.equals(toTransactionNumber) ? "txs.transaction_id = ?" : "txs.transaction_id > ? AND txs.transaction_id <= ?";
      String tables =
         "(SELECT rel.a_art_id AS art_id FROM osee_relation_link rel, osee_txs txs WHERE rel.gamma_id = txs.gamma_id AND " + transactionCheck + " AND txs.branch_id = ? UNION ALL SELECT rel.b_art_id AS art_id FROM osee_relation_link rel, osee_txs txs WHERE rel.gamma_id = txs.gamma_id AND " + transactionCheck + " AND txs.branch_id=?) t1";
      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
      dataList.add(branchId);
      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
      dataList.add(branchId);

      return tables;
   }

   @Override
   public String toString() {
      if (fromTransactionNumber.equals(toTransactionNumber)) {
         return "Transaction Number: " + toTransactionNumber;
      } else {
         return "Transactions: " + fromTransactionNumber + " to " + toTransactionNumber;
      }
   }

   public static RelationInTransactionSearch getPrimitive(String storageString) throws NumberFormatException, OseeCoreException {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalArgumentException("Unable to parse the storage string:" + storageString);
      }

      return new RelationInTransactionSearch(TransactionManager.getTransactionId(Integer.parseInt(values[0])),
         TransactionManager.getTransactionId(Integer.parseInt(values[1])));
   }

   @Override
   public String getStorageString() {
      return fromTransactionNumber + TOKEN + toTransactionNumber;
   }
}
