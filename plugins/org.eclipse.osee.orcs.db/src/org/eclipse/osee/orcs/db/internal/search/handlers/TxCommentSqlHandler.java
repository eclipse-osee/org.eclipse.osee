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

import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxComment;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Roberto E. Escobar
 */
public class TxCommentSqlHandler extends SqlHandler<CriteriaTxComment> {

   private CriteriaTxComment criteria;

   private String txdAlias;

   @Override
   public void setData(CriteriaTxComment criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      txdAlias = writer.getMainTableAlias(TableEnum.TX_DETAILS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      String value = criteria.getValue();
      if (criteria.isPattern()) {
         writer.writePatternMatch(txdAlias + ".osee_comment", "?");
      } else {
         writer.write(txdAlias);
         writer.write(".osee_comment = ?");
      }
      writer.addParameter(value);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_COMMENT.ordinal();
   }
}
