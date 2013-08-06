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
package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private final ArtifactQueryCallableFactory factory1;

   public QueryEngineImpl(ArtifactQueryCallableFactory factory1) {
      super();
      this.factory1 = factory1;
   }

   @Override
   public CancellableCallable<Integer> createArtifactCount(OrcsSession session, QueryData queryData) {
      return factory1.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createArtifactQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return factory1.createQuery(session, queryData, handler);
   }

}
