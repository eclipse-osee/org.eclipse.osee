package org.eclipse.osee.framework.skynet.core.internal.accessors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Ryan D. Brooks
 */
public class MoveBranchDatabaseCallable {

   private static final String INSERT_ADDRESSING =
      "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id, app_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id, app_id from %s where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";
   private final boolean archive;
   private final BranchId branch;
   private final JdbcClient jdbcClient;

   public MoveBranchDatabaseCallable(JdbcClient jdbcClient, boolean archive, BranchId branch) {
      this.jdbcClient = jdbcClient;
      this.archive = archive;
      this.branch = branch;
   }

   protected IStatus handleTxWork() throws OseeCoreException {
      JdbcConnection connection = jdbcClient.getConnection();
      try {
         String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
         String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

         String sql = String.format(INSERT_ADDRESSING, destinationTableName, sourceTableName);
         jdbcClient.runPreparedUpdate(connection, sql, branch);

         sql = String.format(DELETE_ADDRESSING, sourceTableName);
         jdbcClient.runPreparedUpdate(connection, sql, branch);
      } finally {
         connection.close();
      }
      return Status.OK_STATUS;
   }
}