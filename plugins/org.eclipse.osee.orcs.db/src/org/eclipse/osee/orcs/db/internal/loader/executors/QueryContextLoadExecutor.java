/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.loader.executors;

import static org.eclipse.osee.framework.core.data.RelationalConstants.MIN_FETCH_SIZE;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.SqlObjectLoader;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaOrcsLoad;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext.ObjectQueryType;

/**
 * @author Andrew M. Finkbeiner
 */
public class QueryContextLoadExecutor extends AbstractLoadExecutor {

   private final QuerySqlContext queryContext;

   public QueryContextLoadExecutor(SqlObjectLoader loader, JdbcClient jdbcClient, QuerySqlContext queryContext) {
      super(loader, jdbcClient);
      this.queryContext = queryContext;
   }

   @Override
   public void load(HasCancellation cancellation, LoadDataHandler handler, CriteriaOrcsLoad criteria, Options options) {
      int fetchSize = LoadUtil.computeFetchSize(MIN_FETCH_SIZE);
      ObjectQueryType typeToLoad = queryContext.getOrcsObjectType();
      switch (typeToLoad) {
         case DYNAMIC_OBJECT:
            getLoader().loadDynamicObjects(cancellation, handler, queryContext, fetchSize);
            break;
         default:
            throw new OseeStateException("Unable to determine object to load from [%s]", queryContext);
      }
   }
}