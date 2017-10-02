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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.GuidsPredicateHandler;
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
public class GuidsPredicateHandlerTest {

   @Mock
   private QueryBuilder builder;

   @Captor
   private ArgumentCaptor<Collection<String>> guidsCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHandleGuids()  {
      GuidsPredicateHandler handler = new GuidsPredicateHandler();
      // no type params for ids - any passed are ignored
      // if not all digits, treated as guid
      String id2 = "AGUID234";
      List<String> values = Collections.singletonList(id2);
      Predicate testPredicate = new Predicate(SearchMethod.GUIDS, null, values);
      handler.handle(builder, testPredicate);
      verify(builder).andGuids(guidsCaptor.capture());
      Assert.assertEquals(1, guidsCaptor.getValue().size());
      Assert.assertTrue(guidsCaptor.getValue().contains(id2));
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues()  {
      GuidsPredicateHandler handler = new GuidsPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.GUIDS, null, null);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod()  {
      GuidsPredicateHandler handler = new GuidsPredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, values);
      handler.handle(builder, testPredicate);
   }
}
