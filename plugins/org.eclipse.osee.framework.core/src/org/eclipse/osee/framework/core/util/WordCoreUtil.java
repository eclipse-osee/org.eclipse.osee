/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;

/**
 * @author Megumi Telles
 */

public class WordCoreUtil {
   public static String FEATUREAPP = "feature";
   public static String CONFIGAPP = "configuration";
   public static String CONFIGGRPAPP = "configurationgroup";

   public static String MAX_TAG_OCCURENCE = "30";
   public static String WORD_ML_TAGS = "(\\<[^>]*?>){0," + MAX_TAG_OCCURENCE + "}";

   public static String TABLE_CELL = "<w:tc>";
   public static String TABLE = "<w:tbl>";
   public static String START_TABLE_ROW = "<w:tr wsp:rsidR=";
   public static String END_TABLE_ROW = "</w:tr>";
   public static String LIST = "<w:listPr>";
   public static String START_PARAGRAPH = "<w:p wsp:rsid";
   public static String WHOLE_END_PARAGRAPH = "</w:t></w:r></w:p>";
   public static String END_PARAGRAPH = "</w:p>";

   public static String END = "E" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "d ?" + WORD_ML_TAGS + " ?";
   public static String ELSE = "E" + WORD_ML_TAGS + "l" + WORD_ML_TAGS + "s" + WORD_ML_TAGS + "e ?";
   public static String FEATURE =
      "F" + WORD_ML_TAGS + "e" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "e";
   public static String CONFIG =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n";
   public static String CONFIGGRP =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "G" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "p";

   public static String NOT = "N" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "t";

   public static String ENDBRACKETS = WORD_ML_TAGS + " ?(\\[(.*?)\\]) ?";
   public static String OPTIONAL_ENDBRACKETS = " ?(" + WORD_ML_TAGS + "(\\[.*?\\]))?";
   public static String BEGINFEATURE = FEATURE + WORD_ML_TAGS + " ?" + ENDBRACKETS;
   public static String ENDFEATURE = END + WORD_ML_TAGS + FEATURE + OPTIONAL_ENDBRACKETS;
   public static String BEGINCONFIG =
      CONFIG + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static String ENDCONFIG = END + WORD_ML_TAGS + CONFIG + OPTIONAL_ENDBRACKETS;

   public static String BEGINCONFIGGRP =
      CONFIGGRP + WORD_ML_TAGS + "( " + WORD_ML_TAGS + NOT + WORD_ML_TAGS + ")? ?" + ENDBRACKETS;
   public static String ENDCONFIGGRP = END + WORD_ML_TAGS + CONFIGGRP + OPTIONAL_ENDBRACKETS;
   public static String ELSE_EXP =
      "(" + FEATURE + "|" + CONFIGGRP + "|" + CONFIG + ")" + WORD_ML_TAGS + " " + WORD_ML_TAGS + ELSE;

   public static Pattern ELSE_PATTERN = Pattern.compile(ELSE_EXP, Pattern.DOTALL | Pattern.MULTILINE);

