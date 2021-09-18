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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllTxs;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 */
public class AllTxsSqlHandler extends SqlHandler<CriteriaAllTxs> {

   @Override
   public void addTables(AbstractSqlWriter writer) {
      writer.getMainTableAlias(OseeDb.TX_DETAILS_TABLE);
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_LAST.ordinal();
   }
}