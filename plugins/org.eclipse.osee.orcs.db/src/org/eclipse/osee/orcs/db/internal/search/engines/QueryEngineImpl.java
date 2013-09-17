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
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private final QueryCallableFactory factory1;
   private final QueryCallableFactory factory2;

   public QueryEngineImpl(QueryCallableFactory factory1, QueryCallableFactory factory2) {
      super();
      this.factory1 = factory1;
      this.factory2 = factory2;
   }

   @Override
   public CancellableCallable<Integer> createArtifactCount(OrcsSession session, QueryData queryData) {
      return factory1.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createArtifactQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return factory1.createQuery(session, queryData, handler);
   }

   @Override
   public CancellableCallable<Integer> createBranchCount(OrcsSession session, QueryData queryData) {
      return factory2.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createBranchQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return factory2.createQuery(session, queryData, handler);
   }

}
