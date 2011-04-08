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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.server.admin.branch.BranchCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class BranchManagementCommandProvider implements CommandProvider {

   private final BranchCommands branchCmds;

   public BranchManagementCommandProvider() {
      this.branchCmds = new BranchCommands();
   }

   public Job _export_branch(CommandInterpreter ci) {
      return branchCmds.startBranchExport(ci);
   }

   public void _export_branch_stop(CommandInterpreter ci) {
      branchCmds.stopBranchExport(ci);
   }

   public Job _import_branch(CommandInterpreter ci) {
      return branchCmds.startBranchImport(ci);
   }

   public void _import_branch_stop(CommandInterpreter ci) {
      branchCmds.stopBranchImport(ci);
   }

   public Job _check_exchange(CommandInterpreter ci) {
      return branchCmds.startBranchIntegrityCheck(ci);
   }

   public void _check_exchange_stop(CommandInterpreter ci) {
      branchCmds.stopBranchIntegrityCheck(ci);
   }

   public Job _purge_deleted_branches(CommandInterpreter ci) {
      return branchCmds.purgeDeletedBranches(ci);
   }

   public Job _purge_branch_recursive(CommandInterpreter ci) throws OseeCoreException {
      return branchCmds.purgeBranchRecursive(ci);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Branch Commands---\n");
      sb.append("\texport_branch <exchangeFileName> [-compress] [-minTx <value>] [-maxTx <value>] [-exclude_baseline_txs] [-includeArchivedBranches] -excludeBranchIds [<branchId>]+ - export a specific set of branches into an exchange zip file.\n");
      sb.append("\texport_branch_stop - stop branch export\n");
      sb.append("\timport_branch <exchangeFileName> [-exclude_baseline_txs] [-allAsRootBranches] [-minTx <value>] [-maxTx <value>] [-clean] [<branchId>]+ - import a specific set of branches from an exchange zip file.\n");
      sb.append("\tcheck_exchange <exchangeFileName> - checks an exchange file to ensure data integrity\n");
      sb.append("\tcheck_exchange_stop - stop exchange integrity check\n");
      sb.append("\tpurge_deleted_branches - permenatly remove all branches that are both archived and deleted \n");
      sb.append("\tpurge_branch_recursive <guid> - removes the branch defined by guid and all its children excluding baseline branches\n");

      return sb.toString();
   }
}