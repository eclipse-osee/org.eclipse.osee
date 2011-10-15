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

import java.util.List;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.ArtifactRowHandler;
import org.eclipse.osee.orcs.core.ds.AttributeRowHandlerFactory;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.RelationRowHandlerFactory;
import org.eclipse.osee.orcs.db.internal.search.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;

public class DataLoaderImpl implements DataLoader {

   private static final int MAX_FETCH_SIZE = 10000;

   private Log logger;
   private StaticSqlProvider sqlProvider;
   private IOseeDatabaseService oseeDatabaseService;
   private IdentityService identityService;
   private SystemPreferences preferences;
   private DataStoreTypeCache dataStoreTypeCache;
   private DataProxyFactoryProvider dataProxyFactoryProvider;

   private ArtifactLoader artifactLoader;
   private AttributeLoader attributeLoader;
   private RelationLoader relationLoader;

   public void start() {
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(logger);
      sqlProvider.setPreferences(preferences);
      artifactLoader = new ArtifactLoader(logger, sqlProvider, oseeDatabaseService, identityService);
      AttributeDataProxyFactory attributeDataProxyFactory =
         new AttributeDataProxyFactory(dataProxyFactoryProvider, dataStoreTypeCache.getAttributeTypeCache());
      attributeLoader =
         new AttributeLoader(sqlProvider, oseeDatabaseService, identityService, attributeDataProxyFactory);
      relationLoader = new RelationLoader(sqlProvider, oseeDatabaseService, identityService);
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSystemPreferences(SystemPreferences preferences) {
      this.preferences = preferences;
   }

   public void setOseeDatabaseService(IOseeDatabaseService oseeDatabaseService) {
      this.oseeDatabaseService = oseeDatabaseService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDataProxyFactoryProvider(DataProxyFactoryProvider dataProxyFactoryProvider) {
      this.dataProxyFactoryProvider = dataProxyFactoryProvider;
   }

   public void setDataStoreTypeCache(DataStoreTypeCache dataStoreTypeCache) {
      this.dataStoreTypeCache = dataStoreTypeCache;
   }

   private SqlContext toSqlContext(QueryContext queryContext) throws OseeCoreException {
      SqlContext sqlContext = null;
      if (queryContext instanceof SqlContext) {
         sqlContext = (SqlContext) queryContext;
      } else {
         throw new OseeCoreException(String.format("Invalid data store context type[%s], expected SqlContext.",
            queryContext.getClass().getName()));
      }
      return sqlContext;
   }

   @Override
   public int countArtifacts(QueryContext queryContext) throws OseeCoreException {
      SqlContext sqlContext = toSqlContext(queryContext);
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         join.store();
      }
      String query = sqlContext.getSql();
      List<Object> params = sqlContext.getParameters();
      try {
         return oseeDatabaseService.runPreparedQueryFetchObject(-1, query, params.toArray());
      } finally {
         for (AbstractJoinQuery join : sqlContext.getJoins()) {
            join.delete();
         }
      }
   }

   @Override
   public void loadArtifacts(ArtifactRowHandler handler, QueryContext queryContext, LoadOptions loadOptions, RelationRowHandlerFactory relationRowHandlerFactory, AttributeRowHandlerFactory attributeRowHandlerFactory) throws OseeCoreException {
      SqlContext sqlContext = toSqlContext(queryContext);
      int fetchSize = computeFetchSize(sqlContext);

      ArtifactJoinQuery join = JoinUtility.createArtifactJoinQuery(oseeDatabaseService);
      populateArtifactJoin(join, sqlContext, fetchSize);
      try {
         join.store();
         artifactLoader.loadFromQueryId(handler, loadOptions, fetchSize, join.getQueryId());
         if (loadOptions.getLoadLevel() == LoadLevel.ATTRIBUTE || loadOptions.getLoadLevel() == LoadLevel.ALL_CURRENT || loadOptions.getLoadLevel() == LoadLevel.FULL) {
            attributeLoader.loadFromQueryId(attributeRowHandlerFactory.createAttributeRowHandler(), loadOptions,
               fetchSize, join.getQueryId());
         }
         if (loadOptions.getLoadLevel() == LoadLevel.RELATION || loadOptions.getLoadLevel() == LoadLevel.ALL_CURRENT || loadOptions.getLoadLevel() == LoadLevel.FULL) {
            relationLoader.loadFromQueryId(relationRowHandlerFactory.createRelationRowHandler(), loadOptions,
               fetchSize, join.getQueryId());
         }
      } finally {
         join.delete();
      }
   }

   private int computeFetchSize(SqlContext sqlContext) {
      int fetchSize = Integer.MIN_VALUE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      if (fetchSize < 0 || fetchSize > MAX_FETCH_SIZE) {
         fetchSize = MAX_FETCH_SIZE;
      }
      return fetchSize;
   }

   private void populateArtifactJoin(ArtifactJoinQuery artifactJoin, SqlContext sqlContext, int fetchSize) throws OseeCoreException {
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         join.store();
      }
      String query = sqlContext.getSql();
      List<Object> params = sqlContext.getParameters();
      try {
         IOseeStatement chStmt = oseeDatabaseService.getStatement();
         try {
            chStmt.runPreparedQuery(fetchSize, query, params.toArray());
            while (chStmt.next()) {
               Integer artId = chStmt.getInt("art_id");
               Integer branchId = chStmt.getInt("branch_id");
               Integer transactionId = -1;
               if (sqlContext.getOptions().isHistorical()) {
                  transactionId = chStmt.getInt("transaction_id");
               }
               artifactJoin.add(artId, branchId, transactionId);
            }
         } finally {
            chStmt.close();
         }
      } finally {
         for (AbstractJoinQuery join : sqlContext.getJoins()) {
            join.delete();
         }
      }
   }

}
