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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.db.internal.loader.LoadSqlContext;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.loader.SqlArtifactLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;

/**
 * @author Andrew M. Finkbeiner
 */
public class QueryContextLoadExecutor extends AbstractLoadExecutor {

   private final QuerySqlContext queryContext;

   public QueryContextLoadExecutor(SqlArtifactLoader loader, IOseeDatabaseService dbService, QuerySqlContext queryContext) {
      super(loader, dbService);
      this.queryContext = queryContext;
   }

   @Override
   public void load(HasCancellation cancellation, ArtifactBuilder builder, CriteriaOrcsLoad criteria, LoadOptions options) throws OseeCoreException {
      int fetchSize = computeFetchSize(queryContext);

      ArtifactJoinQuery join = createArtifactIdJoin(getDatabaseService(), cancellation, fetchSize);
      LoadSqlContext loadContext = new LoadSqlContext(queryContext.getSessionId(), options);
      loadFromJoin(join, cancellation, builder, criteria, loadContext, fetchSize);
   }

   private int computeFetchSize(QuerySqlContext sqlContext) {
      int fetchSize = RelationalConstants.MIN_FETCH_SIZE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      return computeFetchSize(fetchSize);
   }

   private ArtifactJoinQuery createArtifactIdJoin(IOseeDatabaseService dbService, HasCancellation cancellation, int fetchSize) throws OseeCoreException {
      ArtifactJoinQuery artifactJoin = JoinUtility.createArtifactJoinQuery(dbService);
      for (AbstractJoinQuery join : queryContext.getJoins()) {
         join.store();
      }
      String query = queryContext.getSql();
      List<Object> params = queryContext.getParameters();
      try {
         checkCancelled(cancellation);

         Integer transactionId = -1;
         IOseeStatement chStmt = dbService.getStatement();
         try {
            chStmt.runPreparedQuery(fetchSize, query, params.toArray());
            while (chStmt.next()) {
               checkCancelled(cancellation);
               Integer artId = chStmt.getInt("art_id");
               Integer branchId = chStmt.getInt("branch_id");
               if (queryContext.getOptions().isHistorical()) {
                  transactionId = chStmt.getInt("transaction_id");
               }
               artifactJoin.add(artId, branchId, transactionId);
            }
         } finally {
            chStmt.close();
         }
      } finally {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            join.delete();
         }
      }
      return artifactJoin;
   }

}
