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

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllArtifacts;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.SqlProvider;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link QueryEngineImpl}
 * 
 * @author Roberto E. Escobar
 */
public class QueryEngineImplTest {

   private static final Criteria<?> GUIDS = new CriteriaArtifactGuids(Arrays.asList(GUID.create(), GUID.create()));
   private static final Criteria<?> IDS = new CriteriaArtifactIds(Arrays.asList(1, 2, 3, 4, 5));
   private static final Criteria<?> HRIDS = new CriteriaArtifactHrids(Arrays.asList("ABCDE", "FGHIJ"));
   private static final Criteria<?> TYPES = new CriteriaArtifactType(null, Arrays.asList(CoreArtifactTypes.CodeUnit));
   private static final Criteria<?> ATTRIBUTE = new CriteriaAttributeOther(CoreAttributeTypes.Name,
      Arrays.asList("Hello"), Operator.EQUAL);

   private static final Criteria<?> ATTR_TYPE_EXITS = new CriteriaAttributeTypeExists(
      Arrays.asList(CoreAttributeTypes.Name));
   private static final Criteria<?> REL_TYPE_EXISTS = new CriteriaRelationTypeExists(
      CoreRelationTypes.Default_Hierarchical__Child);

   private static final Criteria<?> ATTRIBUTE_KEYWORD = new CriteriaAttributeKeywords(false, Arrays.asList(
      CoreAttributeTypes.Name, CoreAttributeTypes.WordTemplateContent), null, "hello1_two_three",
      TokenDelimiterMatch.ANY, TokenOrderType.MATCH_ORDER, MatchTokenCountType.IGNORE_TOKEN_COUNT, CaseType.MATCH_CASE);

   private static final Criteria<?> RELATED_TO = new CriteriaRelatedTo(CoreRelationTypes.Default_Hierarchical__Child,
      Arrays.asList(45, 61));

   private static final Criteria<?> ALL_ARTIFACTS = new CriteriaAllArtifacts();
   //   private static final Criteria TWO_TYPES = new CriteriaArtifactType(Arrays.asList(CoreArtifactTypes.CodeUnit,
   //      CoreArtifactTypes.Artifact));

   // @formatter:off
   @Mock private Log logger;
   @Mock private IOseeDatabaseService dbService;
   @Mock private SqlProvider sqlProvider;
   @Mock private IdentityService identityService;
   @Mock private ExecutorAdmin executorAdmin;
   @Mock private BranchCache branchCache;
   @Mock private AttributeTypeCache attributeTypeCache;
   @Mock private OrcsSession session;
   // @formatter:on

   private final static int EXPECTED_BRANCH_ID = 65;
   private final static int EXPECTED_TX_ID = 45678;

   private QueryEngineImpl queryEngine;
   private QueryData queryData;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      QueryModuleFactory queryModule = new QueryModuleFactory(logger, executorAdmin);

      TaggingEngine taggingEngine = queryModule.createTaggingEngine();

      DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessorFactory =
         queryModule.createAttributeKeywordPostProcessor(taggingEngine);
      SqlHandlerFactory handlerFactory =
         queryModule.createHandlerFactory(identityService, postProcessorFactory, taggingEngine.getTagProcessor());
      queryEngine = queryModule.createQueryEngine(dbService, handlerFactory, sqlProvider, branchCache);

      CriteriaSet criteriaSet = new CriteriaSet(CoreBranches.COMMON);
      QueryOptions options = new QueryOptions();
      queryData = new QueryData(criteriaSet, options);

      when(branchCache.getLocalId(CoreBranches.COMMON)).thenReturn(EXPECTED_BRANCH_ID);
      when(identityService.getLocalId(CoreArtifactTypes.CodeUnit)).thenReturn(
         CoreArtifactTypes.CodeUnit.getGuid().intValue());
      when(identityService.getLocalId(CoreAttributeTypes.Name)).thenReturn(CoreAttributeTypes.Name.getGuid().intValue());

      when(identityService.getLocalId(CoreRelationTypes.Default_Hierarchical__Child)).thenReturn(
         CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue());

