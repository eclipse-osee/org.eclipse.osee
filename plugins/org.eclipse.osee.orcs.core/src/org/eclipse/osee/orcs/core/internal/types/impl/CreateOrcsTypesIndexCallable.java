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

import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class CreateOrcsTypesIndexCallable extends CancellableCallable<OrcsTypesIndex> {

   private final Log logger;
   private final OrcsTypesIndexer indexer;
   private final OrcsTypesResourceProvider provider;

   public CreateOrcsTypesIndexCallable(Log logger, OrcsTypesIndexer indexer, OrcsTypesResourceProvider provider) {
      super();
      this.logger = logger;
      this.indexer = indexer;
      this.provider = provider;
   }

   @Override
   public OrcsTypesIndex call() throws Exception {
      return indexer.index(provider.getOrcsTypesResource());
   }
}