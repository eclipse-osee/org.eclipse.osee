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

import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class XtraAttributeDataSqlHandler extends AbstractXtraTableSqlHandler {

   private String txsAlias;
   private String attAlias;
   private String artAlias;

   @Override
   public void addTables(AbstractSqlWriter writer) {
      attAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE, ObjectType.ATTRIBUTE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ATTRIBUTE);
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writer.writeEqualsAnd(artAlias, attAlias, "art_id");
      writer.writeEqualsAnd(attAlias, txsAlias, "gamma_id");
      writer.writeTxBranchFilter(txsAlias);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_DATA_XTRA.ordinal();
   }
}