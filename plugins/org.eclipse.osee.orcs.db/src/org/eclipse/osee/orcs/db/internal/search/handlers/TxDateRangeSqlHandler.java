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

import java.sql.Timestamp;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateRange;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 */
public class TxDateRangeSqlHandler extends SqlHandler<CriteriaDateRange> {

   private CriteriaDateRange criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaDateRange criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.getMainTableAlias(OseeDb.TX_DETAILS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Timestamp from = criteria.getFrom();
      Timestamp to = criteria.getTo();
      writer.write(txdAlias);
      writer.write(".time >= ? and ");
      writer.write(txdAlias);
      writer.write(".time <= ? ");
      writer.addParameter(from);
      writer.addParameter(to);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_DATE.ordinal();
   }
}