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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;

/**
 * @author Roberto E. Escobar
 */
public class BranchExportWorker extends BaseServerCommand {

   private static final String ALL_BRANCHES_QUERY =
         "SELECT x1.branch_id FROM (" + "SELECT br1.branch_id FROM osee_branch br1%s br1.branch_type <> 3 " + "UNION " + "SELECT om1.merge_branch_id FROM osee_merge om1, osee_branch ob1 WHERE om1.dest_branch_id = ob1.branch_id%s " + "UNION " + "SELECT om2.source_branch_id from osee_merge om2, osee_branch ob2 WHERE om2.dest_branch_id = ob2.branch_id%s " + ") x1 ORDER BY x1.branch_id";

   protected BranchExportWorker() {
      super("");
   }

   private boolean isValidArg(String arg) {
      return arg != null && arg.length() > 0;
   }

   private String getAllBranchesQuery(boolean includeArchivedBranches) {
      return String.format(ALL_BRANCHES_QUERY, includeArchivedBranches ? " where" : " where br1.archived <> 1 and",
            includeArchivedBranches ? "" : " and ob1.archived <> 1",
            includeArchivedBranches ? "" : " and ob2.archived <> 1");
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
            if (arg.equals("-excludeBaselineTxs")) {
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
         throw new IllegalArgumentException(String.format("exportFileName was invalid: [%s]", exportFileName));
      }

      if (branchIds.isEmpty()) {
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            chStmt.runPreparedQuery(getAllBranchesQuery(includeArchivedBranches));
            while (chStmt.next()) {
               branchIds.add(chStmt.getInt("branch_id"));
            }
         } finally {
            chStmt.close();
         }
      }
      println(String.format("Exporting: [%s] branches\n", branchIds.size()));

      Activator.getInstance().getBranchExchange().exportBranch(exportFileName, options, branchIds);
   }
}
