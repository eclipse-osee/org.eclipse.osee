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
package org.eclipse.osee.framework.skynet.core.internal.accessors;

import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerBranchAccessor extends AbstractServerDataAccessor<Branch> {

   private final TransactionCache transactionCache;

   public ServerBranchAccessor(IOseeModelFactoryServiceProvider factoryProvider, TransactionCache transactionCache) {
      super(factoryProvider);
      this.transactionCache = transactionCache;
   }

   protected BranchFactory getFactory() throws OseeCoreException {
      return getOseeFactoryService().getBranchFactory();
   }

   @Override
   public void load(IOseeCache<Branch> cache) throws OseeCoreException {
      transactionCache.ensurePopulated();
      super.load(cache);
   }

   //   @Override
   //   protected void updateCache(AbstractOseeCache<Branch> cache, CacheUpdateResponse<Branch> updateResponse) throws OseeCoreException {
   //   }

}
