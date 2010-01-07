/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.change;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.data.ArtifactChangeItem;
import org.eclipse.osee.framework.core.data.AttributeChangeItem;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.RelationChangeItem;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadChangeDataOperation extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select txs.transaction_id, gamma_id, mod_type from osee_tx_details txd, osee_txs txs where txd.branch_id = ? and txd.tx_type = ? and txd.transaction_id = txs.transaction_id and txs.tx_current <> ?";

   private static final String SELECT_SOURCE_TRANSACTION_CHANGES =
         "select gamma_id, mod_type from osee_txs txs where txs.branch_id = ? and txs.transaction_id = ?";

   private final HashMap<Integer, ChangeItem> artifactChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> relationChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> attributeChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Long, Pair<Integer, ModificationType>> changeByGammaId =
         new HashMap<Long, Pair<Integer, ModificationType>>();

   private final Collection<ChangeItem> changeData;
   private final TransactionRecord sourceTransactionId;
   private final TransactionRecord destinationTransactionId;
   private final TransactionRecord mergeTransactionId;
   private final TransactionRecord transactionNumber;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;

   private static enum LoadingMode {
      FROM_SINGLE_TRANSACTION,
      FROM_ALL_BRANCH_TRANSACTIONS;
   }

   private final LoadingMode loadChangesEnum;

   public LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionRecord sourceTransaction, TransactionRecord destinationTransactionId, Collection<ChangeItem> changeData) {
      this(oseeDatabaseProvider, null, destinationTransactionId, null, changeData, sourceTransaction,
            LoadingMode.FROM_SINGLE_TRANSACTION);
   }

   public LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionRecord sourceBranch, TransactionRecord destinationBranch, TransactionRecord mergeBranch, Collection<ChangeItem> changeData) {
      this(oseeDatabaseProvider, sourceBranch, destinationBranch, mergeBranch, changeData, null,
            LoadingMode.FROM_ALL_BRANCH_TRANSACTIONS);
   }

   private LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionRecord sourceTransactionId, TransactionRecord destinationTransactionId, TransactionRecord mergeTransactionId, Collection<ChangeItem> changeData, TransactionRecord transactionNumber, LoadingMode loadMode) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.mergeTransactionId = mergeTransactionId;
      this.sourceTransactionId = sourceTransactionId;
      this.destinationTransactionId = destinationTransactionId;
      this.changeData = changeData;
      this.loadChangesEnum = loadMode;
      this.transactionNumber = transactionNumber;
   }

   private int getSourceBranchId() {
      return sourceTransactionId.getBranchId();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      TransactionJoinQuery txJoin = loadSourceBranchChanges(monitor);
      loadArtifactItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), artifactChangesByItemId);
      loadAttributeItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), attributeChangesByItemId);
      loadRelationItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), relationChangesByItemId);
      txJoin.delete();

      loadByItemId(monitor, "osee_artifact_version", "art_id", artifactChangesByItemId, null);
      loadByItemId(monitor, "osee_attribute", "attr_id", attributeChangesByItemId, "value");
      loadByItemId(monitor, "osee_relation_link", "rel_link_id", relationChangesByItemId, "rationale");

      changeData.addAll(artifactChangesByItemId.values());
      changeData.addAll(attributeChangesByItemId.values());
      changeData.addAll(relationChangesByItemId.values());
   }

   private TransactionJoinQuery loadSourceBranchChanges(IProgressMonitor monitor) throws OseeCoreException {
      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      Integer currentTransactionNumber;

      try {
         switch (loadChangesEnum) {
            case FROM_ALL_BRANCH_TRANSACTIONS:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
                     TransactionDetailsType.NonBaselined.getId(), TxChange.NOT_CURRENT.getValue());
               currentTransactionNumber = sourceTransactionId.getId();
               break;
            case FROM_SINGLE_TRANSACTION:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_TRANSACTION_CHANGES, transactionNumber.getBranchId(),
                     transactionNumber.getId());
               currentTransactionNumber = transactionNumber.getId();
               break;
            default:
               throw new UnsupportedOperationException(String.format("Invalid load changes [%s] mode not supported",
                     loadChangesEnum));
         }

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            txJoin.add(chStmt.getLong("gamma_id"), -1);
            changeByGammaId.put(chStmt.getLong("gamma_id"), new Pair<Integer, ModificationType>(
                  currentTransactionNumber, ModificationType.getMod(chStmt.getInt("mod_type"))));
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
      return txJoin;
   }

   private void loadArtifactItemIdsBasedOnGammas(IProgressMonitor monitor, int queryId, HashMap<Integer, ChangeItem> changesByItemId) throws OseeDataStoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query =
            "select art_id, txj.gamma_id from osee_artifact_version id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            Pair<Integer, ModificationType> txsTableData = changeByGammaId.get(chStmt.getLong("gamma_id"));
            ArtifactChangeItem changeItem =
                  new ArtifactChangeItem(chStmt.getLong("gamma_id"), txsTableData.getSecond(), txsTableData.getFirst(),
                        chStmt.getInt("art_id"));
            changesByItemId.put(changeItem.getItemId(), changeItem);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadAttributeItemIdsBasedOnGammas(IProgressMonitor monitor, int queryId, HashMap<Integer, ChangeItem> changesByItemId) throws OseeDataStoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query =
            "select art_id, attr_id, value, txj.gamma_id from osee_attribute id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            Pair<Integer, ModificationType> txsTableData = changeByGammaId.get(chStmt.getLong("gamma_id"));
            AttributeChangeItem changeItem =
                  new AttributeChangeItem(chStmt.getLong("gamma_id"), txsTableData.getSecond(),
                        txsTableData.getFirst(), chStmt.getInt("attr_id"), chStmt.getInt("art_id"),
                        chStmt.getString("value"));

            changesByItemId.put(changeItem.getItemId(), changeItem);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadRelationItemIdsBasedOnGammas(IProgressMonitor monitor, int queryId, HashMap<Integer, ChangeItem> changesByItemId) throws OseeDataStoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query =
            "select a_art_id, b_art_id, rel_link_id, rel_link_type_id, rationale, txj.gamma_id from osee_relation_link id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            Pair<Integer, ModificationType> txsTableData = changeByGammaId.get(chStmt.getLong("gamma_id"));
            RelationChangeItem changeItem =
                  new RelationChangeItem(chStmt.getLong("gamma_id"), txsTableData.getSecond(), txsTableData.getFirst(),
                        chStmt.getInt("a_art_id"), chStmt.getInt("b_art_id"), chStmt.getInt("rel_link_id"),
                        chStmt.getInt("rel_link_type_id"), chStmt.getString("rationale"));

            changesByItemId.put(changeItem.getItemId(), changeItem);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadByItemId(IProgressMonitor monitor, String tableName, String columnName, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      for (Entry<Integer, ChangeItem> entry : changesByItemId.entrySet()) {
         idJoin.add(entry.getKey());
      }
      idJoin.store();

      if (hasMergeBranch()) {
         loadCurrentData(monitor, tableName, columnName, idJoin, mergeTransactionId, changesByItemId);
      }

      if (hasDestinationBranch()) {
         loadCurrentData(monitor, tableName, columnName, idJoin, destinationTransactionId, changesByItemId);
      }

      if (hasSourceBranch()) {
         loadNonCurrentSourceData(monitor, tableName, columnName, idJoin, changesByItemId, columnValueName);
      }

      idJoin.delete();
   }

   private boolean hasMergeBranch() {
      return mergeTransactionId != null;
   }

   private boolean hasDestinationBranch() {
      return destinationTransactionId != null;
   }

   private boolean hasSourceBranch() {
      return sourceTransactionId != null;
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, TransactionRecord destinationTransaction, HashMap<Integer, ChangeItem> changesByItemId) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query;

      try {
         switch (loadChangesEnum) {
            case FROM_ALL_BRANCH_TRANSACTIONS:
               query =
                     "select txs.transaction_id, txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                           + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
                           " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

               chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
                     destinationTransaction.getBranchId(), destinationTransaction.getId());
               break;
            case FROM_SINGLE_TRANSACTION:
               query =
                     "select txs.transaction_id, txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                           + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
                           " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?";

               chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), destinationTransaction.getBranchId(),
                     destinationTransaction.getId());
               break;
            default:
               throw new UnsupportedOperationException(String.format("Invalid load changes [%s] mode not supported",
                     loadChangesEnum));
         }

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int itemId = chStmt.getInt(columnName);

            Long gammaId = chStmt.getLong("gamma_id");
            Integer transactionId = chStmt.getInt("transaction_id");
            ChangeItem change = changesByItemId.get(itemId);

            if (destinationTransaction.getBranch().getBranchType().isMergeBranch()) {
               change.getNetChange().setTransactionNumber(transactionId);
               change.getNetChange().setGammaId(gammaId);
               change.getNetChange().setModType(ModificationType.MERGED);
            } else {
               change.getDestinationVersion().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestinationVersion().setGammaId(gammaId);
               change.getDestinationVersion().setTransactionNumber(transactionId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query;

      try {
         query =
               "select " + (columnValueName != null ? "item." + columnValueName + ", item." + columnName : "item." + columnName) + ", txs.gamma_id, txs.mod_type, txd.tx_type, txs.transaction_id from osee_join_id idj, " //
                     + tableName + " item, osee_txs txs, osee_tx_details txd where idj.query_id = ? and idj.id = item." + columnName + //
                     " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ? order by idj.id, txs.transaction_id asc";

         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               getSourceBranchId());

         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);

            int itemId = chStmt.getInt(columnName);
            boolean isBaseline =
                  TransactionDetailsType.toEnum(chStmt.getInt("tx_type")) == TransactionDetailsType.Baselined;
            ChangeItem change = changesByItemId.get(itemId);

            if (previousItemId != itemId) {
               isFirstSet = false;
            }

            if (isBaseline) {
               loadVersionData(chStmt, change.getBaselineVersion(), columnValueName);
            } else if (!isFirstSet) {
               loadVersionData(chStmt, change.getFirstNonCurrentChange(), columnValueName);
               isFirstSet = true;
            }

            previousItemId = itemId;
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadVersionData(IOseeStatement chStmt, ChangeVersion versionedChange, String columnValueName) throws OseeArgumentException, OseeDataStoreException {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId() < chStmt.getLong("gamma_id")) {
         if (columnValueName != null) {
            versionedChange.setValue(chStmt.getString(columnValueName));
         }

         versionedChange.setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
         versionedChange.setGammaId(chStmt.getLong("gamma_id"));
         versionedChange.setTransactionNumber(chStmt.getInt("transaction_id"));
      }
   }
}