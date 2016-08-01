/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Ryan D. Brooks
 */
public class ApplicabilityQueryImpl implements ApplicabilityQuery {
   private static final String SELECT_APPLIC_FOR_ART =
      "SELECT distinct e2, value FROM osee_artifact art, osee_txs txs1, osee_tuple2 app, osee_txs txs2, osee_key_value WHERE art_id = ? and art.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current = 1 and tuple_type = 2 AND e2 = txs1.app_id AND app.gamma_id = txs2.gamma_id AND txs2.branch_id = txs1.branch_id AND txs2.tx_current = 1 AND e2 = key";

   private static final String SELECT_APPLIC_FOR_ARTS =
      "SELECT distinct e2, value, art.art_id FROM osee_artifact art, osee_txs txs1, osee_tuple2 app, osee_txs txs2, osee_key_value, osee_join_id jid WHERE art_id = jid.id and jid.query_id =? and art.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current = 1 and tuple_type = 2 AND e2 = txs1.app_id AND app.gamma_id = txs2.gamma_id AND txs2.branch_id = txs1.branch_id AND txs2.tx_current = 1 AND e2 = key";

   private final TupleQuery tupleQuery;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory sqlJoinFactory;

   public ApplicabilityQueryImpl(JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, TupleQuery tupleQuery) {
      this.jdbcClient = jdbcClient;
      this.sqlJoinFactory = sqlJoinFactory;
      this.tupleQuery = tupleQuery;
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch) {
      ApplicabilityToken[] result = new ApplicabilityToken[] {ApplicabilityToken.BASE};
      jdbcClient.runQuery(stmt -> result[0] = new ApplicabilityToken(stmt.getLong("e2"), stmt.getString("value")),
         SELECT_APPLIC_FOR_ART, artId, branch);
      return result[0];
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityTokens(List<ArtifactId> artIds, BranchId branch) {
      IdJoinQuery idJoin = sqlJoinFactory.createIdJoinQuery();
      for (ArtifactId artId : artIds) {
         idJoin.add(artId.getId());
      }
      idJoin.store();

      Map<Long, ApplicabilityToken> artIdToApplic = new HashMap<>();
      jdbcClient.runQuery(
         stmt -> artIdToApplic.put(stmt.getLong("art_id"),
            new ApplicabilityToken(stmt.getLong("e2"), stmt.getString("value"))),
         SELECT_APPLIC_FOR_ARTS, idJoin.getQueryId(), branch);

      idJoin.delete();

      List<ApplicabilityToken> toReturn = new ArrayList<>();
      for (ArtifactId artId : artIds) {
         if (artIdToApplic.containsKey(artId.getId())) {
            toReturn.add(artIdToApplic.get(artId.getId()));
         } else {
            toReturn.add(ApplicabilityToken.BASE);
         }
      }
      return toReturn;
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch, consumer);
      return tokens;
   }

   @Override
   public HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch1, BranchId branch2) {
      HashMap<Long, ApplicabilityToken> tokens = new HashMap<>();
      BiConsumer<Long, String> consumer = (id, name) -> tokens.put(id, new ApplicabilityToken(id, name));
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch1, consumer);
      tupleQuery.getTuple2UniqueE2Pair(CoreTupleTypes.ViewApplicability, branch2, consumer);
      return tokens;
   }
}