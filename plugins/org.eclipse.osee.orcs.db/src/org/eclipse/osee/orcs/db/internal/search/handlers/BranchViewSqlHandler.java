/*********************************************************************
 * Copyright (c) 2025 Boeing
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
/**
 * @author Audrey Denk
 */
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class BranchViewSqlHandler extends SqlHandler {


   @Override
   public void addTables(AbstractSqlWriter writer) {

      writer.addTable(writer.getAliasManager().getFirstUsedAlias(AbstractSqlWriter.validApps));

   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_VIEW.ordinal();
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      String artTxsAlias = writer.getMainTableAlias(OseeDb.TXS_TABLE);
      writer.writeEquals(writer.getAliasManager().getFirstUsedAlias(AbstractSqlWriter.validApps), artTxsAlias,
         "app_id");
   }
}