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
package org.eclipse.osee.define.report.internal.wordupdate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class WordMLApplicabilityHandler {

   private static String FEATUREAPP = "feature";
   private static String CONFIGAPP = "config";

   private static String MAX_TAG_OCCURENCE = "30";
   private static String WORD_ML_TAGS = "(\\<[^>]*?>){0," + MAX_TAG_OCCURENCE + "}";

   private static String TABLE_CELL = "<w:tc>";
   private static String TABLE = "<w:tbl>";

   public static String END = "E" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "d ?" + WORD_ML_TAGS + " ?";
   public static String ELSE = "E" + WORD_ML_TAGS + "l" + WORD_ML_TAGS + "s" + WORD_ML_TAGS + "e ?";
   public static String FEATURE =
      "F" + WORD_ML_TAGS + "e" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "e";
   public static String CONFIG =
      "C" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n" + WORD_ML_TAGS + "f" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "g" + WORD_ML_TAGS + "u" + WORD_ML_TAGS + "r" + WORD_ML_TAGS + "a" + WORD_ML_TAGS + "t" + WORD_ML_TAGS + "i" + WORD_ML_TAGS + "o" + WORD_ML_TAGS + "n";

   public static String ENDBRACKETS = WORD_ML_TAGS + "(\\[(.*?)\\]) ?";
   public static String OPTIONAL_ENDBRACKETS = " ?(" + WORD_ML_TAGS + "(\\[.*?\\]))?";
   public static String BEGINFEATURE = FEATURE + ENDBRACKETS;
   public static String ENDFEATURE = END + FEATURE + OPTIONAL_ENDBRACKETS;
   public static String BEGINCONFIG = CONFIG + ENDBRACKETS;
   public static String ENDCONFIG = END + CONFIG + OPTIONAL_ENDBRACKETS;

   public static String LOGICAL_STRING = WORD_ML_TAGS + " ?(LM|ID).*?";

   public static Pattern LOGICAL_PATTERN =
      Pattern.compile(LOGICAL_STRING, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern FEATURE_CONFIG_PATTERN =
      Pattern.compile("(" + BEGINFEATURE + "(.*?)" + ENDFEATURE + ")|(" + BEGINCONFIG + "(.*?)" + ENDCONFIG + ")",
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern ELSE_PATTERN = Pattern.compile("(" + FEATURE + "|" + CONFIG + ")" + " " + ELSE,
      Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   public static Pattern TABLE_PATTERN =
      Pattern.compile("<w:tbl>(.*?)</w:tbl>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   public static Pattern TABLE_ROW_PATTERN =
      Pattern.compile("<w:tr wsp:rsidR=\".*?\" wsp:rsidRPr=\".*?\" wsp:rsidTr=\".*?\">(.*?)</w:tr>",
         Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   public static Pattern TABLE_CELL_PATTERN =
      Pattern.compile("<w:tc>(.*?)</w:tc>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

   private static int startSearchIndex = 0;
   private static int startReplaceIndex = 0;
   private static int endReplaceIndex = 0;

   public static String previewValidApplicabilityContent(OrcsApi orcsApi, String content, BranchId branch) {
      String toReturn = content;
      startReplaceIndex = 0;
      endReplaceIndex = 0;
      startSearchIndex = 0;

      HashCollection<String, String> featureValuesAllowed =
         orcsApi.getQueryFactory().applicabilityQuery().getBranchViewFeatureValues(branch, branch.getViewId());

      String configuration = featureValuesAllowed.getValues("Config").iterator().next();

      ApplicabilityExpression appExp = new ApplicabilityExpression(configuration, featureValuesAllowed);

      // need to do this to make sure index keeps getting updated for adding and removing content
      while (startSearchIndex < toReturn.length()) {
         String toSearch = toReturn.substring(startSearchIndex);

         String applicabilityContent = findNextApplicability(toSearch);

         if (applicabilityContent == null) {
            break;
         }

         String plainText = WordUtilities.textOnly(applicabilityContent);
         String plExpression = plainText.substring(0, plainText.indexOf("]") + 1);
         toReturn = parseExpression(orcsApi, appExp, plExpression, applicabilityContent, toReturn, branch);
      }

      return toReturn;
   }

   private static String parseExpression(OrcsApi orcsApi, ApplicabilityExpression featureAppExp, String plExpression, String contentBlock, String toReturn, BranchId branch) {
      String validContent = null;
      try {
         ApplicabilityGrammarLexer lex = new ApplicabilityGrammarLexer(new ANTLRStringStream(plExpression));
         ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));
         parser.start();

         String applicabilityType = parser.getApplicabilityType();

         if (applicabilityType.equals(FEATUREAPP)) {
            ArtifactReadable featureDefArt = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
               CoreArtifactTypes.FeatureDefinition).getResults().getExactlyOne();

            validContent = featureAppExp.getValidFeatureContent(contentBlock, parser.getFeatureIdValuesMap(),
               parser.getFeatureOperators(), featureDefArt);
         } else if (applicabilityType.equals(CONFIGAPP)) {
            validContent = featureAppExp.getValidConfigurationContent(contentBlock, parser.getConfigIds());
         }

      } catch (RecognitionException ex) {
         throw new OseeCoreException(
            "Failed to parse expression: " + plExpression + " at start Index: " + startReplaceIndex);
      }

      if (validContent != null) {
         String toReplace = toReturn.substring(startReplaceIndex, endReplaceIndex);
         toReturn = toReturn.replace(toReplace, validContent);
         startSearchIndex = startReplaceIndex + validContent.length();
      } else {
         String toReplace = toReturn.substring(startReplaceIndex, endReplaceIndex);
         toReturn = toReturn.replace(toReplace, "");
         startSearchIndex = startReplaceIndex;
      }

      return toReturn;
   }

   private static String findNextApplicability(String toSearch) {
      String toReturn = null;

      Matcher match = FEATURE_CONFIG_PATTERN.matcher(toSearch);

      if (match.find()) {
         // If match contains the table tag, the Feature/Config is around entire table and not just a row so no special parsing needed
         if (!match.group(0).contains(TABLE) && match.group(0).contains(TABLE_CELL)) {
            String findStartOfRow = toSearch.substring(0, match.start());
            int startRowIndex = findStartOfRow.lastIndexOf("<w:tr wsp:rsidR=");

            if (startRowIndex != -1) {

               String findEndOfRow = toSearch.substring(startRowIndex);
               int endRowIndex = findEndOfRow.indexOf("</w:tr>");
               if (endRowIndex != -1) {
                  endRowIndex = endRowIndex + startRowIndex + 7;
                  toReturn = toSearch.substring(startRowIndex, endRowIndex);
                  startReplaceIndex = startRowIndex + startSearchIndex;
                  endReplaceIndex = startReplaceIndex + toReturn.length();
               }
            }
         } else {
            //this is End Feature optional bracket
            int actualEnd = match.end();
            // Group 21 is ending brackets for features, Group 56 is ending brackets for Configuration
            String endBracket = null;
            int endIndex = -1;
            int startIndex = -1;
            if (match.group(21) != null) {
               endBracket = match.group(21);
               endIndex = match.end(21);
               startIndex = match.start(21);
            } else if (match.group(56) != null) {
               endBracket = match.group(56);
               endIndex = match.end(56);
               startIndex = match.start(56);
            }
            if (endBracket != null) {
               String endBracketText = WordUtilities.textOnly(endBracket);

               // Don't include because it is not a feature/configuration tag
               if (endBracketText.contains(".") || toSearch.substring(endIndex).matches(LOGICAL_STRING)) {
                  actualEnd = startIndex;
               }
            }
            int e = match.group().length() - (match.end() - actualEnd);
            toReturn = match.group(0).substring(0, e);
            startReplaceIndex = match.start() + startSearchIndex;
            endReplaceIndex = actualEnd + startSearchIndex;
         }
      }

      return toReturn;
   }
}
