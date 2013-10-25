/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.accessor;

import static org.eclipse.osee.framework.database.core.IOseeStatement.MAX_FETCH;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class UpdatePreviousTxCurrent {
   private static final String UPDATE_TXS_NOT_CURRENT =
      "update osee_txs SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " where branch_id = ? AND gamma_id = ? and transaction_id = ?";
   private static final String SELECT_TXS_AND_GAMMAS =
      "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id idj, %s item, osee_txs txs WHERE idj.query_id = ? and idj.id = item.%s AND item.gamma_id = txs.gamma_id AND txs.branch_id = ? AND txs.tx_current <> ?";
// @formatter:off
   private static final String SELECT_TXS_AND_GAMMAS_FROM_TXS ="with\n"+
      "txs as (select gamma_id from osee_txs where branch_id = ? and transaction_id = ?),\n"+
      "item as (\n"+
      "   SELECT item2.gamma_id FROM osee_attribute item1, txs, osee_attribute item2 where txs.gamma_id = item1.gamma_id and item1.attr_id = item2.attr_id\n"+
      "UNION ALL\n"+
      "   SELECT item2.gamma_id FROM osee_artifact item1, txs, osee_artifact item2 where txs.gamma_id = item1.gamma_id and item1.art_id = item2.art_id\n"+
      "UNION ALL\n"+
      "   SELECT item2.gamma_id FROM osee_relation_link item1, txs, osee_relation_link item2 where txs.gamma_id = item1.gamma_id and item1.rel_link_id = item2.rel_link_id)\n"+
      "select txsb.transaction_id, txsb.gamma_id FROM item, osee_txs txsb where item.gamma_id = txsb.gamma_id AND txsb.branch_id = ? AND transaction_id <> ? AND txsb.tx_current <> ?";
// @formatter:on

   private final IOseeDatabaseService dbService;
   private final long branchId;
   private final OseeConnection connection;
   private IdJoinQuery artifactJoin;
   private IdJoinQuery attributeJoin;
   private IdJoinQuery relationJoin;

   public UpdatePreviousTxCurrent(IOseeDatabaseService dbService, OseeConnection connection, long branchId) {
      this.dbService = dbService;
      this.branchId = branchId;
      this.connection = connection;
   }

   public void addAttribute(int attributeId) {
      if (attributeJoin == null) {
         attributeJoin = JoinUtility.createIdJoinQuery(dbService);
      }
      attributeJoin.add(attributeId);
   }

   public void addArtifact(int artifactId) {
      if (artifactJoin == null) {
         artifactJoin = JoinUtility.createIdJoinQuery(dbService);
      }
      artifactJoin.add(artifactId);
   }

   public void addRelation(int relationId) {
      if (relationJoin == null) {
         relationJoin = JoinUtility.createIdJoinQuery(dbService);
      }
      relationJoin.add(relationId);
   }

   public void updateTxNotCurrents() throws OseeCoreException {
      updateTxNotCurrents("osee_artifact", "art_id", artifactJoin);
      updateTxNotCurrents("osee_attribute", "attr_id", attributeJoin);
      updateTxNotCurrents("osee_relation_link", "rel_link_id", relationJoin);
   }

   private void updateTxNotCurrents(String tableName, String columnName, IdJoinQuery idJoin) throws OseeCoreException {
      if (idJoin != null) {
         idJoin.store(connection);
         updateNoLongerCurrentGammas(tableName, columnName, idJoin.getQueryId());
         idJoin.delete(connection);
      }
   }

   private void updateNoLongerCurrentGammas(String tableName, String columnName, int queryId) throws OseeCoreException {
      String query = String.format(SELECT_TXS_AND_GAMMAS, tableName, columnName);

      List<Object[]> updateData = new ArrayList<Object[]>();
      IOseeStatement chStmt = dbService.getStatement(connection);
      try {
         chStmt.runPreparedQuery(MAX_FETCH, query, queryId, branchId, TxChange.NOT_CURRENT.getValue());
         while (chStmt.next()) {
            updateData.add(new Object[] {branchId, chStmt.getLong("gamma_id"), chStmt.getInt("transaction_id")});
         }
      } finally {
         chStmt.close();
      }

      dbService.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, updateData);
   }

   public void updateTxNotCurrentsFromTx(int transaction_id) throws OseeCoreException {
      List<Object[]> updateData = new ArrayList<Object[]>();
      IOseeStatement chStmt = dbService.getStatement(connection);
      try {
         chStmt.runPreparedQuery(MAX_FETCH, SELECT_TXS_AND_GAMMAS_FROM_TXS, branchId, transaction_id, branchId,
            transaction_id, TxChange.NOT_CURRENT.getValue());
         while (chStmt.next()) {
            updateData.add(new Object[] {branchId, chStmt.getLong("gamma_id"), chStmt.getInt("transaction_id")});
         }
      } finally {
         chStmt.close();
      }

      dbService.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, updateData);
   }
}
