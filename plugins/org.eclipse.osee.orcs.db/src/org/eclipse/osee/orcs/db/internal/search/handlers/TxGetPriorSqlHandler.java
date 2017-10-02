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

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxGetPriorSqlHandler extends SqlHandler<CriteriaTxGetPrior> {

   private CriteriaTxGetPrior criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaTxGetPrior criteria) {
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
      writer.write("(SELECT max(td2.transaction_id) FROM ");
      writer.write(TableEnum.TX_DETAILS_TABLE.getName());
      writer.write(" td1,");
      writer.write(TableEnum.TX_DETAILS_TABLE.getName());
      writer.write(" td2");
      writer.write(" WHERE ");
      writer.write("td1");
      writer.write(".transaction_id = ?");
      writer.write(" AND ");
      writer.write("td1.branch_id = td2.branch_id");
      writer.write(" AND ");
      writer.write("td1.transaction_id > td2.transaction_id)");
      writer.addParameter(criteria.getTxId());
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_ID.ordinal();
   }
}
