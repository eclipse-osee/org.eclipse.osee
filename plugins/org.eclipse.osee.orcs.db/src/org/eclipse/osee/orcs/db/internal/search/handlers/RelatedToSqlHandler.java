/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.AliasEntry;
import org.eclipse.osee.orcs.db.internal.sql.ObjectType;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlUtil;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;
import org.eclipse.osee.orcs.db.internal.sql.WithClause;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class RelatedToSqlHandler extends SqlHandler<CriteriaRelatedTo> {

   private static final AliasEntry RELATION_WITH = SqlUtil.newAlias("relatedTo", "relTo");

   private CriteriaRelatedTo criteria;

   private String jIdAlias;
   private String relAlias;
   private String txsAlias;
   private String artAlias;
   private String artTxsAlias;

   private String withClauseName;
   private WithClause withClause;

   @Override
   public void addWithTables(AbstractSqlWriter writer) {
      if (OptionsUtil.isHistorical(writer.getOptions())) {
         StringBuilder sb = new StringBuilder();
         sb.append("SELECT max(txs.transaction_id) as transaction_id, rel.a_art_id as art_id\n");
         sb.append("    FROM osee_txs txs, osee_relation_link rel");
         if (criteria.hasMultipleIds()) {
            sb.append(", ");
            sb.append(TableEnum.ID_JOIN_TABLE.getName());
            sb.append(" ");
            sb.append(jIdAlias);
         }
         List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
         if (!aliases.isEmpty()) {
            int aSize = aliases.size();
            for (int index = 0; index < aSize; index++) {
               String artAlias = aliases.get(index);
               sb.append(", ");
               sb.append(TableEnum.ARTIFACT_TABLE.getName());
               sb.append(" ");
               sb.append(artAlias);
            }
         }
         sb.append("\n    WHERE  txs.gamma_id = rel.gamma_id AND \n");
         sb.append(getPredicate(writer, "txs", "rel"));
         sb.append(" AND ");
         sb.append(writer.getWithClauseTxBranchFilter("txs", false));
         sb.append("\n    GROUP BY rel.a_art_id\n");
         String body = sb.toString();

         withClauseName = writer.getNextAlias(RELATION_WITH);
         withClause = SqlUtil.newSimpleWithClause(withClauseName, body);
         writer.addWithClause(withClause);
         writer.addTable(withClauseName);
      }
   }

   @Override
   public void setData(CriteriaRelatedTo criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.hasMultipleIds()) {
         jIdAlias = writer.addTable(TableEnum.ID_JOIN_TABLE);
      }
      relAlias = writer.addTable(TableEnum.RELATION_TABLE);
      txsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.RELATION);

      List<String> artAliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (artAliases.isEmpty()) {
         artAlias = writer.addTable(TableEnum.ARTIFACT_TABLE);
         artTxsAlias = writer.addTable(TableEnum.TXS_TABLE, ObjectType.ARTIFACT);
      }
   }

   private String getPredicate(AbstractSqlWriter writer, String txsAliasName, String relAliasName)  {
      StringBuilder sb = new StringBuilder();
      RelationTypeSide typeSide = criteria.getType();
      sb.append(relAliasName);
      sb.append(".rel_link_type_id = ?");
      writer.addParameter(typeSide.getGuid());

      sb.append(" AND ");
      String aOrbArtId = typeSide.getSide().isSideA() ? ".a_art_id" : ".b_art_id";
      if (criteria.hasMultipleIds()) {
         AbstractJoinQuery joinQuery = writer.writeJoin(criteria.getIds());
         sb.append(relAliasName);
         sb.append(aOrbArtId);
         sb.append(" = ");
         sb.append(jIdAlias);
         sb.append(".id AND ");
         sb.append(jIdAlias);
         sb.append(".query_id = ?");
         writer.addParameter(joinQuery.getQueryId());
      } else {
         sb.append(relAliasName);
         sb.append(aOrbArtId);
         sb.append(" = ?");
         writer.addParameter(criteria.getId());
      }

      List<String> aliases = writer.getAliases(TableEnum.ARTIFACT_TABLE);
      if (!aliases.isEmpty()) {
         sb.append("\n AND \n");
         String oppositeAOrBartId = typeSide.getSide().isSideA() ? ".b_art_id" : ".a_art_id";
         int aSize = aliases.size();
         for (int index = 0; index < aSize; index++) {
            String artAlias = aliases.get(index);

            sb.append(relAliasName);
            sb.append(oppositeAOrBartId);
            sb.append(" = ");
            sb.append(artAlias);
            sb.append(".art_id");

            if (index + 1 < aSize) {
               sb.append("\n AND \n");
            }
         }
      }
      sb.append("\n AND \n");
      sb.append(relAliasName);
      sb.append(".gamma_id = ");
      sb.append(txsAliasName);
      sb.append(".gamma_id");
      return sb.toString();
   }

   @Override
   public boolean addPredicates(AbstractSqlWriter writer)  {
      if (artAlias != null && artTxsAlias != null) {
         writer.writeEquals(artAlias, artTxsAlias, "gamma_id");
         writer.write(" AND ");
         writer.write(writer.getTxBranchFilter(artTxsAlias));
         writer.writeAndLn();
      }
      writer.write(getPredicate(writer, txsAlias, relAlias));
      if (withClause != null) {
         writer.writeAndLn();
         writer.write(txsAlias);
         writer.write(".transaction_id = ");
         writer.write(withClauseName);
         writer.write(".transaction_id");
      }
      writer.writeAndLn();
      boolean includeDeletedRelations = OptionsUtil.areDeletedRelationsIncluded(writer.getOptions());
      writer.write(writer.getTxBranchFilter(txsAlias, includeDeletedRelations));
      return true;
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.RELATED_TO_ART_IDS.ordinal();
   }

}
