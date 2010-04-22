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
import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadChangeDataOperation extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select txs.transaction_id, gamma_id, mod_type from osee_txs txs where txs.branch_id = ? and txs.tx_current <> ? and txs.transaction_id <> ?";

   private static final String SELECT_SOURCE_TRANSACTION_CHANGES =
         "select gamma_id, mod_type from osee_txs txs where txs.branch_id = ? and txs.transaction_id = ?";

   private final HashMap<Integer, ChangeItem> artifactChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> relationChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> attributeChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Long, ModificationType> changeByGammaId = new HashMap<Long, ModificationType>();

   private final Collection<ChangeItem> changeData;
   private final TransactionRecord sourceTransaction;
   private final TransactionRecord destinationTransaction;
   private final TransactionRecord mergeTransaction;
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;

   private static enum LoadingMode {
      FROM_SINGLE_TRANSACTION,
      FROM_ALL_BRANCH_TRANSACTIONS;
   }

   private final LoadingMode loadChangesEnum;

   public LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionDelta txDelta, Collection<ChangeItem> changeData) {
      this(oseeDatabaseProvider, txDelta, null, changeData, LoadingMode.FROM_SINGLE_TRANSACTION);
   }

   public LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionDelta txDelta, TransactionRecord mergeTransaction, Collection<ChangeItem> changeData) {
      this(oseeDatabaseProvider, txDelta, mergeTransaction, changeData, LoadingMode.FROM_ALL_BRANCH_TRANSACTIONS);
   }

   private LoadChangeDataOperation(IOseeDatabaseServiceProvider oseeDatabaseProvider, TransactionDelta txDelta, TransactionRecord mergeTransaction, Collection<ChangeItem> changeData, LoadingMode loadMode) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.mergeTransaction = mergeTransaction;
      this.sourceTransaction = txDelta.getStartTx();
      this.destinationTransaction = txDelta.getEndTx();
      this.changeData = changeData;
      this.loadChangesEnum = loadMode;
   }

   private int getSourceBranchId() {
      return sourceTransaction.getBranchId();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      TransactionJoinQuery txJoin = loadSourceBranchChanges(monitor);
      loadArtifactItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), artifactChangesByItemId);
      loadAttributeItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), attributeChangesByItemId);
      loadRelationItemIdsBasedOnGammas(monitor, txJoin.getQueryId(), relationChangesByItemId);
      txJoin.delete();

      loadByItemId(monitor, "osee_arts", "art_id", artifactChangesByItemId, null);
      loadByItemId(monitor, "osee_attribute", "attr_id", attributeChangesByItemId, "value");
      loadByItemId(monitor, "osee_relation_link", "rel_link_id", relationChangesByItemId, "rationale");

      changeData.addAll(artifactChangesByItemId.values());
      changeData.addAll(attributeChangesByItemId.values());
      changeData.addAll(relationChangesByItemId.values());
   }

   private TransactionJoinQuery loadSourceBranchChanges(IProgressMonitor monitor) throws OseeCoreException {
      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();

      try {
         switch (loadChangesEnum) {
            case FROM_ALL_BRANCH_TRANSACTIONS:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
                     TxChange.NOT_CURRENT.getValue(), sourceTransaction.getBranch().getBaseTransaction().getId());
               break;
            case FROM_SINGLE_TRANSACTION:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_TRANSACTION_CHANGES, getSourceBranchId(),
                     sourceTransaction.getId());
               break;
            default:
               throw new UnsupportedOperationException(String.format("Invalid load changes [%s] mode not supported",
                     loadChangesEnum));
         }

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            txJoin.add(chStmt.getLong("gamma_id"), -1);
            changeByGammaId.put(chStmt.getLong("gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type")));
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
            "select art_id, art_type_id, txj.gamma_id from osee_arts id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int artId = chStmt.getInt("art_id");
            int artTypeId = chStmt.getInt("art_type_id");

            long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = changeByGammaId.get(gammaId);

            ArtifactChangeItem changeItem = new ArtifactChangeItem(artId, artTypeId, gammaId, modType);
            changesByItemId.put(changeItem.getItemId(), changeItem);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadAttributeItemIdsBasedOnGammas(IProgressMonitor monitor, int queryId, HashMap<Integer, ChangeItem> changesByItemId) throws OseeDataStoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query =
            "select art_id, attr_id, value, attr_type_id, txj.gamma_id from osee_attribute id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int attrId = chStmt.getInt("attr_id");
            int attrTypeId = chStmt.getInt("attr_type_id");
            int artId = chStmt.getInt("art_id");

            long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = changeByGammaId.get(gammaId);

            String value = chStmt.getString("value");

            AttributeChangeItem changeItem =
                  new AttributeChangeItem(attrId, attrTypeId, artId, gammaId, modType, value);

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
            int relLinkId = chStmt.getInt("rel_link_id");
            int relTypeId = chStmt.getInt("rel_link_type_id");

            long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = changeByGammaId.get(gammaId);

            int aArtId = chStmt.getInt("a_art_id");
            int bArtId = chStmt.getInt("b_art_id");
            String rationale = chStmt.getString("rationale");

            RelationChangeItem changeItem =
                  new RelationChangeItem(relLinkId, relTypeId, gammaId, modType, aArtId, bArtId, rationale);

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
         loadCurrentData(monitor, tableName, columnName, idJoin, changesByItemId, mergeTransaction);
      }

      loadCurrentData(monitor, tableName, columnName, idJoin, changesByItemId, destinationTransaction);

      if (loadChangesEnum == LoadingMode.FROM_ALL_BRANCH_TRANSACTIONS) {
         loadNonCurrentSourceData(monitor, tableName, columnName, idJoin, changesByItemId, columnValueName);
      }

      idJoin.delete();
   }

   private boolean hasMergeBranch() {
      return mergeTransaction != null;
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, TransactionRecord transactionLimit) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query;

      try {
         switch (loadChangesEnum) {
            case FROM_ALL_BRANCH_TRANSACTIONS:
               query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                     + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
                     " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.branch_id = ? and txs.transaction_id <= ?";

               chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
                     transactionLimit.getBranchId(), transactionLimit.getId());
               break;
            case FROM_SINGLE_TRANSACTION:
               query = "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                     + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
                     " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?";

               chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), transactionLimit.getBranchId(),
                     transactionLimit.getId());
               break;
            default:
               throw new UnsupportedOperationException(String.format("Invalid load changes [%s] mode not supported",
                     loadChangesEnum));
         }

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int itemId = chStmt.getInt(columnName);

            Long gammaId = chStmt.getLong("gamma_id");
            ChangeItem change = changesByItemId.get(itemId);

            if (transactionLimit.getBranch().getBranchType().isMergeBranch()) {
               change.getNetChange().setGammaId(gammaId);
               change.getNetChange().setModType(ModificationType.MERGED);
            } else {
               change.getDestinationVersion().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestinationVersion().setGammaId(gammaId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String idColumnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      String query;

      try {
         String valueColumnName = columnValueName != null ? "item." + columnValueName + "," : "";
         query =
               "select " + valueColumnName + "item." + idColumnName + ", txs.gamma_id, txs.mod_type, txs.transaction_id from osee_join_id idj, " //
                     + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + idColumnName + //
                     " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.branch_id = ? order by idj.id, txs.transaction_id asc";

         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               getSourceBranchId());

         int baselineTransactionId = sourceTransaction.getBranch().getBaseTransaction().getId();
         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int itemId = chStmt.getInt(idColumnName);
            Integer transactionId = chStmt.getInt("transaction_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            Long gammaId = chStmt.getLong("gamma_id");

            String value = null;
            if (columnValueName != null) {
               value = chStmt.getString(columnValueName);
            }

            ChangeItem change = changesByItemId.get(itemId);
            if (previousItemId != itemId) {
               isFirstSet = false;
            }
            if (baselineTransactionId == transactionId) {
               loadVersionData(change.getBaselineVersion(), gammaId, modType, value);
            } else if (!isFirstSet) {
               loadVersionData(change.getFirstNonCurrentChange(), gammaId, modType, value);
               isFirstSet = true;
            }

            previousItemId = itemId;
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadVersionData(ChangeVersion versionedChange, Long gammaId, ModificationType modType, String value) {
      // Tolerates the case of having more than one version of an item on a
      // baseline transaction by picking the most recent one
      if (versionedChange.getGammaId() == null || versionedChange.getGammaId().compareTo(gammaId) < 0) {
         versionedChange.setValue(value);
         versionedChange.setModType(modType);
         versionedChange.setGammaId(gammaId);
      }
   }
}