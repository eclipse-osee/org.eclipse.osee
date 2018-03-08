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

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxGetPriorSqlHandler extends SqlHandler<CriteriaTxGetPrior> {
   private CriteriaTxGetPrior criteria;
   private String txdAlias;

   @Override
   public void setData(CriteriaTxGetPrior criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.getOrCreateTableAlias(TableEnum.TX_DETAILS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) {
      BranchId branch = criteria.getTxId().getBranch();
      writer.writeEqualsParameter(txdAlias, "branch_id", branch);
      writer.write(" AND ");
      writer.write(txdAlias);
      writer.write(".transaction_id = ");
      writer.write("(SELECT max(transaction_id) FROM ");
      writer.writeTableNoAlias(TableEnum.TX_DETAILS_TABLE);
      writer.write(" WHERE ");
      writer.writeEqualsParameter("branch_id", branch);
      writer.write(" AND ");
      writer.write("transaction_id < ?");
      writer.addParameter(criteria.getTxId());
      writer.write(")");
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}