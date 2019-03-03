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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.TypeEqualsPredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

/**
 * @author Roberto E. Escobar
 */
public class TypeEqualsPredicateHandlerTest {

   @Mock
   private QueryBuilder builder;

   @Captor
   private ArgumentCaptor<Collection<IArtifactType>> artifactTypesCaptor;

   private PredicateHandler handler;

   @Before
   public void initialize() {
      initMocks(this);

      handler = new TypeEqualsPredicateHandler();
   }

   @Test
   public void testHandleSingle() {
      //no type params, op, or flags for ids - any passed are ignored

      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.TYPE_EQUALS, null, values);
      handler.handle(builder, testPredicate);

      verify(builder).andTypeEquals(artifactTypesCaptor.capture());

      assertEquals(1, artifactTypesCaptor.getValue().size());
      assertTrue(artifactTypesCaptor.getValue().iterator().next().getIdString().equals(id1));
   }

   @Test
   public void testHandleMultiple() {
      String id1 = "12345";
      String id2 = "45678";
      List<String> values = Arrays.asList(id1, id2);

      Predicate testPredicate = new Predicate(SearchMethod.TYPE_EQUALS, null, values);
      handler.handle(builder, testPredicate);

      verify(builder).andTypeEquals(artifactTypesCaptor.capture());

      assertEquals(2, artifactTypesCaptor.getValue().size());

      Iterator<IArtifactType> iterator = artifactTypesCaptor.getValue().iterator();
      assertEquals(id1, iterator.next().getIdString());
      assertEquals(id2, iterator.next().getIdString());
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() {
      Predicate testPredicate = new Predicate(SearchMethod.TYPE_EQUALS, null, null);
      handler.handle(builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() {
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, values);
      handler.handle(builder, testPredicate);
   }
}
