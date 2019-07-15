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
package org.eclipse.osee.orcs.db.internal.search.handlers;

import static org.eclipse.osee.orcs.db.internal.search.handlers.SqlHandlerFactoryUtil.createArtifactSqlHandlerFactory;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeRaw;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.search.tagger.HasTagProcessor;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

/**
 * Test Case for {@link SqlHandlerFactoryUtil}
 *
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryUtilTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private IdentityLocator identityService;
   @Mock private TagProcessor tagProcessor;
   // @formatter:on

   private SqlHandlerFactory factory;

   @Before
   public void setUp() {
      initMocks(this);

      factory = createArtifactSqlHandlerFactory(logger, identityService, tagProcessor);
   }

   @Test
   public void testQueryModuleFactory() throws Exception {
      QueryData queryData = QueryData.mock();
      queryData.addCriteria(new CriteriaArtifactGuids(null));
      queryData.addCriteria(new CriteriaArtifactIds(Collections.emptyList()));
      queryData.addCriteria(new CriteriaRelationTypeFollow(null));
      queryData.addCriteria(new CriteriaArtifactType(null, null, true));
      queryData.addCriteria(new CriteriaRelationTypeExists(null));
      queryData.addCriteria(new CriteriaAttributeTypeExists(null));
      queryData.addCriteria(new CriteriaAttributeTypeNotExists((Collection<AttributeTypeId>) null));
      queryData.addCriteria(new CriteriaAttributeRaw(null, null));
      queryData.addCriteria(
         new CriteriaAttributeKeywords(false, null, null, Collections.<String> emptyList(), null, null, null));
      queryData.addCriteria(new CriteriaRelatedTo(null, (Collection<? extends ArtifactId>) null));

      List<SqlHandler<?>> handlers = factory.createHandlers(queryData);

      Assert.assertEquals(10, handlers.size());

      Iterator<SqlHandler<?>> iterator = handlers.iterator();
      assertHandler(iterator.next(), ArtifactIdsSqlHandler.class, SqlHandlerPriority.ARTIFACT_ID);
      assertHandler(iterator.next(), ArtifactGuidSqlHandler.class, SqlHandlerPriority.ARTIFACT_GUID);
      assertHandler(iterator.next(), RelatedToSqlHandler.class, SqlHandlerPriority.RELATED_TO_ART_IDS);
      assertHandler(iterator.next(), AttributeRawSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_VALUE);

      assertHandler(iterator.next(), AttributeTokenSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_TOKENIZED_VALUE,
         tagProcessor);
      assertHandler(iterator.next(), AttributeTypeExistsSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_TYPE_EXISTS);
      assertHandler(iterator.next(), AttributeTypeNotExistsSqlHandler.class,
         SqlHandlerPriority.ATTRIBUTE_TYPE_NOT_EXISTS);
      assertHandler(iterator.next(), RelationTypeExistsSqlHandler.class, SqlHandlerPriority.RELATION_TYPE_EXISTS);
      assertHandler(iterator.next(), ArtifactTypeSqlHandler.class, SqlHandlerPriority.ARTIFACT_TYPE);
      assertHandler(iterator.next(), RelationTypeFollowSqlHandler.class, SqlHandlerPriority.FOLLOW_RELATION_TYPES);
   }

   private void assertHandler(SqlHandler<?> actual, Class<?> type, SqlHandlerPriority priority) {
      assertHandler(actual, type, priority, null);
   }

   private void assertHandler(SqlHandler<?> actual, Class<?> type, SqlHandlerPriority priority, TagProcessor actualProcessor) {
      Assert.assertNotNull(actual);
      Assert.assertEquals(type, actual.getClass());
      Assert.assertEquals(logger, actual.getLogger());
      Assert.assertEquals(identityService, actual.getIdentityService());
      Assert.assertEquals(priority.ordinal(), actual.getPriority());

      if (actualProcessor != null) {
         HasTagProcessor hasProcessor = (HasTagProcessor) actual;
         Assert.assertEquals(actualProcessor, hasProcessor.getTagProcessor());
      }
   }
}