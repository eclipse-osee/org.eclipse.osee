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
package org.eclipse.osee.orcs.db.internal.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaArtifact;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaAttribute;
import org.eclipse.osee.orcs.db.internal.loader.criteria.CriteriaRelation;
import org.eclipse.osee.orcs.db.internal.loader.handlers.ArtifactSqlHandler;
import org.eclipse.osee.orcs.db.internal.loader.handlers.AttributeSqlHandler;
import org.eclipse.osee.orcs.db.internal.loader.handlers.LoaderSqlHandlerFactoryUtil;
import org.eclipse.osee.orcs.db.internal.loader.handlers.RelationSqlHandler;
import org.eclipse.osee.orcs.db.internal.loader.handlers.SqlHandlerPriority;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link LoaderSqlHandlerFactoryUtil}
 * 
 * @author Roberto E. Escobar
 */
public class LoaderSqlHandlerFactoryUtilTest {

   // @formatter:off
   @Mock private Log logger;
   @Mock private IdentityLocator identityService;
   // @formatter:on

   private SqlHandlerFactory factory;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);

      factory = LoaderSqlHandlerFactoryUtil.createHandlerFactory(logger, identityService);
   }

   @Test
   public void testQueryModuleFactory() throws Exception {
      List<Criteria> criteria = new ArrayList<Criteria>();
      criteria.add(new CriteriaArtifact());
      criteria.add(new CriteriaAttribute(null, null));
      criteria.add(new CriteriaRelation(null, null));

      Collections.shuffle(criteria);

      CriteriaSet criteriaSet = createCriteria(criteria);
      List<SqlHandler<?>> handlers = factory.createHandlers(criteriaSet);

      Assert.assertEquals(3, handlers.size());

      Iterator<SqlHandler<?>> iterator = handlers.iterator();
      assertSqlHandler(iterator.next(), ArtifactSqlHandler.class, SqlHandlerPriority.ARTIFACT_LOADER);
      assertSqlHandler(iterator.next(), AttributeSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_LOADER);
      assertSqlHandler(iterator.next(), RelationSqlHandler.class, SqlHandlerPriority.RELATION_LOADER);
   }

   private void assertSqlHandler(SqlHandler<?> handler, Class<?> clazz, SqlHandlerPriority priority) {
      assertHandler(handler, clazz, priority, logger, identityService);
   }

   private static void assertHandler(SqlHandler<?> actual, Class<?> type, SqlHandlerPriority priority, Log logger, IdentityLocator idService) {
      Assert.assertNotNull(actual);
      Assert.assertEquals(type, actual.getClass());
      Assert.assertEquals(logger, actual.getLogger());
      Assert.assertEquals(idService, actual.getIdentityService());
      Assert.assertEquals(priority.ordinal(), actual.getPriority());
   }

   private static CriteriaSet createCriteria(Collection<? extends Criteria> criteria) {
      CriteriaSet set = new CriteriaSet();
      for (Criteria crit : criteria) {
         set.add(crit);
      }
      return set;
   }
}
