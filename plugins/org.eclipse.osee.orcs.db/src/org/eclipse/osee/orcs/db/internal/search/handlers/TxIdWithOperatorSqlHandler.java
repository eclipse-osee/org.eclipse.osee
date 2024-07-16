/*********************************************************************
 * Copyright (c) 2013 Boeing
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

import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithOperator;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class TxIdWithOperatorSqlHandler extends SqlHandler<CriteriaTxIdWithOperator> {

   private CriteriaTxIdWithOperator criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaTxIdWithOperator criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.getMainTableAlias(OseeDb.TX_DETAILS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Operator op = criteria.getOperator();
      int id = criteria.getId();
      writer.write(txdAlias);
      writer.write(".transaction_id ");
      writer.write(op.toString());
      writer.write(" ?");
      writer.addParameter(id);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_LAST.ordinal();
   }
}