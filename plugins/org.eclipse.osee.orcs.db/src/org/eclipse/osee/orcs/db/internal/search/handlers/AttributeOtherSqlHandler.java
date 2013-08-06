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
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class AttributeOtherSqlHandler extends SqlHandler<CriteriaAttributeOther> {

   private CriteriaAttributeOther criteria;

   private String attrAlias;
   private String txsAlias1;

   private String artAlias2;
   private String txs2Alias2;

   private String joinAlias;
   private String value;

   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(CriteriaAttributeOther criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      Collection<String> values = criteria.getValues();
      if (values.size() == 1) {
         this.value = values.iterator().next();
      } else {
         joinQuery = writer.writeCharJoin(values);
      }
      if (joinQuery != null && criteria.getOperator().isEquals()) {
         joinAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }
      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      List<String> txs = writer.getAliases(TableEnum.TXS_TABLE);

      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias1 = writer.addTable(TableEnum.TXS_TABLE);

      if (aliases.isEmpty()) {
         artAlias2 = writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      if (txs.isEmpty()) {
         txs2Alias2 = writer.addTable(TableEnum.TXS_TABLE);
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      IAttributeType attributeType = criteria.getAttributeType();
      Operator operator = criteria.getOperator();

      if (attributeType != null) {
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(toLocalId(attributeType));
      }

      if (value != null) {
         writer.write(" AND ");
         writer.write(attrAlias);
         writer.write(".value ");
         if (value.contains("%")) {
            if (operator.isNotEquals()) {
               writer.write(" NOT");
            }
            writer.write(" LIKE ");
         } else {
            writer.write(operator.toString());
         }
         writer.write(" ?");
         writer.addParameter(value);
      }

      if (joinQuery != null) {
         writer.write(" AND ");
         if (operator.isEquals()) {
            writer.write(attrAlias);
            writer.write(".value = ");
            writer.write(joinAlias);
            writer.write(".id AND ");
            writer.write(joinAlias);
            writer.write(".query_id = ?");
         } else {
            writer.write("NOT EXISTS (SELECT 1 FROM ");
            writer.write(TableEnum.CHAR_JOIN_TABLE.getName());
            writer.write(" WHERE id = ");
            writer.write(attrAlias);
            writer.write(".value AND query_id = ?)");
         }
         writer.addParameter(joinQuery.getQueryId());
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

      if (txs2Alias2 != null && artAlias2 != null) {
         writer.writeAndLn();
         writer.write(artAlias2);
         writer.write(".gamma_id = ");
         writer.write(txs2Alias2);
         writer.write(".gamma_id AND ");
         writer.write(writer.getTxBranchFilter(txs2Alias2));
      }
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_VALUE.ordinal();
   }
}
