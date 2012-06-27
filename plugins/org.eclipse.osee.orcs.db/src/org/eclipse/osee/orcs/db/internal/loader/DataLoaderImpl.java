/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.AttributeDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.RelationDataHandler;
import org.eclipse.osee.orcs.db.internal.search.SqlContext;

/**
 * @author Andrew M. Finkbeiner
 */
public class DataLoaderImpl implements DataLoader {

   private static final int MAX_FETCH_SIZE = 10000;

   private final IOseeDatabaseService oseeDatabaseService;
   private final ArtifactLoader artifactLoader;
   private final AttributeLoader attributeLoader;
   private final RelationLoader relationLoader;
   private final IdFactory idFactory;

   public DataLoaderImpl(IOseeDatabaseService oseeDatabaseService, IdFactory idFactory, ArtifactLoader artifactLoader, AttributeLoader attributeLoader, RelationLoader relationLoader) {
      super();
      this.oseeDatabaseService = oseeDatabaseService;
      this.idFactory = idFactory;
      this.artifactLoader = artifactLoader;
      this.attributeLoader = attributeLoader;
      this.relationLoader = relationLoader;
   }

   private SqlContext toSqlContext(QueryContext queryContext) throws OseeCoreException {
      SqlContext sqlContext = null;
      if (queryContext instanceof SqlContext) {
         sqlContext = (SqlContext) queryContext;
      } else {
         throw new OseeCoreException("Invalid query context type [%s] - expected SqlContext",
            queryContext.getClass().getName());
      }
      return sqlContext;
   }

   @Override
   public int countArtifacts(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException {
      SqlContext sqlContext = toSqlContext(queryContext);
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         join.store();
      }
      String query = sqlContext.getSql();
      List<Object> params = sqlContext.getParameters();
      try {
         checkCancelled(cancellation);

         return oseeDatabaseService.runPreparedQueryFetchObject(-1, query, params.toArray());
      } finally {
         for (AbstractJoinQuery join : sqlContext.getJoins()) {
            join.delete();
         }
      }
   }

   private void checkCancelled(HasCancellation cancellation) throws CancellationException {
      if (cancellation != null) {
         cancellation.checkForCancelled();
      }
   }

   @Override
   public void loadArtifacts(HasCancellation cancellation, ArtifactBuilder builder, IOseeBranch branch, Collection<Integer> artIds, LoadOptions loadOptions) throws OseeCoreException {
      if (!artIds.isEmpty()) {
         int branchId = idFactory.getBranchId(branch);
         int fetchSize = computeFetchSize(artIds.size());

         ArtifactJoinQuery join = JoinUtility.createArtifactJoinQuery(oseeDatabaseService);
         Integer transactionId = -1;
         for (Integer artId : artIds) {
            join.add(artId, branchId, transactionId);
         }

         try {
            join.store();
            loadArtifacts(cancellation, builder, join, fetchSize, loadOptions);
         } finally {
            join.delete();
         }
      }
   }

   @Override
   public void loadArtifacts(HasCancellation cancellation, ArtifactBuilder builder, QueryContext queryContext, LoadOptions loadOptions) throws OseeCoreException {
      SqlContext sqlContext = toSqlContext(queryContext);
      int fetchSize = computeFetchSize(sqlContext);

      AbstractJoinQuery join = createArtifactIdJoin(cancellation, sqlContext, fetchSize);

      try {
         join.store();
         loadArtifacts(cancellation, builder, join, fetchSize, loadOptions);
      } finally {
         join.delete();
      }
   }

   private void loadArtifacts(HasCancellation cancellation, ArtifactBuilder builder, AbstractJoinQuery join, int fetchSize, LoadOptions loadOptions) throws OseeCoreException {
      int queryId = join.getQueryId();

      checkCancelled(cancellation);

      ArtifactDataHandler artHandler = builder.createArtifactDataHandler();
      artifactLoader.loadFromQueryId(artHandler, loadOptions, fetchSize, queryId);

      checkCancelled(cancellation);

      if (isAttributeLoadingAllowed(loadOptions.getLoadLevel())) {
         AttributeDataHandler attrHandler = builder.createAttributeDataHandler();
         attributeLoader.loadFromQueryId(attrHandler, loadOptions, fetchSize, queryId);
      }

      checkCancelled(cancellation);

      if (isRelationLoadingAllowed(loadOptions.getLoadLevel())) {
         RelationDataHandler relHandler = builder.createRelationDataHandler();
         relationLoader.loadFromQueryId(relHandler, loadOptions, fetchSize, queryId);
      }
   }

   private boolean isAttributeLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.SHALLOW && level != LoadLevel.RELATION;
   }

   private boolean isRelationLoadingAllowed(LoadLevel level) {
      return level != LoadLevel.SHALLOW && level != LoadLevel.ATTRIBUTE;
   }

   private int computeFetchSize(int initialSize) {
      int fetchSize = initialSize;

      if (fetchSize < 10) {
         fetchSize = 10;
      }

      // Account for attribute and relation loading
      fetchSize *= 20;

      if (fetchSize < 0 || fetchSize > MAX_FETCH_SIZE) {
         fetchSize = MAX_FETCH_SIZE;
      }
      return fetchSize;
   }

   private int computeFetchSize(SqlContext sqlContext) {
      int fetchSize = 10;//Integer.MIN_VALUE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      return computeFetchSize(fetchSize);
   }

   private AbstractJoinQuery createArtifactIdJoin(HasCancellation cancellation, SqlContext sqlContext, int fetchSize) throws OseeCoreException {
      ArtifactJoinQuery artifactJoin = JoinUtility.createArtifactJoinQuery(oseeDatabaseService);
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         join.store();
      }
      String query = sqlContext.getSql();
      List<Object> params = sqlContext.getParameters();
      try {
         checkCancelled(cancellation);

         Integer transactionId = -1;
         IOseeStatement chStmt = oseeDatabaseService.getStatement();
         try {
            chStmt.runPreparedQuery(fetchSize, query, params.toArray());
            while (chStmt.next()) {
               Integer artId = chStmt.getInt("art_id");
               Integer branchId = chStmt.getInt("branch_id");
               if (sqlContext.getOptions().isHistorical()) {
                  transactionId = chStmt.getInt("transaction_id");
               }
               artifactJoin.add(artId, branchId, transactionId);

               checkCancelled(cancellation);
            }
         } finally {
            chStmt.close();
         }
      } finally {
         for (AbstractJoinQuery join : sqlContext.getJoins()) {
            join.delete();
         }
      }
      return artifactJoin;
   }

}
