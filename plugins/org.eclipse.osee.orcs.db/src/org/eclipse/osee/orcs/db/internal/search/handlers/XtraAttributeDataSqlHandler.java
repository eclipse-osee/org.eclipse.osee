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
      writer.writeEquals(artAlias, attAlias, "art_id");
      writer.write(" AND ");
      writer.writeEquals(attAlias, txsAlias, "gamma_id");
      writer.write(" AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_DATA_XTRA.ordinal();
   }
}