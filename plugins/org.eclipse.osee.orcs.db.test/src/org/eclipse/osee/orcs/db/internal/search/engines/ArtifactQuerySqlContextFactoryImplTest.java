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
package org.eclipse.osee.orcs.db.internal.search.engines;

import static java.util.Arrays.asList;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON_ID;
import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createArtifactSqlHandlerFactory;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllArtifacts;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranch;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideNotExists;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContextFactory;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagCollector;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.CharJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test Case for {@link ArtifactQuerySqlContextFactoryImpl}
 *
 * @author Roberto E. Escobar
 */
public class ArtifactQuerySqlContextFactoryImplTest {

   private static final String QUICK_SEARCH_VALUE = "hello1_two_three";
   private static final String WORD_1 = "hello";
   private static final String WORD_2 = "two";
   private static final String WORD_3 = "three";
   private static final long CODED_WORD_1 = 1520625L;
   private static final long CODED_WORD_2 = 6106L;
   private static final long CODED_WORD_3 = 981274L;

   private static final Criteria GUIDS = new CriteriaArtifactGuids(asList(GUID.create(), GUID.create()));
   private static final Criteria IDS = new CriteriaArtifactIds(asList(1L, 2L, 3L, 4L, 5L));
   private static final Criteria TYPES = new CriteriaArtifactType(null, asList(CoreArtifactTypes.CodeUnit), false);
   private static final Criteria ATTRIBUTE =
      new CriteriaAttributeOther(Collections.singleton(CoreAttributeTypes.Name), asList("Hello"));
   private static final Criteria ATTR_TYPE_EXITS = new CriteriaAttributeTypeExists(asList(CoreAttributeTypes.Name));
   private static final Criteria REL_TYPE_EXISTS =
      new CriteriaRelationTypeExists(CoreRelationTypes.Default_Hierarchical__Child);

   private static final Criteria ATTRIBUTE_KEYWORD =
      new CriteriaAttributeKeywords(false, asList(CoreAttributeTypes.Name, CoreAttributeTypes.WordTemplateContent),
         null, QUICK_SEARCH_VALUE, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.TOKEN_MATCH_ORDER__MATCH,
         QueryOption.TOKEN_COUNT__IGNORE, QueryOption.CASE__MATCH);

   private static final Criteria RELATED_TO =
      new CriteriaRelatedTo(CoreRelationTypes.Default_Hierarchical__Child, asList(45, 61));

   private static final Criteria ALL_ARTIFACTS = new CriteriaAllArtifacts();

   private static final Criteria FOLLOW_RELATION_TYPE =
      new CriteriaRelationTypeFollow(CoreRelationTypes.Default_Hierarchical__Child);

   @Rule
   public ExpectedException thrown = ExpectedException.none();

   // @formatter:off
   @Mock private Log logger;
   @Mock private IdentityLocator identityService;
   @Mock private TagProcessor tagProcessor;

   @Mock private ExecutorAdmin executorAdmin;
   @Mock private OrcsSession session;
   @Mock private SqlJoinFactory joinFactory;
   // @formatter:on

   private final static long EXPECTED_BRANCH_ID = 570;
   private final static int EXPECTED_TX_ID = 45678;

   private QuerySqlContextFactory queryEngine;
   private QueryData queryData;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      String sessionId = GUID.create();
      when(session.getGuid()).thenReturn(sessionId);

      SqlHandlerFactory handlerFactory = createArtifactSqlHandlerFactory(logger, identityService, tagProcessor);
      queryEngine = new ArtifactQuerySqlContextFactoryImpl(logger, joinFactory, null, handlerFactory);

      CriteriaSet criteriaSet = new CriteriaSet();
      Options options = OptionsUtil.createOptions();
      criteriaSet.add(new CriteriaBranch(COMMON_ID));
      queryData = new QueryData(criteriaSet, options);

      doAnswer(new Answer<Void>() {

         @Override
         public Void answer(InvocationOnMock invocation) throws Throwable {
            Object[] args = invocation.getArguments();
            String value = (String) args[0];
            assertEquals(QUICK_SEARCH_VALUE, value);

            TagCollector collector = (TagCollector) args[1];
            collector.addTag(WORD_1, CODED_WORD_1);
            collector.addTag(WORD_2, CODED_WORD_2);
            collector.addTag(WORD_3, CODED_WORD_3);
            return null;
         }
      }).when(tagProcessor).collectFromString(eq(QUICK_SEARCH_VALUE), any(TagCollector.class));

