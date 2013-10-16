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
import java.util.List;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchUuids;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class BranchGuidSqlHandler extends SqlHandler<CriteriaBranchUuids> {

   private CriteriaBranchUuids criteria;

   private String brAlias;
   private String jguidAlias;
   private AbstractJoinQuery joinQuery;

   @Override
   public void setData(CriteriaBranchUuids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getIds().size() > 1) {
         jguidAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }

      List<String> branchAliases = writer.getAliases(TableEnum.BRANCH_TABLE);
      if (branchAliases.isEmpty()) {
         brAlias = writer.addTable(TableEnum.BRANCH_TABLE);
      } else {
         brAlias = branchAliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      Collection<String> ids = criteria.getIds();
      if (ids.size() > 1) {
         joinQuery = writer.writeCharJoin(ids);
         writer.write(brAlias);
         writer.write(".branch_guid = ");
         writer.write(jguidAlias);
         writer.write(".id AND ");
         writer.write(jguidAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(brAlias);
         writer.write(".branch_guid = ?");
         writer.addParameter(ids.iterator().next());
      }
      return true;
   }

   @Override
   public int getPriority() {
      return BranchSqlHandlerPriority.BRANCH_GUID.ordinal();
   }
}
