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
package org.eclipse.osee.framework.server.admin.branch;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public final class BranchExportOperation extends AbstractOperation {
   private static final String SELECT_BRANCHES = "SELECT branch_id FROM osee_branch %s ORDER BY branch_id";

   private final PropertyStore propertyStore;
   private final String exportFileName;
   private final boolean includeArchivedBranches;
   private final List<Integer> branchIds;

   public BranchExportOperation(OperationLogger logger, PropertyStore propertyStore, String exportFileName, boolean includeArchivedBranches, List<Integer> branchIds) {
      super("Branch Export", Activator.PLUGIN_ID, logger);
      this.propertyStore = propertyStore;
      this.exportFileName = exportFileName;
      this.includeArchivedBranches = includeArchivedBranches;
      this.branchIds = branchIds;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (!Strings.isValid(exportFileName)) {
         throw new OseeArgumentException("exportFileName was invalid: [%s]", exportFileName);
      }

      if (branchIds.isEmpty()) {
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(String.format(SELECT_BRANCHES, includeArchivedBranches ? "" : "where archived = 0"));
            while (chStmt.next()) {
               branchIds.add(chStmt.getInt("branch_id"));
            }
         } finally {
            chStmt.close();
         }
      }
      logf("Exporting: [%s] branches\n", branchIds.size());

      Activator.getBranchExchange().exportBranch(exportFileName, propertyStore, branchIds);
   }
}