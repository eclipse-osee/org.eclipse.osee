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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchDsl.DslTranslator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class SearchDslTest {

   private class MockTranslator implements DslTranslator {
      public String rawString;
      private final SearchMethod method;

      public MockTranslator(SearchMethod method) {
         this.method = method;
      }

      @Override
      public List<Predicate> translate(String rawString) {
         this.rawString = rawString;
         Predicate toReturn = new Predicate(method, null, null, null, null);
         return Collections.singletonList(toReturn);
      }
   }

   private class MockPredicateHandler implements PredicateHandler {
      public boolean handleCalled = false;

      @Override
      public QueryBuilder handle(QueryBuilder builder, Predicate predicate) {
         handleCalled = true;
         return builder;
      }
   }

   private class MockQueryFactory implements QueryFactory {
      public IOseeBranch branch;

      @Override
      public QueryBuilder fromBranch(IOseeBranch branch) {
         this.branch = branch;
         return null;
      }

      @Override
      public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) {
         return null;
      }

      @Override
      public QueryBuilder fromArtifacts(Collection<? extends ReadableArtifact> artifacts) {
         return null;
      }
   };

   @Test
   public void testBuildValidSearchType() throws OseeCoreException {
      Map<SearchMethod, PredicateHandler> handlers = new HashMap<SearchMethod, PredicateHandler>();
      MockPredicateHandler handler = new MockPredicateHandler();
      handlers.put(SearchMethod.ATTRIBUTE_TYPE, handler);

      MockTranslator translator = new MockTranslator(SearchMethod.ATTRIBUTE_TYPE);
      SearchDsl dsl = new SearchDsl(handlers, translator);
      MockQueryFactory queryFactory = new MockQueryFactory();

      IOseeBranch branch = CoreBranches.COMMON;
      String rawQuery = "[t:attrType&tp:1000000000000070&op:==&v:AtsAdmin]";
      dsl.build(queryFactory, branch, rawQuery);

      Assert.assertEquals(rawQuery, translator.rawString);
      Assert.assertEquals(branch, queryFactory.branch);
      Assert.assertTrue(handler.handleCalled);
   }

   @Test
   public void testBuildInvalidSearchType() throws OseeCoreException {
      Map<SearchMethod, PredicateHandler> handlers = new HashMap<SearchMethod, PredicateHandler>();
      MockPredicateHandler handler = new MockPredicateHandler();
      handlers.put(SearchMethod.ATTRIBUTE_TYPE, handler);

      MockTranslator translator = new MockTranslator(SearchMethod.EXISTS_TYPE);
      SearchDsl dsl = new SearchDsl(handlers, translator);
      MockQueryFactory queryFactory = new MockQueryFactory();

      IOseeBranch branch = CoreBranches.COMMON;
      String rawQuery = "[t:attrType&tp:1000000000000070&op:==&v:AtsAdmin]";
      dsl.build(queryFactory, branch, rawQuery);

      Assert.assertEquals(rawQuery, translator.rawString);
      Assert.assertEquals(branch, queryFactory.branch);
      Assert.assertFalse(handler.handleCalled);
   }
}
