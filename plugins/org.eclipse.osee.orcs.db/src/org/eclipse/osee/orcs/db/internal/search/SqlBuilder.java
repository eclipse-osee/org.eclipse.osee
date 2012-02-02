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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class SqlBuilder {

   private final SqlProvider sqlProvider;
   private final IOseeDatabaseService dbService;

   public static enum QueryType {
      COUNT_ARTIFACTS,
      SELECT_ARTIFACTS;
   }

   public SqlBuilder(SqlProvider sqlProvider, IOseeDatabaseService dbService) {
      super();
      this.sqlProvider = sqlProvider;
      this.dbService = dbService;
   }

   public void generateSql(SqlContext context, int branchId, List<SqlHandler> handlers, QueryType queryType) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(handlers, "SqlHandlers");

      StringBuilder output = new StringBuilder();
      SqlWriter writer = new SqlWriter(dbService, branchId, context, output);
      List<String> paramList = buildSql(writer, handlers, queryType);

      String sql = output.toString();
      String query = updateSelect(sql, paramList);
      context.setSql(query);
   }

   private List<String> buildSql(SqlWriter writer, List<SqlHandler> handlers, QueryType queryType) throws OseeCoreException {
      if (queryType == QueryType.COUNT_ARTIFACTS) {
         writer.writeCountSelect();
      } else {
         writer.writeSelect();
      }
      writer.write("\n FROM \n");
      writer.writeTables(handlers);
      writer.write("\n WHERE \n");
      writer.writePredicates(handlers);
      writer.writeGroupAndOrder(queryType);

      List<String> paramList = new ArrayList<String>();
      paramList.add(sqlProvider.getSql(OseeSql.QUERY_BUILDER));
      paramList.add("art1");
      if (queryType != QueryType.COUNT_ARTIFACTS) {
         paramList.add("txs1");
      } else {
         if (writer.getOptions().isHistorical()) {
            paramList.add("txs1");
         }
      }
      return paramList;
   }

   private String updateSelect(String sql, List<String> paramList) throws OseeWrappedException {
      String query = null;
      try {
         query = String.format(sql, paramList.toArray());
      } catch (Exception ex) {
         String message = String.format("Error formatting SQL:[%s] Data:[%s]", sql, paramList);
         throw new OseeWrappedException(message, ex);
      }
      return query;
   }

}
