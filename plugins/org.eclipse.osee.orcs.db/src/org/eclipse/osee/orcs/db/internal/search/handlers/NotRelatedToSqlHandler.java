/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaNotRelatedTo;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class NotRelatedToSqlHandler extends SqlHandler<CriteriaNotRelatedTo> {
   private CriteriaNotRelatedTo criteria;

   private String relAlias;
   private String txsAlias;
   private String artAlias;

   @Override
   public void setData(CriteriaNotRelatedTo criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      //NOTE: currently not going to support old rels for this since the only places we need this is with new rels
      if (criteria.getType().isNewRelationTable()) {
         writer.write("NOT EXISTS (");
         writer.write("SELECT 1 FROM ");
         relAlias = writer.getNextAlias(OseeDb.RELATION_TABLE2);
         txsAlias = writer.getNextAlias(OseeDb.TXS_TABLE);
         artAlias = writer.getFirstAlias(OseeDb.ARTIFACT_TABLE);
         writer.write(OseeDb.RELATION_TABLE2.getName());
         writer.write(" ");
         writer.write(relAlias);
         writer.write(",");
         writer.write(OseeDb.TXS_TABLE.getName());
         writer.write(" ");
         writer.write(txsAlias);
         writer.write(" ");
         writer.write("WHERE ");
         writer.writeTxBranchFilter(txsAlias);
         writer.writeAnd();
         writer.writeEqualsAnd(relAlias, txsAlias, "gamma_id");
         //new rel specific:
         writer.writeEqualsParameterAnd(relAlias, "rel_type", criteria.getType().getId());
         String side = criteria.getType().getSide().equals(RelationSide.SIDE_A) ? "a_art_id" : "b_art_id";
         String oppositeSide = criteria.getType().getSide().equals(RelationSide.SIDE_A) ? "b_art_id" : "a_art_id";
         writer.writeEqualsAnd(relAlias, side, artAlias, "art_id");
         writer.writeEqualsParameter(relAlias, oppositeSide, criteria.getId());
         //end new rel specific
         writer.write(")");
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.NOT_RELATED_TO_ART_ID.ordinal();
   }

}
