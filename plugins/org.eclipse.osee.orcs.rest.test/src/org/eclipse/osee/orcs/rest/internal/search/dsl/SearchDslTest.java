/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.dsl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.dsl.SearchQueryBuilder;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John R. Misinco
 */
public class SearchDslTest {

   // @formatter:off
   @Mock private PredicateHandler handler;
   @Mock private QueryFactory queryFactory;
   @Mock private QueryBuilder builder;
   @Captor private ArgumentCaptor<IOseeBranch> fromBranch;
   // @formatter:on

   private static final IOseeBranch BRANCH = CoreBranches.COMMON;
   private SearchQueryBuilder dsl;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      Map<SearchMethod, PredicateHandler> handlers = new HashMap<SearchMethod, PredicateHandler>();
      handlers.put(SearchMethod.ATTRIBUTE_TYPE, handler);

      dsl = new SearchQueryBuilder(handlers);
   }

   @Test
   public void testBuildValidSearchType() throws OseeCoreException {
      when(queryFactory.fromBranch(any(IOseeBranch.class))).thenReturn(builder);

      Predicate predicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, Arrays.asList("1000000000000070"), Arrays.asList("AtsAdmin"));
      SearchRequest params = new SearchRequest(BRANCH.getUuid(), Arrays.asList(predicate), RequestType.IDS, 0, false);

      dsl.build(queryFactory, params);

      verify(queryFactory).fromBranch(fromBranch.capture());
      Assert.assertEquals(BRANCH.getUuid(), fromBranch.getValue().getUuid());
      verify(handler).handle(builder, predicate);
   }
}
