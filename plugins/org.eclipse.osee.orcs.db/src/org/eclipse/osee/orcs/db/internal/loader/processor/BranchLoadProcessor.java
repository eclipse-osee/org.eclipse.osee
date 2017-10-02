/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.processor;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.data.BranchObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public class BranchLoadProcessor extends LoadProcessor<BranchData, BranchObjectFactory> {

   public BranchLoadProcessor(BranchObjectFactory factory) {
      super(factory);
   }

   @Override
   protected BranchData createData(Object conditions, BranchObjectFactory factory, JdbcStatement chStmt, Options options)  {
      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), OptionsUtil.getFromBranchView(options));

      String branchName = chStmt.getString("branch_name");
      BranchState branchState = BranchState.getBranchState(chStmt.getInt("branch_state"));
      BranchType branchType = BranchType.valueOf(chStmt.getInt("branch_type"));
      BranchArchivedState archiveState = BranchArchivedState.valueOf(chStmt.getInt("archived"));

      BranchId parentBranchId = BranchId.valueOf(chStmt.getLong("parent_branch_id"));
      TransactionId sourceTx = TransactionId.valueOf(chStmt.getLong("parent_transaction_id"));
      TransactionId baseTx = TransactionId.valueOf(chStmt.getLong("baseline_transaction_id"));
      ArtifactId assocArtId = ArtifactId.valueOf(chStmt.getLong("associated_art_id"));
      boolean inheritAccessControl = chStmt.getInt("inherit_access_control") != 0;

      return factory.createBranchData(branch, branchType, branchName, parentBranchId, baseTx, sourceTx, archiveState,
         branchState, assocArtId, inheritAccessControl);
   }
}
