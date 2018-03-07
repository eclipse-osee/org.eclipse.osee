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

import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * Used to write branch and transaction queries
 *
 * @author Roberto E. Escobar
 */
public class QuerySqlWriter extends AbstractSqlWriter {
   private final String idColumn;
   private final TableEnum table;
   private String tableAlias;

   public QuerySqlWriter(Log logger, SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryType queryType, TableEnum table, String idColumn) {
      super(joinFactory, jdbcClient, context, queryType);
      this.idColumn = idColumn;
      this.table = table;
   }

   @Override
   protected void writeSelectFields(Iterable<SqlHandler<?>> handlers) {
      tableAlias = getMainTableAlias(table);
      writeCommaIfNotFirst();
      write(tableAlias);
      write(".*");
   }

   @Override
   public void writeGroupAndOrder() {
      if (!isCountQueryType()) {
         write("\n ORDER BY %s.%s", tableAlias, idColumn);
      }
   }

   @Override
   public String getTxBranchFilter(String txsAlias) {
      return Strings.emptyString();
   }

   @Override
   public String getTxBranchFilter(String txsAlias, boolean allowDeleted) {
      return Strings.emptyString();
   }

   @Override
   public String getWithClauseTxBranchFilter(String txsAlias, boolean deletedPredicate) {
      return Strings.emptyString();
   }
}