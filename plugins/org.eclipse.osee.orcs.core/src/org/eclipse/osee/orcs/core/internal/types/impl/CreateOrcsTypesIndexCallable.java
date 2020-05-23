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

import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesResourceProvider;

/**
 * @author Roberto E. Escobar
 */
public class CreateOrcsTypesIndexCallable extends CancellableCallable<OrcsTypesIndex> {

   private final OrcsTypesIndexer indexer;
   private final OrcsTypesResourceProvider provider;

   public CreateOrcsTypesIndexCallable(Log logger, OrcsTypesIndexer indexer, OrcsTypesResourceProvider provider) {
      super();
      this.indexer = indexer;
      this.provider = provider;
   }

   @Override
   public OrcsTypesIndex call() throws Exception {
      return indexer.index(provider.getOrcsTypesResource());
   }
}