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

import java.util.List;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.HasBranch;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlContextFactoryImpl implements QuerySqlContextFactory {

   private final Log logger;
   private final BranchCache branchCache;
   private final SqlHandlerFactory handlerFactory;
   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;

   public ArtifactQuerySqlContextFactoryImpl(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, BranchCache branchCache, SqlHandlerFactory handlerFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
      this.branchCache = branchCache;
      this.handlerFactory = handlerFactory;
   }

   @Override
   public QuerySqlContext createCountContext(OrcsSession session, QueryData queryData) throws OseeCoreException {
      return createQueryContext(session, queryData, QueryType.COUNT);
   }

   @Override
   public QuerySqlContext createQueryContext(OrcsSession session, QueryData queryData) throws OseeCoreException {
      return createQueryContext(session, queryData, QueryType.SELECT);
   }

   private QuerySqlContext createQueryContext(OrcsSession session, QueryData queryData, QueryType queryType) throws OseeCoreException {
      QuerySqlContext context = createContext(session, queryData);
      CriteriaSet criteriaSet = queryData.getCriteriaSet();

      AbstractSqlWriter writer = createQueryWriter(context, queryData, queryType);
      List<SqlHandler<?>> handlers = handlerFactory.createHandlers(criteriaSet);
      writer.build(handlers);
      return context;
   }

   private QuerySqlContext createContext(OrcsSession session, QueryData queryData) throws OseeCoreException {
      IOseeBranch branch = getBranchToSearch(queryData);
      return new ArtifactQuerySqlContext(session, branch, queryData.getOptions());
   }

   private AbstractSqlWriter createQueryWriter(SqlContext context, QueryData queryData, QueryType queryType) throws OseeCoreException {
      int branchId = -1;
      IOseeBranch branch = getBranchToSearch(queryData);
      if (branch != null) {
         branchId = branchCache.getLocalId(branch);
      }
      return new ArtifactQuerySqlWriter(logger, dbService, sqlProvider, context, queryType, branchId);
   }

   private IOseeBranch getBranchToSearch(QueryData queryData) throws OseeCoreException {
      IOseeBranch branch = null;

      Iterable<? extends Criteria> criterias = queryData.getCriteriaSet();
      Optional<? extends Criteria> item = Iterables.tryFind(criterias, new Predicate<Criteria>() {

         @Override
         public boolean apply(Criteria criteria) {
            return HasBranch.class.isAssignableFrom(criteria.getClass());
         }

      });
      if (item.isPresent()) {
         HasBranch criteria = (HasBranch) item.get();
         branch = criteria.getBranch();
      }
      return branch;
   }

}
