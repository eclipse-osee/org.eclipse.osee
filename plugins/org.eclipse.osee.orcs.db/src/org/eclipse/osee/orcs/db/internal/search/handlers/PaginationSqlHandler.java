/*********************************************************************
 * Copyright (c) 2022 Boeing
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
 * @author Luciano Vaglienti
 */
public class PaginationSqlHandler extends SqlHandler<CriteriaPagination> {

   CriteriaPagination criteria;

   @Override
   public void setData(CriteriaPagination criteria) {
      this.criteria = criteria;
   }

   @Override
   public void startWithPreSelect(AbstractSqlWriter writer) {
      writer.write("SELECT * FROM (\n");
   }

   @Override
   public void endWithPreSelect(AbstractSqlWriter writer) {
      Long tempLowerBound = (criteria.getPageNum() - 1) * criteria.getPageSize();
      Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
      Long upperBound =
         tempLowerBound == 0 ? lowerBound + criteria.getPageSize() : lowerBound + criteria.getPageSize() - 1L;
      writer.startRecursiveCommonTableExpression("WHERE rn BETWEEN " + lowerBound + " AND " + upperBound, "");
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      //do nothing
      writer.write(",\n row_number() over (");
      if (writer.getJdbcClient().getDbType().isPaginationOrderingSupported()) {
         //note: need to leverage writer.getJdbcClient().getDbType() to be able to control attribute sorting vs relation sorting since HSQL doesn't support row_number() over (ORDER BY x)

         if (OptionsUtil.getOrderByMechanism(writer.getOptions()).contains(
            "ATTRIBUTE") || OptionsUtil.getOrderByMechanism(writer.getOptions()).contains("RELATION")) {
            writer.write("ORDER BY ");
         }
         if (OptionsUtil.getOrderByMechanism(writer.getOptions()).contains("ATTRIBUTE")) {
            writer.write(writer.getFirstAlias(OseeDb.ATTRIBUTE_TABLE));
            writer.write(".value");
            if (OptionsUtil.getOrderByMechanism(writer.getOptions()).contains("RELATION")) {
               String relTable = writer.getFirstAlias(OseeDb.RELATION_TABLE);
               if (relTable != null) {
                  writer.write(", ");
                  writer.write(relTable);
                  writer.write(".rel_type, ");
                  writer.write(relTable);
                  writer.write(".rel_order ");
               }
            }
         } else if (OptionsUtil.getOrderByMechanism(writer.getOptions()).contains("RELATION")) {
            writer.write(", ");
            String relTable = writer.getFirstAlias(OseeDb.RELATION_TABLE);
            writer.write(relTable);
            writer.write(".rel_type, ");
            writer.write(relTable);
            writer.write(".rel_order ");
         }
      }
      writer.write(") rn");
   }

   @Override
   public boolean shouldWriteAnd() {
      return false;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.PAGINATION.ordinal();
   }

}
