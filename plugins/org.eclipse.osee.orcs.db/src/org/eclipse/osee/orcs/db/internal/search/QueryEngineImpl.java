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
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.SqlBuilder.QueryType;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public class QueryEngineImpl implements QueryEngine {

   private SqlHandlerFactory handlerFactory;
   private SqlBuilder builder;

   private SqlProvider sqlProvider;
   private IOseeDatabaseService dbService;
   private IdentityService identityService;

   private BranchCache branchCache;
   private Log logger;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setSqlProvider(SqlProvider sqlProvider) {
      this.sqlProvider = sqlProvider;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void start() {
      handlerFactory = new SqlHandlerFactoryImpl(identityService);
      builder = new SqlBuilder(sqlProvider, dbService);
   }

   public void stop() {
      handlerFactory = null;
      builder = null;
   }

   public SqlContext createContext(String sessionId, QueryOptions options) {
      return new SqlContext(sessionId, options);
   }

   @Override
   public Object createCount(String sessionId, CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      return createQuery(sessionId, criteriaSet, options, QueryType.COUNT_ARTIFACTS);
   }

   @Override
   public Object create(String sessionId, CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      return createQuery(sessionId, criteriaSet, options, QueryType.SELECT_ARTIFACTS);
   }

   private SqlContext createQuery(String sessionId, CriteriaSet criteriaSet, QueryOptions options, QueryType queryType) throws OseeCoreException {
      IOseeBranch branch = criteriaSet.getBranch();
      int branchId = branchCache.getLocalId(branch);

      List<SqlHandler> handlers = handlerFactory.createHandlers(criteriaSet);
      SqlContext context = createContext(sessionId, options);
      builder.generateSql(context, branchId, handlers, queryType);

      if (logger.isTraceEnabled()) {
         logger.trace("SessionId:[%s] Query:[%s] Parameters:[%s]", sessionId, context.getSql(), context.getParameters());
      }

      return context;
   }

}
