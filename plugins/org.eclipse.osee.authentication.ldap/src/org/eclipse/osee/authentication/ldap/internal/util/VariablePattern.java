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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Parses a variable pattern:
 *  Example:
 *    pattern = ${a}-${b}-${c}
 *    indexedPattern = {0}-{1}-{2}
 * 
 *    Parameters Map -
 *       "b" = dog;
 *       "a" = cat;
 *       "c" = ball;
 * 
 *    getVariableValues = {cat, dog, ball};
 *    expandVariables = cat-dog-ball
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
public final class VariablePattern {

   private final String pattern;
   private final String indexedPattern;
   private final List<Variable> variables;

   private VariablePattern(String pattern, String indexedPattern, List<Variable> variables) {
      super();
      this.pattern = pattern;
      this.indexedPattern = indexedPattern;
      this.variables = variables;
   }

   public String getPattern() {
      return pattern;
   }

   public String getIndexedPattern() {
      return indexedPattern;
   }

   public List<String> getVariableNames() {
      List<String> toReturn = new ArrayList<>(variables.size());
      for (Variable variable : variables) {
         toReturn.add(variable.getName());
      }
      return toReturn;
   }

   public String[] getVariableValues(Map<String, String> params) {
      final String[] toReturn = new String[variables.size()];
      for (int index = 0; index < toReturn.length; index++) {
         StringBuilder builder = new StringBuilder();
         Variable variable = variables.get(index);
         variable.format(builder, params);
         toReturn[index] = builder.toString();
      }
      return toReturn;
   }

   public String expandVariables(Map<String, String> params) {
      StringBuilder builder = new StringBuilder();
      for (Variable variable : variables) {
         variable.format(builder, params);
      }
      return builder.toString();
   }

   @Override
   public String toString() {
      return getPattern();
   }

   public static VariablePattern newPattern(String pattern) {
      List<Variable> variables = new ArrayList<>();
      StringBuilder builder = new StringBuilder();
      int size = pattern.length();
      int index = 0;
      while (index < size) {
         int start = pattern.indexOf("${", index);
         if (start < 0) {
            break;
         }
         int varNameStart = start + 2;
         int end = pattern.indexOf("}", varNameStart);
         if (end < 0) {
            break;
         }

         String variableString = pattern.substring(varNameStart, end);

         int varIndex = variables.size();
         variables.add(newVariable(variableString));

         String constantString = pattern.substring(index, start);
         builder.append(constantString);
         builder.append("{");
         builder.append(varIndex);
         builder.append("}");

         index = end + 1;
      }
      if (index < size) {
         builder.append(pattern.substring(index));
      }

      String indexedPattern = builder.toString();
      return new VariablePattern(pattern, indexedPattern, variables);
   }

   private static Variable newVariable(String variableString) {
      return new Variable(variableString);
   }

   private static class Variable {

      private final String name;

      public Variable(String name) {
         this.name = name;
      }

      public String getName() {
         return name;
      }

      public void format(StringBuilder builder, Map<String, String> parameters) {
         String value = parameters.get(name);
         builder.append(value != null ? value : "");
      }
   }

}
