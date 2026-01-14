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
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.search.ds.criteria.CriteriaRelationTypeExists;

/**
 * @author Roberto E. Escobar
 */
public class RelationTypeExistsSqlHandler extends AbstractRelationSqlHandler<CriteriaRelationTypeExists> {

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      String relAlias = "eRel";
      String relType;
      String relTableName;
      if (this.criteria.getType().isNewRelationTable()) {
         relType = "rel_type";
         relTableName = "osee_relation";
      } else {
         relType = "rel_link_type_id";
         relTableName = "osee_relation_link";
      }
      writer.write(
         " exists (select null from %s eRel, osee_txs relTxs where relTxs.branch_id = ? and relTxs.tx_current = 1 and relTxs.gamma_id = eRel.gamma_id ",
         relTableName);
      writer.addParameter(writer.getRootQueryData().getBranch());
      writer.write(" and eRel.%s = ? ", relType);
      writer.addParameter(this.criteria.getType());

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
      writer.write(")");
   }
}