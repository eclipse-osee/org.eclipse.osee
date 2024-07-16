/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.jdbc.ObjectType;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class RelatedToSqlHandler extends SqlHandler<CriteriaRelatedTo> {
   private CriteriaRelatedTo criteria;

   private String jIdAlias;
   private String relAlias;
   private String txsAlias;
   private String cteAlias;

   @Override
   public void writeCommonTableExpression(AbstractSqlWriter writer) {
      if (OptionsUtil.isHistorical(writer.getOptions())) {
         cteAlias = writer.startCommonTableExpression("relTo");

         writer.write("SELECT max(txs.transaction_id) as transaction_id, rel.a_art_id as art_id\n");
         writer.write(" FROM osee_txs txs, osee_relation_link rel");
         if (criteria.hasMultipleIds()) {
            writer.write(", ");
            writer.write(OseeDb.OSEE_JOIN_ID_TABLE.getName());
            writer.write(" ");
            writer.write(jIdAlias);
         }
         List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
         if (!aliases.isEmpty()) {
            int aSize = aliases.size();
            for (int index = 0; index < aSize; index++) {
               String artAlias = aliases.get(index);
               writer.write(", ");
               writer.write(OseeDb.ARTIFACT_TABLE.getName());
               writer.write(" ");
               writer.write(artAlias);
            }
         }
         writer.write("\n WHERE  txs.gamma_id = rel.gamma_id AND \n");
         writePredicate(writer, "txs", "rel");
         writer.write(" AND ");
         writer.writeTxBranchFilter("txs");
         writer.write("\n GROUP BY rel.a_art_id\n");
      }
   }

   @Override
   public void setData(CriteriaRelatedTo criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (cteAlias != null) {
         writer.addTable(cteAlias);
      }
      if (criteria.hasMultipleIds()) {
         jIdAlias = writer.addTable(OseeDb.OSEE_JOIN_ID_TABLE);
      }
      relAlias = writer.addTable(OseeDb.RELATION_TABLE);
      txsAlias = writer.addTable(OseeDb.TXS_TABLE, ObjectType.RELATION);
   }

   private void writePredicate(AbstractSqlWriter writer, String txsAliasName, String relAliasName) {
      RelationTypeSide typeSide = criteria.getType();
      writer.write(relAliasName);
      writer.write(".rel_link_type_id = ?");
      writer.addParameter(typeSide.getGuid());

      writer.write(" AND ");
      String aOrbArtId = typeSide.getSide().isSideA() ? ".a_art_id" : ".b_art_id";
      if (criteria.hasMultipleIds()) {
         AbstractJoinQuery joinQuery = writer.writeJoin(criteria.getIds());
         writer.write(relAliasName);
         writer.write(aOrbArtId);
         writer.write(" = ");
         writer.write(jIdAlias);
         writer.write(".id AND ");
         writer.write(jIdAlias);
         writer.write(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         writer.write(relAliasName);
         writer.write(aOrbArtId);
         writer.write(" = ?");
         writer.addParameter(criteria.getId());
      }

      List<String> aliases = writer.getAliases(OseeDb.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         writer.write("\n AND \n");
         String oppositeAOrBartId = typeSide.getSide().isSideA() ? ".b_art_id" : ".a_art_id";
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);

            writer.write(relAliasName);
            writer.write(oppositeAOrBartId);
            writer.write(" = ");
            writer.write(artAlias);
            writer.write(".art_id");

            if (index + 1 < aSize) {
               writer.write("\n AND \n");
            }
         }
      }
      writer.write("\n AND \n");
      writer.write(relAliasName);
      writer.write(".gamma_id = ");
      writer.write(txsAliasName);
      writer.write(".gamma_id");
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      writePredicate(writer, txsAlias, relAlias);
      if (cteAlias != null) {
         writer.writeAndLn();
         writer.write(txsAlias);
         writer.write(".transaction_id = ");
         writer.write(cteAlias);
         writer.write(".transaction_id");
      }
      writer.writeAndLn();
      boolean includeDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(writer.getOptions());
      writer.writeTxBranchFilter(txsAlias, includeDeletedRelations);
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATED_TO_ART_IDS.ordinal();
   }
}