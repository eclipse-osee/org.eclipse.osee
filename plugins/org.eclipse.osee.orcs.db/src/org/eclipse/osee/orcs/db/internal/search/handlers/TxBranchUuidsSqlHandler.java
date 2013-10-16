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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxBranchUuids;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxBranchUuidsSqlHandler extends SqlHandler<CriteriaTxBranchUuids> {

   private CriteriaTxBranchUuids criteria;

   private String txAlias;
   private String jCharIdAlias;
   private String brAlias;

   @Override
   public void setData(CriteriaTxBranchUuids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getIds().size() > 1) {
         jCharIdAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }

      List<String> brAliases = writer.getAliases(TableEnum.BRANCH_TABLE);
      if (brAliases.isEmpty()) {
         brAlias = writer.addTable(TableEnum.BRANCH_TABLE);
      } else {
         brAlias = brAliases.iterator().next();
      }

      List<String> aliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (aliases.isEmpty()) {
         txAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txAlias = aliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      Collection<? extends IOseeBranch> ids = criteria.getIds();
      if (ids.size() > 1) {
         Set<String> guids = new LinkedHashSet<String>();
         for (IOseeBranch branch : ids) {
            guids.add(branch.getGuid());
         }
         AbstractJoinQuery joinQuery = writer.writeCharJoin(guids);
         writer.write(brAlias);
         writer.write(".branch_guid = ");
         writer.write(jCharIdAlias);
         writer.write(".id AND ");
         writer.write(jCharIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(brAlias);
         writer.write(".branch_guid = ?");
         writer.addParameter(ids.iterator().next().getGuid());
      }
      writer.writeAndLn();
      writer.write(txAlias);
      writer.write(".branch_id = ");
      writer.write(brAlias);
      writer.write(".branch_id");
      return true;
   }

   @Override
   public int getPriority() {
      return TxSqlHandlerPriority.BRANCH_ID.ordinal();
   }
}
