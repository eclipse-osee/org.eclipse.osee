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
import org.eclipse.osee.framework.skynet.core.commit.OseeChange.GammaKind;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 */
public class LoadChangeDataOperation extends AbstractOperation {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select gamma_id, mod_type from osee_txs txs, osee_tx_details txd where txd.branch_id = ? and txd.transaction_id = txs.transaction_id and txd.tx_type = ? and txs.tx_current <> ?";

   private final HashMap<Integer, OseeChange> artifactChangesByItemId = new HashMap<Integer, OseeChange>();
   private final HashMap<Integer, OseeChange> relationChangesByItemId = new HashMap<Integer, OseeChange>();
   private final HashMap<Integer, OseeChange> attributeChangesByItemId = new HashMap<Integer, OseeChange>();
   private final HashMap<Long, OseeChange> changeByGammaId = new HashMap<Long, OseeChange>();

   private final List<OseeChange> changeData;
   private final Branch sourceBranch;
   private final Branch destinationBranch;
   private final Branch mergeBranch;

   public LoadChangeDataOperation(Branch sourceBranch, Branch destinationBranch, Branch mergeBranch, List<OseeChange> changeData) {
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
      loadChangesByItemId(monitor, "osee_artifact_version", "art_id", txJoin.getQueryId(), GammaKind.Artifact,
            artifactChangesByItemId);
      loadChangesByItemId(monitor, "osee_attribute", "attr_id", txJoin.getQueryId(), GammaKind.Attribute,
            attributeChangesByItemId);
      loadChangesByItemId(monitor, "osee_relation_link", "rel_link_id", txJoin.getQueryId(), GammaKind.Relation,
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

            OseeChange oseeChange =
                  new OseeChange(chStmt.getLong("gamma_id"), ModificationType.getMod(chStmt.getInt("mod_type")));

            changeByGammaId.put(oseeChange.getCurrentSourceGammaId(), oseeChange);
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
      return txJoin;
   }

   private void loadChangesByItemId(IProgressMonitor monitor, String tableName, String idColumnName, int queryId, GammaKind kind, HashMap<Integer, OseeChange> changesByItemId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      String query =
            "select txj.gamma_id, " + idColumnName + " from " + tableName + " id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);
            OseeChange change = changeByGammaId.get(chStmt.getInt("gamma_id"));
            change.setKind(kind);
            change.setItemId(chStmt.getInt(idColumnName));
            changesByItemId.put(change.getItemId(), change);
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadByItemId(IProgressMonitor monitor, String tableName, String columnName, HashMap<Integer, OseeChange> changesByItemId) throws OseeCoreException {
      IdJoinQuery idJoin = JoinUtility.createIdJoinQuery();
      for (Entry<Integer, OseeChange> entry : changesByItemId.entrySet()) {
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

   private void loadCurrentData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, Branch branch, HashMap<Integer, OseeChange> changesByItemId) throws OseeCoreException {
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
            OseeChange change = changesByItemId.get(itemId);

            if (branch.isMergeBranch()) {
               change.setNetGammaId(gammaId);
               change.setNetModType(ModificationType.MERGED);
            } else {
               change.setDestinationModType(ModificationType.getMod(chStmt.getInt("mod_type")));
               change.setDestinationGammaId(gammaId);
            }
         }
      } finally {
         chStmt.close();
      }
   }

   private void loadNonCurrentSourceData(IProgressMonitor monitor, String tableName, String columnName, IdJoinQuery idJoin, HashMap<Integer, OseeChange> changesByItemId) throws OseeCoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      String query =
            "select txs.mod_type, item." + columnName + " from osee_join_id idj, " //
                  + tableName + " item, osee_txs txs, osee_tx_details txd where idj.query_id = ? and idj.id = item." + columnName + //
                  " and item.gamma_id = txs.gamma_id and txs.tx_current = ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ? and txd.tx_type = ? order by idj.id, txs.transaction_id asc";

      try {
         chStmt.runPreparedQuery(10000, query, idJoin.getQueryId(), TxChange.NOT_CURRENT.getValue(),
               getSourceBranchId(), TransactionDetailsType.NonBaselined.getId());
         int previousItemId = -1;
         while (chStmt.next()) {
            checkForCancelledStatus(monitor);

            int itemId = chStmt.getInt(columnName);
            if (previousItemId != itemId) {
               previousItemId = itemId;

               OseeChange change = changesByItemId.get(itemId);
               change.setBaseSourceModType(ModificationType.getMod(chStmt.getInt("mod_type")));
            }
         }
      } finally {
         chStmt.close();
      }
   }
}