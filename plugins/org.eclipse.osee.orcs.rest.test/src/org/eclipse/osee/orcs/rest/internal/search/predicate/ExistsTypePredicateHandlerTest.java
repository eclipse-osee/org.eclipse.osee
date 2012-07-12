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
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchFlag;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchOp;
import org.eclipse.osee.orcs.rest.mocks.MockQueryBuilder;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class ExistsTypePredicateHandlerTest {

   private class TestExistsTypePredicateHandler extends ExistsTypePredicateHandler {

      Collection<IRelationTypeSide> relations;
      Collection<IAttributeType> attributeTypes;

      @Override
      protected QueryBuilder andRelTypeSideExists(QueryBuilder builder, Collection<IRelationTypeSide> relations) {
         this.relations = relations;
         return builder;
      }

      @Override
      protected QueryBuilder andAttrTypesExists(QueryBuilder builder, Collection<IAttributeType> attributeTypes) {
         this.attributeTypes = attributeTypes;
         return builder;
      }

   }

   @Test
   public void testHandleRelationTypeSides() throws OseeCoreException {
      TestExistsTypePredicateHandler handler = new TestExistsTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("relType");
      //no flags for exists type
      List<SearchFlag> flags = Collections.emptyList();
      //for relation type sides, first char must be A or B denoting side, followed by relation type uuid
      String relationValue = "A12345";
      List<String> values = Collections.singletonList(relationValue);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(1, handler.relations.size());
      IRelationTypeSide side = handler.relations.iterator().next();
      Assert.assertEquals(relationValue.substring(1), side.getGuid().toString());
      Assert.assertEquals(RelationSide.SIDE_A, side.getSide());

      //test side B
      relationValue = "B12345";
      values = Collections.singletonList(relationValue);
      testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(1, handler.relations.size());
      side = handler.relations.iterator().next();
      Assert.assertEquals(relationValue.substring(1), side.getGuid().toString());
      Assert.assertEquals(RelationSide.SIDE_B, side.getSide());

      //test multiples
      String relationValue1 = "A12345";
      String relationValue2 = "B34567";
      values = Arrays.asList(relationValue1, relationValue2);
      testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(2, handler.relations.size());
      boolean sideAMatched = false, sideBMatched = false;
      for (IRelationTypeSide rts : handler.relations) {
         if (rts.getSide() == RelationSide.SIDE_A) {
            sideAMatched = true;
            Assert.assertEquals(relationValue1.substring(1), rts.getGuid().toString());
         }
         if (rts.getSide() == RelationSide.SIDE_B) {
            sideBMatched = true;
            Assert.assertEquals(relationValue2.substring(1), rts.getGuid().toString());
         }
      }
      Assert.assertTrue(sideAMatched);
      Assert.assertTrue(sideBMatched);
   }

   @Test
   public void testHandleAttrType() throws OseeCoreException {
      TestExistsTypePredicateHandler handler = new TestExistsTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("attrType");
      //no flags for exists type
      List<SearchFlag> flags = Collections.emptyList();
      //for relation type sides, first char must be A or B denoting side, followed by relation type uuid
      String attrUuid = "12345";
      List<String> values = Collections.singletonList(attrUuid);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(1, handler.attributeTypes.size());
      IAttributeType type = handler.attributeTypes.iterator().next();
      Assert.assertEquals(attrUuid, type.getGuid().toString());

      //test multiples
      String attrType1 = "12345";
      String attrType2 = "34567";
      values = Arrays.asList(attrType1, attrType2);
      testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);

      Assert.assertEquals(2, handler.attributeTypes.size());
      boolean attr1Matched = false, attr2Matched = false;
      for (IAttributeType attr : handler.attributeTypes) {
         if (attr.getGuid().toString().equals(attrType1)) {
            attr1Matched = true;
         }
         if (attr.getGuid().toString().equals(attrType2)) {
            attr2Matched = true;
         }
      }
      Assert.assertTrue(attr1Matched);
      Assert.assertTrue(attr2Matched);
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() throws OseeCoreException {
      TestExistsTypePredicateHandler handler = new TestExistsTypePredicateHandler();
      List<String> typeParameters = Collections.singletonList("attrType");
      //no flags for exists type
      List<SearchFlag> flags = Collections.emptyList();
      //for relation type sides, first char must be A or B denoting side, followed by relation type uuid
      String value = "12A4G";
      List<String> values = Collections.singletonList(value);
      Predicate testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(0, handler.attributeTypes.size());

      value = "A12A4G";
      typeParameters = Collections.singletonList("relType");
      values = Collections.singletonList(value);
      testPredicate = new Predicate(SearchMethod.EXISTS_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
      Assert.assertEquals(0, handler.relations.size());

      testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeParameters, SearchOp.EQUALS, flags, values);
      handler.handle(new MockQueryBuilder(), testPredicate);
   }
}
