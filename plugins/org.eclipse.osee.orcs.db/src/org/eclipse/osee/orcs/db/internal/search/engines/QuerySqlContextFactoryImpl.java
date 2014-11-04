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
package org.eclipse.osee.orcs.db.internal.search.engines;

import java.util.List;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext.ObjectQueryType;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class QuerySqlContextFactoryImpl implements QuerySqlContextFactory {

   private final Log logger;
   private final SqlHandlerFactory handlerFactory;
   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;
   private final TableEnum table;
   private final String idColumn;
   private final ObjectQueryType type;

   public QuerySqlContextFactoryImpl(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlHandlerFactory handlerFactory, TableEnum table, String idColumn, ObjectQueryType type) {
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
      this.handlerFactory = handlerFactory;
      this.table = table;
      this.idColumn = idColumn;
      this.type = type;
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
      QuerySqlContext context = new QuerySqlContext(session, queryData.getOptions(), type);
      AbstractSqlWriter writer =
         new QuerySqlWriter(logger, dbService, sqlProvider, context, queryType, table, idColumn);
      List<SqlHandler<?>> handlers = handlerFactory.createHandlers(queryData.getCriteriaSets());
      writer.build(handlers);
      return context;
   }

}
