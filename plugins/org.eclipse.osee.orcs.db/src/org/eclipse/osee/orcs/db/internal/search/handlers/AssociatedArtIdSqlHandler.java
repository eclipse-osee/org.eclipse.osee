/*********************************************************************
 * Copyright (c) 2014 Boeing
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

import org.eclipse.osee.framework.core.enums.SqlTable;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtId;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author John Misinco
 */
public class AssociatedArtIdSqlHandler extends SqlHandler<CriteriaAssociatedArtId> {

   private CriteriaAssociatedArtId criteria;
   private String brAlias;

   @Override
   public void setData(CriteriaAssociatedArtId criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      brAlias = writer.getMainTableAlias(SqlTable.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write("%s.associated_art_id = ?", brAlias);
      writer.addParameter(criteria.getAssociatedArtId());
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ASSOCIATED_ART_ID.ordinal();
   }
}
