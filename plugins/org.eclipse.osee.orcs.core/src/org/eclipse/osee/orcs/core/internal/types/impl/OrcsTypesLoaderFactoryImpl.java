/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.types.impl;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoader;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesLoaderFactory;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesLoaderFactoryImpl implements OrcsTypesLoaderFactory {

   private final Log logger;

   public OrcsTypesLoaderFactoryImpl(Log logger) {
      this.logger = logger;
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
            OrcsTypesIndexer indexer = new OrcsTypesIndexer(logger);
            return new CreateOrcsTypesIndexCallable(logger, indexer, provider);
         }
      };
   }
}