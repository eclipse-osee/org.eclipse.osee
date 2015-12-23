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

import com.google.common.base.Stopwatch;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
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
      Stopwatch stopwatch = new Stopwatch();
      stopwatch.start();
      OrcsTypesIndex index = null;
      try {
         index = createIndex();
      } finally {
         logger.trace("Created OrcsTypesIndex in [%s]", Lib.getElapseString(stopwatch.elapsedMillis()));
         stopwatch.stop();
      }
      return index;
   }

   private OrcsTypesIndex createIndex() throws Exception {
      long startTime = System.currentTimeMillis();
      IResource source = provider.getOrcsTypesResource();
      checkForCancelled();
      logger.trace("Read OrcsTypes from datastore in [%s]", Lib.getElapseString(startTime));
      OrcsTypesIndex index = indexer.index(source);
      return index;
   }
}
