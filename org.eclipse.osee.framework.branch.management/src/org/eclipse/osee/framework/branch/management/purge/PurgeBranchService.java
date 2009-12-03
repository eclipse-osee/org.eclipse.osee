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
package org.eclipse.osee.framework.branch.management.purge;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.branch.management.IPurgeBranchService;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.data.PurgeBranchRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Jeff C. Phillips
 * @author Megumi Telles
 */
public class PurgeBranchService implements IPurgeBranchService {
   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final IOseeCachingServiceProvider cachingService;

   public PurgeBranchService(IOseeDatabaseServiceProvider oseeDatabaseProvider, IOseeCachingServiceProvider cachingService) {
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.cachingService = cachingService;
   }

   @Override
   public void purge(IProgressMonitor monitor, PurgeBranchRequest request) throws OseeCoreException {
      BranchCache branchCache = cachingService.getOseeCachingService().getBranchCache();
      IOperation operation = new PurgeBranchOperation(branchCache.getById(request.getBranchId()), cachingService, oseeDatabaseProvider);
      Operations.executeWork(operation, new NullProgressMonitor(), -1);
      try {
         Operations.checkForStatusSeverityMask(operation.getStatus(), IStatus.ERROR | IStatus.WARNING);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
