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

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.search.QueryCallableFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {
   private final QueryCallableFactory artifactQueryEngineFactory;
   private final QuerySqlContextFactory branchSqlContextFactory;
   private final QuerySqlContextFactory txSqlContextFactory;
   private final QueryCallableFactory allQueryEngineFactory;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;
   private final QuerySqlContextFactory artifactSqlContextFactory;
   private final SqlObjectLoader sqlObjectLoader;

   public QueryEngineImpl(QueryCallableFactory artifactQueryEngineFactory, QuerySqlContextFactory branchSqlContextFactory, QuerySqlContextFactory txSqlContextFactory, QueryCallableFactory allQueryEngineFactory, JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, QuerySqlContextFactory artifactSqlContextFactory, SqlObjectLoader sqlObjectLoader) {
      this.artifactQueryEngineFactory = artifactQueryEngineFactory;
      this.branchSqlContextFactory = branchSqlContextFactory;
      this.txSqlContextFactory = txSqlContextFactory;
      this.allQueryEngineFactory = allQueryEngineFactory;
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.artifactSqlContextFactory = artifactSqlContextFactory;
      this.sqlObjectLoader = sqlObjectLoader;
   }

   @Override
   public int getArtifactCount(QueryData queryData) {
      if (isPostProcessRequired(queryData)) {
         return artifactQueryEngineFactory.getArtifactCount(queryData);
      }
      return getCount(artifactSqlContextFactory, queryData);
   }

   private boolean isPostProcessRequired(QueryData queryData) {
      return queryData.hasCriteriaType(CriteriaAttributeKeywords.class);
   }

   @Override
   public void runArtifactQuery(QueryData queryData, LoadDataHandler handler) throws Exception {
      artifactQueryEngineFactory.createQuery(null, queryData, handler).call();
      queryData.reset();
   }

   @Override
   public int getBranchCount(QueryData queryData) {
      return getCount(branchSqlContextFactory, queryData);
   }

   @Override
   public void runBranchQuery(QueryData queryData, List<? super Branch> branches) {
      QuerySqlContext queryContext = branchSqlContextFactory.createQueryContext(null, queryData, QueryType.SELECT);
      sqlObjectLoader.loadBranches(branches, queryContext);
      queryData.reset();
   }

   @Override
   public int getTxCount(QueryData queryData) {
      return getCount(txSqlContextFactory, queryData);
   }

   private int getCount(QuerySqlContextFactory sqlContextFactory, QueryData queryData) {
      QuerySqlContext queryContext = sqlContextFactory.createQueryContext(null, queryData, QueryType.COUNT);
      int count = sqlObjectLoader.getCount(queryContext);
      queryData.reset();
      return count;
   }

   @Override
   public void runTxQuery(QueryData queryData, List<? super TransactionReadable> txs) {
      QuerySqlContext queryContext = txSqlContextFactory.createQueryContext(null, queryData, QueryType.SELECT);
      sqlObjectLoader.loadTransactions(txs, queryContext);
      queryData.reset();
   }

   @Override
   public CancellableCallable<Integer> createQuery(OrcsSession session, QueryData queryData, LoadDataHandler handler) {
      return allQueryEngineFactory.createQuery(session, queryData, handler);
   }

   @Override
   public TupleQuery createTupleQuery() {
      return new TupleQueryImpl(jdbcClient, sqlJoinFactory);
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens(QueryData queryData) {
      List<ArtifactToken> tokens = new ArrayList<>(100);
      loadArtifactX(queryData, QueryType.TOKEN, stmt -> tokens.add(ArtifactToken.valueOf(stmt.getLong("art_id"),
         stmt.getString("value"), queryData.getBranch(), ArtifactTypeId.valueOf(stmt.getLong("art_type_id")))));
      return tokens;
   }

   @Override
   public List<ArtifactId> loadArtifactIds(QueryData queryData) {
      List<ArtifactId> ids = new ArrayList<>(100);
      loadArtifactX(queryData, QueryType.ID, stmt -> ids.add(ArtifactId.valueOf(stmt.getLong("art_id"))));
      return ids;
   }

   private void loadArtifactX(QueryData queryData, QueryType queryType, Consumer<JdbcStatement> consumer) {
      ArtifactQuerySqlContext queryContext =
         (ArtifactQuerySqlContext) artifactSqlContextFactory.createQueryContext(null, queryData, queryType);
      jdbcClient.runQuery(consumer, JDBC__MAX_FETCH_SIZE, queryContext.getSql(),
         queryContext.getParameters().toArray());
      queryData.reset();
   }

   @Override
   public ApplicabilityDsQuery createApplicabilityDsQuery() {
      return new ApplicabilityDsQueryImpl(jdbcClient, sqlJoinFactory);
   }
}