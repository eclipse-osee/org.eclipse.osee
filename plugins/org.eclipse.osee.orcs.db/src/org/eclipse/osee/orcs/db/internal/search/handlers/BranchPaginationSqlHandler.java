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

import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.ds.OptionsUtil;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaPagination;

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
         // Outer wrapper: applies row_number() AFTER deduplication
         writer.write("SELECT * FROM (\n");
         writer.write("SELECT t1.*");
         if (writer.getJdbcClient().getDbType().isPaginationOrderingSupported()) {
            writer.write(", row_number() over (ORDER BY ");
            if (OptionsUtil.getBranchOrder(writer.getOptions()).equals("name")) {
               writer.write("t1.branch_name,");
            }
            writer.write("t1.branch_id) rn");
         } else {
            writer.write(", row_number() over () rn");
         }
         writer.write(" FROM (\n");
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
         writer.addParameter(lowerBound);
         writer.addParameter(upperBound);
         // Close inner subquery (t1), then close outer subquery with pagination filter
         writer.write(") t1\n) t2 WHERE rn BETWEEN ? AND ?");
      }
   }
}
