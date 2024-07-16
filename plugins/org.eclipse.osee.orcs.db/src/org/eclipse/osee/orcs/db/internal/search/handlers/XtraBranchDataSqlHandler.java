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

import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class XtraBranchDataSqlHandler extends AbstractXtraTableSqlHandler {

   private final SqlHandlerPriority priority;
   private final ObjectType objectType;

   public XtraBranchDataSqlHandler(SqlHandlerPriority priority, ObjectType objectType) {
      super();
      this.priority = priority;
      this.objectType = objectType;
   }

   private String branchAlias;
   private String txsAlias;

   @Override
   public void addTables(AbstractSqlWriter writer) {
      branchAlias = writer.addTable(OseeDb.BRANCH_TABLE, objectType);
      txsAlias = writer.getFirstAlias(getLevel(), OseeDb.TXS_TABLE, objectType);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEquals(txsAlias, branchAlias, "branch_id");
   }

   @Override
   public boolean hasPredicates() {
      return txsAlias != null && !txsAlias.isEmpty();
   }

   @Override
   public int getPriority() {
      return priority.ordinal();
   }
}