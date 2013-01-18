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

import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John R. Misinco
 */
public class IdsPredicateHandlerTest {

   @Mock
   private QueryBuilder builder;

   @Captor
   private ArgumentCaptor<Collection<String>> guidsCaptor;
   @Captor
   private ArgumentCaptor<Collection<Integer>> localIdsCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHandleLocalId() throws OseeCoreException {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      //all digits get treated as artId
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, null, values);
      handler.handle(builder, testPredicate);
      verify(builder).andLocalIds(localIdsCaptor.capture());
      Assert.assertEquals(1, localIdsCaptor.getValue().size());
      Assert.assertTrue(localIdsCaptor.getValue().contains(12345));
   }

   @Test
   public void testHandleGuids() throws OseeCoreException {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      // no type params, op, or flags for ids - any passed are ignored
      // if not all digits, treated as guid
      String id2 = "AGUID234";
      List<String> values = Collections.singletonList(id2);
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, null, values);
      handler.handle(builder, testPredicate);
      verify(builder).andGuidsOrHrids(guidsCaptor.capture());
      Assert.assertEquals(1, guidsCaptor.getValue().size());
      Assert.assertTrue(guidsCaptor.getValue().contains(id2));
   }

   @Test
   public void testHandleIdsAndGuids() throws OseeCoreException {
      //test a rawId and guid
      IdsPredicateHandler handler = new IdsPredicateHandler();
      List<String> values = Arrays.asList("AGUID234", "12345");
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, null, values);
      handler.handle(builder, testPredicate);
      verify(builder).andGuidsOrHrids(guidsCaptor.capture());
      Assert.assertTrue(guidsCaptor.getValue().contains("AGUID234"));
      Assert.assertEquals(1, guidsCaptor.getValue().size());

      verify(builder).andLocalIds(localIdsCaptor.capture());
      Assert.assertEquals(1, localIdsCaptor.getValue().size());
      Assert.assertTrue(localIdsCaptor.getValue().contains(12345));
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() throws OseeCoreException {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null, null, null, null);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() throws OseeCoreException {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, null, null, null, values);
      handler.handle(builder, testPredicate);
   }
}
