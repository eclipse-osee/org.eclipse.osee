/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.executors;

import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
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
import org.eclipse.osee.orcs.db.internal.sql.join.ArtifactJoinQuery;
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
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) throws OseeCoreException {
      int fetchSize = computeFetchSize(queryContext);

      ArtifactJoinQuery join = createArtifactIdJoin(getJdbcClient(), cancellation, fetchSize);

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

   private ArtifactJoinQuery createArtifactIdJoin(JdbcClient jdbcClient, HasCancellation cancellation, int fetchSize) throws OseeCoreException {
      ArtifactJoinQuery artifactJoin = joinFactory.createArtifactJoinQuery();
      try {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            join.store();
            checkCancelled(cancellation);
         }
         TransactionId transactionId = OptionsUtil.getFromTransaction(queryContext.getOptions());
         Consumer<JdbcStatement> consumer = stmt -> {
            checkCancelled(cancellation);
            Integer artId = stmt.getInt("art_id");
            BranchId branchUuid = BranchId.valueOf(stmt.getLong("branch_id"));
            artifactJoin.add(artId, branchUuid, transactionId);
            checkCancelled(cancellation);
         };
         checkCancelled(cancellation);
         String query = queryContext.getSql();
         List<Object> params = queryContext.getParameters();
         getJdbcClient().runQuery(consumer, fetchSize, query, params.toArray());
      } finally {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            try {
               join.delete();
            } catch (OseeCoreException ex) {
               // Ensure we try to delete all
            }
         }
      }
      return artifactJoin;
   }

}
