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

import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMergeBranchFor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author John Misinco
 */
public class MergeBranchForSqlHandler extends SqlHandler<CriteriaMergeBranchFor> {
   private CriteriaMergeBranchFor criteria;
   private String brAlias, mergeAlias;

   @Override
   public void setData(CriteriaMergeBranchFor criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      mergeAlias = writer.addTable(OseeDb.OSEE_MERGE_TABLE);
      brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEqualsParameterAnd(mergeAlias, "source_branch_id", criteria.getSource());
      writer.writeEqualsParameterAnd(mergeAlias, "dest_branch_id", criteria.getDestination());
      writer.writeEquals(mergeAlias, "merge_branch_id", brAlias, "branch_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.MERGE_BRANCH_FOR.ordinal();
   }
}