/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedToThruRels;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;

public class RelatedToThruRelsSqlHandler extends SqlHandler<CriteriaRelatedToThruRels> {
   private CriteriaRelatedToThruRels criteria;

   private final LinkedHashMap<RelationTypeSide, Pair<String, String>> relAliases = new LinkedHashMap<>();
   private String artAlias;

   @Override
   public void setData(CriteriaRelatedToThruRels criteria) {
      this.criteria = criteria;
   }

   private void writeTables(AbstractSqlWriter writer) {
      int i = 1;
      for (RelationTypeSide relType : criteria.getRelationTypeSides()) {
         String relAlias = "relExists" + i;
         String txsAlias = "txsRelExists" + i;
         relAliases.put(relType, new Pair<String, String>(txsAlias, relAlias));
         writer.write(
            ((relType.isNewRelationTable()) ? OseeDb.RELATION_TABLE2.getName() : OseeDb.RELATION_TABLE.getName()) + " " + relAlias + ", ");
         writer.write(OseeDb.TXS_TABLE.getName() + " " + txsAlias);
         if (i < criteria.getRelationTypeSides().size()) {
            writer.write(", ");
         }
         i++;
      }
   }

   private void writePredicate(AbstractSqlWriter writer, RelationTypeSide typeSide, String txsAliasName,
      String relAliasName, ArtifactId artId, String prevAlias) {

      writer.write(relAliasName);
      if (typeSide.isNewRelationTable()) {
         writer.write(".rel_type = ?");
      } else {
         writer.write(".rel_link_type_id = ?");
      }
      writer.addParameter(typeSide.getGuid());

      writer.writeAnd();
      String aOrbArtId = typeSide.getSide().isSideA() ? ".a_art_id" : ".b_art_id";
      String oppositeAOrBartId = typeSide.getSide().isSideA() ? ".b_art_id" : ".a_art_id";

      if (prevAlias.isEmpty()) {
         writer.write(relAliasName);
         writer.write(aOrbArtId);
         writer.write(" = " + artAlias + ".art_id");
      } else if (artId.isInvalid()) {
         writer.write(relAliasName);
         writer.write(aOrbArtId);
         writer.write(" = " + prevAlias + "." + oppositeAOrBartId);
      } else {
         writer.write(relAliasName);
         writer.write(aOrbArtId);
         writer.write(" = ? ");
         writer.addParameter(artId);
      }

   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      artAlias = writer.getMainTableAlias(OseeDb.ARTIFACT_TABLE);
      writer.writeAndLn();
      Entry<RelationTypeSide, Pair<String, String>> entry;
      String prevAlias = Strings.EMPTY_STRING;
      writer.write(" exists ( select null from ");
      writeTables(writer);
      Iterator<Entry<RelationTypeSide, Pair<String, String>>> iterator = relAliases.entrySet().iterator();
      writer.write(" where ");
      while (iterator.hasNext()) {
         entry = iterator.next();
         if (prevAlias.isEmpty()) {
            writePredicate(writer, entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond(),
               ArtifactId.SENTINEL, Strings.EMPTY_STRING);
            writer.writeAndLn();
            writer.writeEqualsAnd(entry.getValue().getFirst(), entry.getValue().getSecond(), "gamma_id");
            writer.writeTxBranchFilter(entry.getValue().getFirst());
            prevAlias = entry.getValue().getSecond();
         } else if (!iterator.hasNext()) {
            writer.writeAndLn();
            writePredicate(writer, entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond(),
               criteria.getId(), prevAlias);
            writer.writeAnd();
            writer.writeEqualsAnd(entry.getValue().getFirst(), entry.getValue().getSecond(), "gamma_id");
            writer.writeTxBranchFilter(entry.getValue().getFirst());
         } else {
            writer.writeAndLn();
            writePredicate(writer, entry.getKey(), entry.getValue().getFirst(), entry.getValue().getSecond(),
               ArtifactId.SENTINEL, prevAlias);
            writer.writeAnd();
            writer.writeEqualsAnd(entry.getValue().getFirst(), entry.getValue().getSecond(), "gamma_id");
            writer.writeTxBranchFilter(entry.getValue().getFirst());
         }
      }
      writer.write(") ");
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATED_TO_THRU_RELS.ordinal();
   }

}