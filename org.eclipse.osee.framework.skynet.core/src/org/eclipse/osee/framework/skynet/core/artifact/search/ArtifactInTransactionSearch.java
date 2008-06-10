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

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class ArtifactInTransactionSearch implements ISearchPrimitive {
   private Integer fromTransactionNumber;
   private Integer toTransactionNumber;
   private static final String TOKEN = ";";
   private static final String tables =
         TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_TABLE;

   public ArtifactInTransactionSearch(TransactionId transactionNumber) {
      this(transactionNumber, transactionNumber);
   }

   public ArtifactInTransactionSearch(TransactionId fromTransactionId, TransactionId toTransactionId) {
      if (!fromTransactionId.getBranch().equals(toTransactionId.getBranch())) throw new IllegalArgumentException(
            "The fromTransactionId and toTransactionId must be on the same branch");
      if (fromTransactionId.getTransactionNumber() > toTransactionId.getTransactionNumber()) throw new IllegalArgumentException(
            "The fromTransactionId can not be greater than the toTransactionId.");

      this.fromTransactionNumber = fromTransactionId.getTransactionNumber();
      this.toTransactionNumber = toTransactionId.getTransactionNumber();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String whereConditions =
            (fromTransactionNumber.equals(toTransactionNumber) ? TRANSACTIONS_TABLE.column("transaction_id") + " = ? " : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTIONS_TABLE.column("gamma_id") + "=" + ARTIFACT_VERSION_TABLE.column("gamma_id");

      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(SQL3DataType.INTEGER);
         dataList.add(fromTransactionNumber);
      }
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(toTransactionNumber);
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
      return whereConditions;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
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

   public static ArtifactInTransactionSearch getPrimitive(String storageString) throws NumberFormatException, SQLException, BranchDoesNotExist {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalArgumentException("Unable to parse the storage string:" + storageString);
      }

      TransactionIdManager manager = TransactionIdManager.getInstance();
      return new ArtifactInTransactionSearch(
            manager.getPossiblyEditableTransactionIfFromCache(Integer.parseInt(values[0])),
            manager.getPossiblyEditableTransactionIfFromCache(Integer.parseInt(values[1])));
   }
}
