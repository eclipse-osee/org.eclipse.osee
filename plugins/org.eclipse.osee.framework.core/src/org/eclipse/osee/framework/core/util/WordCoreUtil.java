/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Megumi Telles
 */

public class WordCoreUtil {
   private static String MAX_TAG_OCCURENCE = "30";
   private static String WORD_ML_TAGS = "(\\<[^>]*?>){0," + MAX_TAG_OCCURENCE + "}";

   public static String END = "E" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "d ?" + WORD_ML_TAGS + " ?";
   public static String ELSE = "E" + WORD_ML_TAGS + "l" + WORD_ML_TAGS + "s" + WORD_ML_TAGS + "e ?";
   public static String FEATURE =
      "F" + WORD_ML_TAGS + "e" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "e ?";
   public static String CONFIG =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n ?";

   public static String ENDBRACKETS = WORD_ML_TAGS + " ?(\\[(.*?)\\]) ?";
   public static String OPTIONAL_ENDBRACKETS = " ?(" + WORD_ML_TAGS + " ?(\\[.*?\\]))?";
   public static String BEGINFEATURE = FEATURE + ENDBRACKETS;
   public static String ENDFEATURE = END + FEATURE + OPTIONAL_ENDBRACKETS;
   public static String BEGINCONFIG = CONFIG + ENDBRACKETS;
   public static String ENDCONFIG = END + CONFIG + OPTIONAL_ENDBRACKETS;

   public static Pattern FEATURE_CONFIG_PATTERN =
      Pattern.compile("(" + BEGINFEATURE + "(.*?)" + ENDFEATURE + ")|(" + BEGINCONFIG + "(.*?)" + ENDCONFIG + ")",
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static final Pattern tagKiller = Pattern.compile("<.*?>", Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern = Pattern.compile("<w:p( .*?)?>");
   private static final String AML_ANNOTATION = "<.??aml:annotation.*?>";
   private static final String AML_CONTENT = "<.??aml:content.*?>";
   private static final String DELETIONS = "<w:delText>.*?</w:delText>";

   public static boolean containsWordAnnotations(String wordml) {
      return wordml.contains("<w:delText>") || wordml.contains("w:type=\"Word.Insertion\"") || wordml.contains(
         "w:type=\"Word.Formatting\"") || wordml.contains("w:type=\"Word.Deletion\"");
   }

   public static String removeAnnotations(String wordml) {
      String response = wordml;
      if (Strings.isValid(response)) {
         response = response.replaceAll(AML_ANNOTATION, "");
         response = response.replaceAll(AML_CONTENT, "");
         response = response.replaceAll(DELETIONS, "");
      }
      return response;
   }

   public static boolean areApplicabilityTagsInvalid(String wordml, BranchId branch, HashCollection<String, String> validFeatureValues) {

      Matcher match = FEATURE_CONFIG_PATTERN.matcher(wordml);

      boolean isFeature = false;
      String expression = null;

      while (match.find()) {
         String plainText = textOnly(match.group());

         if (plainText.startsWith("Feature")) {

            // Check if start and end are inconsistent
            int featStartTagGroup = 9;
            int featEndTagGroup = 23;

            String start = textOnly(match.group(featStartTagGroup));
            String end = match.group(featEndTagGroup);
            if (end != null) {
               end = textOnly(end);
               if (!start.equalsIgnoreCase(end)) {
                  return true;
               }
            }

            isFeature = true;
            expression = "Feature" + textOnly(match.group(featStartTagGroup));

         } else if (plainText.startsWith("Configuration")) {

            // Check if start and end are inconsistent
            int configStartTagGroup = 38;
            int configEndTagGroup = 58;

            String start = textOnly(match.group(configStartTagGroup));
            String end = match.group(configEndTagGroup);
            if (end != null) {
               end = textOnly(end);
               if (!start.equalsIgnoreCase(end)) {
                  return true;
               }
            }

            expression = "Configuration" + textOnly(match.group(configStartTagGroup));
         }

         // Check if applicability expression is valid
         if (isExpressionInvalid(expression, branch, validFeatureValues, isFeature)) {
            return true;
         }
      }

      return false;
   }

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }

   public static boolean isExpressionInvalid(String expression, BranchId branch, HashCollection<String, String> validFeatureValues, boolean isFeature) {
      ApplicabilityGrammarLexer lex = new ApplicabilityGrammarLexer(new ANTLRStringStream(expression));
      ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

      try {
         parser.start();
      } catch (RecognitionException ex) {
         return true;
      }

      if (isFeature) {
         HashMap<String, List<String>> featureIdValuesMap = parser.getFeatureIdValuesMap();

         for (String featureId : featureIdValuesMap.keySet()) {
            if (validFeatureValues.containsKey(featureId.toUpperCase())) {
               List<String> values = featureIdValuesMap.get(featureId);
               if (values.contains("Default")) {
                  continue;
               }
               Collection<String> validValues = validFeatureValues.getValues(featureId.toUpperCase());
               for (String val : values) {
                  if (!containsIgnoreCase(validValues, val)) {
                     return true;
                  }
               }
            } else {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }
}
