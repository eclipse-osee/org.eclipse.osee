/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

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
      branchAlias = writer.addTable(TableEnum.BRANCH_TABLE, objectType);
      txsAlias = writer.getFirstAlias(getLevel(), TableEnum.TXS_TABLE, objectType);
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