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
public class XtraRelationDataSqlHandler extends AbstractXtraTableSqlHandler {

   private String itemAlias;
   private String txsAlias;

   public XtraRelationDataSqlHandler() {
      super();
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      itemAlias = writer.addTable(OseeDb.RELATION_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.RELATION);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      String artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      writer.write("(");
      writer.write(artAlias);
      writer.write(".art_id = ");
      writer.write(itemAlias);
      writer.write(".a_art_id");
      writer.write(" OR ");
      writer.write(artAlias);
      writer.write(".art_id = ");
      writer.write(itemAlias);
      writer.write(".b_art_id");
      writer.write(") AND ");
      writer.writeEqualsAnd(itemAlias, txsAlias, "gamma_id");
      writer.writeTxBranchFilter(txsAlias);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATION_DATA_XTRA.ordinal();
   }
}