/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchOrderByName;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Luciano T. Vaglienti
 */
public class BranchOrderByNameSqlHandler extends SqlHandler<CriteriaBranchOrderByName> {

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ORDER_BY_NAME.ordinal();
   }

   @Override
   public void writeOrder(AbstractSqlWriter writer) {
      String brTable = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      writer.write(brTable + ".branch_name, ");
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }
}
