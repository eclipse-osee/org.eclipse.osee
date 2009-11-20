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
package org.eclipse.osee.framework.branch.management.remote;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.IBranchArchivingService;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Roberto E. Escobar
 */
public class BranchArchivingService implements IBranchArchivingService {

   private static final String SELECT_ADDRESSING_BY_BRANCH =
         "select * from %s txs, osee_tx_details txd where txs.transaction_id = txd.transaction_id and txd.branch_id = ?";
   private static final String INSERT_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, mod_type, tx_current) VALUES (?,?,?,?)";

   private static final String DELETE_ADDRESSING = "delete from %s where transaction_id = ? and gamma_id = ?";

   public static void moveBranchAddressing(OseeConnection connection, Branch branch, boolean archive) throws OseeDataStoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      List<Object[]> addressing = new ArrayList<Object[]>();
      List<Object[]> deleteAddressing = new ArrayList<Object[]>();
      String sql = String.format(SELECT_ADDRESSING_BY_BRANCH, sourceTableName);

      try {
         chStmt.runPreparedQuery(10000, sql, branch.getId());
         while (chStmt.next()) {
            addressing.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id"),
                  chStmt.getInt("mod_type"), chStmt.getInt("tx_current")});
            deleteAddressing.add(new Object[] {chStmt.getInt("transaction_id"), chStmt.getLong("gamma_id")});
         }
      } finally {
         chStmt.close();
      }
      sql = String.format(INSERT_ADDRESSING, destinationTableName);
      ConnectionHandler.runBatchUpdate(connection, sql, addressing);

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      ConnectionHandler.runBatchUpdate(connection, sql, deleteAddressing);
   }
}
