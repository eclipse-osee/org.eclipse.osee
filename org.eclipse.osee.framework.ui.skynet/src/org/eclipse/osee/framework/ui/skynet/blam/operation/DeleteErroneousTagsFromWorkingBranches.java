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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class DeleteErroneousTagsFromWorkingBranches extends AbstractBlam {
   public static final String DELETE_ERRONEOUS_TAGS =
         "Delete FROM osee_tag_art_map tam1 WHERE tam1.branch_id = ? AND NOT EXISTS (SELECT * FROM osee_define_tx_details txd1, osee_define_txs txs2, osee_define_artifact_version arv3 WHERE txd1.branch_id = ? AND txd1.transaction_id = txs2.transaction_id AND txs2.tx_type <> -4 AND txs2.gamma_id = arv3.gamma_id AND arv3.art_id = tam1.art_id)";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

      for (Branch branch : branchManager.getBranches()) {
         if (branch.hasParentBranch()) {
            ConnectionHandler.runPreparedUpdate(DELETE_ERRONEOUS_TAGS, SQL3DataType.INTEGER, branch.getBranchId(),
                  SQL3DataType.INTEGER, branch.getBranchId());
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}