      when(joinFactory.createIdJoinQuery()).thenAnswer(new Answer<IdJoinQuery>() {

         @Override
         public IdJoinQuery answer(InvocationOnMock invocation) throws Throwable {
            return new IdJoinQuery(null, -1L, 23);
         }

      });
      when(joinFactory.createCharJoinQuery()).thenAnswer(new Answer<CharJoinQuery>() {

         @Override
         public CharJoinQuery answer(InvocationOnMock invocation) throws Throwable {
            return new CharJoinQuery(null, -1L, 23);
         }

      });
   }

   @Test
   public void testCount() throws Exception {
      String expected = "SELECT/*+ ordered */ count(art2.art_id)\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?";

      queryData.addCriteria(GUIDS, IDS, TYPES);

      QuerySqlContext context = queryEngine.createCountContext(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
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
         "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
         " GROUP BY art1.art_id, txs1.branch_id\n" + //
         ") xTable";

      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);
      queryData.addCriteria(TYPES);

      QuerySqlContext context = queryEngine.createCountContext(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQuery() throws Exception {
      String expected = "SELECT/*+ ordered */ art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, IDS, TYPES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
   }

   @Test
   public void testQuerySqlIncludeDeleted() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current IN (1, 2, 3) AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current IN (1, 2, 3) AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, IDS, TYPES);
      OptionsUtil.setIncludeDeletedArtifacts(queryData.getOptions(), true);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
   }

   @Test
   public void testQueryHistorical() throws OseeCoreException {
      String expected = "WITH artUuid1 AS ( \n" + //
         "SELECT max(txs.transaction_id) as transaction_id, art.art_id as art_id\n" + //
         "    FROM osee_txs txs, osee_artifact art, osee_join_char_id id\n" + //
         "    WHERE txs.gamma_id = art.gamma_id\n" + //
         "    AND art.guid = id.id AND id.query_id = ? AND txs.transaction_id <= ? AND txs.branch_id = ?\n" + //
         "    GROUP BY art.art_id\n" + //
         " )\n" + //
         "SELECT max(txs2.transaction_id) as transaction_id, art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, artUuid1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ?\n" + //
         " AND \n" + //
         "artUuid1.transaction_id = txs2.transaction_id AND artUuid1.art_id = art2.art_id AND art2.gamma_id = txs2.gamma_id AND txs2.transaction_id <= ?\n" + //
         " AND \n" + //"
         "txs2.mod_type <> 3 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " GROUP BY art2.art_id, txs2.branch_id\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, IDS, TYPES);
      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(11, parameters.size());

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

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
   }

   @Test
   public void testQueryHistoricalMultipleItems() throws OseeCoreException {
      String expected = "WITH artUuid1 AS ( \n" + //
         "SELECT max(txs.transaction_id) as transaction_id, art.art_id as art_id\n" + //
         "    FROM osee_txs txs, osee_artifact art, osee_join_char_id id\n" + //
         "    WHERE txs.gamma_id = art.gamma_id\n" + //
         "    AND art.guid = id.id AND id.query_id = ? AND txs.transaction_id <= ? AND txs.branch_id = ?\n" + //
         "    GROUP BY art.art_id\n" + //
         " ), \n" + //
         " attrExt1 AS ( \n" + //
         "SELECT max(txs.transaction_id) as transaction_id, attr.art_id as art_id\n" + //
         "    FROM osee_txs txs, osee_attribute attr\n" + //
         "    WHERE txs.gamma_id = attr.gamma_id\n" + //
         "    AND att.attr_type_id = ? AND txs.transaction_id <= ? AND txs.branch_id = ?\n" + //
         "    GROUP BY attr.art_id\n" + //
         " )\n" + //
         "SELECT max(txs2.transaction_id) as transaction_id, art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_attribute att1, osee_txs txs3, osee_relation_link rel1, osee_txs txs4, artUuid1, attrExt1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ?\n" + //
         " AND \n" + //
         "artUuid1.transaction_id = txs2.transaction_id AND artUuid1.art_id = art2.art_id AND art2.gamma_id = txs2.gamma_id AND txs2.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs2.mod_type <> 3 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id\n" + //
         " AND \n" + //
         "attrExt1.transaction_id = txs3.transaction_id AND attrExt1.art_id = att1.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs3.gamma_id AND txs3.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs3.mod_type <> 3 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs4.gamma_id\n" + //
         " AND \n" + //
         "txs4.transaction_id <= ?\n" + //
         " AND \n" + //
         "txs4.mod_type <> 3 AND txs4.branch_id = ?\n" + //
         " GROUP BY art2.art_id, txs2.branch_id\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, IDS, TYPES, ATTR_TYPE_EXITS, REL_TYPE_EXISTS);
      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(20, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(joins.get(1).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(joins.get(2).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), iterator.next());

      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), iterator.next());
      Assert.assertEquals(EXPECTED_TX_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryGuids() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
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

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(2, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAttribute() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_attribute att1, osee_txs txs1, osee_artifact art1, osee_txs txs2\n" + //
         " WHERE \n" + //
         "att1.attr_type_id = ? AND att1.value = ?\n" + //
         " AND \n" + //
         "art1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs2.branch_id";

      queryData.addCriteria(ATTRIBUTE);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(4, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAtttributeKeyword() throws OseeCoreException {
      String expected = "WITH gamma1 AS ( \n" + //
         "  ( \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         "  ) \n" + //
         " ), \n" + //
         " att1 AS ( \n" + //
         "   SELECT art_id FROM osee_attribute att, osee_txs txs, osee_join_id jid1, gamma1\n" + //
         " WHERE \n" + //
         "   att.gamma_id = gamma1.gamma_id AND att.attr_type_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "   att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = ?\n" + //
         " )\n" + //
         "SELECT art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1, att1\n" + //
         " WHERE \n" + //
         "art1.art_id = att1.art_id\n" + //
         " AND \n" + //
         "txs1.gamma_id = art1.gamma_id\n" + //
         " AND \n" + //
         "txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ATTRIBUTE_KEYWORD);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(6, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CODED_WORD_1, iterator.next()); // Coded Hello
      Assert.assertEquals(CODED_WORD_2, iterator.next()); // Coded two
      Assert.assertEquals(CODED_WORD_3, iterator.next()); // Coded three
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testQueryAtttributeCombined() throws OseeCoreException {
      String expected = "WITH gamma1 AS ( \n" + //
         "  ( \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         "  ) \n" + //
         " ), \n" + //
         " att2 AS ( \n" + //
         "   SELECT art_id FROM osee_attribute att, osee_txs txs, osee_join_id jid1, gamma1\n" + //
         " WHERE \n" + //
         "   att.gamma_id = gamma1.gamma_id AND att.attr_type_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "   att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = ?\n" + //
         " )\n" + //
         "SELECT art1.art_id, txs2.branch_id\n" + //
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
         "art1.art_type_id = ?\n" + //
         " ORDER BY art1.art_id, txs2.branch_id";

      queryData.addCriteria(ATTRIBUTE, ATTRIBUTE_KEYWORD, TYPES);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(10, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(1, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      Assert.assertEquals(CODED_WORD_1, iterator.next()); // Coded Hello
      Assert.assertEquals(CODED_WORD_2, iterator.next()); // Coded two
      Assert.assertEquals(CODED_WORD_3, iterator.next()); // Coded three
      Assert.assertEquals(joins.get(0).getQueryId(), iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), iterator.next());
      Assert.assertEquals("Hello", iterator.next());
      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());

      Assert.assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), iterator.next());
   }

   @Test
   public void testQueryExistsNoBranch() throws OseeCoreException {

      String expected = "SELECT/*+ ordered */ art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_attribute att1, osee_txs txs3, " + //
         "osee_relation_link rel1, osee_txs txs4\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs4.gamma_id\n" + //
         " AND \n" + //
         "txs4.tx_current = 1 AND txs4.branch_id = ?\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, TYPES, REL_TYPE_EXISTS, IDS, ATTR_TYPE_EXITS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(10, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(3));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(5));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), parameters.get(6));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(7));
      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), parameters.get(8));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(9));
   }

   @Test
   public void testQueryExistsWithBranch() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, " + //
         "osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, " + //
         "osee_attribute att1, osee_txs txs3, " + //
         "osee_relation_link rel1, osee_txs txs4\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art2.guid = jch1.id AND jch1.query_id = ? AND art2.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_type_id = ? AND art2.art_type_id = ?\n" + //
         " AND \n" + //
         "att1.attr_type_id = ?\n" + //
         " AND \n" + //
         "att1.art_id = art1.art_id AND att1.art_id = art2.art_id\n" + //
         " AND \n" + //
         "att1.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art1.art_id OR rel1.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "(rel1.a_art_id = art2.art_id OR rel1.b_art_id = art2.art_id)\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs4.gamma_id\n" + //
         " AND \n" + //
         "txs4.tx_current = 1 AND txs4.branch_id = ?\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, TYPES, REL_TYPE_EXISTS, IDS, ATTR_TYPE_EXITS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(10, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(1));

      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(3));

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(5));

      Assert.assertEquals(CoreAttributeTypes.Name.getGuid(), parameters.get(6));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(7));

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), parameters.get(8));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(9));
   }

   @Test
   public void testRelatedTo() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art2.art_id, txs2.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1, osee_join_char_id jch1, osee_artifact art2, osee_txs txs2, osee_join_id jid2, osee_relation_link rel1, osee_txs txs3\n" + //
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
         "rel1.gamma_id = txs3.gamma_id\n" + //
         " AND \n" + //
         "txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " ORDER BY art2.art_id, txs2.branch_id";

      queryData.addCriteria(GUIDS, TYPES, IDS, RELATED_TO);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(9, parameters.size());
      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(3, joins.size());

      Assert.assertEquals(joins.get(0).getQueryId(), parameters.get(0));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(1));

      Assert.assertEquals(joins.get(1).getQueryId(), parameters.get(2));
      Assert.assertEquals(EXPECTED_BRANCH_ID, parameters.get(3));

      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(4));
      Assert.assertEquals(CoreArtifactTypes.CodeUnit.getGuid(), parameters.get(5));

      Assert.assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), parameters.get(6));
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

      QuerySqlContext context = queryEngine.createCountContext(session, queryData);

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
         "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
         " GROUP BY art1.art_id, txs1.branch_id\n" + //
         ") xTable";

      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);
      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.createCountContext(session, queryData);

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

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

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
            "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
            " GROUP BY art1.art_id, txs1.branch_id\n" + //
            " ORDER BY art1.art_id, txs1.branch_id";

      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);
      queryData.addCriteria(ALL_ARTIFACTS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);

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
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_join_id jid1, osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.art_id = jid1.id AND jid1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      queryData.addCriteria(ALL_ARTIFACTS, IDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
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
   public void testRelationTypeNotExists() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "NOT EXISTS (SELECT 1 FROM osee_relation_link rel, osee_txs txs WHERE rel.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "(rel.a_art_id = art1.art_id OR rel.b_art_id = art1.art_id)\n" + //
         " AND \n" + //
         "rel.gamma_id = txs.gamma_id\n" + //
         " AND \n" + //
         "txs.tx_current = 1 AND txs.branch_id = ?)\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      Criteria relTypeNotExists = new CriteriaRelationTypeNotExists(CoreRelationTypes.Default_Hierarchical__Child);
      queryData.addCriteria(relTypeNotExists);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());
   }

   @Test
   public void testRelationTypeSideNotExists() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1\n" + //
         " WHERE \n" + //
         "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "NOT EXISTS (SELECT 1 FROM osee_relation_link rel, osee_txs txs WHERE rel.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "rel.b_art_id = art1.art_id\n" + //
         " AND \n" + //
         "rel.gamma_id = txs.gamma_id\n" + //
         " AND \n" + //
         "txs.tx_current = 1 AND txs.branch_id = ?)\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      Criteria relTypeSideNotExists =
         new CriteriaRelationTypeSideNotExists(CoreRelationTypes.Default_Hierarchical__Child);
      queryData.addCriteria(relTypeSideNotExists);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());
   }

   @Test
   public void testRelationTypeSideExists() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art1.art_id, txs1.branch_id\n" + //
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1, osee_relation_link rel1, osee_txs txs2\n" + //
         " WHERE \n" + //
         "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ?\n" + //
         " AND \n" + //
         "rel1.b_art_id = art1.art_id\n" + //
         " AND \n" + //
         "rel1.gamma_id = txs2.gamma_id\n" + //
         " AND \n" + //
         "txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " ORDER BY art1.art_id, txs1.branch_id";

      Criteria relTypeSideNotExists = new CriteriaRelationTypeSideExists(CoreRelationTypes.Default_Hierarchical__Child);
      queryData.addCriteria(relTypeSideNotExists);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(3, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());
   }

   @Test
   public void testRelationTypeFollow() throws OseeCoreException {
      String expected = "SELECT/*+ ordered */ art2.art_id, txs3.branch_id\n" + //
         " FROM \n" + //
         "osee_artifact art1, osee_txs txs1, osee_relation_link rel1, osee_txs txs2, osee_artifact art2, osee_txs txs3\n" + //
         " WHERE \n" + //
         "art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ? AND rel1.a_art_id = art1.art_id AND rel1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.b_art_id = art2.art_id AND art2.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " ORDER BY art2.art_id, txs3.branch_id";

      queryData.addCriteria(FOLLOW_RELATION_TYPE);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(4, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testRelationTypeFollowCombined() throws OseeCoreException {
      String expected = "WITH gamma1 AS ( \n" + //
         "  ( \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         " INTERSECT \n" + //
         "    SELECT gamma_id FROM osee_search_tags WHERE coded_tag_id = ?\n" + //
         "  ) \n" + //
         " ), \n" + //
         " att1 AS ( \n" + //
         "   SELECT art_id FROM osee_attribute att, osee_txs txs, osee_join_id jid1, gamma1\n" + //
         " WHERE \n" + //
         "   att.gamma_id = gamma1.gamma_id AND att.attr_type_id = jid1.id AND jid1.query_id = ?\n" + //
         " AND \n" + //
         "   att.gamma_id = txs.gamma_id AND txs.tx_current = 1 AND txs.branch_id = ?\n" + //
         " )\n" + //
         "SELECT art2.art_id, txs3.branch_id\n" + //
         " FROM \n" + //
         "osee_join_char_id jch1, osee_artifact art1, osee_txs txs1, osee_relation_link rel1, osee_txs txs2, osee_artifact art2, osee_txs txs3, att1\n" + //
         " WHERE \n" + //
         "art1.guid = jch1.id AND jch1.query_id = ? AND art1.gamma_id = txs1.gamma_id AND txs1.tx_current = 1 AND txs1.branch_id = ?\n" + //
         " AND \n" + //
         "art1.art_id = att1.art_id\n" + //
         " AND \n" + //
         "rel1.rel_link_type_id = ? AND rel1.a_art_id = art1.art_id AND rel1.gamma_id = txs2.gamma_id AND txs2.tx_current = 1 AND txs2.branch_id = ?\n" + //
         " AND \n" + //
         "rel1.b_art_id = art2.art_id AND art2.gamma_id = txs3.gamma_id AND txs3.tx_current = 1 AND txs3.branch_id = ?\n" + //
         " ORDER BY art2.art_id, txs3.branch_id";

      queryData.addCriteria(ATTRIBUTE_KEYWORD, FOLLOW_RELATION_TYPE, GUIDS);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(10, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(2, joins.size());

      Iterator<Object> iterator = parameters.iterator();

      assertEquals(CODED_WORD_1, iterator.next()); // Coded Hello
      assertEquals(CODED_WORD_2, iterator.next()); // Coded two
      assertEquals(CODED_WORD_3, iterator.next()); // Coded three
      assertEquals(joins.get(0).getQueryId(), iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(joins.get(1).getQueryId(), iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }

   @Test
   public void testRelationTypeFollowHistorical() throws OseeCoreException {
      String expected =
         "SELECT/*+ ordered */ max(txs3.transaction_id) as transaction_id, art2.art_id, txs3.branch_id\n" + //
            " FROM \n" + //
            "osee_artifact art1, osee_txs txs1, osee_relation_link rel1, osee_txs txs2, osee_artifact art2, osee_txs txs3\n" + //
            " WHERE \n" + //
            "art1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= ?\n" + //
            " AND \n" + //
            "txs1.mod_type <> 3 AND txs1.branch_id = ?\n" + //
            " AND \n" + //
            "rel1.rel_link_type_id = ? AND rel1.a_art_id = art1.art_id AND rel1.gamma_id = txs2.gamma_id AND txs2.transaction_id <= ?\n" + //
            " AND \n" + //
            "txs2.mod_type <> 3 AND txs2.branch_id = ?\n" + //
            " AND \n" + //
            "rel1.b_art_id = art2.art_id AND art2.gamma_id = txs3.gamma_id AND txs3.transaction_id <= ?\n" + //
            " AND \n" + //
            "txs3.mod_type <> 3 AND txs3.branch_id = ?\n" + //
            " GROUP BY art2.art_id, txs3.branch_id\n" + //
            " ORDER BY art2.art_id, txs3.branch_id";

      OptionsUtil.setFromTransaction(queryData.getOptions(), EXPECTED_TX_ID);
      queryData.addCriteria(FOLLOW_RELATION_TYPE);

      QuerySqlContext context = queryEngine.createQueryContext(session, queryData);
      Assert.assertEquals(expected, context.getSql());

      List<Object> parameters = context.getParameters();
      Assert.assertEquals(7, parameters.size());

      List<AbstractJoinQuery> joins = context.getJoins();
      Assert.assertEquals(0, joins.size());

      Iterator<Object> iterator = parameters.iterator();
      assertEquals(EXPECTED_TX_ID, iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(CoreRelationTypes.Default_Hierarchical__Child.getGuid(), iterator.next());
      assertEquals(EXPECTED_TX_ID, iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
      assertEquals(EXPECTED_TX_ID, iterator.next());
      assertEquals(EXPECTED_BRANCH_ID, iterator.next());
   }
}
