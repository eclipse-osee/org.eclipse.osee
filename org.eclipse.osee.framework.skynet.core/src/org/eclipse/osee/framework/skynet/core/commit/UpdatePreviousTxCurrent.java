package org.eclipse.osee.framework.skynet.core.commit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public class UpdatePreviousTxCurrent {
   private final Branch branch;
   private final OseeConnection connection;
   private final IdJoinQuery artifactJoin = JoinUtility.createIdJoinQuery();
   private final IdJoinQuery attributeJoin = JoinUtility.createIdJoinQuery();
   private final IdJoinQuery relationJoin = JoinUtility.createIdJoinQuery();
   private static final String UPDATE_TXS_NOT_CURRENT =
         "update osee_txs SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " where transaction_id = ? AND gamma_id = ?";

   public UpdatePreviousTxCurrent(Branch branch, OseeConnection connection) {
      this.branch = branch;
      this.connection = connection;
   }

   public void addAttribute(int attributeId) {
      attributeJoin.add(attributeId);
   }

   public void addArtifact(int artifactId) {
      artifactJoin.add(artifactId);
   }

   public void addRelation(int relationId) {
      relationJoin.add(relationId);
   }

   public void updateTxNotCurrents() throws OseeDataStoreException {
      updateTxNotCurrents("osee_artifact_version", "art_id", artifactJoin);
      updateTxNotCurrents("osee_attribute", "attr_id", attributeJoin);
      updateTxNotCurrents("osee_relation_link", "rel_link_id", relationJoin);
   }

   private void updateTxNotCurrents(String tableName, String columnName, IdJoinQuery idJoin) throws OseeDataStoreException {
      idJoin.store(connection);
      updateNoLongerCurrentGammas(tableName, columnName, idJoin.getQueryId());
      idJoin.delete(connection);
   }

   private void updateNoLongerCurrentGammas(String tableName, String columnName, int queryId) throws OseeDataStoreException {
      List<Object[]> gammaTxPairs = new ArrayList<Object[]>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      String query =
            "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id idj, " + tableName + " item, osee_txs txs, osee_tx_details txd WHERE idj.query_id = ? and idj.id = item." + columnName + " AND item.gamma_id = txs.gamma_id AND txs.transaction_id = txd.transaction_id AND txd.branch_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId, branch.getBranchId());
         while (chStmt.next()) {
            gammaTxPairs.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }

      ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, gammaTxPairs);
   }
}
