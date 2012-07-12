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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchDsl.DslTranslator;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class DslTranslatorImpl implements DslTranslator {

   private static final String ARGUMENT_REGEX = ":([^&\\]]+)";
   private static final Pattern queryPattern = Pattern.compile("(\\[[^\\]]+\\])");
   private static final Pattern typePattern = Pattern.compile("t" + ARGUMENT_REGEX);
   private static final Pattern typeParametersPattern = Pattern.compile("tp" + ARGUMENT_REGEX);
   private static final Pattern opPattern = Pattern.compile("op" + ARGUMENT_REGEX);
   private static final Pattern flagsPattern = Pattern.compile("f" + ARGUMENT_REGEX);
   private static final Pattern valuePattern = Pattern.compile("v" + ARGUMENT_REGEX);

   private final Matcher queryMatcher, typeMatcher, opMatcher, flagsMatcher, valueMatcher, typeParametersMatcher;

   public DslTranslatorImpl() {
      queryMatcher = queryPattern.matcher("");
      typeMatcher = typePattern.matcher("");
      opMatcher = opPattern.matcher("");
      flagsMatcher = flagsPattern.matcher("");
      valueMatcher = valuePattern.matcher("");
      typeParametersMatcher = typeParametersPattern.matcher("");
   }

   @Override
   public List<Predicate> translate(String rawString) throws OseeCoreException {
      List<Predicate> predicates = new LinkedList<Predicate>();
      queryMatcher.reset(rawString);
      while (queryMatcher.find()) {
         String queryBlock = queryMatcher.group(1);
         typeMatcher.reset(queryBlock);
         typeParametersMatcher.reset(queryBlock);
         opMatcher.reset(queryBlock);
         flagsMatcher.reset(queryBlock);
         valueMatcher.reset(queryBlock);

         String type = getMatch(typeMatcher);
         String typeParams = getMatch(typeParametersMatcher);
         String op = getMatch(opMatcher);
         String flags = getMatch(flagsMatcher);
         String value = getMatch(valueMatcher);

         predicates.add(createPredicate(type, typeParams, op, flags, value));
      }
      return predicates;
   }

   private Predicate createPredicate(String type, String typeParameters, String op, String flags, String value) throws OseeCoreException {
      SearchMethod searchMethod = SearchMethod.fromString(type);
      SearchOp searchOp = SearchOp.fromString(op);

      List<SearchFlag> searchFlags = new LinkedList<SearchFlag>();
      for (String flag : flags.split(",")) {
         if (Strings.isValid(flag)) {
            searchFlags.add(SearchFlag.fromString(flag));
         }
      }

      List<String> values = Arrays.asList(value.split(",\\s*"));
      List<String> typeParams = Arrays.asList(typeParameters.split(",\\s*"));
      return new Predicate(searchMethod, typeParams, searchOp, searchFlags, values);
   }

   private String getMatch(Matcher m) {
      if (m.find()) {
         return m.group(1);
      }
      return "";
   }

}
