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
public class XtraRelationDataSqlHandler extends AbstractXtraTableSqlHandler {

   private String itemAlias;
   private String txsAlias;

   public XtraRelationDataSqlHandler() {
      super();
   }

   @Override
   public void addTables(AbstractSqlWriter writer)  {
      itemAlias = writer.addTable(TableEnum.RELATION_TABLE, ObjectType.RELATION);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.RELATION);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      String artAlias = writer.getLastAlias(TableEnum.ARTIFACT_TABLE);
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
      writer.writeEquals(itemAlias, txsAlias, "gamma_id");
      writer.write(" AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATION_DATA_XTRA.ordinal();
   }
}