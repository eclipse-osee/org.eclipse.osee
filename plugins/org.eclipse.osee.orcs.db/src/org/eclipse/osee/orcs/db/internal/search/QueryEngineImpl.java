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
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.db.internal.search.SqlBuilder.QueryType;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private final Log logger;
   private final BranchCache branchCache;
   private final SqlHandlerFactory handlerFactory;
   private final SqlBuilder builder;

   public QueryEngineImpl(Log logger, BranchCache branchCache, SqlHandlerFactory handlerFactory, SqlBuilder builder) {
      super();
      this.logger = logger;
      this.branchCache = branchCache;
      this.handlerFactory = handlerFactory;
      this.builder = builder;
   }

   public SqlContext createContext(String sessionId, QueryOptions options) {
      return new SqlContext(sessionId, options);
   }

   @Override
   public QueryContext createCount(String sessionId, QueryData queryData) throws OseeCoreException {
      return createQuery(sessionId, queryData, QueryType.COUNT_ARTIFACTS);
   }

   @Override
   public QueryContext create(String sessionId, QueryData queryData) throws OseeCoreException {
      return createQuery(sessionId, queryData, QueryType.SELECT_ARTIFACTS);
   }

   private SqlContext createQuery(String sessionId, QueryData queryData, QueryType queryType) throws OseeCoreException {
      IOseeBranch branch = queryData.getCriteriaSet().getBranch();
      int branchId = branchCache.getLocalId(branch);

      List<SqlHandler> handlers = handlerFactory.createHandlers(queryData.getCriteriaSet());
      SqlContext context = createContext(sessionId, queryData.getOptions());
      builder.generateSql(context, branchId, handlers, queryType);

      if (logger.isTraceEnabled()) {
         logger.trace("SessionId:[%s] Query:[%s] Parameters:[%s]", sessionId, context.getSql(), context.getParameters());
      }

      return context;
   }

}
