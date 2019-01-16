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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeOtherSqlHandler extends SqlHandler<CriteriaAttributeOther> {

   private CriteriaAttributeOther criteria;

   private String attrAlias;
   private String txsAlias1;

   private String valueJoinAlias;
   private AbstractJoinQuery valueJoinQuery;

   private String typeJoinAlias;
   private AbstractJoinQuery typeJoinQuery;

   private Collection<String> values;
   private boolean ignoreCase;
   private Collection<AttributeTypeId> types;

   @Override
   public void setData(CriteriaAttributeOther criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      values = getValuesForSearch();
      if (values.size() > 1) {
         valueJoinQuery = writer.writeCharJoin(values);
         valueJoinAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }

      types = criteria.getAttributeTypes();
      if (types.size() > 1) {
         typeJoinQuery = writer.writeJoin(types);
         typeJoinAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias1 = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   private Collection<String> getValuesForSearch() {
      List<String> copy = Lists.newArrayList(criteria.getValues());
      ignoreCase = criteria.getOptions().contains(QueryOption.CASE__IGNORE);
      if (ignoreCase) {
         copy = Lists.transform(copy, new Function<String, String>() {

            @Override
            public String apply(String arg0) {
               return arg0.toLowerCase();
            }
         });
      }
      return copy;
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<AttributeTypeId> attributeTypes = criteria.getAttributeTypes();

      if (attributeTypes.size() == 1) {
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(attributeTypes.iterator().next());
      } else if (attributeTypes.size() > 1) {
         writer.write(attrAlias);
         writer.write(".attr_type_id = ");
         writer.write(typeJoinAlias);
         writer.write(".id");
         writer.write(" AND ");
         writer.write(typeJoinAlias);
         writer.write(".query_id = ?");
         writer.addParameter(typeJoinQuery.getQueryId());
      }

      if (values.size() == 1) {
         String value = values.iterator().next();
         writer.write(" AND ");
         if (ignoreCase) {
            writer.write("lower(");
         }
         writer.write(attrAlias);
         writer.write(".value");
         String ending = ignoreCase ? ") " : " ";
         writer.write(ending);
         if (value.contains("%")) {
            writer.write(" LIKE ");
         } else {
            writer.write("=");
         }
         writer.write(" ?");
         writer.addParameter(value);
      }

      if (valueJoinQuery != null) {
         writer.write(" AND ");
         if (ignoreCase) {
            writer.write("lower(");
         }
         writer.write(attrAlias);
         writer.write(".value");
         String ending = ignoreCase ? ") = " : " = ";
         writer.write(ending);
         writer.write(valueJoinAlias);
         writer.write(".id AND ");
         writer.write(valueJoinAlias);
         writer.write(".query_id = ?");
         writer.addParameter(valueJoinQuery.getQueryId());
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);
            writer.write(artAlias);
            writer.write(".art_id = ");
            writer.write(attrAlias);
            writer.write(".art_id");
            if (index + 1 < aSize) {
               writer.write(" AND ");
            }
         }
      }
      writer.write(" AND ");
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias1);
      writer.write(".gamma_id AND ");
      writer.write(writer.getTxBranchFilter(txsAlias1));

   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_VALUE.ordinal();
   }
}
