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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public class XtraAttributeDataSqlHandler extends AbstractXtraTableSqlHandler {

   private String txsAlias;
   private String attrAlias;

   @Override
   public void addTables(AbstractSqlWriter writer)  {
      attrAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE, ObjectType.ATTRIBUTE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      String artAlias = writer.getLastAlias(TableEnum.ARTIFACT_TABLE);
      writer.writeEquals(artAlias, attrAlias, "art_id");
      writer.write(" AND ");
      writer.writeEquals(attrAlias, txsAlias, "gamma_id");
      writer.write(" AND ");
      writer.write(writer.getTxBranchFilter(txsAlias));
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_DATA_XTRA.ordinal();
   }
}