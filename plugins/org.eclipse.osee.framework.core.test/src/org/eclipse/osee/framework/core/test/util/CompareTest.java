/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.util.Compare;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link Compare}
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class CompareTest {

   protected final Object object1;
   protected final Object object2;
   protected final boolean expected;

   public CompareTest(Object object1, Object object2, boolean expected) {
      this.object1 = object1;
      this.object2 = object2;
      this.expected = expected;
   }

   @Test
   public void testObjects() {
      boolean actual = Compare.isDifferent(object1, object2);
      Assert.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      Collection<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {"abc", "abc", false});
      data.add(new Object[] {"abc", "abc1", true});
      data.add(new Object[] {null, "abc", true});
      data.add(new Object[] {"abc", null, true});
      data.add(new Object[] {null, null, false});

      data.add(new Object[] {1, null, true});
      data.add(new Object[] {null, 2, true});
      data.add(new Object[] {1, 1, false});

      data.add(new Object[] {"1", 1, true});

      data.add(new Object[] {Arrays.asList("one", "two", "three"), Arrays.asList("one", "two", "three"), false});
      data.add(new Object[] {Arrays.asList("two", "one", "three"), Arrays.asList("three", "one", "two"), false});
      data.add(new Object[] {Arrays.asList("one", "three"), Arrays.asList("one", "two", "three"), true});

      data.add(new Object[] {new String[] {"one", "two", "three"}, new String[] {"one", "two", "three"}, false});
      data.add(new Object[] {new String[] {"two", "one", "three"}, new String[] {"three", "one", "two"}, false});
      data.add(new Object[] {new String[] {"one", "three"}, new String[] {"one", "two", "three"}, true});
      data.add(new Object[] {new String[] {"one", "two", "two", "three"}, new String[] {"one", "two", "three"}, true});

      data.add(new Object[] {map("a", "b", "c", "d"), map("a", "b", "c", "d"), false});
      data.add(new Object[] {map("a", "b", "c", "d"), map("a", "c", "b", "d"), false});
      data.add(new Object[] {map("a", "b", "c", "d"), map("a", "c", "b", "b", "d"), true});
      return data;
   }

   private static Map<Object, Collection<Object>> map(Object key, Object... values) {
      Map<Object, Collection<Object>> map = new HashMap<Object, Collection<Object>>();
      Collection<Object> objects = map.get(key);
      if (objects == null) {
         objects = new ArrayList<Object>();
         map.put(key, objects);
      } else {
         objects.clear();
      }
      if (values != null && values.length > 0) {
         objects.addAll(Arrays.asList(values));
      }
      return map;
   }
}
