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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class BranchNameSqlHandler extends SqlHandler<CriteriaBranchName> {

   private CriteriaBranchName criteria;

   private String brAlias;

   @Override
   public void setData(CriteriaBranchName criteria) {
      this.criteria = criteria;
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
      String value = criteria.getValue();
      if (criteria.isPattern()) {
         writer.writePatternMatch(brAlias + ".branch_name", "?");
      } else {
         writer.write(brAlias);
         writer.write(".branch_name = ?");
      }
      writer.addParameter(value);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_NAME.ordinal();
   }
}
