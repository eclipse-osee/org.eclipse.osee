/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.tests;

import com.google.common.collect.Lists;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.junit.Assert;

/**
 * @author Roberto E. Escobar
 */
public final class ParserVerification {

   private final IParser parser;
   private final IGrammarAccess grammar;

   public ParserVerification(IParser parser, IGrammarAccess grammar) {
      this.parser = parser;
      this.grammar = grammar;
   }

   private void fail(String message, Object... args) {
      String msg = message;
      if (args != null && args.length > 0) {
         msg = String.format(message, args);
      }
      Assert.fail(msg);
   }

   public void rule(Class<? extends EObject> rule, String textToParse) {
      checkRule(rule, textToParse, false);
   }

   public void ruleError(Class<? extends EObject> rule, String textToParse) {
      checkRule(rule, textToParse, true);
   }

   private List<SyntaxErrorMessage> checkRule(Class<? extends EObject> rule, String textToParse, boolean errorsExpected) {

      String ruleName = rule.getSimpleName();
      ParserRule parserRule = (ParserRule) GrammarUtil.findRuleForName(grammar.getGrammar(), ruleName);

      if (parserRule == null) {
         fail("Could not find ParserRule [%s] for class [%s]", ruleName, rule);
      }

      IParseResult result = parser.parse(parserRule, new StringReader(textToParse));

      ArrayList<SyntaxErrorMessage> errors = Lists.newArrayList();
      ArrayList<String> errMsg = Lists.newArrayList();

      for (INode err : result.getSyntaxErrors()) {
         errors.add(err.getSyntaxErrorMessage());
         errMsg.add(err.getSyntaxErrorMessage().getMessage());
      }

      if (!errorsExpected && !errors.isEmpty()) {
         fail("Parsing of text [%s] for rule [%s] failed with errors: [%s]", textToParse, ruleName, errMsg);
      }
      if (errorsExpected && errors.isEmpty()) {
         fail("Parsing of text [%s] for rule [%s] was expected to have parse errors", textToParse, ruleName);
      }

      return errors;
   }

   public void checkRuleErrors(Class<? extends EObject> rule, String textToParse, String... expectedErrorSubstrings) {
      List<SyntaxErrorMessage> errors = checkRule(rule, textToParse, true);

      Set<String> matchingSubstrings = new HashSet<>();
      Set<String> assertedErrors = new HashSet<>();

      boolean hadError = false;
      for (final SyntaxErrorMessage err : errors) {
         for (final String substring : expectedErrorSubstrings) {
            boolean contains = err.getMessage().contains(substring);
            if (contains) {
               matchingSubstrings.add(substring);
            }
         }

         assertedErrors.add(err.getMessage());
      }

      StringBuilder error = new StringBuilder();
      if (expectedErrorSubstrings.length != matchingSubstrings.size()) {
         error.append("Unmatched assertions:");
         for (String string : expectedErrorSubstrings) {
            if (!matchingSubstrings.contains(string)) {
               error.append("\n  - any error containing '" + string + "'");
            }
         }
         error.append("\n");
         hadError = true;
      }

      if (assertedErrors.size() != errors.size()) {
         error.append("Unasserted Errors:");
         for (SyntaxErrorMessage err : errors) {
            if (!assertedErrors.contains(err.getMessage())) {
               error.append("\n  - " + err.getMessage());
            }
         }
      }

      String failMessage = error.toString();
      if (hadError || !failMessage.equals("")) {
         fail(failMessage);
      }
   }
}
