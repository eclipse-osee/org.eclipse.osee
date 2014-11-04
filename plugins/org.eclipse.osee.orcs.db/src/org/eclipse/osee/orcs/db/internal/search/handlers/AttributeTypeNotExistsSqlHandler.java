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
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author John Misinco
 */
public class AttributeTypeNotExistsSqlHandler extends SqlHandler<CriteriaAttributeTypeNotExists> {

   private CriteriaAttributeTypeNotExists criteria;

   private String txsAlias;

   @Override
   public void setData(CriteriaAttributeTypeNotExists criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         writer.addTable(TableEnum.ARTIFACT_TABLE);
      }
      txsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer) throws OseeCoreException {
      IAttributeType type = criteria.getType();

      writer.write("NOT EXISTS (SELECT 1 FROM ");
      writer.write(TableEnum.ATTRIBUTE_TABLE.getName());
      writer.write(" attr, ");
      writer.write(TableEnum.TXS_TABLE.getName());
      writer.write(" txs WHERE attr.attr_type_id = ?");
      writer.addParameter(type.getGuid());

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      writer.writeAndLn();
      int aSize = aliases.size();
      for (int index = 0; index < aSize; index++) {
         String artAlias = aliases.get(index);

         writer.write("attr.art_id = ");
         writer.write(artAlias);
         writer.write(".art_id");

         if (index + 1 < aSize) {
            writer.writeAndLn();
         }
      }
      writer.writeAndLn();
      writer.write("attr.gamma_id = txs.gamma_id");
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter("txs"));
      writer.write(")");
      writer.writeAndLn();

      for (int index = 0; index < aSize; index++) {
         String artAlias = aliases.get(index);
         writer.write(artAlias);
         writer.write(".gamma_id = ");
         writer.write(txsAlias);
         writer.write(".gamma_id");
         if (index + 1 < aSize) {
            writer.writeAndLn();
         }
      }
      writer.writeAndLn();
      writer.write(writer.getTxBranchFilter(txsAlias));

      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TYPE_EXISTS.ordinal();
   }

}
