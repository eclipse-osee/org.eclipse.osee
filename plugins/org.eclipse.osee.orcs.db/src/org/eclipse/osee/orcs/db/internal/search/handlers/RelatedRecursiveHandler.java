/*********************************************************************
 * Copyright (c) 2019 Boeing
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

import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedRecursive;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan D. Brooks
 */
public class RelatedRecursiveHandler extends SqlHandler<CriteriaRelatedRecursive> {
   private CriteriaRelatedRecursive criteria;
   private String cteAlias;
   private String artAlias;

   @Override
   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      if (criteria.getType().isNewRelationTable()) {
         cteAlias =
            writer.startRecursiveCommonTableExpression("recurse", "(id,top_rel_type, top_rel_order, child_level)");
      } else {
         cteAlias = writer.startRecursiveCommonTableExpression("recurse", "(id,child_level)");
      }
      if (criteria.getType().isNewRelationTable()) {
         writer.write(
            "SELECT b_art_id,rel_type top_rel_type,rel_order top_rel_order, 1 FROM osee_relation rel, osee_txs txs WHERE ");
         writer.writeEqualsParameterAnd("a_art_id", criteria.getStartArtifact());
         writer.writeEqualsParameterAnd("rel_type", criteria.getType());
         writer.write("rel.gamma_id = txs.gamma_id");
         writer.writeAnd();
         writer.writeTxBranchFilter("txs");
         writer.writeCteRecursiveUnion();
         writer.write(
            " SELECT b_art_id,rel_type top_rel_type,rel_order top_rel_order, child_level + 1 FROM " + cteAlias);
         writer.write(", osee_relation rel, osee_txs txs");
         writer.write(" WHERE a_art_id = id AND rel_type = ? AND rel.gamma_id = txs.gamma_id");
         writer.writeAnd();
         writer.addParameter(criteria.getType());
         writer.writeTxBranchFilter("txs");
      } else {
         writer.write("SELECT b_art_id, 1 FROM osee_relation_link rel, osee_txs txs WHERE ");
         writer.writeEqualsParameterAnd("a_art_id", criteria.getStartArtifact());
         writer.writeEqualsParameterAnd("rel_link_type_id", criteria.getType());
         writer.write("rel.gamma_id = txs.gamma_id");
         writer.writeAnd();
         writer.writeTxBranchFilter("txs");
         writer.writeCteRecursiveUnion();
         writer.write(" SELECT b_art_id, child_level + 1 FROM " + cteAlias);
         writer.write(", osee_relation_link rel, osee_txs txs");
         writer.write(" WHERE a_art_id = id AND rel_link_type_id = ? AND rel.gamma_id = txs.gamma_id");
         writer.writeAnd();
         writer.addParameter(criteria.getType());
         writer.writeTxBranchFilter("txs");
      }
   }

   @Override
   public void setData(CriteriaRelatedRecursive criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(cteAlias);
      artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(cteAlias, "id", artAlias, "art_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_ID.ordinal();
   }
}