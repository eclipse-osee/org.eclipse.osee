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

   private final QueryCallableFactory artifactQueryEngineFactory;
   private final QueryCallableFactory branchQueryEngineFactory;
   private final QueryCallableFactory txQueryEngineFactory;

   public QueryEngineImpl(QueryCallableFactory artifactQueryEngineFactory, QueryCallableFactory branchQueryEngineFactory, QueryCallableFactory txQueryEngineFactory) {
      super();
      this.artifactQueryEngineFactory = artifactQueryEngineFactory;
      this.branchQueryEngineFactory = branchQueryEngineFactory;
      this.txQueryEngineFactory = txQueryEngineFactory;
   }

   @Override
   public CancellableCallable<Integer> createArtifactCount(OrcsSession session, QueryData queryData) {
      return artifactQueryEngineFactory.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createArtifactQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return artifactQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public CancellableCallable<Integer> createBranchCount(OrcsSession session, QueryData queryData) {
      return branchQueryEngineFactory.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createBranchQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return branchQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public CancellableCallable<Integer> createTxCount(OrcsSession session, QueryData queryData) {
      return txQueryEngineFactory.createCount(session, queryData);
   }

   @Override
   public CancellableCallable<Integer> createTxQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return txQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public CancellableCallable<Integer> createQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return null;
   }

   @Override
   public CancellableCallable<Integer> createQueryCount(OrcsSession session, QueryData queryData) {
      return null;
   }

}
