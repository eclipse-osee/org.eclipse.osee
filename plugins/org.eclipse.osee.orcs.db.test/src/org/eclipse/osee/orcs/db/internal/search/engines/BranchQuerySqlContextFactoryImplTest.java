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

import static org.eclipse.osee.framework.core.enums.BranchArchivedState.ARCHIVED;
import static org.eclipse.osee.framework.core.enums.BranchState.COMMITTED;
import static org.eclipse.osee.framework.core.enums.BranchState.CREATED;
import static org.eclipse.osee.framework.core.enums.BranchState.CREATION_IN_PROGRESS;
import static org.eclipse.osee.framework.core.enums.BranchType.SYSTEM_ROOT;
import static org.eclipse.osee.framework.core.enums.BranchType.WORKING;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.db.intergration.IntegrationUtil.integrationRule;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllBranches;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMergeBranchFor;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.search.Engines;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.sql.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.mock.OsgiService;
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
public class BranchQuerySqlContextFactoryImplTest {

   private static final Criteria UUIDS = uuid(1L, 2L, 3L, 4L, 5L);
   private static final Criteria TYPES = type(WORKING, SYSTEM_ROOT);

   private static final Criteria IS_ARCHIVED = archive(ARCHIVED);
   private static final Criteria NAME_PATTERN = namePattern("Hello.*");
   private static final Criteria STATE = state(CREATED, COMMITTED);

   private static final Criteria ALL_BRANCHES = new CriteriaAllBranches();

   // @formatter:off
   @Rule public TestRule integrationRule = integrationRule(this);
   @OsgiService public SqlJoinFactory joinFactory;
   @Mock private Log logger;
   @Mock private IdentityLocator identityService;
   @Mock private JdbcClient jdbcClient;
   @Mock private OrcsSession session;
   // @formatter:on

   private QuerySqlContextFactory queryEngine;
   private QueryData queryData;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      when(session.getGuid()).thenReturn(GUID.create());
      queryEngine = Engines.newBranchSqlContextFactory(logger, joinFactory, identityService, jdbcClient);
      when(jdbcClient.getDbType()).thenReturn(JdbcDbType.hsql);

