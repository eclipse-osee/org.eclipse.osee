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

import java.sql.Timestamp;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateWithOperator;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.search.Operator;

/**
 * @author Roberto E. Escobar
 */
public class TxDateWithOperatorSqlHandler extends SqlHandler<CriteriaDateWithOperator> {

   private CriteriaDateWithOperator criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaDateWithOperator criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.getMainTableAlias(TableEnum.TX_DETAILS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Operator op = criteria.getOperator();
      Timestamp time = criteria.getTimestamp();
      writer.write(txdAlias);
      writer.write(".time ");
      writer.write(op.toString());
      writer.write(" ?");
      writer.addParameter(time);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_DATE.ordinal();
   }
}