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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.MatchTokenCountType;
import org.eclipse.osee.framework.core.enums.Operator;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchFlag;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.SearchOp;
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
public class AttributeTypePredicateHandlerTest {

   @Mock
   private QueryBuilder builder;
   @Captor
   private ArgumentCaptor<TokenDelimiterMatch> pattern;
   @Captor
   private ArgumentCaptor<Collection<String>> valueCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testStringSearchOptions() throws OseeCoreException {
      AttributeTypePredicateHandler handler = new AttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<SearchFlag> flags = Collections.emptyList();
      List<String> values = Collections.singletonList("value");
      String delimiter = "delim";
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, delimiter, values);
      handler.handle(builder, testPredicate);
      verify(builder).and(anyCollectionOf(IAttributeType.class), eq("value"), pattern.capture(),
         eq(CaseType.IGNORE_CASE), eq(TokenOrderType.ANY_ORDER), eq(MatchTokenCountType.IGNORE_TOKEN_COUNT));
      Assert.assertTrue(delimiter.equals(pattern.getValue().getPattern().pattern()));

      reset(builder);
      flags = Arrays.asList(SearchFlag.values());
      testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, "delim", values);
      handler.handle(builder, testPredicate);
      verify(builder).and(anyCollectionOf(IAttributeType.class), eq("value"), pattern.capture(),
         eq(CaseType.MATCH_CASE), eq(TokenOrderType.MATCH_ORDER), eq(MatchTokenCountType.MATCH_TOKEN_COUNT));
   }

   @Test
   public void testOperatorSelection() throws OseeCoreException {
      AttributeTypePredicateHandler handler = new AttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<SearchFlag> flags = Arrays.asList(SearchFlag.MATCH_CASE);
      List<String> values = Collections.singletonList("value");
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.GREATER_THAN, flags, "", values);
      handler.handle(builder, testPredicate);
      verify(builder).and(any(IAttributeType.class), eq(Operator.GREATER_THAN), valueCaptor.capture());
      Assert.assertEquals(values, valueCaptor.getValue());

      reset(builder);
      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.LESS_THAN, flags, "", values);
      handler.handle(builder, testPredicate);
      verify(builder).and(any(IAttributeType.class), eq(Operator.LESS_THAN), valueCaptor.capture());
      Assert.assertEquals(values, valueCaptor.getValue());
   }
}
