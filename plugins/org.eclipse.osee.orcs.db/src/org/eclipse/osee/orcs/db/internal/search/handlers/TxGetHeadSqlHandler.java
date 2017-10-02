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

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetHead;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxGetHeadSqlHandler extends SqlHandler<CriteriaTxGetHead> {

   private CriteriaTxGetHead criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaTxGetHead criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> aliases = writer.getAliases(TableEnum.TX_DETAILS_TABLE);
      if (aliases.isEmpty()) {
         txdAlias = writer.addTable(TableEnum.TX_DETAILS_TABLE);
      } else {
         txdAlias = aliases.iterator().next();
      }
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      writer.write(txdAlias);
      writer.write(".transaction_id = ");

      writer.write("(SELECT max(transaction_id) FROM ");
      writer.write(TableEnum.TX_DETAILS_TABLE.getName());
      writer.write(" WHERE ");
      writer.write("branch_id = ?)");

      writer.addParameter(criteria.getBranch());
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}