   public static String BIN_DATA_STRING = "<w:binData.*?w:name=\"(.*?)\".*?</w:binData>";
   public static Pattern BIN_DATA_PATTERN =
      Pattern.compile(BIN_DATA_STRING, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern IMG_SRC_PATTERN =
      Pattern.compile("<v:imagedata.*?src=\"([^\"]+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

   public static Pattern FULL_PATTERN = Pattern.compile(
      "(" + BEGINFEATURE + ")|(" + ENDFEATURE + ")|(" + BEGINCONFIGGRP + ")|(" + ENDCONFIGGRP + ")|(" + BEGINCONFIG + ")|(" + ENDCONFIG + ")",
      Pattern.DOTALL | Pattern.MULTILINE);

   public static String EMPTY_LIST_REGEX =
      "<w:p wsp:rsidP=\"[^\"]*?\" wsp:rsidR=\"[^\"]*?\" wsp:rsidRDefault=\"[^\"]*?\"><w:pPr><w:pStyle w:val=\"[^\"]*?\"></w:pStyle><w:listPr><wx:t wx:val=\"([^>]*?)\"></wx:t><wx:font wx:val=\"[^\"]*?\"></wx:font></w:listPr></w:pPr><w:r><w:t></w:t></w:r></w:p>";

   private static final Pattern tagKiller =
      Pattern.compile("<.*?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final Pattern paragraphPattern =
      Pattern.compile("<w:p( .*?)?>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private static final String AML_ANNOTATION = "<.??aml:annotation.*?>";
   private static final String AML_CONTENT = "<.??aml:content.*?>";
   private static final String DELETIONS = "<w:delText>.*?</w:delText>";
   /**
    * The following static numbers are derived from the FULL_PATTERN compiled above
    */
   public final static int beginFeatureMatcherGroup = 1;
   public final static int beginConfigGroupMatcherGroup = 26;
   public final static int beginConfigMatcherGroup = 78;
   public final static int endFeatureMatcherGroup = 12;
   public final static int endConfigGroupMatcherGroup = 53;
   public final static int endConfigMatcherGroup = 100;
   public final static int endFeatureBracketMatcherGroup = 23;
   public final static int endConfigGroupBracketMatcherGroup = 75;
   public final static int endConfigBracketMatcherGroup = 117;

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

   public static boolean areApplicabilityTagsInvalid(String wordml, BranchId branch, HashCollection<String, String> validFeatureValues, Set<String> allValidConfigurations, Set<String> allValidConfigurationGroups) {

      Matcher matcher = FULL_PATTERN.matcher(wordml);
      Stack<ApplicabilityBlock> applicabilityBlocks = new Stack<>();
      int applicBlockCount = 0;

      while (matcher.find()) {
         String beginFeature = matcher.group(beginFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginFeatureMatcherGroup)) : null;
         String beginConfiguration = matcher.group(beginConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigMatcherGroup)) : null;
         String beginConfigurationGroup = matcher.group(beginConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(beginConfigGroupMatcherGroup)) : null;

         String endFeature = matcher.group(endFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endFeatureMatcherGroup)) : null;
         String endConfiguration = matcher.group(endConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigMatcherGroup)) : null;
         String endConfigurationGroup = matcher.group(endConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(endConfigGroupMatcherGroup)) : null;

         if (beginFeature != null && beginFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount += 1;
            applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Feature, beginFeature));
         } else if (beginConfiguration != null && beginConfiguration.toLowerCase().contains(CONFIGAPP)) {
            if (isValidConfigurationBracket(beginConfiguration, allValidConfigurations)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(createApplicabilityBlock(ApplicabilityType.Configuration, beginConfiguration));
            }
         } else if (beginConfigurationGroup != null && beginConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            if (isValidConfigurationGroupBracket(beginConfigurationGroup, allValidConfigurationGroups)) {
               applicBlockCount += 1;
               applicabilityBlocks.add(
                  createApplicabilityBlock(ApplicabilityType.ConfigurationGroup, beginConfigurationGroup));
            }
         } else if (endFeature != null && endFeature.toLowerCase().contains(FEATUREAPP)) {
            applicBlockCount -= 1;

            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidFeatureBlock(applicabilityBlocks.pop(), matcher, branch, validFeatureValues)) {
               return true;
            }

         } else if (endConfiguration != null && endConfiguration.toLowerCase().contains(CONFIGAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         } else if (endConfigurationGroup != null && endConfigurationGroup.toLowerCase().contains(CONFIGGRPAPP)) {
            applicBlockCount -= 1;
            if (applicabilityBlocks.isEmpty()) {
               return true;
            }

            if (isInvalidConfigurationGroupBlock(applicabilityBlocks.pop(), matcher)) {
               return true;
            }
         }
      }

      if (applicBlockCount != 0) {
         return true;
      }

      return false;
   }

   private static boolean isValidConfigurationBracket(String beginConfig, Set<String> allValidConfigurations) {
      beginConfig = WordCoreUtil.textOnly(beginConfig);
      int start = beginConfig.indexOf("[") + 1;
      int end = beginConfig.indexOf("]");
      String applicExpText = beginConfig.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurations.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isValidConfigurationGroupBracket(String beginConfigGroup, Set<String> allValidConfigurationGroups) {
      beginConfigGroup = WordCoreUtil.textOnly(beginConfigGroup);
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");

      for (String config : configs) {
         String configKey = config.split("=")[0].trim().toUpperCase();
         if (!allValidConfigurationGroups.contains(configKey)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isInvalidConfigurationBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.Configuration) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidConfigurationGroupBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher) {
      if (applicabilityBlock.getType() != ApplicabilityType.ConfigurationGroup) {
         return true;
      }

      return false;
   }

   private static boolean isInvalidFeatureBlock(ApplicabilityBlock applicabilityBlock, Matcher matcher, BranchId branch, HashCollection<String, String> validFeatureValues) {

      if (applicabilityBlock.getType() != ApplicabilityType.Feature) {
         return true;
      }
      String applicabilityExpression = applicabilityBlock.getApplicabilityExpression();

      if (isExpressionInvalid(applicabilityExpression, branch, validFeatureValues)) {
         return true;
      }

      return false;
   }

   private static ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock();
      beginApplic.setType(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }

   public static String textOnly(String str) {
      str = paragraphPattern.matcher(str).replaceAll(" ");
      str = tagKiller.matcher(str).replaceAll("").trim();
      return Xml.unescape(str).toString();
   }

   public static boolean isExpressionInvalid(String expression, BranchId branch, HashCollection<String, String> validFeatureValues) {
      ApplicabilityGrammarLexer lex = new ApplicabilityGrammarLexer(new ANTLRStringStream(expression.toUpperCase()));
      ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

      try {
         parser.start();
      } catch (RecognitionException ex) {
         return true;
      }

      HashMap<String, List<String>> featureIdValuesMap = parser.getIdValuesMap();

      for (String featureId : featureIdValuesMap.keySet()) {
         featureId = featureId.trim();
         if (validFeatureValues.containsKey(featureId.toUpperCase())) {
            List<String> values = featureIdValuesMap.get(featureId);
            if (values.contains("Default")) {
               continue;
            }
            Collection<String> validValues = validFeatureValues.getValues(featureId.toUpperCase());
            for (String val : values) {
               val = val.trim();
               if (val.equals("(") || val.equals(")") || val.equals("|") || val.equals("&")) {
                  continue;
               }
               if (!containsIgnoreCase(validValues, val)) {
                  return true;
               }
            }
         } else {
            return true;
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

   public static int endIndexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      if (matcher.find()) {
         toReturn = matcher.end();
      }

      return toReturn;
   }

   public static int lastIndexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      while (matcher.find()) {
         toReturn = matcher.start();
      }

      return toReturn;
   }

   public static int indexOf(String str, String regex) {
      int toReturn = -1;

      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(str);
      if (matcher.find()) {
         toReturn = matcher.start();
      }

      return toReturn;
   }
}
