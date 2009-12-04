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

import org.eclipse.osee.framework.branch.management.IBranchArchivingService;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;

/**
 * @author Roberto E. Escobar
 */
public class BranchArchivingService implements IBranchArchivingService {

   private static final String INSERT_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, tx_current, mod_type, branch_id) select transaction_id, gamma_id, tx_current, mod_type, branch_id from osee_txs where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";

   public static void moveBranchAddressing(OseeConnection connection, Branch branch, boolean archive) throws OseeDataStoreException {
      IOseeDatabaseService service = Activator.getInstance().getOseeDatabaseService();

      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName);
      service.runPreparedUpdate(sql, branch.getId());

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      service.runPreparedUpdate(sql, branch.getId());

   }
}
