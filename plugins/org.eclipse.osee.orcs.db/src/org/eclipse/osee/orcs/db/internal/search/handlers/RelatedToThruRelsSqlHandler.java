/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelatedToThroughRels;

public class RelatedToThruRelsSqlHandler extends SqlHandler<CriteriaRelatedToThroughRels> {
   private CriteriaRelatedToThroughRels criteria;

   private final List<RelationAlias> relationAliases = new ArrayList<>();
   private String artAlias;

   private static final class RelationAlias {
      private final RelationTypeSide relationTypeSide;
      private final String txsAlias;
      private final String relationAlias;

      private RelationAlias(RelationTypeSide relationTypeSide, String txsAlias, String relationAlias) {
         this.relationTypeSide = relationTypeSide;
         this.txsAlias = txsAlias;
         this.relationAlias = relationAlias;
      }
   }

   @Override
   public void setData(CriteriaRelatedToThroughRels criteria) {
      this.criteria = criteria;
      validateCriteria();
   }

   private void writeTables(AbstractSqlWriter writer) {
      relationAliases.clear();
      for (int i = 0; i < criteria.getRelationTypeSides().size(); i++) {
         RelationTypeSide relationTypeSide = criteria.getRelationTypeSides().get(i);
         String relAlias = "relExists" + i;
         String txsAlias = "txsRelExists" + i;
         relationAliases.add(new RelationAlias(relationTypeSide, txsAlias, relAlias));
         String tableName =
            relationTypeSide.isNewRelationTable() ? OseeDb.RELATION_TABLE2.getName() : OseeDb.RELATION_TABLE.getName();
         writer.write(tableName + " " + relAlias + ", ");
         writer.write(OseeDb.TXS_TABLE.getName() + " " + txsAlias + ",");
      }
      writer.removeDanglingSeparator(",");

   }

   private void writeRelationTypePredicate(AbstractSqlWriter writer, RelationAlias relationAlias) {
      writer.write(relationAlias.relationAlias);
      if (relationAlias.relationTypeSide.isNewRelationTable()) {
         writer.write(".rel_type = ?");
      } else {
         writer.write(".rel_link_type_id = ?");
      }
      writer.addParameter(relationAlias.relationTypeSide.getGuid());
   }

   private String getSelectedColumn(RelationTypeSide relationTypeSide) {
      return relationTypeSide.getSide().isSideA() ? "a_art_id" : "b_art_id";
   }

   private String getOppositeColumn(RelationTypeSide relationTypeSide) {
      return relationTypeSide.getSide().isSideA() ? "b_art_id" : "a_art_id";
   }

   private void validateCriteria() {
      if (criteria == null) {
         throw new OseeArgumentException("CriteriaRelatedToThroughRels must be provided");
      }
      if (criteria.getRelationTypeSides() == null || criteria.getRelationTypeSides().isEmpty()) {
         throw new OseeArgumentException("CriteriaRelatedToThroughRels requires at least one relation type side");
      }
      if (criteria.getId() == null || criteria.getId().isInvalid()) {
         throw new OseeArgumentException("CriteriaRelatedToThroughRels requires a valid related artifact id");
      }
      for (RelationTypeSide relationTypeSide : criteria.getRelationTypeSides()) {
         if (relationTypeSide == null || relationTypeSide.isInvalid()) {
            throw new OseeArgumentException("CriteriaRelatedToThroughRels requires valid relation type sides");
         }
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      writer.writeAndLn();
      writer.write(" exists ( select null from ");
      writeTables(writer);
      writer.write(" where ");
      RelationAlias previousRelation = null;
      for (int i = 0; i < relationAliases.size(); i++) {
         RelationAlias currentRelation = relationAliases.get(i);
         writeRelationTypePredicate(writer, currentRelation);
         writer.writeAnd();
         if (previousRelation == null) {
            writer.writeEquals(currentRelation.relationAlias, getSelectedColumn(currentRelation.relationTypeSide),
               artAlias, "art_id");
         } else {
            writer.writeEquals(currentRelation.relationAlias, getSelectedColumn(currentRelation.relationTypeSide),
               previousRelation.relationAlias, getOppositeColumn(previousRelation.relationTypeSide));
         }
         if (i == relationAliases.size() - 1) {
            writer.writeAnd();
            writer.writeEqualsParameter(currentRelation.relationAlias,
               getOppositeColumn(currentRelation.relationTypeSide), criteria.getId());
         }
         writer.writeAnd();
         writer.writeEqualsAnd(currentRelation.txsAlias, currentRelation.relationAlias, "gamma_id");
         writer.writeTxBranchFilter(currentRelation.txsAlias);
         if (i < relationAliases.size() - 1) {
            writer.writeAndLn();
         }
         previousRelation = currentRelation;
      }
      writer.write(") ");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATED_TO_THROUGH_RELS.ordinal();
   }

}
