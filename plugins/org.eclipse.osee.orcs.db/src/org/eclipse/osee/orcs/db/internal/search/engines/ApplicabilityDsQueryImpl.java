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
package org.eclipse.osee.orcs.db.internal.search.engines;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import static org.eclipse.osee.framework.core.enums.TxChange.NOT_CURRENT;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityDsQueryImpl implements ApplicabilityDsQuery {
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;

   private static final String SELECT_APPLIC_FOR_ART =
      "SELECT art_id, key, value FROM osee_artifact art, osee_txs txs1, osee_key_value WHERE art_id = ? and art.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current <> ? AND txs1.app_id = key";

   private static final String SELECT_APPLIC_FOR_ARTS =
      "SELECT art_id, key, value FROM osee_join_id jid, osee_artifact art, osee_txs txs1, osee_key_value WHERE jid.query_id = ? and jid.id = art_id and art.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current <> ? AND txs1.app_id = key";

   ApplicabilityDsQueryImpl(JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory) {
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      return jdbcClient.fetch(BASE, stmt -> new ApplicabilityToken(stmt.getLong("key"), stmt.getString("value")),
         SELECT_APPLIC_FOR_ART, artId, branch, NOT_CURRENT);
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(Collection<? extends ArtifactId> artIds, BranchId branch) {
      List<Pair<ArtifactId, ApplicabilityToken>> result = new ArrayList<>();
      try (IdJoinQuery idJoin = sqlJoinFactory.createIdJoinQuery()) {
         for (ArtifactId artId : artIds) {
            idJoin.add(artId);
         }
         idJoin.store();

         jdbcClient.runQuery(
            stmt -> result.add(new Pair<ArtifactId, ApplicabilityToken>(ArtifactId.valueOf(stmt.getLong("art_id")),
               ApplicabilityToken.create(stmt.getLong("key"), stmt.getString("value")))),
            SELECT_APPLIC_FOR_ARTS, idJoin.getQueryId(), branch, TxChange.NOT_CURRENT.getValue());
      }
      return result;
   }
}