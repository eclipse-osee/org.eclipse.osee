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
package org.eclipse.osee.orcs.rest.internal.search.predicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John R. Misinco
 */
public class IdsPredicateHandlerTest {

   private class TestIdsPredicateHandler extends IdsPredicateHandler {

      Collection<String> guids;
      Collection<Integer> rawIds;

      @Override
      protected QueryBuilder addGuids(QueryBuilder builder, Collection<String> guids) {
         this.guids = guids;
         return builder;
      }

      @Override
      protected QueryBuilder addIds(QueryBuilder builder, Collection<Integer> rawIds) {
         this.rawIds = rawIds;
         return builder;
      }

   }

   // @formatter:off
   @Mock private QueryBuilder builder;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHandle() throws OseeCoreException {
      TestIdsPredicateHandler handler = new TestIdsPredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      //all digits get treated as artId
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, values);
      handler.handle(builder, testPredicate);

      Assert.assertEquals(1, handler.rawIds.size());
      Assert.assertNull(handler.guids);
      Assert.assertEquals(id1, handler.rawIds.iterator().next().toString());

      //if not all digits, treated as guid
      handler = new TestIdsPredicateHandler();
      String id2 = "AGUID234";
      values = Collections.singletonList(id2);
      testPredicate = new Predicate(SearchMethod.IDS, null, null, null, values);
      handler.handle(builder, testPredicate);

      Assert.assertNull(handler.rawIds);
      Assert.assertEquals(1, handler.guids.size());
      Assert.assertEquals(id2, handler.guids.iterator().next());

      //test a rawId and guid
      handler = new TestIdsPredicateHandler();
      values = Arrays.asList(id1, id2);
      testPredicate = new Predicate(SearchMethod.IDS, null, null, null, values);
      handler.handle(builder, testPredicate);

      Assert.assertEquals(1, handler.rawIds.size());
      Assert.assertEquals(1, handler.guids.size());
      Assert.assertEquals(id1, handler.rawIds.iterator().next().toString());
      Assert.assertEquals(id2, handler.guids.iterator().next());

   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() throws OseeCoreException {
      TestIdsPredicateHandler handler = new TestIdsPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, null);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() throws OseeCoreException {
      TestIdsPredicateHandler handler = new TestIdsPredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, null, null, values);
      handler.handle(builder, testPredicate);
   }
}
