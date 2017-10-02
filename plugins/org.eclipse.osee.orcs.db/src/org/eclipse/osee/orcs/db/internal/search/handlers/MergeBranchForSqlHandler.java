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

import java.util.List;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMergeBranchFor;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

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
      List<String> mergeAliases = writer.getAliases(TableEnum.MERGE_TABLE);
      if (mergeAliases.isEmpty()) {
         mergeAlias = writer.addTable(TableEnum.MERGE_TABLE);
      } else {
         mergeAlias = mergeAliases.iterator().next();
      }

      List<String> branchAliases = writer.getAliases(TableEnum.BRANCH_TABLE);
      if (branchAliases.isEmpty()) {
         brAlias = writer.addTable(TableEnum.BRANCH_TABLE);
      } else {
         brAlias = branchAliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      writer.write("%s.source_branch_id = ?", mergeAlias);
      writer.addParameter(criteria.getSource());
      writer.writeAndLn();
      writer.write("%s.dest_branch_id = ?", mergeAlias);
      writer.addParameter(criteria.getDestination());
      writer.writeAndLn();
      writer.write("%s.merge_branch_id = %s.branch_id", mergeAlias, brAlias);
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.MERGE_BRANCH_FOR.ordinal();
   }
}
