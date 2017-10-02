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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchArchivedSqlHandler extends SqlHandler<CriteriaBranchArchived> {

   private CriteriaBranchArchived criteria;

   private String brAlias;
   private String jIdAlias;

   @Override
   public void setData(CriteriaBranchArchived criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getStates().size() > 1) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
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
      Collection<BranchArchivedState> states = criteria.getStates();
      if (states.size() > 1) {
         Set<Integer> ids = new HashSet<>();
         for (BranchArchivedState state : states) {
            ids.add(state.getValue());
         }
         AbstractJoinQuery joinQuery = writer.writeIdJoin(ids);
         writer.write(brAlias);
         writer.write(".archived = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(brAlias);
         writer.write(".archived = ?");
         writer.addParameter(states.iterator().next().getValue());
      }
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ARCHIVED.ordinal();
   }
}
