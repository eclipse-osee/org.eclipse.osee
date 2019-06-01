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

import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedRecursive;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan D. Brooks
 */
public class RelatedRecursiveHandler extends SqlHandler<CriteriaRelatedRecursive> {
   private CriteriaRelatedRecursive criteria;
   private String withAlias;
   private String artAlias;

   @Override
   public void addWithTables(AbstractSqlWriter writer) {
      withAlias = writer.getNextAlias("recurse");
      final StringBuilder body = new StringBuilder();
      body.append("  SELECT b_art_id, 1 FROM osee_relation_link WHERE a_art_id = ? AND rel_link_type_id = ?\n");
      writer.addParameter(criteria.getStartArtifact());
      writer.addParameter(criteria.getRelationType());
      body.append("  UNION ALL\n");
      body.append("  SELECT b_art_id, child_level + 1 FROM ").append(withAlias).append(
         " recurse, osee_relation_link rel, osee_txs txs");
      body.append(" WHERE a_art_id = recurse.id AND rel_link_type_id = ? AND rel.gamma_id = txs.gamma_id AND ");
      writer.addParameter(criteria.getRelationType());
      body.append(writer.getTxBranchFilter("txs"));
      writer.addRecursiveReferencedWithClause(withAlias, "(id, child_level)", body.toString());
   }

   @Override
   public void setData(CriteriaRelatedRecursive criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(withAlias, "id", artAlias, "art_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_ID.ordinal();
   }
}