      CriteriaSet criteriaSet = new CriteriaSet();
      Options options = OptionsUtil.createBranchOptions();
      queryData = new QueryData(criteriaSet, options);
   }

   @Test
   public void testCount() throws Exception {
      String expected = "SELECT count(br1.branch_id)\n" + //
         " FROM osee_join_id jid1, osee_branch br1, osee_join_id jid2\n" + //
         " WHERE br1.branch_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "br1.branch_type = jid2.id AND jid2.query_id = ?";

      queryData.addCriteria(UUIDS, TYPES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.COUNT);

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
   public void testQueryUuidIdsTypes() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_join_id jid1, osee_branch br1, osee_join_id jid2\n" + //
         " WHERE br1.branch_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "br1.branch_type = jid2.id AND jid2.query_id = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(UUIDS, TYPES);

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
   public void testQueryAllBranches() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(ALL_BRANCHES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(0, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());
   }

   @Test
   public void testQueryUuidIdsTypesSingles() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " WHERE br1.branch_id = ?\n" + //
         " AND \n" + //
         "br1.branch_type = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(uuid(CoreBranches.SYSTEM_ROOT.getId()), type(SYSTEM_ROOT));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(CoreBranches.SYSTEM_ROOT, iterator.next());
      assertEquals(SYSTEM_ROOT.getValue(), iterator.next());
   }

   @Test
   public void testQueryName() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " WHERE br1.branch_name = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(name("Hello"));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals("Hello", iterator.next());
   }

   @Test
   public void testQueryNamePattern() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " WHERE REGEXP_MATCHES (br1.branch_name, ?)\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(NAME_PATTERN);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals("Hello.*", iterator.next());
   }

   @Test
   public void testQueryStateArchive() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_join_id jid1, osee_branch br1\n" + //
         " WHERE br1.branch_state = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "br1.archived = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(STATE, IS_ARCHIVED);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(iterator.next(), joins.get(0).getQueryId());
      assertEquals(2, joins.get(0).size());
      assertEquals(ARCHIVED.getValue(), iterator.next());
   }

   @Test
   public void testQuerySingleStateArchive() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " WHERE br1.branch_state = ?\n" + //
         " AND \n" + //
         "br1.archived = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(state(CREATION_IN_PROGRESS), IS_ARCHIVED);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(CREATION_IN_PROGRESS.getValue(), iterator.next());
      assertEquals(ARCHIVED.getValue(), iterator.next());
   }

   @Test
   public void testQueryChildOf() {
      String expected = "WITH RECURSIVE chof1 (child_id, branch_level) AS ( \n" + //
         "  SELECT anch_br1.branch_id, 0 as branch_level FROM osee_branch anch_br1, osee_branch anch_br2\n" + //
         "   WHERE anch_br1.parent_branch_id = anch_br2.branch_id AND anch_br2.branch_id = ?\n" + //
         "  UNION ALL \n" + //
         "  SELECT branch_id, branch_level + 1 FROM chof1 recurse, osee_branch br WHERE recurse.child_id = br.parent_branch_id\n" + //
         " )\n" + //
         "SELECT br1.*\n" + //
         " FROM osee_branch br1, chof1\n" + //
         " WHERE br1.branch_id = chof1.child_id\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(childOf(COMMON));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(COMMON, iterator.next());
   }

   @Test
   public void testQueryAncestorOf() {
      String expected = "WITH RECURSIVE anstrof1 (parent_id, branch_level) AS ( \n" + //
         "  SELECT anch_br1.parent_branch_id, 0 as branch_level FROM osee_branch anch_br1\n" + //
         "   WHERE anch_br1.branch_id = ?\n" + //
         "  UNION ALL \n" + //
         "  SELECT parent_branch_id, branch_level - 1 FROM anstrof1 recurse, osee_branch br WHERE br.branch_id = recurse.parent_id\n" + //
         " )\n" + //
         "SELECT br1.*\n" + //
         " FROM osee_branch br1, anstrof1\n" + //
         " WHERE br1.branch_id = anstrof1.parent_id\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(ancestorOf(COMMON));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(COMMON, iterator.next());
   }

   @Test
   public void testMultiples() {
      String expected = "WITH RECURSIVE chof1 (child_id, branch_level) AS ( \n" + //
         "  SELECT anch_br1.branch_id, 0 as branch_level FROM osee_branch anch_br1, osee_branch anch_br2\n" + //
         "   WHERE anch_br1.parent_branch_id = anch_br2.branch_id AND anch_br2.branch_id = ?\n" + //
         "  UNION ALL \n" + //
         "  SELECT branch_id, branch_level + 1 FROM chof1 recurse, osee_branch br WHERE recurse.child_id = br.parent_branch_id\n" + //
         " )\n" + //
         "SELECT br1.*\n" + //
         " FROM osee_branch br1, osee_join_id jid1, chof1\n" + //
         " WHERE br1.branch_id = chof1.child_id\n" + //
         " AND \n" + //
         "br1.branch_type = ?\n" + //
         " AND \n" + //
         "br1.branch_state = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "br1.archived = ?\n" + //
         " AND \n" + //
         "REGEXP_MATCHES (br1.branch_name, ?)\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(STATE, IS_ARCHIVED, type(WORKING), childOf(COMMON), NAME_PATTERN);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(5, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(1, joins.size());
      assertEquals(2, joins.get(0).size());

      Iterator<Object> iterator = parameters.iterator();

      assertEquals(COMMON, iterator.next());
      assertEquals(WORKING.getValue(), iterator.next());
      assertEquals(joins.get(0).getQueryId(), iterator.next());
      assertEquals(ARCHIVED.getValue(), iterator.next());
      assertEquals("Hello.*", iterator.next());
   }

   @Test
   public void testMergeBranchFor() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_merge mbr1, osee_branch br1\n" + //
         " WHERE mbr1.source_branch_id = ?\n" + //
         " AND \n" + //
         "mbr1.dest_branch_id = ?\n" + //
         " AND \n" + //
         "mbr1.merge_branch_id = br1.branch_id\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(new CriteriaMergeBranchFor(CoreBranches.SYSTEM_ROOT, COMMON));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(CoreBranches.SYSTEM_ROOT, iterator.next());
      assertEquals(COMMON, iterator.next());
   }

   @Test
   public void testAssociatedArtId() throws Exception {
      String expected = "SELECT br1.*\n" + //
         " FROM osee_branch br1\n" + //
         " WHERE br1.associated_art_id = ?\n" + //
         " ORDER BY br1.branch_id";

      queryData.addCriteria(new CriteriaAssociatedArtId(DefaultHierarchyRoot));

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData, QueryType.SELECT);

      assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(DefaultHierarchyRoot, iterator.next());
   }

   private static Criteria ancestorOf(BranchId child) {
      return new CriteriaBranchAncestorOf(child);
   }

   private static Criteria childOf(BranchId parent) {
      return new CriteriaBranchChildOf(parent);
   }

   private static Criteria uuid(Long... values) {
      Collection<BranchId> branchIds = new ArrayList<>(values.length);
      for (Long id : values) {
         branchIds.add(BranchId.valueOf(id));
      }
      return new CriteriaBranchIds(branchIds);
   }

   private static Criteria type(BranchType... values) {
      return new CriteriaBranchType(Arrays.asList(values));
   }

   private static Criteria state(BranchState... values) {
      return new CriteriaBranchState(Arrays.asList(values));
   }

   private static Criteria namePattern(String value) {
      return new CriteriaBranchName(value, true);
   }

   private static Criteria name(String value) {
      return new CriteriaBranchName(value, false);
   }

   private static Criteria archive(BranchArchivedState... states) {
      return new CriteriaBranchArchived(Arrays.asList(states));
   }
}
