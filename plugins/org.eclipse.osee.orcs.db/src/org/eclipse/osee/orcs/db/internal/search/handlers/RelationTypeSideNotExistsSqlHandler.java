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

import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideNotExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;

/**
 * @author John Misinco
 */
public class RelationTypeSideNotExistsSqlHandler extends AbstractRelationSqlHandler<CriteriaRelationTypeSideNotExists> {

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      super.addPredicates(writer);

      RelationTypeSide relationType = criteria.getType();
      writer.write("NOT EXISTS (SELECT 1 FROM ");
      String relAlias = writer.writeTable(OseeDb.RELATION_TABLE);
      writer.write(", ");
      String txsAlias = writer.writeTable(OseeDb.TXS_TABLE);
      writer.write(" WHERE ");
      writer.writeEqualsParameterAnd(relAlias, "rel_link_type_id", relationType);

      List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
      String side = relationType.getSide().isSideA() ? "a" : "b";
      int aSize = aliases.size();
      for (int index = 0; index < aSize; index++) {
         String artAlias = aliases.get(index);

         writer.writeEquals(relAlias, side + "_art_id", artAlias, "art_id");

         if (index + 1 < aSize) {
            writer.writeAndLn();
         }
      }
      writer.writeAndLn();
      writer.writeEquals(relAlias, txsAlias, "gamma_id");
      writer.writeAndLn();
      writer.writeTxBranchFilter(txsAlias);
      writer.write(")");
   }
}