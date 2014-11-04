/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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

/**
 * @author Roberto E. Escobar
 */
public class ObjectQuerySqlContextFactoryImpl implements QuerySqlContextFactory {

   private final Log logger;
   private final SqlHandlerFactory handlerFactory;
   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;

   public ObjectQuerySqlContextFactoryImpl(Log logger, IOseeDatabaseService dbService, SqlProvider sqlProvider, SqlHandlerFactory handlerFactory) {
      super();
      this.logger = logger;
      this.dbService = dbService;
      this.sqlProvider = sqlProvider;
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
      QuerySqlContext context = new QuerySqlContext(session, queryData.getOptions(), ObjectQueryType.DYNAMIC_OBJECT);
      AbstractSqlWriter writer =
         new ObjectQuerySqlWriter(logger, dbService, sqlProvider, context, queryType, queryData);
      List<SqlHandler<?>> handlers = handlerFactory.createHandlers(queryData.getCriteriaSets());
      writer.build(handlers);
      return context;
   }

}
