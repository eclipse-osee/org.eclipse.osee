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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetHead;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxGetHeadSqlHandler extends SqlHandler<CriteriaTxGetHead> {

   private CriteriaTxGetHead criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaTxGetHead criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> aliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (aliases.isEmpty()) {
         txdAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txdAlias = aliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      writer.write(txdAlias);
      writer.write(".transaction_id = ");
      if (criteria.hasBranchToken()) {
         IOseeBranch branch = criteria.getBranch();
         writer.write("(SELECT max(txdi.transaction_id) FROM ");
         writer.write(TableEnum.BRANCH_TABLE.getName());
         writer.write(" obi, ");
         writer.write(TableEnum.TX_DETAILS_TABLE.getName());
         writer.write(" txdi WHERE ");
         writer.write("obi.branch_id = txdi.branch_id AND ");
         writer.write("obi.branch_guid = ?)");
         writer.addParameter(branch.getGuid());
      } else {
         int branch = criteria.getBranchid();
         writer.write("(SELECT max(transaction_id) FROM ");
         writer.write(TableEnum.TX_DETAILS_TABLE.getName());
         writer.write(" WHERE ");
         writer.write("branch_id = ?)");
         writer.addParameter(branch);
      }
      return true;
   }

   @Override
   public int getPriority() {
      return TxSqlHandlerPriority.TX_ID.ordinal();
   }
}
