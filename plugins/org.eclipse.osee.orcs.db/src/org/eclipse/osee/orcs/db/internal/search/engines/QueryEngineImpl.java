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
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {
   private final QueryCallableFactory artifactQueryEngineFactory;
   private final QueryCallableFactory branchQueryEngineFactory;
   private final QueryCallableFactory txQueryEngineFactory;
   private final QueryCallableFactory allQueryEngineFactory;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;

   public QueryEngineImpl(QueryCallableFactory artifactQueryEngineFactory, QueryCallableFactory branchQueryEngineFactory, QueryCallableFactory txQueryEngineFactory, QueryCallableFactory allQueryEngineFactory, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory) {
      super();
      this.artifactQueryEngineFactory = artifactQueryEngineFactory;
      this.branchQueryEngineFactory = branchQueryEngineFactory;
      this.txQueryEngineFactory = txQueryEngineFactory;
      this.allQueryEngineFactory = allQueryEngineFactory;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
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
      return allQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public CancellableCallable<Integer> createQueryCount(OrcsSession session, QueryData queryData) {
      return allQueryEngineFactory.createCount(session, queryData);
   }

   @Override
   public TupleQuery createTupleQuery() {
      return new TupleQueryImpl(jdbcClient, sqlJoinFactory);
   }

   @Override
   public ApplicabilityDsQuery createApplicabilityDsQuery() {
      return new ApplicabilityDsQueryImpl(jdbcClient, sqlJoinFactory);
   }
}