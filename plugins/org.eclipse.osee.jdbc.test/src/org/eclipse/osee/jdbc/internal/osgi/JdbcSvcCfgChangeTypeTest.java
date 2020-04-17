/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jdbc.internal.osgi;

import static org.eclipse.osee.jdbc.JdbcConstants.NAMESPACE;
import static org.eclipse.osee.jdbc.internal.osgi.JdbcSvcCfgChangeType.ALL_CHANGED;
import static org.eclipse.osee.jdbc.internal.osgi.JdbcSvcCfgChangeType.JDBC_PROPERTY;
import static org.eclipse.osee.jdbc.internal.osgi.JdbcSvcCfgChangeType.NO_CHANGE;
import static org.eclipse.osee.jdbc.internal.osgi.JdbcSvcCfgChangeType.OTHER_CHANGE;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link JdbcSvcCfgChangeType}
 *
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class JdbcSvcCfgChangeTypeTest {

   private final JdbcSvcCfgChangeType expected;
   private final Map<String, Object> map1;
   private final Map<String, Object> map2;

   public JdbcSvcCfgChangeTypeTest(JdbcSvcCfgChangeType expected, Map<String, Object> map1, Map<String, Object> map2) {
      super();
      this.expected = expected;
      this.map1 = map1;
      this.map2 = map2;
   }

   @Test
   public void testChangeDetection() {
      JdbcSvcCfgChangeType actual = JdbcSvcCfgChangeType.getChangeType(map1, map2);
      assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> tests = new ArrayList<>();

      //@formatter:off
      tests.add(row(NO_CHANGE, map("a", "1"), map("a", "1")));
      tests.add(row(NO_CHANGE, null, null));
      tests.add(row(ALL_CHANGED, map("a", "1"), null));
      tests.add(row(ALL_CHANGED, null, map("a", "1")));
      tests.add(row(ALL_CHANGED, map("a", "1"), map()));
      tests.add(row(JDBC_PROPERTY, map(NAMESPACE, "a"), map(NAMESPACE, "b")));
      tests.add(row(OTHER_CHANGE, map("a", "1"), map("a", "2")));
      //@formatter:on
      return tests;
   }

   private static Object[] row(Object... inputs) {
      return inputs;
   }

   private static Map<String, Object> map(Object... keyVals) {
      Map<String, Object> data = new HashMap<>();
      String key = null;
      boolean isKey = true;
      for (Object keyVal : keyVals) {
         if (isKey) {
            key = String.valueOf(keyVal);
            isKey = false;
         } else {
            data.put(key, keyVal);
            isKey = true;
         }
      }
      return data;
   }

}
