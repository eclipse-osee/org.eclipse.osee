/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.purge;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author John Misinco
 */
public final class PurgeBranchOperationFactory implements IBranchOperationFactory {

   private final BranchCache branchCache;
   private final IOseeDatabaseService databaseService;
   private final OperationLogger logger;

   public PurgeBranchOperationFactory(OperationLogger logger, BranchCache branchCache, IOseeDatabaseService databaseService) {
      this.branchCache = branchCache;
      this.databaseService = databaseService;
      this.logger = logger;
   }

   @Override
   public IOperation createOperation(Branch branch) {
      return new PurgeBranchOperation(logger, branch, branchCache, databaseService);
   }
}
