/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class AttributeSqlHandler extends SqlHandler<CriteriaAttribute> {

   private CriteriaAttribute criteria;
   private String jIdAlias;
   private String jTypeIdAlias;
   private String jArtAlias;
   private String attrAlias;
   private String txsAlias;

   private AbstractJoinQuery joinIdQuery;
   private AbstractJoinQuery joinTypeQuery;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_LOADER.ordinal();
   }

   @Override
   public void setData(CriteriaAttribute criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addSelect(AbstractSqlWriter writer)  {
      writer.write("%s.attr_id, %s.attr_type_id, %s.value, %s.uri", attrAlias, attrAlias, attrAlias, attrAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer)  {
      jArtAlias = writer.addTable(TableEnum.JOIN_ID4_TABLE);

      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      if (criteria.getTypes().size() > 1) {
         jTypeIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      writer.write(attrAlias);
      writer.write(".art_id = ");
      writer.write(jArtAlias);
      writer.write(".id2 AND ");
      writer.write(jArtAlias);
      writer.write(".query_id = ?");
      writer.addParameter(criteria.getQueryId());

      Collection<Integer> ids = criteria.getIds();
      if (!ids.isEmpty()) {
         writer.write(" AND ");
         if (ids.size() > 1) {
            joinIdQuery = writer.writeIdJoin(ids);
            writer.write(attrAlias);
            writer.write(".attr_id = ");
            writer.write(jIdAlias);
            writer.write(".id AND ");
            writer.write(jIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinIdQuery.getQueryId());
         } else {
            writer.write(attrAlias);
            writer.write(".attr_id = ?");
            writer.addParameter(ids.iterator().next());
         }
      }

      Collection<? extends AttributeTypeId> types = criteria.getTypes();
      if (!types.isEmpty()) {
         writer.write(" AND ");
         if (types.size() > 1) {
            joinTypeQuery = writer.writeJoin(types);
            writer.write(attrAlias);
            writer.write(".attr_type_id = ");
            writer.write(jTypeIdAlias);
            writer.write(".id AND ");
            writer.write(jTypeIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinTypeQuery.getQueryId());
         } else {
            writer.write(attrAlias);
            writer.write(".attr_type_id = ?");
            writer.addParameter(types.iterator().next());
         }
      }

      writer.write(" AND ");
      writer.write(attrAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.write("\n AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }

}
