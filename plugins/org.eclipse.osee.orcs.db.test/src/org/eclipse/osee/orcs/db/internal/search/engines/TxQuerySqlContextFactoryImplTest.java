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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
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
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateRange;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaGetHead;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxComment;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithTwoOperators;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxType;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.Engines;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;
import org.eclipse.osee.orcs.search.Operator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link QuerySqlContextFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class TxQuerySqlContextFactoryImplTest {

   private static final Criteria IDS = id(1, 2, 3, 4, 5);
   private static final Criteria COMMENT = comment("SimpleTemplateProviderTask", false);
   private static final Criteria TYPES = type(Arrays.asList(TransactionDetailsType.Baselined,
      TransactionDetailsType.NonBaselined));
   private static final Criteria BRANCHIDS = branchIds(1, 2, 3, 4, 5);
   private static final Criteria IDS_WITH_OPERATOR = idWithOperator(Operator.LESS_THAN, 1);
   private static final Criteria IDS_WITH_2_OPERATOR = idWithTwoOperator(Operator.GREATER_THAN, 1, Operator.LESS_THAN,
      10);
   private static final Criteria DATE_WITH_OPERATOR = dateWithOperator(Operator.LESS_THAN,
      Timestamp.valueOf("2013-05-06 12:34:56"));
   private static final Criteria DATE_RANGE = dateRange(Timestamp.valueOf("2013-01-02 12:34:56"),
      Timestamp.valueOf("2013-05-06 12:34:56"));
   private static final Criteria AUTHORS = byAuthorId(Arrays.asList(new Integer(1), new Integer(2)));
   private static final Criteria COMMITS = byCommitId(Arrays.asList(new Integer(1), new Integer(2)));
   private static final Criteria HEAD = getHead(1);

   // @formatter:off
   @Mock private Log logger;
   @Mock private IOseeDatabaseService dbService;
   @Mock private SqlProvider sqlProvider;
   @Mock private IdentityLocator identityService;
   
   @Mock private OrcsSession session;
   // @formatter:on

   private QuerySqlContextFactory queryEngine;
   private QueryData queryData;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      queryEngine = Engines.newTxSqlContextFactory(logger, dbService, identityService, sqlProvider);

      CriteriaSet criteriaSet = new CriteriaSet();
      Options options = OptionsUtil.createOptions();
      queryData = new QueryData(criteriaSet, options);

      when(sqlProvider.getSql(OseeSql.QUERY_BUILDER)).thenReturn("/*+ ordered */");
   }

   @Test
   public void testCount() throws Exception {
      String expected = "SELECT/*+ ordered */ count(txd1.transaction_id)\n" + // 
      " FROM \n" + //
      "osee_join_id jid1, osee_tx_details txd1\n" + //
      " WHERE \n" + //
      "txd1.transaction_id = jid1.id AND jid1.query_id = ?";
      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.createCountContext(session, queryData);

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
      String expected = "SELECT/*+ ordered */ txd1.*\n" + //
      " FROM \n" + //
      "osee_join_id jid1, osee_tx_details txd1\n" + //
      " WHERE \n" + //
      "txd1.transaction_id = jid1.id AND jid1.query_id = ?\n" + //
      " ORDER BY txd1.transaction_id";

      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

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
      String expected = "SELECT/*+ ordered */ txd1.*\n" + //
      " FROM \n" + //
      "osee_tx_details txd1\n" + //
      " WHERE \n" + //
      "txd1.osee_comment = ?\n" + //
      " ORDER BY txd1.transaction_id";

      queryData.addCriteria(COMMENT);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());

   }

   @Test
   public void testBranchType() throws Exception {
      String expected = "SELECT/*+ ordered */ txd1.*\n" + //
      " FROM \n" + //
      "osee_join_id jid1, osee_tx_details txd1\n" + //
      " WHERE \n" + //
      "txd1.tx_type = jid1.id AND jid1.query_id = ?\n" + //
      " ORDER BY txd1.transaction_id";

      queryData.addCriteria(TYPES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

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
      String expected = "SELECT/*+ ordered */ txd1.*\n" + //
      " FROM \n" + //
      "osee_join_id jid1, osee_tx_details txd1, osee_join_id jid2\n" + //
      " WHERE \n" + //
      "txd1.transaction_id = jid1.id AND jid1.query_id = ?\n" + //
      " AND \n" + //
      "txd1.tx_type = jid2.id AND jid2.query_id = ?\n" + //
      " ORDER BY txd1.transaction_id";

      queryData.addCriteria(TYPES, IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

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
      /*********************************************************************
       * The nature of this query is that the order of the various portions may show up in different positions and the
       * join table number may change. Verify they are all there by checking each snippet
       */

      String[] expected =
         {
            "SELECT/*+ ordered */ txd1.*\n",
            "osee_join_id jid1, osee_tx_details txd1, osee_join_id jid2, osee_join_id jid3",
            "txd1.commit_art_id = jid",
            ".query_id = ?",
            "txd1.author = jid",
            ".query_id = ?",
            "txd1.branch_id = jid",
            ".query_id = ?",
            "txd1.time < ?",
            "txd1.transaction_id < ?",
            " ORDER BY txd1.transaction_id"};

      queryData.addCriteria(BRANCHIDS, IDS_WITH_OPERATOR, DATE_WITH_OPERATOR, AUTHORS, COMMITS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

      String actual = context.getSql();
      for (int i = 0; i < expected.length; i++) {
         assertTrue(expected[i] + " not found", actual.indexOf(expected[i]) > -1);
      }

      List<Object> parameters = context.getParameters();
      assertEquals(5, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(2, joins.get(0).size());
      assertEquals(iterator.next(), joins.get(1).getQueryId());
      assertEquals(2, joins.get(1).size());
      assertEquals(2, joins.get(0).size());

   }

   private static Criteria id(Integer... values) {
      return new CriteriaTxIds(Arrays.asList(values));
   }

   private static Criteria comment(String value, boolean isPattern) {
      return new CriteriaTxComment(value, isPattern);
   }

   private static Criteria type(Collection<TransactionDetailsType> types) {
      return new CriteriaTxType(types);
   }

   private static Criteria branchIds(Integer... ids) {
      return new CriteriaTxBranchIds(Arrays.asList(ids));
   }

   private static Criteria idWithOperator(Operator op, int id) {
      return new CriteriaTxIdWithOperator(op, id);
   }

   private static Criteria idWithTwoOperator(Operator op1, int id1, Operator op2, int id2) {
      return new CriteriaTxIdWithTwoOperators(op1, id1, op2, id2);
   }

   private static Criteria dateWithOperator(Operator op, Timestamp t) {
      return new CriteriaDateWithOperator(op, t);
   }

   private static Criteria dateRange(Timestamp t1, Timestamp t2) {
      return new CriteriaDateRange(t1, t2);
   }

   private static Criteria byAuthorId(Collection<Integer> ids) {
      return new CriteriaAuthorIds(ids);
   }

   private static Criteria byCommitId(Collection<Integer> ids) {
      return new CriteriaCommitIds(ids);
   }

   private static Criteria getHead(int branchId) {
      return new CriteriaGetHead(branchId);
   }
}
