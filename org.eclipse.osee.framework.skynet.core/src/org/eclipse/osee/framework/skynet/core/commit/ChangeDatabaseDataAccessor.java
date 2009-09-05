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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.skynet.core.commit.OseeChange.GammaKind;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 */
public class ChangeDatabaseDataAccessor implements IChangeDataAccessor {
   private static final String SELECT_SOURCE_BRANCH_CHANGES =
         "select gamma_id, tx_current, mod_type from osee_txs txs, osee_tx_details txd where txd.branch_id = ? and txd.transaction_id = txs.transaction_id and txd.tx_type = ?";

   private final HashMap<Integer, Integer> artifactIds = new HashMap<Integer, Integer>();
   private final HashMap<Integer, Integer> attributeIds = new HashMap<Integer, Integer>();
   private final HashMap<Integer, Integer> relationIds = new HashMap<Integer, Integer>();
   private final List<OseeChange> changeData = new ArrayList<OseeChange>();

   private static final String SELECT_DESTINATION_BRANCH_DATA =
         "select txs.gamma_id, tx_current, mod_type from osee_join_transaction txj, osee_txs txs, osee_tx_details txd where txj.query_id = ? txj.gamma_id = txs.gamma_id and txs.tx_current <> ? and txs.transaction_id = txd.transaction_id and txd.branch_id = ?";

   private void loadIdMap(IProgressMonitor monitor, HashMap<Integer, Integer> idMap, String tableName, String idColumnName, int queryId) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      String query =
            "select " + idColumnName + " from " + tableName + " id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId);
         while (chStmt.next()) {
            if (monitor.isCanceled()) {
               throw new OperationCanceledException();
            }
         }
         idMap.put(chStmt.getInt("gamma_id"), chStmt.getInt(idColumnName));
      } finally {
         chStmt.close();
      }
   }

   private void loadSourceBranchChanges(IProgressMonitor monitor, ChangeLocator locator, TransactionJoinQuery txJoin) throws OseeDataStoreException, OseeArgumentException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      try {
         chStmt.runPreparedQuery(10000, SELECT_SOURCE_BRANCH_CHANGES, locator.getSourceBranch().getBranchId(),
               TransactionDetailsType.NonBaselined);
         while (chStmt.next()) {
            if (monitor.isCanceled()) {
               throw new OperationCanceledException();
            }
            txJoin.add(chStmt.getInt("gamma_id"), -1);

            OseeChange oseeChange =
                  new OseeChange(TxChange.getChangeType(chStmt.getInt("tx_current")), chStmt.getInt("gamma_id"),
                        ModificationType.getMod(chStmt.getInt("mod_type")), -1);

            changeData.add(oseeChange);
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
   }

   private Collection<OseeChange> loadDestinationData(IProgressMonitor monitor, ChangeLocator locator, TransactionJoinQuery txJoin) throws OseeDataStoreException, OseeArgumentException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();

      Collection<OseeChange> data = new ArrayList<OseeChange>();
      try {
         chStmt.runPreparedQuery(10000, SELECT_DESTINATION_BRANCH_DATA, txJoin.getQueryId(), TxChange.NOT_CURRENT,
               locator.getDestinationBranch().getBranchId());
         while (chStmt.next()) {
            if (monitor.isCanceled()) {
               throw new OperationCanceledException();
            }
            txJoin.add(chStmt.getInt("gamma_id"), -1);

            //TxChange.getChangeType(chStmt.getInt("tx_current")), chStmt.getInt("gamma_id"),ModificationType.getMod(chStmt.getInt("mod_type"))
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
      return data;
   }

   @Override
   public void loadChangeData(IProgressMonitor monitor, ChangeLocator locator, List<OseeChange> data) throws Exception {
      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();
      loadSourceBranchChanges(monitor, locator, txJoin);

      HashMap<Integer, Integer> artifactIds = new HashMap<Integer, Integer>();
      HashMap<Integer, Integer> attributeIds = new HashMap<Integer, Integer>();
      HashMap<Integer, Integer> relationIds = new HashMap<Integer, Integer>();

      loadIdMap(monitor, artifactIds, "osee_artifact_version", "art_id", txJoin.getQueryId());
      loadIdMap(monitor, attributeIds, "osee_attribute", "attr_id", txJoin.getQueryId());
      loadIdMap(monitor, relationIds, "osee_relation_link", "rel_link_id", txJoin.getQueryId());

      for (OseeChange oseeChange : changeData) {
         Integer gammaId = oseeChange.getGammaId();
         Integer itemId = artifactIds.get(gammaId);
         if (itemId == null) {
            itemId = attributeIds.get(gammaId);
            if (itemId == null) {
               itemId = relationIds.get(gammaId);
               if (itemId == null) {
                  throw new OseeStateException("Did not find gammaId " + gammaId + " during net change computation");
               }
               oseeChange.setKind(GammaKind.Relation);
            } else {
               oseeChange.setKind(GammaKind.Attribute);
            }
         } else {
            oseeChange.setKind(GammaKind.Artiact);
         }
         oseeChange.setItemId(itemId);
      }

      loadDestinationData(monitor, locator, txJoin);

      txJoin.delete();
   }
}
