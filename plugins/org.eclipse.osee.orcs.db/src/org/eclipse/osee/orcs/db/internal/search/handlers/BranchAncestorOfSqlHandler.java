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

import static org.eclipse.osee.orcs.db.internal.sql.SqlUtil.newRecursiveWithClause;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.AliasEntry;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlUtil;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public class BranchAncestorOfSqlHandler extends SqlHandler<CriteriaBranchAncestorOf> {

   private static final AliasEntry ANCESTOR_OF_ENTRY = SqlUtil.newAlias("ancestor_of", "anstrof");

   private CriteriaBranchAncestorOf criteria;
   private String withAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaBranchAncestorOf criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addWithTables(final AbstractSqlWriter writer) {
      withAlias = writer.getNextAlias(ANCESTOR_OF_ENTRY);
      final StringBuilder body = new StringBuilder();
      body.append("  SELECT anch_br1.parent_branch_id, 0 as branch_level FROM osee_branch anch_br1\n");
      body.append("   WHERE anch_br1.branch_id = ?");
      body.append("\n  UNION ALL \n");
      body.append("  SELECT parent_branch_id, branch_level - 1 FROM ").append(withAlias);
      body.append(" recurse, osee_branch br");
      body.append(" WHERE br.branch_id = recurse.parent_id");
      writer.addParameter(criteria.getChild());
      writer.addWithClause(newRecursiveWithClause(withAlias, "(parent_id, branch_level)", body.toString()));
      writer.addTable(withAlias);
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
   public boolean addPredicates(AbstractSqlWriter writer)  {
      writer.write("%s.branch_id = %s.parent_id", brAlias, withAlias);
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ANCESTOR_OF.ordinal();
   }
}
