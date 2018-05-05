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
package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.types.BranchHierarchyProvider;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoader;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoaderFactory;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesLoaderFactoryImpl implements OrcsTypesLoaderFactory {

   private final Log logger;
   private final BranchHierarchyProvider hierarchyProvider;

   public OrcsTypesLoaderFactoryImpl(Log logger, BranchHierarchyProvider hierarchyProvider) {
      this.logger = logger;
      this.hierarchyProvider = hierarchyProvider;
   }

   @Override
   public OrcsTypesLoader createTypesLoader(final OrcsSession session, final OrcsTypesDataStore ds) {
      return createTypesLoader(session, new OrcsTypesResourceProvider() {

         @Override
         public IResource getOrcsTypesResource() throws Exception {
            return ds.getOrcsTypesLoader(session);
         }
      });
   }

   @Override
   public OrcsTypesLoader createTypesLoader(final OrcsSession session, final OrcsTypesResourceProvider provider) {
      return new OrcsTypesLoader() {

         @Override
         public Callable<OrcsTypesIndex> createLoader() {
            OrcsTypesIndexer indexer = new OrcsTypesIndexer(logger, hierarchyProvider);
            return new CreateOrcsTypesIndexCallable(logger, indexer, provider);
         }
      };
   }
}