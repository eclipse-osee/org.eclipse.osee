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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchFlag;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchOp;
import org.eclipse.osee.orcs.rest.mocks.MockQueryBuilder;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.StringOperator;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class AttributeTypePredicateHandlerTest {

   private class TestAttributeTypePredicateHandler extends AttributeTypePredicateHandler {

      StringOperator stringOperator;
      Operator operator;
      CaseType ct;

      @Override
      protected QueryBuilder and(QueryBuilder builder, Collection<IAttributeType> attributeTypes, StringOperator operator, CaseType ct, String value) {
         this.stringOperator = operator;
         this.ct = ct;
         return builder;
      }

      @Override
      protected QueryBuilder and(QueryBuilder builder, IAttributeType type, Operator operator, List<String> values) {
         this.operator = operator;
         return builder;
      }

   }

   @Test
   public void testStringOperatorSelection() throws OseeCoreException {
      TestAttributeTypePredicateHandler handler = new TestAttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<SearchFlag> flags = Arrays.asList(SearchFlag.TOKENIZED_ANY);
      List<String> values = Collections.singletonList("value");
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(StringOperator.TOKENIZED_ANY_ORDER, handler.stringOperator);

      flags = Arrays.asList(SearchFlag.TOKENIZED_ORDERED);
      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(StringOperator.TOKENIZED_MATCH_ORDER, handler.stringOperator);

      flags = Arrays.asList(SearchFlag.IGNORE_CASE);
      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(StringOperator.EQUALS, handler.stringOperator);

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.NOT_EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(StringOperator.NOT_EQUALS, handler.stringOperator);

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.IN, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(StringOperator.CONTAINS, handler.stringOperator);
   }

   @Test
   public void testCaseTypeSelection() throws OseeCoreException {
      TestAttributeTypePredicateHandler handler = new TestAttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<SearchFlag> flags = Arrays.asList(SearchFlag.IGNORE_CASE);
      List<String> values = Collections.singletonList("value");
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(CaseType.IGNORE_CASE, handler.ct);

      flags = Arrays.asList(SearchFlag.TOKENIZED, SearchFlag.IGNORE_CASE);
      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(CaseType.IGNORE_CASE, handler.ct);

      flags = Arrays.asList(SearchFlag.TOKENIZED, SearchFlag.MATCH_CASE);
      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(CaseType.MATCH_CASE, handler.ct);
   }

   @Test
   public void testOperatorSelection() throws OseeCoreException {
      TestAttributeTypePredicateHandler handler = new TestAttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<SearchFlag> flags = Arrays.asList(SearchFlag.MATCH_CASE);
      List<String> values = Collections.singletonList("value");
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(Operator.EQUAL, handler.operator);

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.GREATER_THAN, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(Operator.GREATER_THAN, handler.operator);

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.LESS_THAN, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(Operator.LESS_THAN, handler.operator);

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.NOT_EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(Operator.NOT_EQUAL, handler.operator);
   }
}
