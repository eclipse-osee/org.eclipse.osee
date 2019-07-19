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
   private String cteAlias;
   private String artAlias;

   @Override
   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      cteAlias = writer.startRecursiveCommonTableExpression("recurse", "(id, child_level)");
      writer.write("  SELECT b_art_id, 1 FROM osee_relation_link WHERE ");
      writer.writeEqualsParameterAnd("a_art_id", criteria.getStartArtifact());
      writer.writeEqualsParameter("rel_link_type_id", criteria.getRelationType());
      writer.write("  UNION ALL\n");
      writer.write("  SELECT b_art_id, child_level + 1 FROM " + cteAlias);
      writer.write(", osee_relation_link rel, osee_txs txs");
      writer.write(" WHERE a_art_id = id AND rel_link_type_id = ? AND rel.gamma_id = txs.gamma_id AND ");
      writer.addParameter(criteria.getRelationType());
      writer.writeTxBranchFilter("txs");
   }

   @Override
   public void setData(CriteriaRelatedRecursive criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(cteAlias);
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
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