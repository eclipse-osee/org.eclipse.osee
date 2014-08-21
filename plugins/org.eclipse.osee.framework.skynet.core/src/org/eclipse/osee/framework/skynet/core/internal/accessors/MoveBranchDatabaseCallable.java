package org.eclipse.osee.framework.skynet.core.internal.accessors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public class MoveBranchDatabaseCallable {

   private static final String INSERT_ADDRESSING =
      "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from %s where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";
   private final boolean archive;
   private final Branch branch;
   private final IOseeDatabaseService dbService;

   public MoveBranchDatabaseCallable(IOseeDatabaseService databaseService, boolean archive, Branch branch) {
      this.dbService = databaseService;
      this.archive = archive;
      this.branch = branch;
   }

   protected IStatus handleTxWork() throws OseeCoreException {
      OseeConnection connection = dbService.getConnection();
      try {
         String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
         String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

         String sql = String.format(INSERT_ADDRESSING, destinationTableName, sourceTableName);
         dbService.runPreparedUpdate(connection, sql, branch.getUuid());

         sql = String.format(DELETE_ADDRESSING, sourceTableName);
         dbService.runPreparedUpdate(connection, sql, branch.getUuid());
      } finally {
         connection.close();
      }
      return Status.OK_STATUS;
   }
}