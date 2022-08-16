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

package org.eclipse.osee.orcs.db.internal.loader.handlers;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation2;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Audrey Denk
 */
public class RelationSqlHandler2 extends SqlHandler<CriteriaRelation2> {
   private CriteriaRelation2 criteria;
   private String jTypeIdAlias;
   private String jArtAlias;
   private String relationAlias;
   private String txsAlias;

   private Collection<RelationTypeToken> typeIds;
   private AbstractJoinQuery joinTypeQuery;

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATION_LOADER.ordinal();
   }

   @Override
   public void setData(CriteriaRelation2 criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      writer.write("%s.rel_type, %s.a_art_id, %s.b_art_id, %s.rel_art_id, %s.rel_order", relationAlias, relationAlias,
         relationAlias, relationAlias, relationAlias);
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      jArtAlias = writer.addTable(OseeDb.OSEE_JOIN_ID4_TABLE);

      typeIds = getLocalTypeIds();
      if (typeIds.size() > 1) {
         jTypeIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID4_TABLE);
      }

      relationAlias = writer.addTable(OseeDb.RELATION_TABLE2);
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

      if (!typeIds.isEmpty()) {
         writer.writeAnd();
         if (typeIds.size() > 1) {
            joinTypeQuery = writer.writeJoin(typeIds);
            writer.write(relationAlias);
            writer.write(".rel_type = ");
            writer.write(jTypeIdAlias);
            writer.write(".id");
            writer.writeAnd();
            writer.write(jTypeIdAlias);
            writer.write(".query_id = ?");
            writer.addParameter(joinTypeQuery.getQueryId());
         } else {
            writer.write(relationAlias);
            writer.write(".rel_type = ?");
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