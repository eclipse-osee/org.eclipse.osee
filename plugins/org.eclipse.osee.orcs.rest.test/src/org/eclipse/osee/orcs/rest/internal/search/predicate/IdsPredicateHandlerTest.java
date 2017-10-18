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

import static org.mockito.Mockito.verify;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IdsPredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
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
public class IdsPredicateHandlerTest {

   @Mock
   private QueryBuilder builder;

   @Captor
   private ArgumentCaptor<Collection<String>> guidsCaptor;
   @Captor
   private ArgumentCaptor<Collection<Long>> idsCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHandleLocalId() {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      //all digits get treated as artId
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, values);
      handler.handle(builder, testPredicate);
      verify(builder).andUuids(idsCaptor.capture());
      Assert.assertEquals(1, idsCaptor.getValue().size());
      Assert.assertTrue(idsCaptor.getValue().contains(12345L));
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleNonId() {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      //all digits get treated as artId
      String id1 = GUID.create();
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, values);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.IDS, null, null);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() {
      IdsPredicateHandler handler = new IdsPredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, values);
      handler.handle(builder, testPredicate);
   }
}
