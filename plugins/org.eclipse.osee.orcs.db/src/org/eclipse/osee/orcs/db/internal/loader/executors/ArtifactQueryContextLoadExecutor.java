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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.db.internal.loader.LoadSqlContext;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.search.engines.ArtifactQuerySqlContext;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;
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

   public ArtifactQueryContextLoadExecutor(SqlObjectLoader loader, IOseeDatabaseService dbService, SqlJoinFactory joinFactory, ArtifactQuerySqlContext queryContext) {
      super(loader, dbService);
      this.queryContext = queryContext;
      this.joinFactory = joinFactory;
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) throws OseeCoreException {
      int fetchSize = computeFetchSize(queryContext);

      ArtifactJoinQuery join = createArtifactIdJoin(getDatabaseService(), cancellation, fetchSize);

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

   private ArtifactJoinQuery createArtifactIdJoin(IOseeDatabaseService dbService, HasCancellation cancellation, int fetchSize) throws OseeCoreException {
      ArtifactJoinQuery artifactJoin = joinFactory.createArtifactJoinQuery();
      try {
         for (AbstractJoinQuery join : queryContext.getJoins()) {
            join.store();
            checkCancelled(cancellation);
         }
         Integer transactionId = OptionsUtil.getFromTransaction(queryContext.getOptions());
         IOseeStatement chStmt = null;
         try {
            chStmt = dbService.getStatement();
            checkCancelled(cancellation);
            String query = queryContext.getSql();
            List<Object> params = queryContext.getParameters();
            chStmt.runPreparedQuery(fetchSize, query, params.toArray());
            while (chStmt.next()) {
               checkCancelled(cancellation);
               Integer artId = chStmt.getInt("art_id");
               Long branchUuid = chStmt.getLong("branch_id");
               artifactJoin.add(artId, branchUuid, transactionId);
               checkCancelled(cancellation);
            }
         } finally {
            Lib.close(chStmt);
         }
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
