/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

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
      txdAlias = writer.getMainTableAlias(TableEnum.TX_DETAILS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      BranchId branch = criteria.getTxId().getBranch();
      writer.writeEqualsParameterAnd(txdAlias, "branch_id", branch);
      writer.write(txdAlias);
      writer.write(".transaction_id = ");
      writer.write("(SELECT max(transaction_id) FROM ");
      writer.writeTableNoAlias(TableEnum.TX_DETAILS_TABLE);
      writer.write(" WHERE ");
      writer.writeEqualsParameterAnd("branch_id", branch);
      writer.write("transaction_id < ?");
      writer.addParameter(criteria.getTxId());
      writer.write(")");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}