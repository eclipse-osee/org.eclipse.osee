/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
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

   private Collection<RelationTypeToken> typeIds;
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
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      writer.write("%s.rel_link_id, %s.rel_link_type_id, %s.a_art_id, %s.b_art_id, %s.rationale", relationAlias,
         relationAlias, relationAlias, relationAlias, relationAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      jArtAlias = writer.addTable(OseeDb.OSEE_JOIN_ID4_TABLE);

      if (criteria.getIds().size() > 1) {
         jIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      typeIds = getLocalTypeIds();
      if (typeIds.size() > 1) {
         jTypeIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }

      relationAlias = writer.addTable(OseeDb.RELATION_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.RELATION);
   }

   private Collection<RelationTypeToken> getLocalTypeIds() {
      Collection<RelationTypeToken> toReturn = new HashSet<>();
      for (RelationTypeToken type : criteria.getTypes()) {
         toReturn.add(type);
      }
      return toReturn;
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write("(%s.a_art_id = %s.id2 OR %s.b_art_id = %s.id2)", relationAlias, jArtAlias, relationAlias,
         jArtAlias);
      writer.writeAnd();
      writer.write(jArtAlias);
      writer.write(".query_id = ?");
      writer.addParameter(criteria.getQueryId());

      Collection<RelationId> ids = criteria.getIds();
      if (!ids.isEmpty()) {
         writer.writeAnd();
         if (ids.size() > 1) {
            joinIdQuery = writer.writeJoin(ids);
            writer.write(relationAlias);
            writer.write(".rel_link_id = ");
            writer.write(jIdAlias);
            writer.write(".id");
            writer.writeAnd();
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
         writer.writeAnd();
         if (typeIds.size() > 1) {
            joinTypeQuery = writer.writeJoin(ids);
            writer.write(relationAlias);
            writer.write(".rel_link_type_id = ");
            writer.write(jTypeIdAlias);
            writer.write(".id");
            writer.writeAnd();
            writer.write(jTypeIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinTypeQuery.getQueryId());
         } else {
            writer.write(relationAlias);
            writer.write(".rel_link_type_id = ?");
            writer.addParameter(typeIds.iterator().next());
         }
      }

      writer.writeAnd();
      writer.write(relationAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.writeAndLn();
      writer.writeTxBranchFilter(txsAlias);
   }
}