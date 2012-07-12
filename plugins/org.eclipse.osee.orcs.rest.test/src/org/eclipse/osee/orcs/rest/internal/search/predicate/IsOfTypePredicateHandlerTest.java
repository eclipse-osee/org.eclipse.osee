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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.rest.mocks.MockQueryBuilder;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class IsOfTypePredicateHandlerTest {

   private class TestIsOfTypePredicateHandler extends IsOfTypePredicateHandler {

      Collection<IArtifactType> artTypes;

      @Override
      protected QueryBuilder andIsOfType(QueryBuilder builder, Collection<IArtifactType> artTypes) {
         this.artTypes = artTypes;
         return builder;
      }

   }

   @Test
   public void testHandle() throws OseeCoreException {
      TestIsOfTypePredicateHandler handler = new TestIsOfTypePredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, null, null, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(1, handler.artTypes.size());
      Assert.assertEquals(id1, handler.artTypes.iterator().next().getGuid().toString());

      String id2 = "45678";
      values = Arrays.asList(id1, id2);

      testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, null, null, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(2, handler.artTypes.size());
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() throws OseeCoreException {
      TestIsOfTypePredicateHandler handler = new TestIsOfTypePredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, null, null, null);
      handler.handle(new MockQueryBuilder(), testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() throws OseeCoreException {
      TestIsOfTypePredicateHandler handler = new TestIsOfTypePredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, null, null, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
   }
}
