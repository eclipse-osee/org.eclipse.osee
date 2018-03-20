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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithTwoOperators;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class TxIdWithTwoOperatorsSqlHandler extends SqlHandler<CriteriaTxIdWithTwoOperators> {

   private CriteriaTxIdWithTwoOperators criteria;

   private String txAlias;

   @Override
   public void setData(CriteriaTxIdWithTwoOperators criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> branchAliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (branchAliases.isEmpty()) {
         txAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txAlias = branchAliases.iterator().next();
      }
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Operator op1 = criteria.getOperator1();
      int id1 = criteria.getId1();
      Operator op2 = criteria.getOperator2();
      int id2 = criteria.getId2();
      writer.write(txAlias);
      writer.write(".transaction_id ");
      writer.write(op1.toString());
      writer.write(" ? and ");
      writer.write(txAlias);
      writer.write(".transaction_id ");
      writer.write(op2.toString());
      writer.write(" ?");
      writer.addParameter(id1);
      writer.addParameter(id2);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}