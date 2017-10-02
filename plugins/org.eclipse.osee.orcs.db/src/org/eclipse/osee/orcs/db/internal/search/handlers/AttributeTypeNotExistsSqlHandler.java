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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author John Misinco
 */
public class AttributeTypeNotExistsSqlHandler extends SqlHandler<CriteriaAttributeTypeNotExists> {

   private CriteriaAttributeTypeNotExists criteria;

   private String jIdAlias;
   private String artAlias;
   private String txsAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(CriteriaAttributeTypeNotExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getTypes().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }
      artAlias = writer.getOrCreateTableAlias(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.getOrCreateTableAlias(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      Collection<AttributeTypeId> types = criteria.getTypes();

      writer.writeEquals(artAlias, txsAlias, "gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter(txsAlias));
      writer.writeAndLn();
      writer.write("NOT EXISTS (SELECT 1 FROM ");
      writer.write(TableEnum.ATTRIBUTE_TABLE.getName());
      writer.write(" attr, ");
      writer.write(TableEnum.TXS_TABLE.getName());
      writer.write(" txs WHERE ");

      if (types.size() > 1) {
         Set<AttributeTypeId> typeIds = new HashSet<>();
         for (AttributeTypeId type : types) {
            typeIds.add(type);
         }
         joinQuery = writer.writeJoin(typeIds);

         writer.write("attr.attr_type_id = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         AttributeTypeId type = types.iterator().next();
         writer.write("attr.attr_type_id = ?");
         writer.addParameter(type);
      }

      writer.writeAndLn();

      writer.writeEquals("attr", "txs", "gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter("txs"));
      writer.writeAndLn();
      writer.writeEquals("attr", artAlias, "art_id");
      writer.write(")");

      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_NOT_EXISTS.ordinal();
   }

}
