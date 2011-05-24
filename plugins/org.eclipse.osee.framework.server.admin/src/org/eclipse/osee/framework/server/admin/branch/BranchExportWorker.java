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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osee.framework.server.admin.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class BranchExportWorker extends BaseServerCommand {
   private static final String SELECT_BRANCHES = "SELECT branch_id FROM osee_branch %s ORDER BY branch_id";

   protected BranchExportWorker() {
      super("");
   }

   private boolean isValidArg(String arg) {
      return arg != null && arg.length() > 0;
   }

   @Override
   protected void doCommandWork(IProgressMonitor monitor) throws Exception {
      Options options = new Options();
      String arg = null;
      int count = 0;
      String exportFileName = null;
      boolean includeArchivedBranches = false;
      List<Integer> branchIds = new ArrayList<Integer>();
      do {
         arg = getCommandInterpreter().nextArgument();
         if (isValidArg(arg)) {
            if (arg.equals("-excludeBranchIds")) {
               options.put(ExportOptions.EXCLUDE_BRANCH_IDS.name(), true);
            } else if (arg.equals("-excludeBaselineTxs")) {
               options.put(ExportOptions.EXCLUDE_BASELINE_TXS.name(), true);
            } else if (arg.equals("-includeArchivedBranches")) {
               includeArchivedBranches = true;
            } else if (arg.equals("-compress")) {
               options.put(ExportOptions.COMPRESS.name(), true);
            } else if (arg.equals("-minTx")) {
               arg = getCommandInterpreter().nextArgument();
               if (isValidArg(arg)) {
                  options.put(ExportOptions.MIN_TXS.name(), arg);
               }
               count++;
            } else if (arg.equals("-maxTx")) {
               arg = getCommandInterpreter().nextArgument();
               if (isValidArg(arg)) {
                  options.put(ExportOptions.MAX_TXS.name(), arg);
               }
               count++;
            } else {
               if (count == 0 && !arg.startsWith("-")) {
                  exportFileName = arg;
               } else {
                  branchIds.add(new Integer(arg));
               }
            }
         }
         count++;
      } while (isValidArg(arg));

      if (!isValidArg(exportFileName)) {
         throw new OseeArgumentException("exportFileName was invalid: [%s]", exportFileName);
      }

      if (options.getBoolean(ExportOptions.EXCLUDE_BRANCH_IDS.name())) {
         List<Integer> toExclude = branchIds;

         branchIds = new ArrayList<Integer>();
         loadBranchIds(branchIds, includeArchivedBranches);

         for (Integer item : toExclude) {
            branchIds.remove(item);
         }
      } else if (branchIds.isEmpty()) {
         loadBranchIds(branchIds, includeArchivedBranches);
      }
      println(String.format("Exporting: [%s] branches\n", branchIds.size()));

      Activator.getBranchExchange().exportBranch(exportFileName, options, branchIds);
   }

   private void loadBranchIds(Collection<Integer> branchIds, boolean includeArchivedBranches) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         String filter =
            !includeArchivedBranches ? String.format("where archived = %s", BranchArchivedState.UNARCHIVED.getValue()) : "";
         String query = String.format(SELECT_BRANCHES, filter);
         chStmt.runPreparedQuery(query);
         while (chStmt.next()) {
            branchIds.add(chStmt.getInt("branch_id"));
         }
      } finally {
         chStmt.close();
      }
   }
}
