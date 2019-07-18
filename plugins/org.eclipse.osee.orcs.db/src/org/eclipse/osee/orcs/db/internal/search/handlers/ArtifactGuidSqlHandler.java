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
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactGuidSqlHandler extends SqlHandler<CriteriaArtifactGuids> {
   private CriteriaArtifactGuids criteria;
   private String jguidAlias;
   private String withAlias;
   private String artAlias;
   private String txsAlias;

   @Override
   public void addWithTables(AbstractSqlWriter writer) {
      if (OptionsUtil.isHistorical(writer.getOptions())) {
         withAlias = writer.startWithClause("artMax");

         /*
          * Use max to find the latest txs entry for the artifact regardless of mod_type, so use true for allowDeleted
          * in call to writeTxBranchFilter. The mod_type must be checked outside the max in case of do not allow deleted
          * so that filter doesn't force the max back to a prior version when the correct version is deleted.
          */
         writer.write("SELECT max(txs.transaction_id) as transaction_id, art.art_id as art_id\n    FROM ");
         Collection<String> ids = criteria.getIds();
         if (ids.size() > 1) {
            writer.write("osee_join_char_id jid, ");
         }
         writer.write("osee_artifact art, osee_txs txs\n    WHERE ");
         if (ids.size() > 1) {
            AbstractJoinQuery joinQuery = writer.writeCharJoin(ids);
            writer.writeEqualsParameterAnd("jid", "query_id", joinQuery.getQueryId());
            writer.writeEqualsAnd("jid", "id", "art", "guid");
         } else {
            writer.writeEqualsParameterAnd("art", "guid", ids.iterator().next());
         }
         writer.writeEqualsAnd("art", "txs", "gamma_id");
         writer.writeTxBranchFilter("txs", true);
         writer.write("\n    GROUP BY art.art_id");
      }
   }

   @Override
   public void setData(CriteriaArtifactGuids criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      if (withAlias == null) {
         if (criteria.getIds().size() > 1) {
            jguidAlias = writer.addTable(TableEnum.CHAR_JOIN_TABLE);
         }
      } else {
         writer.addTable(withAlias);
      }
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      txsAlias = writer.getMainTableAlias(TableEnum.TXS_TABLE);
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      Collection<String> ids = criteria.getIds();

      if (withAlias == null) {
         if (ids.size() > 1) {
            AbstractJoinQuery joinQuery = writer.writeCharJoin(ids);
            writer.writeEqualsParameterAnd(jguidAlias, "query_id", joinQuery.getQueryId());
            writer.writeEquals(jguidAlias, "id", artAlias, "guid");
         } else {
            writer.writeEqualsParameter(artAlias, "guid", ids.iterator().next());
         }
      } else {
         writer.writeEqualsAnd(withAlias, artAlias, "art_id");
         writer.writeEquals(withAlias, txsAlias, "transaction_id");
      }
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ARTIFACT_GUID.ordinal();
   }
}