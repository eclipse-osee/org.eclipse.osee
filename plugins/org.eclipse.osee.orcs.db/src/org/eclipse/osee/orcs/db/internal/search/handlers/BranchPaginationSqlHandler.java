/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaPagination;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Luciano T. Vaglienti
 */
public class BranchPaginationSqlHandler extends SqlHandler<CriteriaPagination> {

   CriteriaPagination criteria;

   @Override
   public void setData(CriteriaPagination criteria) {
      this.criteria = criteria;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.PAGINATION.ordinal();
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }

   @Override
   public void startWithPreSelect(AbstractSqlWriter writer) {
      if (criteria.isValid()) {
         writer.write("SELECT * FROM (\n");
      }
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      String brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      if (criteria.isValid()) {
         writer.write(",\n row_number() over (");
         if (writer.getJdbcClient().getDbType().isPaginationOrderingSupported()) {
            writer.write("ORDER BY ");
            if (OptionsUtil.getBranchOrder(writer.getOptions()).equals("name")) {
               writer.write(brAlias + ".branch_name,");
            }
            writer.write(brAlias + ".branch_id");
            writer.write(") rn");
         } else {
            writer.write(") rn");
         }
      } else {
         writer.write(",0 as rn");
      }
   }

   @Override
   public void writeOrder(AbstractSqlWriter writer) {
      /**
       * Note: this isn't actually writing an order, just this is the right time to write the sql
       */
      if (criteria.isValid()) {
         Long tempLowerBound = (criteria.getPageNum() - 1) * criteria.getPageSize();
         Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
         Long upperBound =
            tempLowerBound == 0 ? lowerBound + criteria.getPageSize() : lowerBound + criteria.getPageSize() - 1L;
         writer.write(") t1 WHERE rn BETWEEN " + lowerBound + " AND " + upperBound, "");
      }
   }
}
