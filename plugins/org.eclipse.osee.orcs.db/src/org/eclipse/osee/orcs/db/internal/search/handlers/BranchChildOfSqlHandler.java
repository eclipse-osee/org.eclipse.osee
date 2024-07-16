/*********************************************************************
 * Copyright (c) 2013 Boeing
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchChildOfSqlHandler extends SqlHandler<CriteriaBranchChildOf> {
   private CriteriaBranchChildOf criteria;
   private String cteAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaBranchChildOf criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeCommonTableExpression(final AbstractSqlWriter writer) {
      cteAlias = writer.startRecursiveCommonTableExpression("chof", "(child_id, branch_level)");
      writer.write("SELECT anch_br1.branch_id, 0 as branch_level FROM osee_branch anch_br1, osee_branch anch_br2\n");
      writer.write(" WHERE anch_br1.parent_branch_id = anch_br2.branch_id");
      writer.writeAnd();
      writer.writeEqualsParameter("anch_br2", "branch_id", criteria.getParent());
      writer.writeCteRecursiveUnion();
      writer.write(" SELECT branch_id, branch_level + 1 FROM " + cteAlias + ", osee_branch br");
      writer.write(" WHERE child_id = br.parent_branch_id");
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(cteAlias);
      brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(cteAlias, "child_id", brAlias, "branch_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_CHILD_OF.ordinal();
   }
}
