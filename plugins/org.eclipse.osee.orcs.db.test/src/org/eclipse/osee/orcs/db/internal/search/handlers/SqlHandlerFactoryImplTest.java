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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.DataStoreTypeCache;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactHrids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeOther;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.db.internal.search.SearchAsserts;
import org.eclipse.osee.orcs.db.internal.search.SqlConstants.CriteriaPriority;
import org.eclipse.osee.orcs.db.internal.search.SqlHandler;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.db.mocks.MockDataStoreTypeCache;
import org.eclipse.osee.orcs.db.mocks.MockIdentityService;
import org.eclipse.osee.orcs.db.mocks.MockLog;
import org.eclipse.osee.orcs.db.mocks.SqlUtility;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link SqlHandlerFactoryImpl}
 * 
 * @author Roberto E. Escobar
 */
public class SqlHandlerFactoryImplTest {

   private Log logger;
   private IdentityService idService;
   private TaggingEngine taggingEngine;
   private DataStoreTypeCache caches;

   @Before
   public void setUp() {
      logger = new MockLog();
      idService = new MockIdentityService();
      taggingEngine = new TaggingEngine(null, null);
      caches = new MockDataStoreTypeCache();
   }

   @After
   public void tearDown() {
      logger = null;
      idService = null;
      taggingEngine = null;
      caches = null;
   }

   @Test
   public void testFactory() throws Exception {
      SqlHandlerFactoryImpl factory = new SqlHandlerFactoryImpl(logger, idService, taggingEngine, caches);

      List<Criteria> criteria = new ArrayList<Criteria>();
      criteria.add(new CriteriaArtifactGuids(null));
      criteria.add(new CriteriaArtifactHrids(null));
      criteria.add(new CriteriaArtifactIds(null));
      criteria.add(new CriteriaArtifactType(null));
      criteria.add(new CriteriaRelationTypeExists(null));
      criteria.add(new CriteriaAttributeTypeExists(null));
      criteria.add(new CriteriaAttributeOther(null, null, null));
      criteria.add(new CriteriaAttributeKeyword(null, null, null, null));

      Collections.shuffle(criteria);

      CriteriaSet criteriaSet = SqlUtility.createCriteria(CoreBranches.COMMON, criteria);
      List<SqlHandler> handlers = factory.createHandlers(criteriaSet);

      Assert.assertEquals(8, handlers.size());

      Iterator<SqlHandler> iterator = handlers.iterator();
      assertSqlHandler(iterator.next(), ArtifactIdsSqlHandler.class, CriteriaPriority.ARTIFACT_ID);
      assertSqlHandler(iterator.next(), ArtifactGuidSqlHandler.class, CriteriaPriority.ARTIFACT_GUID);
      assertSqlHandler(iterator.next(), ArtifactHridsSqlHandler.class, CriteriaPriority.ARTIFACT_HRID);
      assertSqlHandler(iterator.next(), AttributeOtherSqlHandler.class, CriteriaPriority.ATTRIBUTE_VALUE);
      assertSqlHandler(iterator.next(), AttributeTokenSqlHandler.class, CriteriaPriority.ATTRIBUTE_TOKENIZED_VALUE);
      assertSqlHandler(iterator.next(), ArtifactTypeSqlHandler.class, CriteriaPriority.ARTIFACT_TYPE);
      assertSqlHandler(iterator.next(), AttributeTypeExistsSqlHandler.class, CriteriaPriority.ATTRIBUTE_TYPE_EXISTS);
      assertSqlHandler(iterator.next(), RelationTypeExistsSqlHandler.class, CriteriaPriority.RELATION_TYPE_EXISTS);

   }

   private void assertSqlHandler(SqlHandler handler, Class<? extends SqlHandler> clazz, CriteriaPriority priority) {
      SearchAsserts.assertHandler(handler, clazz, priority, logger, idService, taggingEngine, caches);
   }
}
