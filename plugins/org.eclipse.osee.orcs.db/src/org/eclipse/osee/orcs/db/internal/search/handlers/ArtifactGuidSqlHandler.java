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

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.WithClause;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactGuidSqlHandler extends SqlHandler<CriteriaArtifactGuids> {
   private CriteriaArtifactGuids criteria;
   private String jguidAlias;
   private AbstractJoinQuery joinQuery;
   private String withClauseName;
   private WithClause withClause;
   private String artAlias;
   private String txsAlias;

   @Override
   public void addWithTables(AbstractSqlWriter writer) {
      if (OptionsUtil.isHistorical(writer.getOptions())) {
         StringBuilder sb = new StringBuilder();
         sb.append("SELECT max(txs.transaction_id) as transaction_id, art.art_id as art_id\n");
         Collection<String> ids = criteria.getIds();
         if (ids.size() > 1) {
            sb.append("    FROM osee_txs txs, osee_artifact art, osee_join_char_id id\n");
         } else {
            sb.append("    FROM osee_txs txs, osee_artifact art\n");
         }
         sb.append("    WHERE txs.gamma_id = art.gamma_id\n");
         if (ids.size() > 1) {
            AbstractJoinQuery joinQuery = writer.writeCharJoin(ids);
            sb.append("    AND art.guid = id.id AND id.query_id = ?");

            writer.addParameter(joinQuery.getQueryId());
         } else {
            sb.append("    AND art.guid = ?");
            writer.addParameter(ids.iterator().next());
         }
         sb.append(" AND ");
         sb.append(writer.getWithClauseTxBranchFilter("txs", false));
         sb.append("\n    GROUP BY art.art_id");
         String body = sb.toString();

         withClauseName = writer.addReferencedWithClause("artUuid", body);
      }
   }

   @Override
   public void setData(CriteriaArtifactGuids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (criteria.getIds().size() > 1) {
         jguidAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
      }
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.getMainTableAlias(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<String> ids = criteria.getIds();
      if (ids.size() > 1) {
         joinQuery = writer.writeCharJoin(ids);
         writer.writeEquals(artAlias, "guid", jguidAlias, "id");
         writer.write(" AND ");
         writer.writeEqualsParameter(jguidAlias, "query_id", joinQuery.getQueryId());
      } else {
         writer.writeEqualsParameter(artAlias, "guid", ids.iterator().next());
      }
      if (withClause != null) {
         writer.write(" AND ");
         writer.writeEquals(withClauseName, txsAlias, "transaction_id");
         writer.write(" AND ");
         writer.writeEquals(withClauseName, artAlias, "art_id");
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_GUID.ordinal();
   }
}