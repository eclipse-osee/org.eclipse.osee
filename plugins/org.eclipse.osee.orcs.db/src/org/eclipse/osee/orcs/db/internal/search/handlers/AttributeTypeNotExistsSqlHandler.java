/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author John Misinco
 */
public class AttributeTypeNotExistsSqlHandler extends SqlHandler<CriteriaAttributeTypeNotExists> {

   private CriteriaAttributeTypeNotExists criteria;

   private String artAlias;
   private String txsAlias;

   @Override
   public void setData(CriteriaAttributeTypeNotExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      IAttributeType type = criteria.getType();

      writer.writeEquals(artAlias, txsAlias, "gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter(txsAlias));
      writer.writeAndLn();

      writer.write("NOT EXISTS (SELECT 1 FROM ");
      writer.write(TableEnum.ATTRIBUTE_TABLE.getName());
      writer.write(" attr, ");
      writer.write(TableEnum.TXS_TABLE.getName());
      writer.write(" txs WHERE attr.attr_type_id = ? ");
      writer.addParameter(type.getGuid());
      writer.writeAndLn();

      writer.writeEquals("attr", "txs", "gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter("txs"));
      writer.writeAndLn();
      writer.writeEquals("attr", artAlias, "art_id");
      writer.write(")");

      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_NOT_EXISTS.ordinal();
   }

}
