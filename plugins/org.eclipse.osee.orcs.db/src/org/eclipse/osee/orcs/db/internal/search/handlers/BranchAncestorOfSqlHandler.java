/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchAncestorOfSqlHandler extends SqlHandler<CriteriaBranchAncestorOf> {
   private CriteriaBranchAncestorOf criteria;
   private String withAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaBranchAncestorOf criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addWithTables(final AbstractSqlWriter writer) {
      withAlias = writer.startRecursiveWithClause("anstrof", "(parent_id, branch_level)");
      writer.write("  SELECT anch_br1.parent_branch_id, 0 as branch_level FROM osee_branch anch_br1\n   WHERE ");
      writer.writeEqualsParameter("anch_br1", "branch_id", criteria.getChild());
      writer.write("\n  UNION ALL \n");
      writer.write("  SELECT parent_branch_id, branch_level - 1 FROM " + withAlias);
      writer.write(", osee_branch br WHERE br.branch_id = parent_id");
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.addTable(withAlias);
      brAlias = writer.getMainTableAlias(TableEnum.BRANCH_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(withAlias, "parent_id", brAlias, "branch_id");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ANCESTOR_OF.ordinal();
   }
}