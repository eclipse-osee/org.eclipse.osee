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
import java.util.List;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateRange;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxDateRangeSqlHandler extends SqlHandler<CriteriaDateRange> {

   private CriteriaDateRange criteria;

   private String txAlias;

   @Override
   public void setData(CriteriaDateRange criteria) {
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
      Timestamp from = criteria.getFrom();
      Timestamp to = criteria.getTo();
      writer.write(txAlias);
      writer.write(".time >= ? and ");
      writer.write(txAlias);
      writer.write(".time <= ? ");
      writer.addParameter(from);
      writer.addParameter(to);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_DATE.ordinal();
   }
}