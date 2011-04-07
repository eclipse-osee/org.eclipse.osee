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

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author John Misinco
 */

public class PurgeBranchRecursive extends AbstractOperation {

   private final IOseeCachingService cachingService;
   private final IOseeDatabaseService databaseService;
   private final Branch toDelete;

   public PurgeBranchRecursive(OperationLogger logger, Branch toDelete, IOseeCachingService cachingService, IOseeDatabaseService databaseService) {
      super("Purge Branch Recursive", Activator.PLUGIN_ID, logger);
      this.cachingService = cachingService;
      this.databaseService = databaseService;
      this.toDelete = toDelete;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      Collection<Branch> children = toDelete.getAllChildBranches(true);
      //add parent so it gets deleted last
      children.add(toDelete);
      logf("Found %s branches to purge.", children.size());
      for (Branch branch : children) {
         logf("Purging %s.", branch);
         try {
            doSubWork(new PurgeBranchOperation(getLogger(), branch, cachingService, databaseService), monitor, 0);
         } catch (OseeCoreException ex) {
            log(ex);
         }
      }
   }
}
