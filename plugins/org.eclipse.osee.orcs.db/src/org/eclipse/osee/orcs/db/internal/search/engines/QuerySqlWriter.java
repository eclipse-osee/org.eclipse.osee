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

package org.eclipse.osee.orcs.db.internal.search.engines;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * Used to write branch and transaction queries
 *
 * @author Roberto E. Escobar
 */
public class QuerySqlWriter extends AbstractSqlWriter {
   private final String idColumn;
   private final SqlTable table;
   private String tableAlias;

   public QuerySqlWriter(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryData queryData, SqlTable table, String idColumn) {
      super(joinFactory, jdbcClient, context, queryData);
      this.idColumn = idColumn;
      this.table = table;
   }

   @Override
   protected void writeSelectFields() {
      tableAlias = getMainTableAlias(table);
      writeSelectFields(tableAlias, "*");
   }

   @Override
   public void writeGroupAndOrder() {
      if (!rootQueryData.isCountQueryType()) {
         write("\n ORDER BY %s.%s", tableAlias, idColumn);
      }
   }

   @Override
   public void writeTxBranchFilter(String txsAlias, boolean allowDeleted) {
   }
}