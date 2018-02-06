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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlContextFactoryImpl implements QuerySqlContextFactory {

   private final Log logger;
   private final SqlHandlerFactory handlerFactory;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;

   public ArtifactQuerySqlContextFactoryImpl(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlHandlerFactory handlerFactory) {
      super();
      this.logger = logger;
      this.joinFactory = joinFactory;
      this.jdbcClient = jdbcClient;
      this.handlerFactory = handlerFactory;
   }

   @Override
   public QuerySqlContext createQueryContext(OrcsSession session, QueryData queryData, QueryType queryType) {
      QuerySqlContext context = createContext(session, queryData);
      AbstractSqlWriter writer = createQueryWriter(context, queryData, queryType);
      writer.build(handlerFactory.createHandlers(queryData));
      return context;
   }

   private QuerySqlContext createContext(OrcsSession session, QueryData queryData) {
      BranchId branch = getBranchToSearch(queryData);
      Conditions.checkNotNull(branch, "branch");
      return new ArtifactQuerySqlContext(session, branch, queryData.getOptions());
   }

   private AbstractSqlWriter createQueryWriter(SqlContext context, QueryData queryData, QueryType queryType) {
      BranchId branch = getBranchToSearch(queryData);
      Conditions.checkNotNull(branch, "branch");
      return new ArtifactQuerySqlWriter(logger, joinFactory, jdbcClient, context, queryType, branch);
   }

   private BranchId getBranchToSearch(QueryData queryData) {
      BranchId branch = BranchId.SENTINEL;

      Iterable<? extends Criteria> criterias = queryData.getAllCriteria();
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