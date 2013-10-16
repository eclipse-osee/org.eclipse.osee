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

import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchFlag;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.SearchOp;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class DslTranslatorImplTest {

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchType() throws OseeCoreException {
      DslTranslatorImpl_V1 translator = new DslTranslatorImpl_V1();

      //test bad search type
      String test = "[t:attrTypes&tp:1000000000000070&op:==&v:AtsAdmin]";
      translator.translate(test);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadOp() throws OseeCoreException {
      DslTranslatorImpl_V1 translator = new DslTranslatorImpl_V1();

      //test bad op
      String test = "[t:attrType&tp:1000000000000070&op:<>&v:AtsAdmin]";
      translator.translate(test);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadFlag() throws OseeCoreException {
      DslTranslatorImpl_V1 translator = new DslTranslatorImpl_V1();

      //test bad flags
      String test = "[t:attrType&tp:1000000000000070&op:==&f:ti&v:AtsAdmin]";
      translator.translate(test);
   }

   private int getNextInt(Random r) {
      return Math.abs(r.nextInt());
   }

   private String getSearchMethod(Random r) {
      int len = SearchMethod.values().length;
      return SearchMethod.values()[getNextInt(r) % len].getToken();
   }

   private String getOp(Random r) {
      int len = SearchOp.values().length;
      return SearchOp.values()[getNextInt(r) % len].getToken();
   }

   private String getFlags(Random r, int num) {
      if (num == 0) {
         return "";
      }
      StringBuilder toReturn = new StringBuilder();
      for (int i = 0; i < num; i++) {
         int len = SearchFlag.values().length;
         toReturn.append(SearchFlag.values()[getNextInt(r) % len].getToken());
         toReturn.append(",");
      }
      int last = toReturn.length();
      return toReturn.substring(0, last - 1);
   }

   private String getTestQuery(int num) {
      Random r = new Random();
      StringBuilder toReturn = new StringBuilder();
      for (int i = 0; i < num; i++) {
         toReturn.append("[t:");
         toReturn.append(getSearchMethod(r));
         toReturn.append("&tp:");
         toReturn.append(r.nextLong());
         toReturn.append("&op:");
         toReturn.append(getOp(r));
         toReturn.append("&f:");
         toReturn.append(getFlags(r, getNextInt(r) % 3));
         toReturn.append("&v:value1,value2]&");
      }
      int last = toReturn.length();
      return toReturn.substring(0, last - 1);
   }

   private boolean compareStringToPredicate(String query, Predicate predicate) {
      if (!query.contains(predicate.getType().getToken())) {
         return false;
      }
      if (!query.contains(predicate.getOp().getToken())) {
         return false;
      }
      for (SearchFlag flag : predicate.getFlags()) {
         if (!query.contains(flag.getToken())) {
            return false;
         }
      }
      for (String typeParam : predicate.getTypeParameters()) {
         if (!query.contains(typeParam)) {
            return false;
         }
      }
      for (String value : predicate.getValues()) {
         if (!query.contains(value)) {
            return false;
         }
      }
      return true;
   }

   @Test
   public void testMultiplePredicates() throws OseeCoreException {
      DslTranslatorImpl_V1 translator = new DslTranslatorImpl_V1();
      int size = 5;

      String test = getTestQuery(size);
      List<Predicate> translated = translator.translate(test);
      Assert.assertEquals(size, translated.size());
      String[] predicates = test.split("\\]&\\[");

      for (int i = 0; i < size; i++) {
         Assert.assertTrue(compareStringToPredicate(predicates[i], translated.get(i)));
      }
   }

   @Test
   public void testAttrTypeSearches() throws OseeCoreException {
      DslTranslatorImpl_V1 translator = new DslTranslatorImpl_V1();
      List<Predicate> predicates;

      String test = "[t:attrType&tp:0x1000000000000070&op:==&v:AtsAdmin]";
      predicates = translator.translate(test);
      Assert.assertEquals(1, predicates.size());
      Predicate predicate = predicates.iterator().next();
      Assert.assertEquals(SearchMethod.ATTRIBUTE_TYPE, predicate.getType());
      Assert.assertEquals(SearchOp.EQUALS, predicate.getOp());
      Assert.assertTrue(predicate.getValues().contains("AtsAdmin"));

      test = "[t:isOfType&tp:1000000000000070&op:>&v:AtsAdmin]";
      predicates = translator.translate(test);
      Assert.assertEquals(1, predicates.size());
      predicate = predicates.iterator().next();
      Assert.assertEquals(SearchMethod.IS_OF_TYPE, predicate.getType());
      Assert.assertEquals(SearchOp.GREATER_THAN, predicate.getOp());
      Assert.assertTrue(predicate.getValues().contains("AtsAdmin"));

      test = "[t:ids&tp:1000000000000070&op:<&v:AtsAdmin&d:'bo'b]''']";
      test += "&[t:attrType&tp:1000000000000070&op:==&v:TestAdmin&d:'bo'b]'[]&]'']";
      predicates = translator.translate(test);
      Assert.assertEquals(2, predicates.size());
      predicate = predicates.get(0);
      Assert.assertEquals(SearchMethod.IDS, predicate.getType());
      Assert.assertEquals(SearchOp.LESS_THAN, predicate.getOp());
      Assert.assertTrue(predicate.getValues().contains("AtsAdmin"));
      Assert.assertEquals("bo'b]''", predicate.getDelimiter());
      predicate = predicates.get(1);
      Assert.assertEquals(SearchMethod.ATTRIBUTE_TYPE, predicate.getType());
      Assert.assertEquals(SearchOp.EQUALS, predicate.getOp());
      Assert.assertTrue(predicate.getValues().contains("TestAdmin"));
      Assert.assertEquals("bo'b]'[]&]'", predicate.getDelimiter());
   }
}
