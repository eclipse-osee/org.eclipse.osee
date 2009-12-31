package org.eclipse.osee.framework.branch.management.commit;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;

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
         "update osee_txs SET tx_current = " + TxChange.NOT_CURRENT.getValue() + " where branch_id = ? AND gamma_id = ? and tx_current <> ? and transaction_id = ?";

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
      List<Object[]> updateData = new ArrayList<Object[]>();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      String query =
            "SELECT txs.transaction_id, txs.gamma_id FROM osee_join_id idj, " + tableName + " item, osee_txs txs WHERE idj.query_id = ? and idj.id = item." + columnName + " AND item.gamma_id = txs.gamma_id AND txs.branch_id = ?";

      try {
         chStmt.runPreparedQuery(10000, query, queryId, branch.getId());
         while (chStmt.next()) {
            updateData.add(new Object[] {branch.getId(), chStmt.getLong("gamma_id"), TxChange.NOT_CURRENT.getValue(),
                  chStmt.getInt("transaction_id")});
         }
      } finally {
         chStmt.close();
      }

      ConnectionHandler.runBatchUpdate(connection, UPDATE_TXS_NOT_CURRENT, updateData);
   }
}
