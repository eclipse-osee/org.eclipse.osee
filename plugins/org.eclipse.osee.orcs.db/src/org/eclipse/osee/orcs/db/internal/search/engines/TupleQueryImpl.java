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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.Tuple4Type;
import org.eclipse.osee.framework.core.data.TupleTypeImpl;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.TriConsumer;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.KeyValueStore;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Angel Avila
 */
public class TupleQueryImpl implements TupleQuery {
   private static final String SELECT_E2_FROM_E1 =
      "select e2, value from osee_txs txs, osee_tuple2 tp2, osee_key_value where tuple_type = ? and e1 = ? and tp2.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key";

   private static final String SELECT_E2_BY_TUPLE_TYPE =
      "select distinct e2, value from osee_txs txs, osee_tuple2 tp2, osee_key_value where tuple_type = ? and tp2.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e2 = key";

   private static final String SELECT_E2_BY_TUPLE_TYPE_RAW =
      "select distinct e2 from osee_txs txs, osee_tuple2 tp2 where tuple_type = ? and tp2.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e1 = ?";

   private static final String SELECT_KEY_VALUE_FROM_BRANCH_VIEW =
      "SELECT distinct e2, value from osee_tuple2 tp2, osee_txs txs1, osee_key_value where tuple_type = ? AND e1 = ? and tp2.gamma_id = txs1.gamma_id and txs1.branch_id = ? AND txs1.tx_current = 1 and e2 = key";

   private static final String SELECT_TUPLE3_E1_VAL_BY_TYPE =
      "select distinct e1, value from osee_txs txs, osee_tuple3 tp3, osee_key_value where tuple_type = ? and tp3.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e1 = key";

   private static final String SELECT_TUPLE3_E3_VAL_BY_TYPE =
      "select distinct e3, value from osee_txs txs, osee_tuple3 tp3, osee_key_value where tuple_type = ? and tp3.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e3 = key";

   private static final String SELECT_TUPLE3_E3_VAL_FROM_E1 =
      "select e3, value from osee_txs txs, osee_tuple3 tp3, osee_key_value where tuple_type = ? and e1 = ? and tp3.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1 and e3 = key";

   private static final String SELECT_TUPLE3_GAMMA_FROM_E1 =
      "select tp3.gamma_id from osee_txs txs, osee_tuple3 tp3 where tuple_type = ? and e1 = ? and tp3.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1";

   private static final String SELECT_TUPLE3_E2_FROM_E3 =
      "select distinct e2 from osee_txs txs, osee_tuple3 tp3, osee_key_value where tuple_type = ? and e3 = ? and tp3.gamma_id = txs.gamma_id and branch_id = ? and tx_current = 1";

   private static final String SELECT_TUPLE3_COUNT_FROM_E3 =
      "select count(1) from osee_tuple3 where tuple_type = ? and e3 = ?";

   private static final String SELECT_TUPLE4_COUNT_FROM_E3 =
      "select count(1) from osee_tuple4 where tuple_type = ? and e3 = ?";

   private static final String SELECT_TUPLE2_GAMMA_ANY_BRANCH_FROM_E1_E2 =
      "SELECT gamma_id FROM osee_tuple2 WHERE tuple_type=? AND e1 = ? AND e2 = ?";

   private static final String SELECT_TUPLE2_BY_TUPLE_TYPE =
      "select distinct e1, e2 from osee_txs txs, osee_tuple2 tp2 where tuple_type = ? and txs.gamma_id = tp2.gamma_id and branch_id = ? and tx_current = 1";

   private static final String SELECT_TUPLE2_GAMMA_FROM_E1_E2 =
      "select tp2.gamma_id from osee_txs txs, osee_tuple2 tp2 where tuple_type = ? and e1 = ? and e2 = ? and tp2.gamma_id = txs.gamma_id and branch_id = ? and tx_current = " + TxCurrent.CURRENT;

   private static final String SELECT_TUPLE4_GAMMA_FROM_E1_E2 =
      "select tp4.gamma_id from osee_txs txs, osee_tuple4 tp4 where tuple_type = ? and e1 = ? and e2 = ? and tp4.gamma_id = txs.gamma_id and branch_id = ? and tx_current = " + TxCurrent.CURRENT;

   private static final String SELECT_TUPLE4_E3_E4_FROM_E1_E2 =
      "select e3, e4 from osee_txs txs, osee_tuple4 tp4 where tuple_type = ? and e1 = ? and e2 = ? and tp4.gamma_id = txs.gamma_id and branch_id = ? and tx_current = " + TxCurrent.CURRENT;

   private static final String SELECT_TUPLE4_E2_E3_E4_FROM_E1 =
      "select e2, e3, e4 from osee_txs txs, osee_tuple4 tp4 where tuple_type = ? and e1 = ? and tp4.gamma_id = txs.gamma_id and branch_id = ? and tx_current = " + TxCurrent.CURRENT;

