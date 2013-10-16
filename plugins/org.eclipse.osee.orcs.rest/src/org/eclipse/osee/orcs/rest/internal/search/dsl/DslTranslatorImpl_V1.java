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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.SearchFlag;
import org.eclipse.osee.orcs.rest.model.search.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.SearchOp;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class DslTranslatorImpl_V1 implements DslTranslator {

   private static final String ARGUMENT_REGEX = ":([^&\\]]+)";
   private static final Pattern queryPattern = Pattern.compile("\\]&\\[");
   private static final Pattern typePattern = Pattern.compile("t" + ARGUMENT_REGEX);
   private static final Pattern typeParametersPattern = Pattern.compile("tp" + ARGUMENT_REGEX);
   private static final Pattern opPattern = Pattern.compile("op" + ARGUMENT_REGEX);
   private static final Pattern flagsPattern = Pattern.compile("f" + ARGUMENT_REGEX);
   private static final Pattern valuePattern = Pattern.compile("v" + ARGUMENT_REGEX);
   private static final Pattern delimiterPattern = Pattern.compile("d:'(.+)'");

   private final Matcher typeMatcher, opMatcher, flagsMatcher, valueMatcher, typeParametersMatcher, delimiterMatcher;
   private Scanner scanner;

   public DslTranslatorImpl_V1() {
      typeMatcher = typePattern.matcher("");
      opMatcher = opPattern.matcher("");
      flagsMatcher = flagsPattern.matcher("");
      valueMatcher = valuePattern.matcher("");
      typeParametersMatcher = typeParametersPattern.matcher("");
      delimiterMatcher = delimiterPattern.matcher("");
   }

   @Override
   public List<Predicate> translate(String rawString) throws OseeCoreException {
      List<Predicate> predicates = new LinkedList<Predicate>();
      scanner = new Scanner(rawString);
      scanner.useDelimiter(queryPattern);
      while (scanner.hasNext()) {
         String queryBlock = scanner.next();
         typeMatcher.reset(queryBlock);
         typeParametersMatcher.reset(queryBlock);
         opMatcher.reset(queryBlock);
         flagsMatcher.reset(queryBlock);
         valueMatcher.reset(queryBlock);
         delimiterMatcher.reset(queryBlock);

         String type = getMatch(typeMatcher);
         String typeParams = getMatch(typeParametersMatcher);
         String op = getMatch(opMatcher);
         String flags = getMatch(flagsMatcher);
         String value = getMatch(valueMatcher);
         String delimiter = getMatch(delimiterMatcher);

         predicates.add(createPredicate(type, typeParams, op, flags, delimiter, value));
      }
      return predicates;
   }

   private Predicate createPredicate(String type, String typeParameters, String op, String flags, String delimiter, String value) throws OseeCoreException {
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
      return new Predicate(searchMethod, typeParams, searchOp, searchFlags, delimiter, values);
   }

   private String getMatch(Matcher m) {
      if (m.find()) {
         return m.group(1);
      }
      return Strings.EMPTY_STRING;
   }

}
