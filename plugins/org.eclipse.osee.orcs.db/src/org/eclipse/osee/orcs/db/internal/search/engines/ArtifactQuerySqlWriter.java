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
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlWriter extends AbstractSqlWriter {
   public ArtifactQuerySqlWriter(SqlJoinFactory joinFactory, JdbcClient jdbcClient, SqlContext context, QueryData queryData) {
      super(joinFactory, jdbcClient, context, queryData);
   }

   @Override
   protected void writeSelectFields() {
      String txAlias = getMainTableAlias(OseeDb.TXS_TABLE);
      String artAlias = getMainTableAlias(OseeDb.ARTIFACT_TABLE);

      writeSelectFields(artAlias, "art_id", txAlias, "branch_id");
      if (OptionsUtil.isHistorical(getOptions())) {
         writeSelectFields(txAlias, "transaction_id");
      }
   }

   @Override
   public void writeGroupAndOrder(Iterable<SqlHandler<?>> handlers) {
      if (rootQueryData.isCountQueryType()) {
         if (OptionsUtil.isHistorical(getOptions())) {
            write("\n) xTable");
         }
      } else {
         write("\n ORDER BY %s.art_id, %s.branch_id", getMainTableAlias(OseeDb.ARTIFACT_TABLE),
            getMainTableAlias(OseeDb.TXS_TABLE));
      }
   }
}