   private final JdbcClient jdbcClient;
   private final KeyValueStore keyValue;

   TupleQueryImpl(JdbcClient jdbcClient, SqlJoinFactory sqlJoinFactory, KeyValueStore keyValue) {
      this.jdbcClient = jdbcClient;
      this.keyValue = keyValue;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <E1, E2> Iterable<E2> getTuple2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1) {
      Map<Long, String> consumer = new TreeMap<>();
      getTuple2NamedId(tupleType, branchId, e1, (e2, value) -> consumer.put(e2, value));
      return (Iterable<E2>) consumer.values();
   }

   @Override
   public <E1, E2> Iterable<Long> getTuple2Raw(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1) {
      List<Long> consumer = new ArrayList<>();
      runQuery("e2", consumer, SELECT_E2_BY_TUPLE_TYPE_RAW, tupleType, branchId, e1);
      return consumer;
   }

   @Override
   public <E1, E2> void getTuple2NamedId(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1, BiConsumer<Long, String> consumer) {
      runQuery(consumer, SELECT_E2_FROM_E1, "e2", tupleType, e1, branchId);
   }

   @Override
   public <E1, E2> void getTuple2KeyValuePair(Tuple2Type<E1, E2> tupleType, E1 e1, BranchId branch, BiConsumer<Long, String> consumer) {
      runQuery(consumer, SELECT_KEY_VALUE_FROM_BRANCH_VIEW, "e2", tupleType, e1, branch);
   }

   @Override
   public <E1, E2> void getTuple2UniqueE2Pair(Tuple2Type<E1, E2> tupleType, BranchId branchId, BiConsumer<Long, String> consumer) {
      runQuery(consumer, SELECT_E2_BY_TUPLE_TYPE, "e2", tupleType, branchId);
   }

   @Override
   public <E1, E2> boolean doesTuple2Exist(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2) {
      return jdbcClient.fetch(0, SELECT_TUPLE2_GAMMA_ANY_BRANCH_FROM_E1_E2, tupleType, toLong(e1), toLong(e2)) > 0;
   }

   //////  Tuple3 //////
   @Override
   public <E1, E2, E3> void getTuple3E1ValueFromType(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, BiConsumer<E1, String> consumer) {
      jdbcClient.runQuery(stmt -> consumer.accept(e1FromLong(tupleType, stmt), stmt.getString("value")),
         SELECT_TUPLE3_E1_VAL_BY_TYPE, tupleType, branchId);
   }

   @Override
   public <E1, E2, E3> void getTuple3E3ValueFromType(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, BiConsumer<E3, String> consumer) {
      jdbcClient.runQuery(stmt -> consumer.accept(e3FromLong(tupleType, stmt), stmt.getString("value")),
         SELECT_TUPLE3_E3_VAL_BY_TYPE, tupleType, branchId);
   }

   @Override
   public <E1, E2, E3> void getTuple3E3ValueFromE1(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, Long e1, BiConsumer<E3, String> consumer) {
      jdbcClient.runQuery(stmt -> consumer.accept(e3FromLong(tupleType, stmt), stmt.getString("value")),
         SELECT_TUPLE3_E3_VAL_FROM_E1, tupleType, e1, branchId);
   }

   @Override
   public <E1, E2, E3> void getTuple3GammaFromE1(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, Long e1, Consumer<GammaId> consumer) {
      runQuery(consumer, SELECT_TUPLE3_GAMMA_FROM_E1, tupleType, e1, branchId);
   }

   @Override
   public <E1, E2, E3> void getTuple3E2FromE3(Tuple3Type<E1, E2, E3> tupleType, BranchId branchId, E3 e3, Consumer<E2> consumer) {
      jdbcClient.runQuery(stmt -> consumer.accept(e2FromLong(tupleType, stmt)), SELECT_TUPLE3_E2_FROM_E3, tupleType, e3,
         branchId);
   }

   @Override
   public <E1, E2> void getTuple2E1E2FromType(Tuple2Type<E1, E2> tupleType, BranchId branchId, BiConsumer<Long, Long> consumer) {
      runQuery(consumer, SELECT_TUPLE2_BY_TUPLE_TYPE, "e1", "e2", tupleType, branchId);
   }

   @Override
   public <E1, E2, E3> boolean doesTuple3E3Exist(Tuple3Type<E1, E2, E3> tupleType, E3 e3) {
      return jdbcClient.fetch(0, SELECT_TUPLE3_COUNT_FROM_E3, tupleType, toLong(e3)) > 0;
   }

