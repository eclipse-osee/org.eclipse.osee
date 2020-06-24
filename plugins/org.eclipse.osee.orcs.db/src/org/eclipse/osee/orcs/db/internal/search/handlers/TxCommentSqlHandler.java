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
         writer.writePatternMatch(txdAlias, "osee_comment", value);
      } else {
         writer.write(txdAlias);
         writer.write(".osee_comment = ?");
         writer.addParameter(value);
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.TX_COMMENT.ordinal();
   }
}