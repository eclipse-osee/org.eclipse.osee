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

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
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
      this.logger = logger;
      this.joinFactory = joinFactory;
      this.jdbcClient = jdbcClient;
      this.handlerFactory = handlerFactory;
   }

   @Override
   public QuerySqlContext createQueryContext(OrcsSession session, QueryData queryData, QueryType queryType) {
      QuerySqlContext context = new ArtifactQuerySqlContext(session, queryData);

      AbstractSqlWriter writer =
         new ArtifactQuerySqlWriter(logger, joinFactory, jdbcClient, context, queryType, queryData.getBranch());
      writer.build(handlerFactory.createHandlers(queryData));
      return context;
   }
}