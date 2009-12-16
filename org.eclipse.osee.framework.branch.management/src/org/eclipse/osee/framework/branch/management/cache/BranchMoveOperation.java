/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.cache;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Ryan D. Brooks
 */

public class BranchMoveOperation extends AbstractOperation {

   private static final String INSERT_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from osee_txs where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";

   private final OseeConnection connection;
   private final boolean archive;
   private final Branch branch;
   private final IOseeDatabaseService dbService;

   public BranchMoveOperation(OseeConnection connection, boolean archive, Branch branch, IOseeDatabaseService dbService) {
      super("Branch Move", Activator.PLUGIN_ID);
      this.connection = connection;
      this.archive = archive;
      this.branch = branch;
      this.dbService = dbService;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName);
      dbService.runPreparedUpdate(connection, sql, branch.getId());

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      dbService.runPreparedUpdate(connection, sql, branch.getId());
   }
}