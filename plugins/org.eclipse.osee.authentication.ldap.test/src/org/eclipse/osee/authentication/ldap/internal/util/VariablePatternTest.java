/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.authentication.ldap.internal.util;

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
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class VariablePatternTest {

   private final String pattern;
   private final String indexedPattern;
   private final String[] variables;

   private final Map<String, String> params;
   private final String[] values;
   private final String expanded;

   private final VariablePattern varPattern;

   public VariablePatternTest(String pattern, String indexedPattern, String[] variables, Map<String, String> params, String[] values, String expanded) {
      super();
      this.pattern = pattern;
      this.indexedPattern = indexedPattern;
      this.variables = variables;
      this.params = params;
      this.values = values;
      this.expanded = expanded;
      varPattern = VariablePattern.newPattern(pattern);
   }

   @Test
   public void testParsing() {
      assertEquals(pattern, varPattern.getPattern());
      assertEquals(indexedPattern, varPattern.getIndexedPattern());

      List<String> varNames = varPattern.getVariableNames();
      assertEquals(variables.length, varNames.size());

      for (int index = 0; index < variables.length; index++) {
         assertEquals(variables[index], varNames.get(index));
      }

      assertEquals(expanded, varPattern.expandVariables(params));

      String[] actualValues = varPattern.getVariableValues(params);
      assertEquals(values.length, actualValues.length);
      for (int index = 0; index < values.length; index++) {
         assertEquals(values[index], actualValues[index]);
      }
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();

      Map<String, String> params = new HashMap<>();
      params.put("y", "hello");
      params.put("c", "ball");
      params.put("a", "cat");
      params.put("b", "dog");
      params.put("x", "bye");

      //@formatter:off
      addTest(data, "${a}-${b}-${c}", "{0}-{1}-{2}", array("a", "b", "c"), params, array("cat", "dog", "ball"), "catdogball");
      addTest(data, "x${b} ${c}$1${a}", "x{0} {1}$1{2}", array("b", "c", "a"), params, array("dog", "ball","cat"), "dogballcat");
      //@formatter:on

      return data;
   }

   private static String[] array(String... values) {
      return values;
   }

   private static void addTest(Collection<Object[]> data, String pattern, String indexedPattern, String[] variables, Map<String, String> params, String[] values, String expanded) {
      data.add(new Object[] {pattern, indexedPattern, variables, params, values, expanded});
   }
}
