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
package org.eclipse.osee.framework.skynet.core.commit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadChangeDataOperation extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select txs.transaction_id, gamma_id, mod_type from osee_txs txs, osee_tx_details txd where txd.branch_id = ? and txd.transaction_id = txs.transaction_id and txd.tx_type = ? and txs.tx_current <> ?";

   private static final String SELECT_SOURCE_TRANSACTION_CHANGES =
         "select txs.transaction_id, gamma_id, mod_type from osee_txs txs, osee_tx_details txd where txd.transaction_id = ? and txd.transaction_id = txs.transaction_id and txd.tx_type = ?";

   private final HashMap<Integer, ChangeItem> artifactChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> relationChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Integer, ChangeItem> attributeChangesByItemId = new HashMap<Integer, ChangeItem>();
   private final HashMap<Long, Pair<Integer, ModificationType>> changeByGammaId =
         new HashMap<Long, Pair<Integer, ModificationType>>();

   private final Collection<ChangeItem> changeData;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final Branch mergeBranch;
   private final Integer transactionNumber;

   private static enum LoadingMode {
      FROM_SINGLE_TRANSACTION, FROM_ALL_BRANCH_TRANSACTIONS;
   }

   private final LoadingMode loadChangesEnum;

   public LoadChangeDataOperation(Integer transactionNumber, Collection<ChangeItem> changeData) {
      this(null, null, null, changeData, transactionNumber, LoadingMode.FROM_SINGLE_TRANSACTION);
   }

   public LoadChangeDataOperation(Branch sourceBranch, Collection<ChangeItem> changeData) {
      this(sourceBranch, null, null, changeData);
   }

   public LoadChangeDataOperation(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<ChangeItem> changeData) {
      this(sourceBranch, destinationBranch, mergeBranch, changeData, null, LoadingMode.FROM_ALL_BRANCH_TRANSACTIONS);
   }

   private LoadChangeDataOperation(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, Collection<ChangeItem> changeData, Integer transactionNumber, LoadingMode loadMode) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.mergeBranch = mergeBranch;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.changeData = changeData;
      this.loadChangesEnum = loadMode;
      this.transactionNumber = transactionNumber;
   }

   private int getSourceBranchId() {
      return sourceBranch.getBranchId();
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
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      Integer currentTransactionNumber;

      try {
         switch (loadChangesEnum) {
            case FROM_ALL_BRANCH_TRANSACTIONS:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
                     TransactionDetailsType.NonBaselined.getId(), TxChange.NOT_CURRENT.getValue());
               currentTransactionNumber =
                     Integer.valueOf(TransactionIdManager.getlatestTransactionForBranch(getSourceBranchId()).getTransactionNumber());
               break;
            case FROM_SINGLE_TRANSACTION:
               chStmt.runPreparedQuery(10000, SELECT_SOURCE_TRANSACTION_CHANGES, transactionNumber,
                     TransactionDetailsType.NonBaselined.getId());
               currentTransactionNumber = transactionNumber;
               break;
            default:
               throw new UnsupportedOperationException(String.format("Invalid load changes [%s] mode not supported",
                     loadChangesEnum));
         }

         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            txJoin.add(chStmt.getLong("gamma_id"), -1);
            changeByGammaId.put(chStmt.getLong("gamma_id"), new Pair<Integer, ModificationType>(
                  currentTransactionNumber,
                  ModificationType.getMod(chStmt.getInt("mod_type"))));
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
      return txJoin;
   }

   private void loadArtifactItemIdsBasedOnGammas(IProgressMonitor monitor, int queryId, HashMap<Integer, ChangeItem> changesByItemId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
         loadCurrentData(monitor, tableName, columnName, idJoin, mergeBranch, changesByItemId);
      }

      if (hasDestinationBranch()) {
         loadCurrentData(monitor, tableName, columnName, idJoin, destinationBranch, changesByItemId);
      }

      if (hasSourceBranch()) {
         loadNonCurrentSourceData(monitor, tableName, columnName, idJoin, changesByItemId, columnValueName);
      }

      idJoin.delete();
   }

   private boolean hasMergeBranch() {
      return mergeBranch != null;
   }

   private boolean hasDestinationBranch() {
      return destinationBranch != null;
   }

   private boolean hasSourceBranch() {
      return sourceBranch != null;
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, Branch branch, HashMap<Integer, ChangeItem> changesByItemId) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      String query =
            "select txs.transaction_id, txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                  + tableName + " item, osee_txs txs, osee_tx_details txd where idj.query_id = ? and idj.id = item." + columnName + //
                  " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               branch.getBranchId());
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int itemId = chStmt.getInt(columnName);
            Long gammaId = chStmt.getLong("gamma_id");
            Integer transactionId = chStmt.getInt("transaction_id");
            ChangeItem change = changesByItemId.get(itemId);

            if (branch.getBranchType().isMergeBranch()) {
               change.getNet().setTransactionNumber(transactionId);
               change.getNet().setGammaId(gammaId);
               change.getNet().setModType(ModificationType.MERGED);
            } else {
               change.getDestination().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestination().setGammaId(gammaId);
               change.getDestination().setTransactionNumber(transactionId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, ChangeItem> changesByItemId, String columnValueName) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
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
               previousItemId = itemId;
               if (isBaseline) {
                  loadVersionData(chStmt, change.getBase(), columnValueName);
               } else {
                  loadVersionData(chStmt, change.getFirst(), columnValueName);
                  isFirstSet = true;
               }
            } else {
               if (!isFirstSet) {
                  loadVersionData(chStmt, change.getFirst(), columnValueName);
                  isFirstSet = true;
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadVersionData(ConnectionHandlerStatement chStmt, ChangeVersion versionedChange, String columnValueName) throws OseeArgumentException, OseeDataStoreException {
      if (columnValueName != null) {
         versionedChange.setValue(chStmt.getString(columnValueName));
      }

      versionedChange.setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
      versionedChange.setGammaId(chStmt.getLong("gamma_id"));
      versionedChange.setTransactionNumber(chStmt.getInt("transaction_id"));
   }
}