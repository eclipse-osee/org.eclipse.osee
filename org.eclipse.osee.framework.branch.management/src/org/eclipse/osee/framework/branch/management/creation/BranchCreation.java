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
package org.eclipse.osee.framework.branch.management.creation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.IBranchCreation;
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.data.BranchCreationResponse;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Andrew M. Finkbeiner
 */
public class BranchCreation implements IBranchCreation {
   private final IOseeDatabaseServiceProvider provider;
   private final IOseeCachingServiceProvider cachingService;
   private final IOseeModelFactoryServiceProvider modelFactory;

   public BranchCreation(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingService, IOseeModelFactoryServiceProvider modelFactory) {
      this.provider = provider;
      this.cachingService = cachingService;
      this.modelFactory = modelFactory;
   }

   @Override
   public void createBranch(IProgressMonitor monitor, BranchCreationRequest request, BranchCreationResponse response) throws Exception {
      IOperation operation = new CreateBranchOperation(provider, modelFactory, cachingService, request, response);
      Operations.executeWorkAndCheckStatus(operation, new LogProgressMonitor(), -1);
   }
}
