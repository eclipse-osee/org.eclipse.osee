/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTokenQuery;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

/**
 * @author Ryan D. Brooks
 */
public class TokenQueryHandler extends SqlHandler<CriteriaTokenQuery> {

   private String attTxsAlias;
   private String attAlias;
   private CriteriaTokenQuery criteria;

   @Override
   public void setData(CriteriaTokenQuery criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      attAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      attTxsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ATTRIBUTE);
   }

   @Override
   public void writeSelectFields(AbstractSqlWriter writer) {
      writer.writeCommaIfNotFirst();
      String artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      writer.write("%s.art_type_id, %s.attr_type_id, %s.value", artAlias, attAlias, attAlias);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      String artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      writer.writeEqualsAnd(artAlias, attAlias, "art_id");
      if (criteria.getAttributeType().isValid()) {
         writer.write(attAlias);
         writer.write(".attr_type_id = ? AND ");
         writer.addParameter(criteria.getAttributeType());
      }
      writer.writeEqualsAnd(attAlias, attTxsAlias, "gamma_id");
      writer.writeTxBranchFilter(attTxsAlias);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_TOKEN_QUERY.ordinal();
   }
}