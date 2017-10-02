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

import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.AttributeTypePredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
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
   private ArgumentCaptor<QueryOption> delimiter;
   @Captor
   private ArgumentCaptor<Collection<String>> valueCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testStringSearchOptions() {
      AttributeTypePredicateHandler handler = new AttributeTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("0x0123");
      List<String> values = Collections.singletonList("value");
      Predicate testPredicate =
         new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, values, QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(builder, testPredicate);
      verify(builder).and(anyCollectionOf(AttributeTypeId.class), eq("value"), eq(QueryOption.TOKEN_DELIMITER__ANY));
   }

}
