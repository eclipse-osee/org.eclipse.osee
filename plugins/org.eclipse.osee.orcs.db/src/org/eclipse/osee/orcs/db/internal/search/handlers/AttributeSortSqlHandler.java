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

import java.util.List;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.ds.OptionsUtil;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaAttributeSort;

/**
 * @author Luciano Vaglienti
 */
public class AttributeSortSqlHandler extends SqlHandler<CriteriaAttributeSort> {

   private String txsAlias;
   private String attrAlias;
   private CriteriaAttributeSort criteria;

   @Override
   public void setData(CriteriaAttributeSort criteria) {
      this.criteria = criteria;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_SORT.ordinal();
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      if (criteria.isValid()) {

         writer.write(attrAlias);
         writer.write(".value order_value");
      } else {
         writer.write("'' as order_value");
      }
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.isValid()) {

         txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.ATTRIBUTE);
         attrAlias = writer.addTable(OseeDb.ATTRIBUTE_TABLE);
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (criteria.isValid()) {

         writer.writeTxBranchFilter(txsAlias);
         writer.write(" ");
         writer.writeAnd();
         writer.writeEqualsAnd(attrAlias, txsAlias, "gamma_id");
         List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
         if (!aliases.isEmpty()) {
            int aSize = aliases.size();
            for (int index = 0; index < aSize; index++) {
               String artAlias = aliases.get(index);
               writer.write(attrAlias);
               writer.write(".art_id = ");
               writer.write(artAlias);
               writer.write(".art_id");
               if (index + 1 < aSize) {
                  writer.writeAnd();
               }
            }
            writer.writeAndLn();
         }
         writer.write(attrAlias);
         writer.addParameter(criteria.getAttributeType().getId());
         writer.write(".attr_type_id = ?");
         if (criteria.getAttributeType().isDate() && OptionsUtil.getMaxTime(writer.getOptions()) != null) {
            writer.addParameter(OptionsUtil.getMaxTime(writer.getOptions()).getTime());
            writer.write(
               " and " + writer.getJdbcClient().getDbType().getPostgresCastStart() + attrAlias + ".value " + writer.getJdbcClient().getDbType().getPostgresCastBigIntEnd() + " > ?");
         }
      }

   }

   @Override
   public boolean criteriaIsValid() {
      return criteria.isValid();
   }

   @Override()
   public boolean shouldWriteAnd() {
      System.out.println(criteria.isValid());
      return criteria.isValid();
   }
}
