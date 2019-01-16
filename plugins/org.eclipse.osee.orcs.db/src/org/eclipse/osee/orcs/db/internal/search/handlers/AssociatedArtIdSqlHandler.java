/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
      brAlias = writer.getMainTableAlias(TableEnum.BRANCH_TABLE);
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
