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

import java.util.HashMap;
import java.util.List;
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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.commit.CommitItem.GammaKind;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 */
public class LoadChangeDataOperation extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select gamma_id, mod_type from osee_txs txs, osee_tx_details txd where txd.branch_id = ? and txd.transaction_id = txs.transaction_id and txd.tx_type = ? and txs.tx_current <> ?";

   private final HashMap<Integer, CommitItem> artifactChangesByItemId = new HashMap<Integer, CommitItem>();
   private final HashMap<Integer, CommitItem> relationChangesByItemId = new HashMap<Integer, CommitItem>();
   private final HashMap<Integer, CommitItem> attributeChangesByItemId = new HashMap<Integer, CommitItem>();
   private final HashMap<Long, CommitItem> changeByGammaId = new HashMap<Long, CommitItem>();

   private final List<CommitItem> changeData;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final Branch mergeBranch;

   public LoadChangeDataOperation(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, List<CommitItem> changeData) {
      super("Load Change Data", Activator.PLUGIN_ID);
      this.mergeBranch = mergeBranch;
      this.sourceBranch = sourceBranch;
      this.destinationBranch = destinationBranch;
      this.changeData = changeData;
   }

   private int getSourceBranchId() {
      return sourceBranch.getBranchId();
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      TransactionJoinQuery txJoin = loadSourceBranchChanges(monitor);
      loadItemIdsBasedOnGammas(monitor, "osee_artifact_version", "art_id", txJoin.getQueryId(), GammaKind.Artifact,
            artifactChangesByItemId);
      loadItemIdsBasedOnGammas(monitor, "osee_attribute", "attr_id", txJoin.getQueryId(), GammaKind.Attribute,
            attributeChangesByItemId);
      loadItemIdsBasedOnGammas(monitor, "osee_relation_link", "rel_link_id", txJoin.getQueryId(), GammaKind.Relation,
            relationChangesByItemId);
      txJoin.delete();

      loadByItemId(monitor, "osee_artifact_version", "art_id", artifactChangesByItemId);
      loadByItemId(monitor, "osee_attribute", "attr_id", attributeChangesByItemId);
      loadByItemId(monitor, "osee_relation_link", "rel_link_id", relationChangesByItemId);

      changeData.addAll(artifactChangesByItemId.values());
      changeData.addAll(attributeChangesByItemId.values());
      changeData.addAll(relationChangesByItemId.values());

   }

   private TransactionJoinQuery loadSourceBranchChanges(IProgressMonitor monitor) throws OseeDataStoreException, OseeArgumentException {
      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, getSourceBranchId(),
               TransactionDetailsType.NonBaselined.getId(), TxChange.NOT_CURRENT.getValue());
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            txJoin.add(chStmt.getLong("gamma_id"), -1);

            CommitItem oseeChange =
                  new CommitItem(chStmt.getLong("gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type")));

            changeByGammaId.put(oseeChange.getCurrent().getGammaId(), oseeChange);
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
      return txJoin;
   }

   private void loadItemIdsBasedOnGammas(IProgressMonitor monitor, String tableName, String idColumnName, int queryId, GammaKind kind, HashMap<Integer, CommitItem> changesByItemId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      String query =
            "select txj.gamma_id, " + idColumnName + " from " + tableName + " id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            CommitItem change = changeByGammaId.get(chStmt.getLong("gamma_id"));
            change.setKind(kind);
            change.setItemId(chStmt.getInt(idColumnName));
            changesByItemId.put(change.getItemId(), change);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadByItemId(IProgressMonitor monitor, String tableName, String columnName, HashMap<Integer, CommitItem> changesByItemId) throws OseeCoreException {
      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      for (Entry<Integer, CommitItem> entry : changesByItemId.entrySet()) {
         idJoin.add(entry.getKey());
      }
      idJoin.store();

      if (mergeBranch != null) {
         loadCurrentData(monitor, tableName, columnName, idJoin, mergeBranch, changesByItemId);
      }
      loadCurrentData(monitor, tableName, columnName, idJoin, destinationBranch, changesByItemId);
      loadNonCurrentSourceData(monitor, tableName, columnName, idJoin, changesByItemId);

      idJoin.delete();
   }

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, Branch branch, HashMap<Integer, CommitItem> changesByItemId) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      String query =
            "select txs.gamma_id, txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                  + tableName + " item, osee_txs txs, osee_tx_details txd where idj.query_id = ? and idj.id = item." + columnName + //
                  " and item.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               branch.getBranchId());
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            int itemId = chStmt.getInt(columnName);
            long gammaId = chStmt.getLong("gamma_id");
            CommitItem change = changesByItemId.get(itemId);

            if (branch.isMergeBranch()) {
               change.getNet().setGammaId(gammaId);
               change.getNet().setModType(ModificationType.MERGED);
            } else {
               change.getDestination().setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.getDestination().setGammaId(gammaId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, CommitItem> changesByItemId) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      String query =
            "select item." + columnName + ", txs.gamma_id, txs.mod_type, txd.tx_type from osee_join_id idj, " //
                  + tableName + " item, osee_txs txs, osee_tx_details txd where idj.query_id = ? and idj.id = item." + columnName + //
                  " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ? order by idj.id, txs.transaction_id asc";

      try {
         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               getSourceBranchId());
         int previousItemId = -1;
         boolean isFirstSet = false;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);

            int itemId = chStmt.getInt(columnName);
            boolean isBaseline =
                  TransactionDetailsType.toEnum(chStmt.getInt("tx_type")) == TransactionDetailsType.Baselined;
            CommitItem change = changesByItemId.get(itemId);

            if (previousItemId != itemId) {
               isFirstSet = false;
               previousItemId = itemId;
               if (isBaseline) {
                  loadVersionData(chStmt, change.getBase());
               } else {
                  loadVersionData(chStmt, change.getFirst());
                  isFirstSet = true;
               }
            } else {
               if (!isFirstSet) {
                  loadVersionData(chStmt, change.getFirst());
                  isFirstSet = true;
               }
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadVersionData(ConnectionHandlerStatement chStmt, ChangePair changePair) throws OseeArgumentException, OseeDataStoreException {
      changePair.setModType(ModificationType.getMod(chStmt.getInt("mod_type")));
      changePair.setGammaId(chStmt.getLong("gamma_id"));
   }
}