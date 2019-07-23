/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan D. Brooks
 */
public class FollowRelationSqlHandler extends SqlHandler<CriteriaRelationTypeFollow> {
   private final FollowRelationSqlHandler previousFollow;
   private String sourceArtTable;
   private CriteriaRelationTypeFollow criteria;
   private String relAlias;
   private String toArtField;
   private String relTxsAlias;

   public FollowRelationSqlHandler(String sourceArtTable) {
      this.sourceArtTable = sourceArtTable;
      this.previousFollow = null;
   }

   public FollowRelationSqlHandler(FollowRelationSqlHandler previousFollow) {
      this.previousFollow = previousFollow;
   }

   @Override
   public void setData(CriteriaRelationTypeFollow criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (sourceArtTable != null) {
         writer.addTable(sourceArtTable);
      }
      relAlias = writer.addTable(TableEnum.RELATION_TABLE);
      relTxsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.RELATION);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      boolean includeDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(writer.getOptions());
      RelationTypeSide typeSide = criteria.getType();

      String fromArtField;
      if (typeSide.getSide().isSideA()) {
         fromArtField = "b_art_id";
         toArtField = "a_art_id";
      } else {
         fromArtField = "a_art_id";
         toArtField = "b_art_id";
      }

      String sourceArtColumn;
      if (previousFollow == null) {
         sourceArtColumn = "art_id";
      } else {
         sourceArtTable = previousFollow.relAlias;
         sourceArtColumn = previousFollow.toArtField;
      }

      writer.writeEqualsAnd(sourceArtTable, sourceArtColumn, relAlias, fromArtField);
      writer.writeEqualsParameterAnd(relAlias, "rel_link_type_id", typeSide.getRelationType());
      writer.writeEqualsAnd(relAlias, relTxsAlias, "gamma_id");
      writer.writeTxBranchFilter(relTxsAlias, includeDeletedRelations);
      if (criteria.isTerminalFollow()) {
         writer.write(" AND ");
         writer.writeEquals(relAlias, toArtField, writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE), "art_id");
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.FOLLOW_RELATION_TYPES.ordinal();
   }

   public RelationSide getRelationSide() {
      return criteria.getType().getSide();
   }
}