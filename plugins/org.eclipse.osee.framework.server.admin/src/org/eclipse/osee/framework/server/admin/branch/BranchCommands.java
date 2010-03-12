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

import org.eclipse.osee.framework.core.operation.Operations;
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

   public void startBranchExport(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.branchExportWorker.setCommandInterpreter(ci);
         this.branchExportWorker.setExecutionAllowed(true);
         Operations.executeAsJob(branchExportWorker, false);
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
      }
   }

   public void stopBranchExport(CommandInterpreter ci) {
      if (this.branchExportWorker.isRunning()) {
         this.branchExportWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Export is not running.");
      }
   }

   public void startBranchImport(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.branchImportWorker.setCommandInterpreter(ci);
         this.branchImportWorker.setExecutionAllowed(true);
         Operations.executeAsJob(branchImportWorker, false);
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
      }
   }

   public void stopBranchImport(CommandInterpreter ci) {
      if (this.branchImportWorker.isRunning()) {
         this.branchImportWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Import is not running.");
      }
   }

   public void startBranchIntegrityCheck(CommandInterpreter ci) {
      if (!this.branchExportWorker.isRunning() && !this.branchImportWorker.isRunning() && !this.integrityWorker.isRunning()) {
         this.integrityWorker.setCommandInterpreter(ci);
         this.integrityWorker.setExecutionAllowed(true);
         Operations.executeAsJob(integrityWorker, false);
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
      }
   }

   public void stopBranchIntegrityCheck(CommandInterpreter ci) {
      if (this.integrityWorker.isRunning()) {
         this.integrityWorker.setExecutionAllowed(false);
      } else {
         ci.println("Branch Integrity Check is not running.");
      }
   }
}
