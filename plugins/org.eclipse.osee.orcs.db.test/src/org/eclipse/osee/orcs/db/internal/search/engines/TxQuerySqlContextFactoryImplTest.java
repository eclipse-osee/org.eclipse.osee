/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAuthorIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaCommitIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxComment;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxType;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.search.Engines;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.mock.OsgiService;
import org.eclipse.osee.orcs.search.Operator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link QuerySqlContextFactoryImpl}
 *
 * @author Roberto E. Escobar
 */
public class TxQuerySqlContextFactoryImplTest {

   private static final Criteria IDS =
      new CriteriaTxIds(Lists.transform(Arrays.asList(1L, 2L, 3L, 4l, 5L), TransactionId::valueOf));
   private static final Criteria COMMENT = comment("SimpleTemplateProviderTask", false);
   private static final Criteria TYPES =
      type(Arrays.asList(TransactionDetailsType.Baselined, TransactionDetailsType.NonBaselined));
   private static final Criteria BRANCHIDS = branchIds(1L, 2L, 3L, 4L, 5L);
   private static final Criteria IDS_WITH_OPERATOR = idWithOperator(Operator.LESS_THAN, 1);
   private static final Criteria DATE_WITH_OPERATOR =
      dateWithOperator(Operator.LESS_THAN, Timestamp.valueOf("2013-05-06 12:34:56"));
   private static final Criteria AUTHORS = byAuthorId(Arrays.asList(new Integer(1), new Integer(2)));
   private static final Criteria COMMITS = byCommitId(Arrays.asList(new Integer(1), new Integer(2)));

   // @formatter:off
   @Rule public TestRule integrationRule = integrationRule(this);
   @OsgiService public SqlJoinFactory joinFactory;
   @Mock private Log logger;
   @Mock private IdentityLocator identityService;
   @Mock private OrcsSession session;
   @Mock private IdJoinQuery idJoinQuery;
   // @formatter:on

   private QuerySqlContextFactory queryEngine;
   private QueryData queryData;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      queryEngine = Engines.newTxSqlContextFactory(logger, joinFactory, identityService, null);

      CriteriaSet criteriaSet = new CriteriaSet();
      Options options = OptionsUtil.createOptions();
      queryData = new QueryData(criteriaSet, options);
   }

   @Test
   public void testCount() throws Exception {
      String expected = "SELECT count(txd1.transaction_id)\n" + //
         " FROM osee_join_id jid1, osee_tx_details txd1\n" + //
         " WHERE txd1.transaction_id = jid1.id AND jid1.query_id = ?";
      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.COUNT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(5, joins.get(0).size());
   }

   @Test
   public void testQueryTxIds() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_join_id jid1, osee_tx_details txd1\n" + //
         " WHERE txd1.transaction_id = jid1.id AND jid1.query_id = ?\n" + //
         " ORDER BY txd1.transaction_id";

      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(5, joins.get(0).size());
   }

   @Test
   public void testComment() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_tx_details txd1\n" + //
         " WHERE txd1.osee_comment = ?\n" + //
         " ORDER BY txd1.transaction_id";

      queryData.addCriteria(COMMENT);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());

   }

   @Test
   public void testBranchType() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_join_id jid1, osee_tx_details txd1\n" + //
         " WHERE txd1.tx_type = jid1.id AND jid1.query_id = ?\n" + //
         " ORDER BY txd1.transaction_id";

      queryData.addCriteria(TYPES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(2, joins.get(0).size());

   }

   @Test
   public void testBranchTypeAndTxId() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_join_id jid1, osee_tx_details txd1, osee_join_id jid2\n" + //
         " WHERE txd1.transaction_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "txd1.tx_type = jid2.id AND jid2.query_id = ?\n" + //
         " ORDER BY txd1.transaction_id";

      queryData.addCriteria(TYPES, IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(2, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(5, joins.get(0).size());
      assertEquals(iterator.next(), joins.get(1).getQueryId());
      assertEquals(2, joins.get(1).size());

   }

   @Test
   public void testSixItemQuery() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_tx_details txd1, osee_join_id jid1, osee_join_id jid2, osee_join_id jid3\n" + //
         " WHERE txd1.transaction_id < ?\n" + //
         " AND \n" + //
         "txd1.author = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "txd1.commit_art_id = jid2.id AND jid2.query_id = ?\n" + //
         " AND \n" + //
         "txd1.time < ?\n" + //
         " AND \n" + //
         "txd1.branch_id = jid3.id AND jid3.query_id = ?\n" + //
         " ORDER BY txd1.transaction_id";

      queryData.addCriteria(BRANCHIDS, IDS_WITH_OPERATOR, DATE_WITH_OPERATOR, AUTHORS, COMMITS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(5, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), 1);
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(iterator.next(), joins.get(1).getQueryId());
      assertEquals(iterator.next(), Timestamp.valueOf("2013-05-06 12:34:56"));
      assertEquals(iterator.next(), joins.get(2).getQueryId());

      assertEquals(2, joins.get(0).size());
      assertEquals(2, joins.get(1).size());
      assertEquals(5, joins.get(2).size());

   }

   @Test
   public void testQueryTxPrior() throws Exception {
      String expected = "SELECT txd1.*\n" + //
         " FROM osee_tx_details txd1\n" + //
         " WHERE txd1.transaction_id = (SELECT max(td2.transaction_id) FROM osee_tx_details td1,osee_tx_details td2 WHERE td1.transaction_id = ? AND td1.branch_id = td2.branch_id AND td1.transaction_id > td2.transaction_id)\n" + //
         " ORDER BY txd1.transaction_id";

      TransactionId tx = TransactionId.valueOf(3);
      queryData.addCriteria(new CriteriaTxGetPrior(tx));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(tx, iterator.next());
   }

   private static Criteria comment(String value, boolean isPattern) {
      return new CriteriaTxComment(value, isPattern);
   }

   private static Criteria type(Collection<TransactionDetailsType> types) {
      return new CriteriaTxType(types);
   }

   private static Criteria branchIds(Long... ids) {
      List<BranchId> values = new ArrayList<>(ids.length);
      for (Long id : ids) {
         values.add(BranchId.valueOf(id));
      }
      return new CriteriaTxBranchIds(values);
   }

   private static Criteria idWithOperator(Operator op, int id) {
      return new CriteriaTxIdWithOperator(op, id);
   }

   private static Criteria dateWithOperator(Operator op, Timestamp t) {
      return new CriteriaDateWithOperator(op, t);
   }

   private static Criteria byAuthorId(Collection<Integer> ids) {
      return new CriteriaAuthorIds(ids);
   }

   private static Criteria byCommitId(Collection<Integer> ids) {
      return new CriteriaCommitIds(ids);
   }
}