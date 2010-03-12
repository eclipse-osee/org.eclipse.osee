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
package org.eclipse.osee.framework.server.admin.management;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.cache.BranchMoveOperation;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Ryan D. Brooks
 */
public class FinishPartiallyArchivedBranchesCommand extends BaseServerCommand {

   public FinishPartiallyArchivedBranchesCommand(CommandInterpreter ci) {
      super("Finish Partially Archived Branches", ci);
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      String sql =
            "select branch_id from osee_branch br where exists (select 1 from osee_txs txs where txs.branch_id = br.branch_id and br.archived = " + BranchArchivedState.ARCHIVED.getValue() + ")";
      IOseeStatement chStmt = null;
      try {
         IOseeDatabaseService databaseService = Activator.getInstance().getOseeDatabaseService();
         chStmt = databaseService.getStatement();

         List<Branch> branches = new ArrayList<Branch>(100);
         chStmt.runPreparedQuery(100, sql);
         BranchCache branchCache = Activator.getInstance().getOseeCachingService().getBranchCache();
         while (chStmt.next()) {
            branches.add(branchCache.getById(chStmt.getInt("branch_id")));
         }
         for (Branch branch : branches) {
            Operations.executeWorkAndCheckStatus(new BranchMoveOperation(Activator.getInstance(), true, branch),
                  monitor, 0);
         }
      } catch (OseeCoreException ex) {
         printStackTrace(ex);
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
   }
}
