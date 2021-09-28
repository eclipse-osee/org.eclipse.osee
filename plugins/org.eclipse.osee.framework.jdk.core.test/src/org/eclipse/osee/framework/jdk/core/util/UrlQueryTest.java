/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link UrlQuery}
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class UrlQueryTest {

   private final String queryString;
   private final TestData expected;

   public UrlQueryTest(String queryString, TestData expected) {
      this.queryString = queryString;
      this.expected = expected;
   }

   @Test
   public void testParse() throws UnsupportedEncodingException {
      UrlQuery query = new UrlQuery();
      query.parse(queryString);

      Set<String> expectedKeys = expected.keySet();
      for (String key : expectedKeys) {
         Assert.assertTrue(query.containsKey(key));
      }
      Assert.assertFalse(query.containsKey("dummy"));

      Enumeration<String> keys = query.getParameterNames();
      Set<String> data = new HashSet<>();
      while (keys.hasMoreElements()) {
         data.add(keys.nextElement());
      }

      assertEquals(expectedKeys, data);

      for (Entry<String, List<String>> entry : expected.entrySet()) {
         String[] values = query.getParameterValues(entry.getKey());
         List<String> actual = new ArrayList<>();
         if (values != null) {
            actual.addAll(Arrays.asList(values));
            java.util.Collections.sort(actual);
         }
         List<String> expected = entry.getValue();
         java.util.Collections.sort(expected);
         assertEquals(expected, actual);
      }

      for (Entry<String, String[]> entry : query.getParameterMap().entrySet()) {

         List<String> actual = new ArrayList<>();
         actual.addAll(Arrays.asList(entry.getValue()));
         java.util.Collections.sort(actual);

         List<String> expData = expected.get(entry.getKey());
         java.util.Collections.sort(expData);

         assertEquals(expData, actual);
      }

      Assert.assertEquals(queryString, query.toUrl());
      Assert.assertEquals(queryString, query.toString());
   }

   private static <T> void assertEquals(Collection<T> expected, Collection<T> actual) {
      Collection<T> set1 = Collections.setComplement(expected, actual);
      Collection<T> set2 = Collections.setComplement(actual, expected);
      Assert.assertTrue(set1.toString(), set1.isEmpty());
      Assert.assertTrue(set2.toString(), set2.isEmpty());
   }

   @Parameters
   public static List<Object[]> getData() {
      List<Object[]> data = new LinkedList<>();
      add(data, "phrase=Hello+Dude&value1=%2212345%22&value2=4%3C6",
         new TestData().put("phrase", "Hello Dude").put("value1", "\"12345\"").put("value2", "4<6"));
      add(data, "query+name=Hello+dude&query+name=one+more+string",
         new TestData().put("query name", "Hello dude", "one more string"));
      return data;
   }

   private static final void add(List<Object[]> data, Object... args) {
      data.add(args);
   }

   private static final class TestData {
      private final Map<String, List<String>> data = new TreeMap<>();

      public TestData put(String key, String... values) {
         List<String> vals = get(key);
         if (vals == null) {
            vals = new ArrayList<>();
            data.put(key, vals);
         }
         for (String value : values) {
            vals.add(value);
         }
         return this;
      }

      public List<String> get(String key) {
         return data.get(key);
      }

      public Set<Entry<String, List<String>>> entrySet() {
         return data.entrySet();
      }

      public Set<String> keySet() {
         return data.keySet();
      }
   }

}
