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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaOrderByBranchId;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Luciano T. Vaglienti
 */
public class OrderByBranchIdSqlHandler extends SqlHandler<CriteriaOrderByBranchId> {

   @Override
   public void writeOrder(AbstractSqlWriter writer) {
      String brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      writer.write("%s.%s", brAlias, "branch_id");
   }

   @Override
   public boolean hasPredicates() {
      return false;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_ORDER_BY_ID.ordinal();
   }

}
