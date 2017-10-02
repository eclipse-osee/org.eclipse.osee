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
import java.util.HashSet;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class RelationSqlHandler extends SqlHandler<CriteriaRelation> {
   private CriteriaRelation criteria;
   private String jIdAlias;
   private String jTypeIdAlias;
   private String jArtAlias;
   private String relationAlias;
   private String txsAlias;

   private Collection<Long> typeIds;
   private AbstractJoinQuery joinIdQuery;
   private AbstractJoinQuery joinTypeQuery;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATION_LOADER.ordinal();
   }

   @Override
   public void setData(CriteriaRelation criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addSelect(AbstractSqlWriter writer) {
      writer.write("%s.rel_link_id, %s.rel_link_type_id, %s.a_art_id, %s.b_art_id, %s.rationale", relationAlias,
         relationAlias, relationAlias, relationAlias, relationAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      jArtAlias = writer.addTable(TableEnum.JOIN_ID4_TABLE);

      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      typeIds = getLocalTypeIds();
      if (typeIds.size() > 1) {
         jTypeIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }

      relationAlias = writer.addTable(TableEnum.RELATION_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   private Collection<Long> getLocalTypeIds() {
      Collection<Long> toReturn = new HashSet<>();
      for (Id type : criteria.getTypes()) {
         toReturn.add(type.getId());
      }
      return toReturn;
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) {
      writer.write("(%s.a_art_id = %s.id2 OR %s.b_art_id = %s.id2)", relationAlias, jArtAlias, relationAlias,
         jArtAlias);
      writer.write(" AND ");
      writer.write(jArtAlias);
      writer.write(".query_id = ?");
      writer.addParameter(criteria.getQueryId());

      Collection<Integer> ids = criteria.getIds();
      if (!ids.isEmpty()) {
         writer.write(" AND ");
         if (ids.size() > 1) {
            joinIdQuery = writer.writeIdJoin(ids);
            writer.write(relationAlias);
            writer.write(".rel_link_id = ");
            writer.write(jIdAlias);
            writer.write(".id AND ");
            writer.write(jIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinIdQuery.getQueryId());
         } else {
            writer.write(relationAlias);
            writer.write(".rel_link_id = ?");
            writer.addParameter(ids.iterator().next());
         }
      }

      if (!typeIds.isEmpty()) {
         writer.write(" AND ");
         if (typeIds.size() > 1) {
            joinTypeQuery = writer.writeIdJoin(typeIds);
            writer.write(relationAlias);
            writer.write(".rel_link_type_id = ");
            writer.write(jTypeIdAlias);
            writer.write(".id AND ");
            writer.write(jTypeIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinTypeQuery.getQueryId());
         } else {
            writer.write(relationAlias);
            writer.write(".rel_link_type_id = ?");
            writer.addParameter(typeIds.iterator().next());
         }
      }

      writer.write(" AND ");
      writer.write(relationAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.write("\n AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }
}
