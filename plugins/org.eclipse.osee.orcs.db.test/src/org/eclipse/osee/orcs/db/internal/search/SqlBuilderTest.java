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
package org.eclipse.osee.orcs.db.internal.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.db.internal.search.SqlBuilder.QueryType;
import org.eclipse.osee.orcs.db.internal.sql.StaticSqlProvider;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.MockSystemPreferences;
import org.eclipse.osee.orcs.db.mocks.SqlUtility;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.StringOperator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link SqlBuilder}
 * 
 * @author Roberto E. Escobar
 */
public class SqlBuilderTest {

   private static final Criteria GUIDS = new CriteriaArtifactGuids(Arrays.asList(GUID.create(), GUID.create()));
   private static final Criteria IDS = new CriteriaArtifactIds(Arrays.asList(1, 2, 3, 4, 5));
   private static final Criteria HRIDS = new CriteriaArtifactHrids(Arrays.asList("ABCDE", "FGHIJ"));
   private static final Criteria TYPES = new CriteriaArtifactType(Arrays.asList(CoreArtifactTypes.CodeUnit));
   private static final Criteria ATTRIBUTE = new CriteriaAttributeOther(CoreAttributeTypes.Name,
      Arrays.asList("Hello"), Operator.EQUAL);

   private static final Criteria ATTRIBUTE_KEYWORD = new CriteriaAttributeKeyword(Arrays.asList(
      CoreAttributeTypes.Name, CoreAttributeTypes.WordTemplateContent), "hello1_two_three",
      StringOperator.TOKENIZED_MATCH_ORDER, CaseType.IGNORE_CASE);

   //   private static final Criteria TWO_TYPES = new CriteriaArtifactType(Arrays.asList(CoreArtifactTypes.CodeUnit,
   //      CoreArtifactTypes.Artifact));

   private static StaticSqlProvider sqlProvider;

   @BeforeClass
   public static void setUp() {
      sqlProvider = new StaticSqlProvider();
      sqlProvider.setLogger(new MockLog());
      sqlProvider.setPreferences(new MockSystemPreferences());
   }

   @AfterClass
   public static void tearDown() {
      sqlProvider = null;
   }

   @Test(expected = OseeArgumentException.class)
   public void testEmptyHandlers() throws OseeCoreException {
      int branchId = 4;

      List<SqlHandler> handlers = Collections.emptyList();
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(null, branchId, handlers, QueryType.COUNT_ARTIFACTS);
   }

