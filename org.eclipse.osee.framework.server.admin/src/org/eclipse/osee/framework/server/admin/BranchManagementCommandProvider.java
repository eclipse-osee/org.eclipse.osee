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

import java.io.File;
import org.eclipse.osee.framework.server.admin.branch.BranchCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class BranchManagementCommandProvider implements CommandProvider {

   public void _export_branch(CommandInterpreter ci) {
      BranchCommands.getInstance().startBranchExport(ci);
   }

   public void _export_branch_stop(CommandInterpreter ci) {
      BranchCommands.getInstance().stopBranchExport(ci);
   }

   public void _import_branch(CommandInterpreter ci) {
      BranchCommands.getInstance().startBranchImport(ci);
   }

   public void _import_branch_stop(CommandInterpreter ci) {
      BranchCommands.getInstance().stopBranchImport(ci);
   }

   public void _check_exchange(CommandInterpreter ci) {
      BranchCommands.getInstance().startBranchIntegrityCheck(ci);
   }

   public void _check_exchange_stop(CommandInterpreter ci) {
      BranchCommands.getInstance().stopBranchIntegrityCheck(ci);
   }

   public void _configini(CommandInterpreter ci) {
      StringBuilder sb = new StringBuilder();
      sb.append("eclipse.ignoreApp=true\n");
      sb.append("osgi.bundles= \\\n");

      String arg = ci.nextArgument();

      File folder = new File(arg);
      File[] files = folder.listFiles();
      for (File f : files) {
         if (!f.isDirectory()) {
            sb.append(f.toURI());
            sb.append("@start, \\\n");
         }
      }
      System.out.println(sb.toString());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
    */
   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Branch Commands---\n");
      sb.append("        export_branch <exchangeFileName> [-compress] [-minTx <value>] [-maxTx <value>] [-exclude_baseline_txs] [-includeArchivedBranches][<branchId>]+ - export a specific set of branches into an exchange zip file.\n");
      sb.append("        export_branch_stop - stop branch export\n");
      sb.append("        import_branch <exchangeFilePath> [-exclude_baseline_txs] [-allAsRootBranches] [-minTx <value>] [-maxTx <value>] [-clean] [<branchId>]+ - import a specific set of branches from an exchange zip file.\n");
      sb.append("        check_exchange <exchangeFilePath> - checks an exchange file to ensure data integrity\n");
      sb.append("        check_exchange_stop - stop exchange integrity check\n");
      return sb.toString();
   }

}
