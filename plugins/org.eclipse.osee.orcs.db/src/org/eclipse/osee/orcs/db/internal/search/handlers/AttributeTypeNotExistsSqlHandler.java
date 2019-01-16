/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.TableEnum;
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
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.getMainTableAlias(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<AttributeTypeId> types = criteria.getTypes();
      writer.write("NOT EXISTS (SELECT 1 FROM ");
      String attAlias = writer.writeTable(TableEnum.ATTRIBUTE_TABLE);
      writer.write(", ");
      String txsNotAlias = writer.writeTable(TableEnum.TXS_TABLE);
      writer.write(" WHERE ");
      writer.writeEquals(attAlias, artAlias, "art_id");
      writer.write(" AND ");

      if (types.size() > 1) {
         Set<AttributeTypeId> typeIds = new HashSet<>();
         for (AttributeTypeId type : types) {
            typeIds.add(type);
         }
         joinQuery = writer.writeJoin(typeIds);

         writer.writeEquals(attAlias, "attr_type_id", jIdAlias, "id");
         writer.write(" AND ");
         writer.writeEqualsParameter(jIdAlias, "query_id", joinQuery.getQueryId());
      } else {
         writer.writeEqualsParameter(attAlias, "attr_type_id", types.iterator().next());
      }
      if (criteria.getValue() != null) {
         writer.write(" AND value = ?");
         writer.addParameter(criteria.getValue());
      }
      writer.write(" AND ");

      writer.writeEquals(attAlias, txsNotAlias, "gamma_id");
      writer.write(" AND ");
      writer.write(writer.getTxBranchFilter(txsNotAlias));
      writer.write(")");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_NOT_EXISTS.ordinal();
   }
}