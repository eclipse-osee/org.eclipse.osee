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

import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Angel Avila
 */
public class TupleQueryImpl implements TupleQuery {

   private final JdbcClient jdbcClient;
   private static final String SELECT_E2_FROM_E1 =
      "select e2, value from osee_txs txs, osee_tuple2 app, osee_key_value where app.tuple_type = ? and e1 = ? and app.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key";

   private static final String SELECT_E2_BY_TUPLE_TYPE =
      "select distinct e2, value from osee_txs txs, osee_tuple2 app, osee_key_value where tuple_type = ? and app.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key";

   public TupleQueryImpl(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public <E1, E2> Iterable<E2> getTuple2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1) {
      return null;
   }

   @Override
   public <E1, E2> Iterable<Long> getTuple2Raw(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1) {
      return null;
   }

   @Override
   public <E1, E2> void getTuple2NamedId(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1, BiConsumer<Long, String> consumer) {
      runQuery(consumer, SELECT_E2_FROM_E1, tupleType, e1, branchId);
   }

   @Override
   public <E1, E2> void getTuple2UniqueE2Pair(Tuple2Type<E1, E2> tupleType, BranchId branchId, BiConsumer<Long, String> consumer) {
      runQuery(consumer, SELECT_E2_BY_TUPLE_TYPE, tupleType, branchId);
   }

   private void runQuery(BiConsumer<Long, String> consumer, String query, Object... data) {
      jdbcClient.runQuery(stmt -> consumer.accept(stmt.getLong("e2"), stmt.getString("value")), query, data);
   }
}