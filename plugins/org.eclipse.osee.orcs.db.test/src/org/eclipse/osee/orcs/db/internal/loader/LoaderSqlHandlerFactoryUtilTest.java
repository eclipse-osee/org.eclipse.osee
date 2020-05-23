/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.loader;

import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.orcs.core.ds.QueryData;
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
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link LoaderSqlHandlerFactoryUtil}
 *
 * @author Roberto E. Escobar
 */
public class LoaderSqlHandlerFactoryUtilTest {

   private SqlHandlerFactory factory;

   @Before
   public void setUp() {
      MockitoAnnotations.initMocks(this);
      factory = LoaderSqlHandlerFactoryUtil.createHandlerFactory();
   }

   @Test
   public void testQueryModuleFactory() throws Exception {
      QueryData queryData = QueryData.mock();
      queryData.addCriteria(new CriteriaArtifact());
      queryData.addCriteria(new CriteriaAttribute(null, null));
      queryData.addCriteria(new CriteriaRelation(null, null));

      List<SqlHandler<?>> handlers = factory.createHandlers(queryData);

      Assert.assertEquals(3, handlers.size());

      Iterator<SqlHandler<?>> iterator = handlers.iterator();
      assertSqlHandler(iterator.next(), ArtifactSqlHandler.class, SqlHandlerPriority.ARTIFACT_LOADER);
      assertSqlHandler(iterator.next(), AttributeSqlHandler.class, SqlHandlerPriority.ATTRIBUTE_LOADER);
      assertSqlHandler(iterator.next(), RelationSqlHandler.class, SqlHandlerPriority.RELATION_LOADER);
   }

   private void assertSqlHandler(SqlHandler<?> actual, Class<?> type, SqlHandlerPriority priority) {
      Assert.assertNotNull(actual);
      Assert.assertEquals(type, actual.getClass());
      Assert.assertEquals(priority.ordinal(), actual.getPriority());
   }
}