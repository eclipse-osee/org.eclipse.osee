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

package org.eclipse.osee.orcs.core.internal.types;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.OrcsTypesDataStore;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesImpl;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesIndexProviderImpl;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesLoaderFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class OrcsTypesModule {

   private final Log logger;
   private final OrcsTypesDataStore dataStore;

   private OrcsTypesLoaderFactory factory;
   private OrcsTypesIndexProvider indexer;

   public OrcsTypesModule(Log logger, OrcsTypesDataStore dataStore) {
      this.logger = logger;
      this.dataStore = dataStore;
   }

   public void start(OrcsSession session) {
      factory = createFactory();
      indexer = createIndexer(factory.createTypesLoader(session, dataStore));
   }

   public void stop() {
      factory = null;
      indexer = null;
   }

   protected OrcsTypesLoaderFactory createFactory() {
      return new OrcsTypesLoaderFactoryImpl(logger);
   }

   protected OrcsTypesIndexProvider createIndexer(OrcsTypesLoader loader) {
      return new OrcsTypesIndexProviderImpl(loader);
   }

   public OrcsTypes createOrcsTypes(OrcsSession session) {
      return new OrcsTypesImpl(logger, session, dataStore, factory, indexer);
   }

}
