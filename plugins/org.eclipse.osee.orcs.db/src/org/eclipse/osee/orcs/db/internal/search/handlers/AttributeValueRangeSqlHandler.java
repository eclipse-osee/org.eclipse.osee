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

import java.util.List;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeValueRange;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Vaibhav Patel
 */
public class AttributeValueRangeSqlHandler extends SqlHandler<CriteriaAttributeValueRange> {

   private CriteriaAttributeValueRange criteria;
   private String attrAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaAttributeValueRange criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      attrAlias = writer.addTable(OseeDb.ATTRIBUTE_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {

      writer.write(attrAlias);
      writer.write(".attr_type_id = ?");
      writer.addParameter(criteria.getAttributeType().getId());
      writer.writeAnd();
      writer.write(attrAlias);
      writer.write(".value < " + criteria.getToValue());
      writer.writeAnd();
      writer.write(attrAlias);
      writer.write(".value > " + criteria.getFromValue());

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
      return SqlHandlerPriority.ATTRIBUTE_RANGE.ordinal();
   }
}