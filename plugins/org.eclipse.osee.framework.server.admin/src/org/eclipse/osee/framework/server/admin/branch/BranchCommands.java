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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.branch.management.purge.BranchOperation;
import org.eclipse.osee.framework.branch.management.purge.DeletedBranchProvider;
import org.eclipse.osee.framework.branch.management.purge.IBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.purge.IBranchesProvider;
import org.eclipse.osee.framework.branch.management.purge.PurgeBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.purge.RecursiveBranchProvider;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.CommandInterpreterLogger;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
public class BranchCommands {

   private final BranchExportWorker branchExportWorker;
   private final BranchImportWorker branchImportWorker;
   private final ExchangeIntegrityWorker integrityWorker;

   public BranchCommands() {
      this.branchExportWorker = new BranchExportWorker();
      this.branchExportWorker.setExecutionAllowed(true);

      this.branchImportWorker = new BranchImportWorker();
      this.branchImportWorker.setExecutionAllowed(true);

      this.integrityWorker = new ExchangeIntegrityWorker();
      this.integrityWorker.setExecutionAllowed(true);
   }

   public Job startBranchExport(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.branchExportWorker.setCommandInterpreter(ci);
         this.branchExportWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(branchExportWorker, false);
      } else {
         if (this.branchExportWorker.isRunning()) {
            ci.println("Branch Export is already running.");
         }
         if (this.branchImportWorker.isRunning()) {
            ci.println("Branch Import is already running.");
         }
         if (this.integrityWorker.isRunning()) {
            ci.println("Branch Integrity Check is already running.");
         }
         return null;
      }
   }

   public void stopBranchExport(CommandInterpreter ci) {
      if (this.branchExportWorker.isRunning()) {
         this.branchExportWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Export is not running.");
      }
   }

   public Job startBranchImport(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.branchImportWorker.setCommandInterpreter(ci);
         this.branchImportWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(branchImportWorker, false);
      } else {
         if (this.branchExportWorker.isRunning()) {
            ci.println("Branch Export is already running.");
         }
         if (this.branchImportWorker.isRunning()) {
            ci.println("Branch Import is already running.");
         }
         if (this.integrityWorker.isRunning()) {
            ci.println("Branch Integrity Check is already running.");
         }
         return null;
      }
   }

   public void stopBranchImport(CommandInterpreter ci) {
      if (this.branchImportWorker.isRunning()) {
         this.branchImportWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Import is not running.");
      }
   }

   public Job startBranchIntegrityCheck(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.integrityWorker.setCommandInterpreter(ci);
         this.integrityWorker.setExecutionAllowed(true);
         return Operations.executeAsJob(integrityWorker, false);
      } else {
         if (this.branchExportWorker.isRunning()) {
            ci.println("Branch Export is already running.");
         }
         if (this.branchImportWorker.isRunning()) {
            ci.println("Branch Import is already running.");
         }
         if (this.integrityWorker.isRunning()) {
            ci.println("Branch Integrity Check is already running.");
         }
         return null;
      }
   }

   public void stopBranchIntegrityCheck(CommandInterpreter ci) {
      if (this.integrityWorker.isRunning()) {
         this.integrityWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Integrity Check is not running.");
      }
   }

   public Job purgeDeletedBranches(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      BranchCache branchCache = Activator.getOseeCachingService().getBranchCache();
      IBranchesProvider provider = new DeletedBranchProvider(branchCache);

      return internalPurgeBranch(logger, branchCache, provider);
   }

   public Job purgeBranchRecursive(CommandInterpreter ci) throws OseeCoreException {
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
}