   @Override
   public <E1, E2, E3, E4> boolean doesTuple4E3Exist(Tuple4Type<E1, E2, E3, E4> tupleType, E3 e3) {
      return jdbcClient.fetch(0, SELECT_TUPLE4_COUNT_FROM_E3, tupleType, toLong(e3)) > 0;
   }

   @Override
   public <E1, E2> void getTuple2GammaFromE1E2(Tuple2Type<E1, E2> tupleType, BranchId branchId, E1 e1, E2 e2, Consumer<GammaId> consumer) {
      runQuery(consumer, SELECT_TUPLE2_GAMMA_FROM_E1_E2, tupleType, toLong(e1), toLong(e2), branchId);
   }

   @Override
   public <E1, E2> GammaId getTuple2GammaFromE1E2(Tuple2Type<E1, E2> tupleType, E1 e1, E2 e2) {
      return GammaId.valueOf(jdbcClient.fetch(0L, SELECT_TUPLE2_GAMMA_ANY_BRANCH_FROM_E1_E2, tupleType, toLong(e1), toLong(e2)));
   }

   @Override
   public <E1, E2, E3, E4> void getTuple4GammaFromE1E2(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1, E2 e2, Consumer<GammaId> consumer) {

      runQuery(consumer, SELECT_TUPLE4_GAMMA_FROM_E1_E2, tupleType, toLong(e1), toLong(e2), branchId);
   }

   @Override
   public <E1, E2, E3, E4> void getTuple4E3E4FromE1E2(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1, E2 e2, BiConsumer<E3, E4> consumer) {
      jdbcClient.runQuery(stmt -> consumer.accept(e3FromLong(tupleType, stmt), e4FromLong(tupleType, stmt)),
         SELECT_TUPLE4_E3_E4_FROM_E1_E2, tupleType, toLong(e1), toLong(e2), branchId);
   }

   @Override
   public <E1, E2, E3, E4> void getTuple4E2E3E4FromE1(Tuple4Type<E1, E2, E3, E4> tupleType, BranchId branchId, E1 e1, TriConsumer<E2, E3, E4> consumer) {
      jdbcClient.runQuery(
         stmt -> consumer.accept(e2FromLong(tupleType, stmt), e3FromLong(tupleType, stmt), e4FromLong(tupleType, stmt)),
         SELECT_TUPLE4_E2_E3_E4_FROM_E1, tupleType, toLong(e1), branchId);
   }

   private <E> E fromLong(Function<Long, E> valueOfE1, JdbcStatement stmt, String column) {
      Long rawValue = stmt.getLong(column);
      if (valueOfE1 == TupleTypeImpl.KeyedString) {
         return (E) keyValue.getByKey(rawValue);
      } else {
         return valueOfE1.apply(rawValue);
      }
   }

   private <E1, E2, E3> E1 e1FromLong(Tuple3Type<E1, E2, E3> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE1(), stmt, "e1");
   }

   private <E1, E2, E3> E2 e2FromLong(Tuple3Type<E1, E2, E3> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE2(), stmt, "e2");
   }

   private <E1, E2, E3> E3 e3FromLong(Tuple3Type<E1, E2, E3> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE3(), stmt, "e3");
   }

   private <E1, E2, E3, E4> E2 e2FromLong(Tuple4Type<E1, E2, E3, E4> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE2(), stmt, "e2");
   }

   private <E1, E2, E3, E4> E3 e3FromLong(Tuple4Type<E1, E2, E3, E4> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE3(), stmt, "e3");
   }

   private <E1, E2, E3, E4> E4 e4FromLong(Tuple4Type<E1, E2, E3, E4> tupleType, JdbcStatement stmt) {
      return fromLong(tupleType.getValueOfE4(), stmt, "e4");
   }

   private Long toLong(Object element) {
      if (element instanceof String) {
         return keyValue.getByValue((String) element);
      } else if (element instanceof Id) {
         return ((Id) element).getId();
      }
      return (Long) element;
   }

   private void runQuery(BiConsumer<Long, String> consumer, String query, String column, Object... data) {
      jdbcClient.runQuery(stmt -> consumer.accept(stmt.getLong(column), stmt.getString("value")), query, data);
   }

   private void runQuery(BiConsumer<Long, Long> consumer, String query, String column1, String column2, Object... data) {
      jdbcClient.runQuery(stmt -> consumer.accept(stmt.getLong(column1), stmt.getLong(column2)), query, data);
   }

   private void runQuery(String column, List<Long> consumer, String query, Object... data) {
      jdbcClient.runQuery(stmt -> consumer.add(stmt.getLong(column)), query, data);
   }

   private void runQuery(Consumer<GammaId> consumer, String query, Object... data) {
      jdbcClient.runQuery(stmt -> consumer.accept(GammaId.valueOf(stmt.getLong("gamma_id"))), query, data);
   }
}