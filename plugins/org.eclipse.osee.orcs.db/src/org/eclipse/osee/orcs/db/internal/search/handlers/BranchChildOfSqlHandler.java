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

import java.util.List;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchChildOfSqlHandler extends SqlHandler<CriteriaBranchChildOf> {
   private CriteriaBranchChildOf criteria;
   private String withAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaBranchChildOf criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addWithTables(final AbstractSqlWriter writer) {
      withAlias = writer.getNextAlias("chof");
      final StringBuilder body = new StringBuilder();
      body.append("  SELECT anch_br1.branch_id, 0 as branch_level FROM osee_branch anch_br1, osee_branch anch_br2\n");
      body.append("   WHERE anch_br1.parent_branch_id = anch_br2.branch_id AND anch_br2.branch_id = ?");
      body.append("\n  UNION ALL \n");
      body.append("  SELECT branch_id, branch_level + 1 FROM ").append(withAlias).append(" recurse, osee_branch br");
      body.append(" WHERE recurse.child_id = br.parent_branch_id");
      writer.addParameter(criteria.getParent());
      writer.addRecursiveReferencedWithClause(withAlias, "(child_id, branch_level)", body.toString());
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> branchAliases = writer.getAliases(TableEnum.BRANCH_TABLE);
      if (branchAliases.isEmpty()) {
         brAlias = writer.addTable(TableEnum.BRANCH_TABLE);
      } else {
         brAlias = branchAliases.iterator().next();
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write("%s.branch_id = %s.child_id", brAlias, withAlias);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_CHILD_OF.ordinal();
   }
}
