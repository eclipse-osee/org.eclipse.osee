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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchControlled;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class MoveArchivedBranches extends AbstractBlam {

   private static final String TEST_TXS =
         "select count(1) from osee_tx_details txd where txd.branch_id = ? AND txd.tx_type = 1 AND exists (select 1 from osee_txs txs where txd.transaction_id = txs.transaction_id)";

   @Override
   public String getName() {
      return "Move Archived Branches";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      IOseeDatabaseService service = SkynetGuiPlugin.getInstance().getOseeDatabaseService();
      int increment = 0;
      if (!variableMap.getBoolean("Even Branches")) {
         increment = 1;
      }

      for (Branch branch : BranchManager.getBranches(BranchArchivedState.ARCHIVED, BranchControlled.ALL,
            BranchType.WORKING)) {
         if ((branch.getId() + increment) % 2 == 0) {
            if (service.runPreparedQueryFetchObject(0, TEST_TXS, branch.getId()) == 1) {
               System.out.println("Moving: " + branch);
               moveBranchAddressing(null, branch, true);
            }
         }
      }
   }

   private static final String INSERT_ADDRESSING =
         "insert into %s (transaction_id, gamma_id, tx_current, mod_type) select transaction_id, gamma_id, tx_current, mod_type from osee_txs where branch_id = ?";

   public static final String DELETE_ADDRESSING = "delete from %s where branch_id = ?";

   public void moveBranchAddressing(IOseeDatabaseService service, Branch branch, boolean archive) throws OseeDataStoreException {
      String sourceTableName = archive ? "osee_txs" : "osee_txs_archived";
      String destinationTableName = archive ? "osee_txs_archived" : "osee_txs";

      String sql = String.format(INSERT_ADDRESSING, destinationTableName);
      service.runPreparedUpdate(sql, branch.getId());

      sql = String.format(DELETE_ADDRESSING, sourceTableName);
      service.runPreparedUpdate(sql, branch.getId());
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Even Branches\" /></xWidgets>";
   }
}