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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.DataPostProcessorFactory;
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
import org.eclipse.osee.orcs.db.internal.search.handlers.AllArtifactsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactGuidSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactHridsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactIdsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.ArtifactTypeSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeOtherSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeTokenSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.AttributeTypeExistsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.RelatedToSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.RelationTypeExistsSqlHandler;
import org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.internal.sql.HasDataPostProcessorFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link SqlHandlerFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class QuerySqlHandlerFactoryImplTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private IdentityService identityService;
   @Mock private TaggingEngine taggingEngine;
   @Mock private ExecutorAdmin executorAdmin;
   // @formatter:on

   private SqlHandlerFactory factory;
   private DataPostProcessorFactory<CriteriaAttributeKeywords> postProcessorFactory;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      QueryModuleFactory queryModule = new QueryModuleFactory(logger);

      postProcessorFactory = queryModule.createAttributeKeywordPostProcessor(executorAdmin, taggingEngine);
      factory =
         queryModule.createHandlerFactory(identityService, postProcessorFactory, taggingEngine.getTagProcessor());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testQueryModuleFactory() throws Exception {
      List<Criteria<?>> criteria = new ArrayList<Criteria<?>>();
      criteria.add(new CriteriaArtifactGuids(null));
      criteria.add(new CriteriaArtifactHrids(null));
      criteria.add(new CriteriaArtifactIds(null));
      criteria.add(new CriteriaArtifactType(null, null));
      criteria.add(new CriteriaRelationTypeExists(null));
      criteria.add(new CriteriaAttributeTypeExists(null));
      criteria.add(new CriteriaAttributeOther(null, null, null));
      criteria.add(new CriteriaAttributeKeywords(false, null, null, Collections.<String> emptyList(), null, null, null));
      criteria.add(new CriteriaRelatedTo(null, null));
      criteria.add(new CriteriaAllArtifacts());

      Collections.shuffle(criteria);

      CriteriaSet criteriaSet = createCriteria(CoreBranches.COMMON, criteria);
      List<SqlHandler<?, QueryOptions>> handlers = factory.createHandlers(criteriaSet);

      Assert.assertEquals(10, handlers.size());

      Iterator<SqlHandler<?, QueryOptions>> iterator = handlers.iterator();
      assertSqlHandler(iterator.next(), ArtifactIdsSqlHandler.class, SqlHandlerPriority.ARTIFACT_ID);
      assertSqlHandler(iterator.next(), ArtifactGuidSqlHandler.class, SqlHandlerPriority.ARTIFACT_GUID);
      assertSqlHandler(iterator.next(), ArtifactHridsSqlHandler.class, SqlHandlerPriority.ARTIFACT_HRID);
      assertSqlHandler(iterator.next(), AttributeOtherSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_VALUE);

      SqlHandler<?, QueryOptions> tokenHandler = iterator.next();
      assertSqlHandler(tokenHandler, AttributeTokenSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_TOKENIZED_VALUE);
      Assert.assertTrue(tokenHandler instanceof HasDataPostProcessorFactory);
      Assert.assertEquals(postProcessorFactory,
         ((HasDataPostProcessorFactory<CriteriaAttributeKeywords>) tokenHandler).getDataPostProcessorFactory());

      assertSqlHandler(iterator.next(), ArtifactTypeSqlHandler.class, SqlHandlerPriority.ARTIFACT_TYPE);
      assertSqlHandler(iterator.next(), AttributeTypeExistsSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_TYPE_EXISTS);
      assertSqlHandler(iterator.next(), RelationTypeExistsSqlHandler.class, SqlHandlerPriority.RELATION_TYPE_EXISTS);
      assertSqlHandler(iterator.next(), RelatedToSqlHandler.class, SqlHandlerPriority.RELATED_TO_ART_IDS);
      assertSqlHandler(iterator.next(), AllArtifactsSqlHandler.class, SqlHandlerPriority.ALL_ARTIFACTS);
   }

   @SuppressWarnings("rawtypes")
   private void assertSqlHandler(SqlHandler<?, ?> handler, Class<? extends SqlHandler> clazz, SqlHandlerPriority priority) {
      assertHandler(handler, clazz, priority, logger, identityService, taggingEngine, executorAdmin);
   }

   private static void assertHandler(SqlHandler<?, ?> actual, Class<?> type, SqlHandlerPriority priority, Log logger, IdentityService idService, TaggingEngine taggingEngine, ExecutorAdmin executorAdmin) {
      Assert.assertNotNull(actual);
      Assert.assertEquals(type, actual.getClass());
      Assert.assertEquals(logger, actual.getLogger());
      Assert.assertEquals(idService, actual.getIdentityService());
      Assert.assertEquals(priority.ordinal(), actual.getPriority());
   }

   @SuppressWarnings("rawtypes")
   private static CriteriaSet createCriteria(IOseeBranch branch, Collection<? extends Criteria> criteria) {
      CriteriaSet set = new CriteriaSet(branch);
      for (Criteria<?> crit : criteria) {
         set.add(crit);
      }
      return set;
   }
}
