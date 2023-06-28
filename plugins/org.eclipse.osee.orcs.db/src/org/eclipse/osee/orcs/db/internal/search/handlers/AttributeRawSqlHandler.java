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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeRaw;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeRawSqlHandler extends SqlHandler<CriteriaAttributeRaw> {

   private CriteriaAttributeRaw criteria;

   private String attrAlias;
   private String txsAlias;

   private String valueJoinAlias;
   private AbstractJoinQuery valueJoinQuery;

   private String typeJoinAlias;
   private AbstractJoinQuery typeJoinQuery;

   private Collection<String> values;
   private boolean ignoreCase;
   private Collection<AttributeTypeToken> types;

   @Override
   public void setData(CriteriaAttributeRaw criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      values = getValuesForSearch();
      if (values.size() > 1) {
         valueJoinQuery = writer.writeCharJoin(values);
         valueJoinAlias = writer.addTable(OseeDb.OSEE_JOIN_CHAR_ID_TABLE);
      }

      types = criteria.getAttributeTypes();
      if (types.size() > 1) {
         typeJoinQuery = writer.writeJoin(types);
         typeJoinAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      attrAlias = writer.addTable(OseeDb.ATTRIBUTE_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.ATTRIBUTE);
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
      Collection<AttributeTypeToken> attributeTypes = criteria.getAttributeTypes();

      if (attributeTypes.size() == 1) {
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(attributeTypes.iterator().next());
      } else if (attributeTypes.size() > 1) {
         writer.write(attrAlias);
         writer.write(".attr_type_id = ");
         writer.write(typeJoinAlias);
         writer.write(".id");
         writer.writeAnd();
         writer.write(typeJoinAlias);
         writer.write(".query_id = ?");
         writer.addParameter(typeJoinQuery.getQueryId());
      }

      if (values.size() == 1) {
         String value = values.iterator().next();
         writer.writeAnd();
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
         writer.writeAnd();
         if (ignoreCase) {
            writer.write("lower(");
         }
         writer.write(attrAlias);
         writer.write(".value");
         String ending = ignoreCase ? ") = " : " = ";
         writer.write(ending);
         writer.write(valueJoinAlias);
         writer.write(".id");
         writer.writeAnd();
         writer.write(valueJoinAlias);
         writer.write(".query_id = ?");
         writer.addParameter(valueJoinQuery.getQueryId());
      }

      List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
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
               writer.writeAnd();
            }
         }
      }
      writer.writeAnd();
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.writeAnd();
      writer.writeTxBranchFilter(txsAlias);

   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_VALUE.ordinal();
   }
}