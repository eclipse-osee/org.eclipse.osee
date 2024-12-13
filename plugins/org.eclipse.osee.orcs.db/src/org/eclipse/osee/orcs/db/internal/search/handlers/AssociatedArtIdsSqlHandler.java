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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtIds;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan T. Baldwin
 */
public class AssociatedArtIdsSqlHandler extends SqlHandler<CriteriaAssociatedArtIds> {

   private CriteriaAssociatedArtIds criteria;
   private String brAlias;

   @Override
   public void setData(CriteriaAssociatedArtIds criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      if (criteria.getAssociatedArtIds().size() == 1) {
         writer.write("%s.associated_art_id = ?", brAlias);
         writer.addParameter(criteria.getAssociatedArtIds().get(0));
         return;
      }
      writer.write("%s.associated_art_id IN (", brAlias);
      for (int i = 0; i < criteria.getAssociatedArtIds().size(); i++) {
         ArtifactId artId = criteria.getAssociatedArtIds().get(i);
         if (i != 0) {
            writer.write(",");
         }
         writer.write("?");
         writer.addParameter(artId);
      }
      writer.write(")");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ASSOCIATED_ART_ID.ordinal();
   }
}
