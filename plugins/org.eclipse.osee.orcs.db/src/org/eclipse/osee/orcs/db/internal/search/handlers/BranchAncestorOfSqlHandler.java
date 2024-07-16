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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchAncestorOfSqlHandler extends SqlHandler<CriteriaBranchAncestorOf> {
   private CriteriaBranchAncestorOf criteria;
   private String cteAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaBranchAncestorOf criteria) {
      this.criteria = criteria;
   }

   @Override
   public void writeCommonTableExpression(final AbstractSqlWriter writer) {
      cteAlias = writer.startRecursiveCommonTableExpression("anstrof", "(parent_id, branch_level)");
      writer.write("SELECT anch_br1.parent_branch_id, 0 as branch_level FROM osee_branch anch_br1\n   WHERE ");
      writer.writeEqualsParameter("anch_br1", "branch_id", criteria.getChild());
      writer.writeCteRecursiveUnion();
      writer.write(" SELECT parent_branch_id, branch_level - 1 FROM " + cteAlias);
      writer.write(", osee_branch br WHERE br.branch_id = parent_id");
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(cteAlias);
      brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(cteAlias, "parent_id", brAlias, "branch_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ANCESTOR_OF.ordinal();
   }
}