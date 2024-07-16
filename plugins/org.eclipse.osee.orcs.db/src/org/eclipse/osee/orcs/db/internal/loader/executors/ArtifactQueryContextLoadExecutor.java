/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.executors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.LoadSqlContext;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.search.engines.ArtifactQuerySqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactQueryContextLoadExecutor extends AbstractLoadExecutor {

   private final ArtifactQuerySqlContext queryContext;
   private final SqlJoinFactory joinFactory;

   public ArtifactQueryContextLoadExecutor(SqlObjectLoader loader, JdbcClient jdbcClient, SqlJoinFactory joinFactory, ArtifactQuerySqlContext queryContext) {
      super(loader, jdbcClient);
      this.queryContext = queryContext;
      this.joinFactory = joinFactory;
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) {
      int fetchSize = computeFetchSize(queryContext);

      Id4JoinQuery join = createId4Join(getJdbcClient(), cancellation, fetchSize);

      OptionsUtil.setFromBranchView(options, queryContext.getBranch().getViewId());
      LoadSqlContext loadContext = new LoadSqlContext(queryContext.getSession(), options, queryContext.getBranch());
      getLoader().loadArtifacts(cancellation, handler, join, criteria, loadContext, fetchSize);
   }

   private int computeFetchSize(SqlContext sqlContext) {
      int fetchSize = RelationalConstants.MIN_FETCH_SIZE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      return LoadUtil.computeFetchSize(fetchSize);
   }

   private Id4JoinQuery createId4Join(JdbcClient jdbcClient, HasCancellation cancellation, int fetchSize) {
      Id4JoinQuery artifactJoin = joinFactory.createId4JoinQuery();
      try {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            join.store();
            checkCancelled(cancellation);
         }
         TransactionId transactionId = OptionsUtil.getFromTransaction(queryContext.getOptions());
         ArtifactId viewId = OptionsUtil.getFromBranchView(queryContext.getOptions());
         Set<ArtifactId> ids = new HashSet<>();
         Consumer<JdbcStatement> consumer = stmt -> {
            checkCancelled(cancellation);
            ArtifactId artifact = ArtifactId.valueOf(stmt.getLong("art_id"));
            BranchId branch = BranchId.valueOf(stmt.getLong("branch_id"));
            // Do not add more than once to join table
            if (!ids.contains(artifact)) {
               artifactJoin.add(branch, artifact, transactionId, viewId);
               ids.add(artifact);
            }
            checkCancelled(cancellation);
         };
         checkCancelled(cancellation);
         String query = queryContext.getSql();
         List<Object> params = queryContext.getParameters();
         getJdbcClient().runQuery(consumer, fetchSize, query, params.toArray());
      } finally {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            try {
               join.close();
            } catch (OseeCoreException ex) {
               // Ensure we try to delete all
            }
         }
      }
      return artifactJoin;
   }

}