      when(sqlProvider.getSql(OseeSql.QUERY_BUILDER)).thenReturn("/*+ ordered */");
   }

   @Test
   public void testCount() throws Exception {
      String expected =
         "SELECT/*+ ordered */ count(art1.art_id)\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_char_id jch2, osee_artifact art3, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?";

      queryData.addCriteria(GUIDS, IDS, HRIDS, TYPES);

      QuerySqlContext context = queryEngine.createCount(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testCountHistorical() throws Exception {
      String expected = "SELECT count(xTable.art_id) FROM (\n" + //
      " SELECT/*+ ordered */ max(txs1.transaction_id) as transaction_id, art1.art_id, txs1.branch_id\n" + //
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_type_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
      " AND \n" + // 
      "txs1.tx_current IN (1, 0) AND txs1.branch_id = ?\n" + //
      " GROUP BY art1.art_id, txs1.branch_id\n" + //
      ") xTable";

      queryData.getOptions().setFromTransaction(EXPECTED_TX_ID);
      queryData.addCriteria(TYPES);

      QuerySqlContext context = queryEngine.createCount(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQuery() throws Exception {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
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
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(GUIDS, IDS, HRIDS, TYPES);

      QuerySqlContext context = queryEngine.create(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testQuerySqlIncludeDeleted() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
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
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(GUIDS, IDS, HRIDS, TYPES);
      queryData.getOptions().setIncludeDeleted(true);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testQueryHistorical() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ max(txs1.transaction_id) as transaction_id, art1.art_id, txs1.branch_id\n" + // 
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
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(GUIDS, IDS, HRIDS, TYPES);
      queryData.getOptions().setFromTransaction(EXPECTED_TX_ID);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(12, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testQueryGuids() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryArtifactTypes() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.art_type_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(TYPES);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAttribute() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
      " FROM \n" + //
      "osee_attribute att1, osee_txs txs1, osee_artifact art1, osee_txs txs2\n" + //
      " WHERE \n" + //
      "att1.attr_type_id = ? AND att1.value = ?\n" + //
      " AND \n" + // 
      "art1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " AND \n" + //
      "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ATTRIBUTE);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(4, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAtttributeKeyword() throws OseeCoreException {
      String expected = "WITH gamma1 as ((SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
      " INTERSECT \n" + //
      "SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
      " INTERSECT \n" + //
      "SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?) ), \n" + //
      "att1 as (SELECT art_id FROM osee_attribute att, osee_txs txs, osee_join_id jid1, " + //
      "gamma1 WHERE att.gamma_id = gamma1.gamma_id AND att.gamma_id = txs.gamma_id AND " + //
      "txs.tx_current = 1 AND txs.branch_id = ? AND att.attr_type_id = jid1.id AND jid1.query_id = ?)\n" + //
      "SELECT art1.art_id, txs1.branch_id\n" + //
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1, att1\n" + //
      " WHERE \n" + //
      "art1.art_id = att1.art_id\n" + //
      " AND \n" + //
      "txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " AND \n" + //
      "txs1.gamma_id = art1.gamma_id\n" + //
      " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ATTRIBUTE_KEYWORD);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(1520625L, iterator.next()); // Coded Hello
      Assert.assertEquals(6106L, iterator.next()); // Coded two
      Assert.assertEquals(981274L, iterator.next()); // Coded three
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAtttributeCombined() throws OseeCoreException {
      String expected = "WITH gamma1 as ((SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
      " INTERSECT \n" + //
      "SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
      " INTERSECT \n" + //
      "SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?) ), \n" + //
      "att2 as (SELECT art_id FROM osee_attribute att, osee_txs txs, osee_join_id jid1, " + //
      "gamma1 WHERE att.gamma_id = gamma1.gamma_id AND att.gamma_id = txs.gamma_id AND " + //
      "txs.tx_current = 1 AND txs.branch_id = ? AND att.attr_type_id = jid1.id AND jid1.query_id = ?)\n" + //
      "SELECT art1.art_id, txs1.branch_id\n" + //
      " FROM \n" + //
      "osee_attribute att1, osee_txs txs1, osee_artifact art1, osee_txs txs2, att2\n" + //
      " WHERE \n" + //
      "att1.attr_type_id = ? AND att1.value = ?\n" + //
      " AND \n" + //
      "art1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " AND \n" + //
      "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
      " AND \n" + //
      "art1.art_id = att2.art_id\n" + //
      " AND \n" + //
      "txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " AND \n" + //
      "txs1.gamma_id = art1.gamma_id\n" + //
      " AND \n" + //
      "art1.art_type_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ATTRIBUTE, ATTRIBUTE_KEYWORD, TYPES);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(11, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(1520625L, iterator.next()); // Coded Hello
      Assert.assertEquals(6106L, iterator.next()); // Coded two
      Assert.assertEquals(981274L, iterator.next()); // Coded three
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), iterator.next());
   }

   @Test
   public void testQueryExistsNoBranch() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_join_char_id jch2, osee_artifact art3, osee_txs txs3, " + //
         "osee_attribute att1, osee_txs txs4, " + //
         "osee_relation_link rel1, osee_txs txs5\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id AND att1.art_id = art3.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs4.gamma_id AND txs4.tx_current = 1\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + // 
         "(rel1.a_art_id = art3.art_id OR rel1.b_art_id = art3.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs5.gamma_id AND txs5.tx_current = 1\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      when(branchCache.getLocalId(CoreBranches.COMMON)).thenReturn(0);

      queryData.addCriteria(GUIDS, TYPES, REL_TYPE_EXISTS, IDS, ATTR_TYPE_EXITS, HRIDS);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(8, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(1));
      Assert.assertEquals(joins.get(2).getQueryId(), parameters.get(2));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(3));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(5));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), parameters.get(6));
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue(), parameters.get(7));
   }

   @Test
   public void testQueryExistsWithBranch() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_join_char_id jch2, osee_artifact art3, osee_txs txs3, " + //
         "osee_attribute att1, osee_txs txs4, " + //
         "osee_relation_link rel1, osee_txs txs5\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art3.human_readable_id = jch2.id AND jch2.query_id = ? AND art3.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ? AND art3.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id AND att1.art_id = art3.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs4.gamma_id AND txs4.tx_current = 1 AND txs4.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + // 
         "(rel1.a_art_id = art3.art_id OR rel1.b_art_id = art3.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs5.gamma_id AND txs5.tx_current = 1 AND txs5.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(GUIDS, TYPES, REL_TYPE_EXISTS, IDS, ATTR_TYPE_EXITS, HRIDS);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(13, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(1));

      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(3));

      Assert.assertEquals(joins.get(2).getQueryId(), parameters.get(4));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(5));

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(6));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(7));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(8));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid().intValue(), parameters.get(9));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(10));

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue(), parameters.get(11));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(12));
   }

   @Test
   public void testRelatedTo() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_join_id jid2, osee_relation_link rel1, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ? AND rel1.b_art_id = jid2.id AND jid2.query_id = ?\n" + //
         " AND \n" + //
         "rel1.a_art_id = art1.art_id\n" + //
         " AND \n" + //
         "rel1.a_art_id = art2.art_id\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(GUIDS, TYPES, IDS, RELATED_TO);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(1));

      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(3));

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid().intValue(), parameters.get(5));

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid().intValue(), parameters.get(6));
      Assert.assertEquals(joins.get(2).getQueryId(), parameters.get(7));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(8));
   }

   @Test
   public void testCountAllArtifactsFromBranch() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ count(art1.art_id)\n" + // 
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?";

      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.createCount(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testCountAllArtifactsFromBranchHistorical() throws OseeCoreException {
      String expected = "SELECT count(xTable.art_id) FROM (\n" + //
      " SELECT/*+ ordered */ max(txs1.transaction_id) as transaction_id, art1.art_id, txs1.branch_id\n" + //
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
      " AND \n" + // 
      "txs1.tx_current IN (1, 0) AND txs1.branch_id = ?\n" + //
      " GROUP BY art1.art_id, txs1.branch_id\n" + //
      ") xTable";

      queryData.getOptions().setFromTransaction(EXPECTED_TX_ID);
      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.createCount(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAllArtifactsFromBranch() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
      " FROM \n" + //
      "osee_artifact art1, osee_txs txs1\n" + //
      " WHERE \n" + //
      "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
      " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.create(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(1, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAllArtifactsFromBranchHistorical() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ max(txs1.transaction_id) as transaction_id, art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
         " AND \n" + // 
         "txs1.tx_current IN (1, 0) AND txs1.branch_id = ?\n" + //
         " GROUP BY art1.art_id, txs1.branch_id\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.getOptions().setFromTransaction(EXPECTED_TX_ID);
      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.create(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testAllArtifactWithOtherCriteria() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + // 
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ALL_ARTIFACTS, IDS);

      QuerySqlContext context = queryEngine.create(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }
}
