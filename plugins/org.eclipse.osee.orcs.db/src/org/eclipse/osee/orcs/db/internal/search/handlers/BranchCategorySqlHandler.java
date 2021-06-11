/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchCategory;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Audrey Denk
 */
public class BranchCategorySqlHandler extends SqlHandler<CriteriaBranchCategory> {

   private CriteriaBranchCategory criteria;
   private String brcAlias;
   private String brAlias;
   private String txAlias;

   @Override
   public void setData(CriteriaBranchCategory criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      brAlias = writer.getMainTableAlias(OseeDb.BRANCH_TABLE);
      brcAlias = writer.getMainTableAlias(OseeDb.BRANCH_CATEGORY);
      txAlias = writer.getMainTableAlias(OseeDb.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.write("%s.category = ? and ", brcAlias);
      writer.write("%s.branch_id = %s.branch_id and ", brAlias, brcAlias);
      writer.write("%s.branch_id = %s.branch_id and ", brAlias, txAlias);
      writer.write("%s.tx_current = ? and ", txAlias);
      writer.write("%s.gamma_id = %s.gamma_id", brcAlias, txAlias);
      writer.addParameter(criteria.getBranchCategory());
      writer.addParameter(TxCurrent.CURRENT);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.BRANCH_CATEGORY.ordinal();
   }
}
