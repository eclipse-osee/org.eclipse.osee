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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.WithClause;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeExistsSqlHandler extends SqlHandler<CriteriaAttributeTypeExists> {
   private CriteriaAttributeTypeExists criteria;
   private String artAlias;
   private String artTxsAlias;
   private String attrAlias;
   private String txsAlias;

   private String jIdAlias;
   private AbstractJoinQuery joinQuery;

   private String withClauseName;
   private WithClause withClause;

   @Override
   public void setData(CriteriaAttributeTypeExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addWithTables(AbstractSqlWriter writer) {
      if (OptionsUtil.isHistorical(writer.getOptions())) {
         StringBuilder sb = new StringBuilder();
         sb.append("SELECT max(txs.transaction_id) as transaction_id, attr.art_id as art_id\n");
         Collection<AttributeTypeId> types = criteria.getTypes();
         if (types.size() > 1) {
            sb.append("    FROM osee_txs txs, osee_attribute attr, osee_join_id id\n");
         } else {
            sb.append("    FROM osee_txs txs, osee_attribute attr\n");
         }
         sb.append("    WHERE txs.gamma_id = attr.gamma_id\n");
         if (types.size() > 1) {
            AbstractJoinQuery joinQuery = writer.writeJoin(types);
            sb.append("   AND attr.attr_type_id = id.id AND id.query_id = ?");
            writer.addParameter(joinQuery.getQueryId());
         } else {
            sb.append("    AND att.attr_type_id = ?");
            writer.addParameter(types.iterator().next());
         }
         sb.append(" AND ");
         sb.append(writer.getWithClauseTxBranchFilter("txs", false));
         sb.append("\n    GROUP BY attr.art_id");
         String body = sb.toString();

         withClauseName = writer.addReferencedWithClause("attrExt", body);
      }
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }
      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
         artTxsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
      }
      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (artAlias != null && artTxsAlias != null) {
         writer.writeEquals(artAlias, artTxsAlias, "gamma_id");
         writer.write(" AND ");
         writer.write(writer.getTxBranchFilter(artTxsAlias));
         writer.writeAndLn();
      }

      Collection<AttributeTypeId> types = criteria.getTypes();
      if (types.size() > 1) {
         joinQuery = writer.writeJoin(types);

         writer.write(attrAlias);
         writer.write(".attr_type_id = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());

      } else {
         AttributeTypeId type = types.iterator().next();
         writer.write(attrAlias);
         writer.write(".attr_type_id = ?");
         writer.addParameter(type);
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);
            writer.write(attrAlias);
            writer.write(".art_id = ");
            writer.write(artAlias);
            writer.write(".art_id");

            if (index + 1 < aSize) {
               writer.write(" AND ");
            }
         }
      }
      if (withClause != null) {
         writer.writeAndLn();
         writer.write(withClauseName);
         writer.write(".transaction_id = ");
         writer.write(txsAlias);
         writer.write(".transaction_id AND ");
         writer.write(withClauseName);
         writer.write(".art_id = ");
         writer.write(attrAlias);
         writer.write(".art_id");
      }
      writer.writeAndLn();
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id AND ");

      boolean includeDeletedAttributes = OptionsUtil.areDeletedAttributesIncluded(writer.getOptions());
      writer.write(writer.getTxBranchFilter(txsAlias, includeDeletedAttributes));
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_EXISTS.ordinal();
   }

}
