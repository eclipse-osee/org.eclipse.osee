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

import java.util.OptionalInt;
import java.util.stream.IntStream;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.jdbc.DatabaseType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaIncludeBranchCategories;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class BranchIncludeCategoriesSqlHandler extends SqlHandler<CriteriaIncludeBranchCategories> {
   String branchAlias;
   String catAlias = "brCat";
   String txsAlias = "catTxs";

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
      if (writer.getJdbcClient().getDbType().equals(DatabaseType.oracle)) {
         writer.write(
            ", listagg(category,',') within group (order by category) over (partition by " + branchAlias + ".branch_Id) categories");
      } else if (writer.getJdbcClient().getDbType().equals(DatabaseType.postgresql)) {
         writer.write(", string_agg(cast(category as varchar),', ' order by category) categories");
      } else if (writer.getJdbcClient().getDbType().equals(DatabaseType.hsql)) {
         writer.write(", GROUP_CONCAT(category ORDER BY category SEPARATOR ', ') AS categories");
      }
   }

   @Override
   public void writeOuterJoins(AbstractSqlWriter writer) {
      branchAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      String toFind = "osee_branch " + branchAlias;
      String toReplace =
         "osee_branch " + branchAlias + " left join osee_branch_category " + catAlias + " on " + branchAlias + ".branch_id = " + catAlias + ".branch_id left join osee_txs " + txsAlias + " on " + txsAlias + ".gamma_id = " + catAlias + ".gamma_id and " + txsAlias + ".branch_id = " + catAlias + ".branch_id and " + txsAlias + ".tx_current = ? ";
      OptionalInt index = IntStream.range(0, writer.getTableEntries().size()).filter(
         i -> writer.getTableEntries().get(i).equals(toFind)).findFirst();

      if (index.isPresent()) {
         writer.getTableEntries().set(index.getAsInt(), toReplace);
         writer.addParameter(TxCurrent.CURRENT);
      }

   }

   @Override
   public boolean getWriteGroupBy(AbstractSqlWriter writer) {
      return writer.getJdbcClient().getDbType().equals(
         DatabaseType.postgresql) || writer.getJdbcClient().getDbType().equals(DatabaseType.hsql);
   }

   @Override
   public void writeGroupBy(AbstractSqlWriter writer) {
      branchAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      writer.write(branchAlias + ".branch_id ");
   }

   @Override
   public boolean requiresDistinct(AbstractSqlWriter writer) {
      return writer.getJdbcClient().getDbType().equals(DatabaseType.oracle);
   }

}
