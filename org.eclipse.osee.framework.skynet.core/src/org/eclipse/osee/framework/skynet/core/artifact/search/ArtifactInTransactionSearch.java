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

import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Robert A. Fisher
 */
public class ArtifactInTransactionSearch implements ISearchPrimitive {
   private final Integer fromTransactionNumber;
   private final Integer toTransactionNumber;
   private static final String TOKEN = ";";
   private static final String tables =
         TRANSACTIONS_TABLE + "," + TRANSACTION_DETAIL_TABLE + "," + ARTIFACT_VERSION_TABLE;

   public ArtifactInTransactionSearch(TransactionRecord transactionNumber) {
      this(transactionNumber, transactionNumber);
   }

   public ArtifactInTransactionSearch(TransactionRecord fromTransactionId, TransactionRecord toTransactionId) {
      if (fromTransactionId.getBranchId() != toTransactionId.getBranchId()) {
         throw new IllegalArgumentException("The fromTransactionId and toTransactionId must be on the same branch");
      }
      if (fromTransactionId.getId() > toTransactionId.getId()) {
         throw new IllegalArgumentException("The fromTransactionId can not be greater than the toTransactionId.");
      }

      this.fromTransactionNumber = fromTransactionId.getId();
      this.toTransactionNumber = toTransactionId.getId();
   }

   public String getArtIdColName() {
      return "art_id";
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String whereConditions =
            (fromTransactionNumber.equals(toTransactionNumber) ? TRANSACTIONS_TABLE.column("transaction_id") + " = ? " : TRANSACTIONS_TABLE.column("transaction_id") + " > ? " + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + " <= ?") + " AND " + TRANSACTIONS_TABLE.column("transaction_id") + "=" + TRANSACTION_DETAIL_TABLE.column("transaction_id") + " AND " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=?" + " AND " + TRANSACTIONS_TABLE.column("gamma_id") + "=" + ARTIFACT_VERSION_TABLE.column("gamma_id");

      if (!fromTransactionNumber.equals(toTransactionNumber)) {
         dataList.add(fromTransactionNumber);
      }
      dataList.add(toTransactionNumber);
      dataList.add(branch.getId());
      return whereConditions;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
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

   public String getStorageString() {
      return fromTransactionNumber + TOKEN + toTransactionNumber;
   }

   public static ArtifactInTransactionSearch getPrimitive(String storageString) throws NumberFormatException, OseeCoreException {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) {
         throw new IllegalArgumentException("Unable to parse the storage string:" + storageString);
      }

      return new ArtifactInTransactionSearch(TransactionManager.getTransactionId(Integer.parseInt(values[0])),
            TransactionManager.getTransactionId(Integer.parseInt(values[1])));
   }
}
