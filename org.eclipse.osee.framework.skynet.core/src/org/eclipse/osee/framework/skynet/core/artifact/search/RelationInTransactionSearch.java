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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;

/**
 * @author Robert A. Fisher
 */
public class RelationInTransactionSearch implements ISearchPrimitive {
   private Integer fromTransactionNumber;
   private Integer toTransactionNumber;
   private Integer branchId;
   private static final String sql = "";
   private static final String TOKEN = ";";

   public RelationInTransactionSearch(TransactionId transactionId) {
      this(transactionId, transactionId);
   }

   public RelationInTransactionSearch(TransactionId fromTransactionId, TransactionId toTransactionId) {
      if (!fromTransactionId.getBranch().equals(toTransactionId.getBranch())) throw new IllegalArgumentException(
            "The fromTransactionId and toTransactionId must be on the same branch");
      if (fromTransactionId.getTransactionNumber() > toTransactionId.getTransactionNumber()) throw new IllegalArgumentException(
            "The fromTransactionId can not be greater than the toTransactionId.");

      this.fromTransactionNumber = fromTransactionId.getTransactionNumber();
      this.toTransactionNumber = toTransactionId.getTransactionNumber();
      this.branchId = fromTransactionId.getBranch().getBranchId();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "t1.art_id";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      String transactionCheck =
            fromTransactionNumber.equals(toTransactionNumber) ? TRANSACTIONS_TABLE.column("transaction_id") + " = ?" : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?";
      String tables =
            "(SELECT " + RELATION_LINK_VERSION_TABLE.columns("a_art_id") + " AS art_id " + " FROM " + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " UNION ALL SELECT " + RELATION_LINK_VERSION_TABLE.columns("b_art_id") + " AS art_id " + " FROM " + RELATION_LINK_VERSION_TABLE + "," + TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + " WHERE " + RELATION_LINK_VERSION_TABLE.column("gamma_id") + "=" + TRANSACTIONS_TABLE.column("gamma_id") + " AND " + transactionCheck + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + ") t1";
      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(SQL3DataType.INTEGER);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branchId);
      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(SQL3DataType.INTEGER);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branchId);

      return tables;
   }

   @Override
   public String toString() {
      if (fromTransactionNumber.equals(toTransactionNumber))
         return "Transaction Number: " + toTransactionNumber;
      else
         return "Transactions: " + fromTransactionNumber + " to " + toTransactionNumber;
   }

   public String getStorageString() {
      return fromTransactionNumber + TOKEN + toTransactionNumber;
   }

   public static RelationInTransactionSearch getPrimitive(String storageString) throws NumberFormatException, SQLException {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) throw new IllegalArgumentException("Unable to parse the storage string:" + storageString);

      TransactionIdManager manager = TransactionIdManager.getInstance();
      return new RelationInTransactionSearch(
            manager.getPossiblyEditableTransactionIfFromCache(Integer.parseInt(values[0])),
            manager.getPossiblyEditableTransactionIfFromCache(Integer.parseInt(values[1])));
   }
}
