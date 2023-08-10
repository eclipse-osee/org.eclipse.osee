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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaFollowSearch;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaPagination;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Audrey Denk
 */
public class FollowSearchSqlHandler extends SqlHandler<CriteriaFollowSearch> {

   CriteriaFollowSearch criteria;

   private String attrSearchAlias;
   private String mainAttAlias;
   @Override
   public void setData(CriteriaFollowSearch criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      String mainArtAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      if (writer.getAliasManager().getPreviousAlias("artWith").isBlank()) {
         writer.write(",','||" + mainArtAlias + ".art_id" + "||',' art_path");
      } else {
         writer.write("," + writer.getTableEntries().get(0) + ".art_path|| " + mainArtAlias + ".art_id||',' art_path");
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.FOLLOW_SEARCH.ordinal();
   }

   public String writeFollowSearchCommonTableExpression(AbstractSqlWriter writer, String attAlias, boolean newRelation,
      CriteriaPagination pagination) {
      attrSearchAlias = writer.startCommonTableExpression("attrSearch");
      List<String> values = new ArrayList<>(criteria.getValues());
      List<AttributeTypeId> types = new ArrayList<>(criteria.getTypes());
    //@formatter:off
    /**
     *        SELECT
         *
  FROM
         (
                SELECT DISTINCT art_id,
                art_path,
                top,
                top_rel_type,
                top_rel_order,
                row_number() OVER (order by top,top_rel_type,top_rel_order) rn
         FROM
                atts1
         WHERE
                LOWER(VALUE) LIKE LOWER('%desired_attribute_value%')
                ) t1
WHERE
  rn BETWEEN start_index AND end_index
  )
     */
//  //@formatter:on
      writer.write("select * from (select distinct art_id, art_path");
      if (newRelation) {
         writer.write(", top, top_rel_type, top_rel_order");
      }
      if (pagination != null && writer.getJdbcClient().getDbType().isPaginationOrderingSupported()) {
         writer.write(", dense_rank() over (order by top");
         if (newRelation) {
            writer.write(", top_rel_type, top_rel_order");
         }
         writer.write(") rn2 from " + attAlias);
      } else if (pagination != null) {
         writer.write(", row_number() over () rn2 from " + attAlias); //hsql does not support dense_rank
      } else {
         writer.write(" from " + attAlias);
      }
      writer.write(" where ");
      List<QueryOption> options = Arrays.asList(criteria.getOptions());

      if (values.size() > 1) {
         writer.write("value in (" + values.toString() + ") ");
      } else {
         if (options.contains(QueryOption.CASE__MATCH) && options.contains(QueryOption.TOKEN_MATCH_ORDER__MATCH)) {
            writer.write("value = '" + values.get(0) + "' ");
         } else {
            writer.write("lower(value) like lower('%" + values.get(0) + "%') ");
         }
      }
      if (pagination != null) {
         Long tempLowerBound = (pagination.getPageNum() - 1) * pagination.getPageSize();
         Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
         Long upperBound =
            tempLowerBound == 0 ? lowerBound + pagination.getPageSize() : lowerBound + pagination.getPageSize() - 1L;
         writer.write(" ) t1 where rn2 between " + lowerBound + " and " + upperBound + " ");
      } else {
         writer.write(" ) t1 ");
      }

      return attrSearchAlias;
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }
}
