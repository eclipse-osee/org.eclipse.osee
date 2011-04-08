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
package org.eclipse.osee.framework.server.admin;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.branch.management.purge.BranchOperation;
import org.eclipse.osee.framework.branch.management.purge.DeletedBranchProvider;
import org.eclipse.osee.framework.branch.management.purge.IBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.purge.IBranchesProvider;
import org.eclipse.osee.framework.branch.management.purge.PurgeBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.purge.RecursiveBranchProvider;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.CommandInterpreterLogger;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.MutexSchedulingRule;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.branch.BranchExportOperation;
import org.eclipse.osee.framework.server.admin.branch.BranchImportOperation;
import org.eclipse.osee.framework.server.admin.branch.ExchangeIntegrityOperation;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class BranchManagementCommandProvider implements CommandProvider {
   private final ISchedulingRule branchMutex = new MutexSchedulingRule();

   public Job _export_branch(CommandInterpreter ci) {
      Options options = new Options();
      String arg = null;
      int count = 0;
      String exportFileName = null;
      boolean includeArchivedBranches = false;
      List<Integer> branchIds = new ArrayList<Integer>();
      do {
         arg = ci.nextArgument();
         if (Strings.isValid(arg)) {
            if (arg.equals("-excludeBaselineTxs")) {
               options.put(ExportOptions.EXCLUDE_BASELINE_TXS.name(), true);
            } else if (arg.equals("-includeArchivedBranches")) {
               includeArchivedBranches = true;
            } else if (arg.equals("-compress")) {
               options.put(ExportOptions.COMPRESS.name(), true);
            } else if (arg.equals("-minTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
                  options.put(ExportOptions.MIN_TXS.name(), arg);
               }
               count++;
            } else if (arg.equals("-maxTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
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
      } while (Strings.isValid(arg));

      OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation op = new BranchExportOperation(logger, options, exportFileName, includeArchivedBranches, branchIds);
      return Operations.executeAsJob(op, false, branchMutex);
   }

   public Job _import_branch(CommandInterpreter ci) {
      Options options = new Options();
      String arg = null;
      int count = 0;

      List<Integer> branchIds = new ArrayList<Integer>();
      List<String> importFiles = new ArrayList<String>();
      do {
         arg = ci.nextArgument();
         if (Strings.isValid(arg)) {
            if (arg.equals("-excludeBaselineTxs")) {
               options.put(ImportOptions.EXCLUDE_BASELINE_TXS.name(), true);
            } else if (arg.equals("-clean")) {
               options.put(ImportOptions.CLEAN_BEFORE_IMPORT.name(), true);
            } else if (arg.equals("-allAsRootBranches")) {
               options.put(ImportOptions.ALL_AS_ROOT_BRANCHES.name(), true);
            } else if (arg.equals("-minTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
                  options.put(ImportOptions.MIN_TXS.name(), arg);
               }
               count++;
            } else if (arg.equals("-maxTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
                  options.put(ImportOptions.MAX_TXS.name(), arg);
               }
               count++;
            } else if (count == 0 && !arg.startsWith("-")) {
               importFiles.add(arg);
            } else {
               branchIds.add(new Integer(arg));
            }
            count++;
         }
      } while (Strings.isValid(arg));

      OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation op = new BranchImportOperation(logger, options, importFiles, branchIds);
      return Operations.executeAsJob(op, false, branchMutex);
   }

   public Job _check_exchange(CommandInterpreter ci) throws OseeArgumentException {
      String arg = ci.nextArgument();
      ArrayList<String> importFiles = new ArrayList<String>();
      if (Strings.isValid(arg) && !arg.startsWith("-")) {
         importFiles.add(arg);
      } else {
         throw new OseeArgumentException("File to check was not specified");
      }

      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new ExchangeIntegrityOperation(logger, importFiles), false, branchMutex);
   }

   public Job _purge_deleted_branches(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      BranchCache branchCache = Activator.getOseeCachingService().getBranchCache();
      IBranchesProvider provider = new DeletedBranchProvider(branchCache);

      return internalPurgeBranch(logger, branchCache, provider);
   }

   public Job _purge_branch_recursive(CommandInterpreter ci) throws OseeCoreException {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      String branchGuid = ci.nextArgument();
      BranchCache branchCache = Activator.getOseeCachingService().getBranchCache();
      Branch seed = branchCache.getByGuid(branchGuid);

      BranchFilter filter = new BranchFilter();
      filter.setNegatedBranchTypes(BranchType.BASELINE);

      IBranchesProvider provider = new RecursiveBranchProvider(seed, filter);
      return internalPurgeBranch(logger, branchCache, provider);
   }

   private Job internalPurgeBranch(OperationLogger logger, BranchCache branchCache, IBranchesProvider provider) {
      IOseeDatabaseService databaseService = Activator.getOseeDatabaseService();
      IBranchOperationFactory factory = new PurgeBranchOperationFactory(logger, branchCache, databaseService);

      IOperation operation = new BranchOperation(logger, factory, provider);
      return Operations.executeAsJob(operation, false);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Branch Commands---\n");
      sb.append("\texport_branch <exchangeFileName> [-compress] [-minTx <value>] [-maxTx <value>] [-exclude_baseline_txs] [-includeArchivedBranches] -excludeBranchIds [<branchId>]+ - export a specific set of branches into an exchange zip file.\n");
      sb.append("\timport_branch <exchangeFileName> [-exclude_baseline_txs] [-allAsRootBranches] [-minTx <value>] [-maxTx <value>] [-clean] [<branchId>]+ - import a specific set of branches from an exchange zip file.\n");
      sb.append("\tcheck_exchange <exchangeFileName> - checks an exchange file to ensure data integrity\n");
      sb.append("\tpurge_deleted_branches - permenatly remove all branches that are both archived and deleted \n");
      sb.append("\tpurge_branch_recursive <guid> - removes the branch defined by guid and all its children excluding baseline branches\n");
      return sb.toString();
   }
}