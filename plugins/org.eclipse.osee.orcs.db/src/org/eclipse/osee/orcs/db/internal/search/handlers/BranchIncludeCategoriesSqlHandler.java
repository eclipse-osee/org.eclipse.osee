/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaIncludeBranchCategories;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class BranchIncludeCategoriesSqlHandler extends SqlHandler<CriteriaIncludeBranchCategories> {
   String branchAlias;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ORDER_BY_NAME.ordinal();
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      branchAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      if (writer.getJdbcClient().getDbType().equals(JdbcDbType.oracle)) {
         writer.write(
            ", listagg(category,',') within group (order by category) over (partition by " + branchAlias + ".branch_Id) categories");
      } else if (writer.getJdbcClient().getDbType().equals(JdbcDbType.postgresql)) {
         writer.write(", string_agg(cast(category as varchar),', ' order by category) categories");
      }
   }

   @Override
   public void writeOuterJoins(AbstractSqlWriter writer) {
      writer.write(
         " left join osee_branch_category bc on " + branchAlias + ".branch_id = bc.branch_id left join osee_txs txs on txs.gamma_id = bc.gamma_id and txs.branch_id = bc.branch_id and txs.tx_current = ? ");
      writer.addParameter(TxCurrent.CURRENT);
   }

   @Override
   public boolean getWriteGroupBy(AbstractSqlWriter writer) {
      return writer.getJdbcClient().getDbType().equals(JdbcDbType.postgresql);

   }

   @Override
   public void writeGroupBy(AbstractSqlWriter writer) {

      writer.write(branchAlias + ".branch_id ");

   }

   @Override
   public boolean requiresDistinct(AbstractSqlWriter writer) {
      return writer.getJdbcClient().getDbType().equals(JdbcDbType.oracle);
   }

}
