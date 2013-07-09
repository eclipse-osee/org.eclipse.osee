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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private final Log logger;
   private final BranchCache branchCache;
   private final SqlHandlerFactory handlerFactory;
   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;

   public QueryEngineImpl(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, BranchCache branchCache, SqlHandlerFactory handlerFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
      this.branchCache = branchCache;
      this.handlerFactory = handlerFactory;
   }

   @Override
   public QuerySqlContext createCount(OrcsSession session, QueryData queryData) throws OseeCoreException {
      return createQuery(session, queryData, QueryType.COUNT_ARTIFACTS);
   }

   @Override
   public QuerySqlContext create(OrcsSession session, QueryData queryData) throws OseeCoreException {
      return createQuery(session, queryData, QueryType.SELECT_ARTIFACTS);
   }

   private QuerySqlContext createQuery(OrcsSession session, QueryData queryData, QueryType queryType) throws OseeCoreException {
      QuerySqlContext context = createContext(session, queryData.getOptions());
      CriteriaSet criteriaSet = queryData.getCriteriaSet();

      AbstractSqlWriter<QueryOptions> writer = createQueryWriter(context, queryType, criteriaSet.getBranch());

      List<SqlHandler<?, QueryOptions>> handlers = handlerFactory.createHandlers(criteriaSet);
      writer.build(handlers);
      return context;
   }

   private QuerySqlContext createContext(OrcsSession session, QueryOptions options) {
      return new QuerySqlContext(session, options);
   }

   private AbstractSqlWriter<QueryOptions> createQueryWriter(SqlContext<QueryOptions, QueryPostProcessor> context, QueryType queryType, IOseeBranch branch) throws OseeCoreException {
      int branchId = branchCache.getLocalId(branch);
      return new QuerySqlWriter(logger, dbService, sqlProvider, context, queryType, branchId);
   }

}
