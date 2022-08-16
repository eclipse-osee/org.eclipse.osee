/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author John Misinco
 */
public class AttributeTypeNotExistsSqlHandler extends SqlHandler<CriteriaAttributeTypeNotExists> {
   private CriteriaAttributeTypeNotExists criteria;
   private String jIdAlias;
   private AbstractJoinQuery joinQuery;
   private String artAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaAttributeTypeNotExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      txsAlias = writer.getMainTableAlias(OseeDb.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<AttributeTypeId> types = criteria.getTypes();
      writer.write("NOT EXISTS (SELECT 1 FROM ");
      String attAlias = writer.writeTable(OseeDb.ATTRIBUTE_TABLE);
      writer.write(", ");
      String txsNotAlias = writer.writeTable(OseeDb.TXS_TABLE);
      writer.write(" WHERE ");
      writer.writeEqualsAnd(attAlias, artAlias, "art_id");

      if (types.size() > 1) {
         Set<AttributeTypeId> typeIds = new HashSet<>();
         for (AttributeTypeId type : types) {
            typeIds.add(type);
         }
         joinQuery = writer.writeJoin(typeIds);

         writer.writeEquals(attAlias, "attr_type_id", jIdAlias, "id");
         writer.writeAnd();
         writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
      } else {
         writer.writeEqualsParameter(attAlias, "attr_type_id", types.iterator().next());
      }
      if (criteria.getValue() != null) {
         writer.write(" AND value = ?");
         writer.addParameter(criteria.getValue());
      }
      writer.writeAnd();

      writer.writeEqualsAnd(attAlias, txsNotAlias, "gamma_id");
      writer.writeTxBranchFilter(txsNotAlias);
      writer.write(")");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_NOT_EXISTS.ordinal();
   }
}