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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.ExistenceTypePredicateHandler;
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
public class ExistenceTypePredicateHandlerTest {

   @Mock
   private QueryBuilder builder;

   @Captor
   private ArgumentCaptor<IRelationType> relationTypeCaptor;
   @Captor
   private ArgumentCaptor<Collection<AttributeTypeId>> attrTypeSideCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testHandleRelationTypeSideA() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("relType");
      //for relation type sides, first char must be A or B denoting side, followed by relation type uuid
      String relationValue = "12345";
      List<String> values = Collections.singletonList(relationValue);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values);
      handler.handle(builder, testPredicate);
      verify(builder).andRelationExists(relationTypeCaptor.capture());
      Assert.assertEquals(1, relationTypeCaptor.getAllValues().size());
      Assert.assertTrue(12345L == relationTypeCaptor.getValue().getId());
   }

   @Test
   public void testHandleRelationTypeSideB() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("relType");
      //no flags for exists type
      String relationValue = "12345";
      List<String> values = Collections.singletonList(relationValue);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values);
      handler.handle(builder, testPredicate);

      verify(builder).andRelationExists(relationTypeCaptor.capture());
      Assert.assertEquals(1, relationTypeCaptor.getAllValues().size());
      Assert.assertTrue(12345L == relationTypeCaptor.getValue().getId());
   }

   @Test
   public void testHandleRelationTypeSideMultiples() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("relType");
      //test multiples
      String relationValue1 = "12345";
      String relationValue2 = "34567";
      List<String> values = Arrays.asList(relationValue1, relationValue2);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values);

      handler.handle(builder, testPredicate);
      verify(builder, times(2)).andRelationExists(relationTypeCaptor.capture());

      Assert.assertEquals(2, relationTypeCaptor.getAllValues().size());
      IRelationType type = relationTypeCaptor.getAllValues().get(0);
      Assert.assertTrue(12345L == type.getId());

      type = relationTypeCaptor.getAllValues().get(1);
      Assert.assertTrue(34567L == type.getId());
   }

   @Test
   public void testHandleAttrTypeSingle() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("attrType");
      //for relation type sides, first char must be A or B denoting side, followed by relation type uuid
      String attrUuid = "12345";
      List<String> values = Collections.singletonList(attrUuid);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values);
      handler.handle(builder, testPredicate);
      verify(builder).andExists(attrTypeSideCaptor.capture());
      Assert.assertEquals(1, attrTypeSideCaptor.getAllValues().size());
      List<AttributeTypeId> attrTypes = new ArrayList<>(attrTypeSideCaptor.getValue());
      Assert.assertEquals(attrTypes.get(0), Long.valueOf(attrUuid));
   }

   @Test
   public void testHandleAttrTypeMultiple() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("attrType");
      String attrType1 = "12345";
      String attrType2 = "34567";
      List<String> values = Arrays.asList(attrType1, attrType2);
      Predicate testPredicate =
         new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values, QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(builder, testPredicate);

      verify(builder).andExists(attrTypeSideCaptor.capture());
      Assert.assertEquals(1, attrTypeSideCaptor.getAllValues().size());
      Iterator<AttributeTypeId> iterator = attrTypeSideCaptor.getValue().iterator();
      Assert.assertEquals(iterator.next(), Long.valueOf(attrType1));
      Assert.assertEquals(iterator.next(), Long.valueOf(attrType2));
   }

   @Test
   public void testHandleBadValues() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("attrType");
      String value = "12A4G";
      List<String> values = Collections.singletonList(value);
      Predicate testPredicate =
         new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values, QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(builder, testPredicate);
      verify(builder, never()).andExists(anyCollectionOf(AttributeTypeId.class));

      value = "12A4G";
      typeParameters = Collections.singletonList("relType");
      values = Collections.singletonList(value);
      testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, values, QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(builder, testPredicate);
      verify(builder, never()).andRelationExists(any(RelationTypeSide.class), any(RelationSide.class));
   }

   @Test(expected = OseeCoreException.class)
   public void testBadValuesThrowException() {
      ExistenceTypePredicateHandler handler = new ExistenceTypePredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, Collections.singletonList("relType"),
         Collections.singletonList("A12A4G"), QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(builder, testPredicate);
   }
}
