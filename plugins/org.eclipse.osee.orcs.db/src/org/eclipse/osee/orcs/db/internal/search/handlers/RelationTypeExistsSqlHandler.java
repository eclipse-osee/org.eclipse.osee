/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeExistsSqlHandler extends AbstractRelationSqlHandler<CriteriaRelationTypeExists> {

   private String relAlias;
   private String txsAlias;

   @Override
   public void addTables(AbstractSqlWriter writer) {
      super.addTables(writer);
      relAlias = writer.addTable(criteria.getType());
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.RELATION);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      super.addPredicates(writer);
      writer.write(relAlias);
      writer.write(".rel_link_type_id = ?");
      writer.addParameter(criteria.getType());

      List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.writeAndLn();
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);

            writer.write("(");
            writer.write(relAlias);
            writer.write(".a_art_id = ");
            writer.write(artAlias);
            writer.write(".art_id");

            writer.write(" OR ");

            writer.write(relAlias);
            writer.write(".b_art_id = ");
            writer.write(artAlias);
            writer.write(".art_id)");

            if (index + 1 < aSize) {
               writer.writeAndLn();
            }
         }
      }
      writer.writeAndLn();
      writer.write(relAlias);
      writer.write(".gamma_id = ");
      writer.write(txsAlias);
      writer.write(".gamma_id");
      writer.writeAndLn();
      writer.writeTxBranchFilter(txsAlias);
   }
}