   @Test
   public void testCountSql() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, GUIDS, IDS, HRIDS, TYPES);

      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.COUNT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT count(art1.art_id)\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_char_id jch2, osee_artifact art3, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?" //
         , sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testHistoricalCountSql() throws OseeCoreException {
      int branchId = 4;
      int transactionId = 45678;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, TYPES);

      QueryOptions options = new QueryOptions();
      options.setFromTransaction(transactionId);

      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.COUNT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals("SELECT count(xTable.art_id) FROM (\n" + //
      " SELECT max(txs1.transaction_id), art1.art_id, txs1.branch_id\n" + //
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_type_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
      " AND \n" + // 
      "txs1.tx_current IN (1, 0) AND txs1.branch_id = ?\n" + //
      " GROUP BY art1.art_id, txs1.branch_id\n" + //
      ") xTable",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(transactionId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());
   }

   @Test
   public void testBuildSql() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, GUIDS, IDS, HRIDS, TYPES);

      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_char_id jch2, osee_artifact art3, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testBuildSqlIncludeDeleted() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, GUIDS, IDS, HRIDS, TYPES);

      QueryOptions options = new QueryOptions();
      options.setIncludeDeleted(true);

      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_char_id jch2, osee_artifact art3, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current IN (1, 2, 3) AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current IN (1, 2, 3) AND txs3.branch_id = ?\n" + //
         " AND \n" + // 
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testBuildSqlHistorical() throws OseeCoreException {
      int branchId = 4;
      int transactionId = 1000;

      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, GUIDS, IDS, HRIDS, TYPES);

      QueryOptions options = new QueryOptions();
      options.setFromTransaction(transactionId);

      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT max(txs1.transaction_id), art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_char_id jch2, osee_artifact art3, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs1.tx_current IN (1, 0) AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs2.tx_current IN (1, 0) AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs3.tx_current IN (1, 0) AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " GROUP BY art1.art_id, txs1.branch_id\n" + //
         " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(12, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(transactionId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());

      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(transactionId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());

      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(transactionId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testBuildGuidsSql() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, IDS);
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
   }

   @Test
   public void testBuildArtifactTypesSql() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, TYPES);
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals("SELECT art1.art_id, txs1.branch_id\n" + // 
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_type_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(branchId, iterator.next());
   }

   @Test
   public void testBuildAttributeSql() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, ATTRIBUTE);
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals("SELECT art1.art_id, txs1.branch_id\n" + // 
      " FROM \n" + //
      "osee_attribute att1, osee_txs txs1, osee_artifact art1, osee_txs txs2\n" + //
      " WHERE \n" + //
      "att1.attr_type_id = ? AND att1.value = ?\n" + //
      " AND \n" + // 
      "art1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " AND \n" + //
      "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(4, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());
   }

   @Test
   public void testBuildAtttributeKeyword() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, ATTRIBUTE_KEYWORD);
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_attribute att1, osee_txs txs1, osee_search_tags tag1, osee_search_tags tag2, osee_search_tags tag3, osee_artifact art1, osee_txs txs2\n" + //
         " WHERE \n" + //
         "att1.attr_type_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "tag1.coded_tag_id = ? AND tag2.coded_tag_id = ? AND tag3.coded_tag_id = ?\n" + //
         " AND \n" + //
         "tag1.gamma_id = tag2.gamma_id AND tag2.gamma_id = tag3.gamma_id\n" + //
         " AND \n" + //
         "art1.art_id = att1.art_id\n" + //
         " AND \n" + //
         "tag1.gamma_id = att1.gamma_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id",//
         sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(1520625L, iterator.next()); // Coded Hello
      Assert.assertEquals(6106L, iterator.next()); // Coded two
      Assert.assertEquals(981274L, iterator.next()); // Coded three
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());
   }

   @Test
   public void testBuildAtttributeCombined() throws OseeCoreException {
      int branchId = 4;
      List<SqlHandler> handlers = SqlUtility.createHandlers(CoreBranches.COMMON, ATTRIBUTE, ATTRIBUTE_KEYWORD, TYPES);
      QueryOptions options = new QueryOptions();
      SqlContext context = new SqlContext("mysession", options);
      SqlBuilder builder = new SqlBuilder(sqlProvider, null);
      builder.generateSql(context, branchId, handlers, QueryType.SELECT_ARTIFACTS);

      String sql = context.getSql();
      Assert.assertEquals(
         "SELECT art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_attribute att1, osee_txs txs1, osee_artifact art1, osee_txs txs2, osee_join_id jid1, osee_attribute att2, osee_txs txs3, osee_search_tags tag1, osee_search_tags tag2, osee_search_tags tag3\n" + //
         " WHERE \n" + //
         "att1.attr_type_id = ? AND att1.value = ?\n" + //
         " AND \n" + //
         "art1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "att2.attr_type_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "tag1.coded_tag_id = ? AND tag2.coded_tag_id = ? AND tag3.coded_tag_id = ?\n" + //
         " AND \n" + //
         "tag1.gamma_id = tag2.gamma_id AND tag2.gamma_id = tag3.gamma_id\n" + //
         " AND \n" + //
         "art1.art_id = att2.art_id\n" + //
         " AND \n" + //
         "tag1.gamma_id = att2.gamma_id AND att2.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id" //
         , sql);

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(10, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(branchId, iterator.next());

      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(1520625L, iterator.next()); // Coded Hello
      Assert.assertEquals(6106L, iterator.next()); // Coded two
      Assert.assertEquals(981274L, iterator.next()); // Coded three
      Assert.assertEquals(branchId, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }
}
