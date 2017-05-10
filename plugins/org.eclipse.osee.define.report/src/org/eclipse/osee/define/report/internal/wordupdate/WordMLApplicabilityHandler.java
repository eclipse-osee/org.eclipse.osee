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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class WordMLApplicabilityHandler {

   public static String previewValidApplicabilityContent(OrcsApi orcsApi, String content, BranchId branch) throws OseeCoreException {
      Map<String, List<String>> featureValuesAllowed = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      featureValuesAllowed =
         orcsApi.getQueryFactory().applicabilityQuery().getBranchViewFeatureValues(branch, branch.getViewId());

      String toReturn = content;

      ArtifactReadable featureDefArt = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.FeatureDefinition).getResults().getExactlyOne();
      String featureDefJson = featureDefArt.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);

      Collection<String> configurationsAllowed = featureValuesAllowed.get("Config");
      Stack<ApplicabilityBlock> applicabilityBlocks = new Stack<>();

      Matcher matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);

      int searchIndex = 0;
      int applicBlockCount = 0;
      while (searchIndex < toReturn.length() && matcher.find(searchIndex)) {
         String beginFeature = matcher.group(1) != null ? WordCoreUtil.textOnly(matcher.group(1)) : null;
         String beginConfiguration = matcher.group(26) != null ? WordCoreUtil.textOnly(matcher.group(26)) : null;

         String endFeature = matcher.group(12) != null ? WordCoreUtil.textOnly(matcher.group(12)) : null;
         String endConfiguration = matcher.group(43) != null ? WordCoreUtil.textOnly(matcher.group(43)) : null;

         if (beginFeature != null && beginFeature.toLowerCase().contains(WordCoreUtil.FEATUREAPP)) {
            applicBlockCount += 1;
            searchIndex = addBeginApplicabilityBlock(ApplicabilityType.Feature, applicabilityBlocks, matcher,
               beginFeature, searchIndex);
         } else if (beginConfiguration != null && beginConfiguration.toLowerCase().contains(WordCoreUtil.CONFIGAPP)) {
            applicBlockCount += 1;
            searchIndex = addBeginApplicabilityBlock(ApplicabilityType.Configuration, applicabilityBlocks, matcher,
               beginConfiguration, searchIndex);
         } else if ((endFeature != null && endFeature.toLowerCase().contains(
            WordCoreUtil.FEATUREAPP)) || (endConfiguration != null && endConfiguration.toLowerCase().contains(
               WordCoreUtil.CONFIGAPP))) {
            applicBlockCount -= 1;

            ApplicabilityBlock applicabilityBlock = getFullApplicabilityBlock(applicabilityBlocks, matcher, toReturn);

            int endBracketGroup = applicabilityBlock.getType().equals(ApplicabilityType.Feature) ? 25 : 60;
            String optionalEndBracket =
               matcher.group(endBracketGroup) != null ? WordCoreUtil.textOnly(matcher.group(endBracketGroup)) : null;

            String toInsert = evaluateApplicabilityBlock(applicabilityBlock, optionalEndBracket, toReturn,
               featureDefJson, featureValuesAllowed, configurationsAllowed);

            String toReplace =
               toReturn.substring(applicabilityBlock.getStartInsertIndex(), applicabilityBlock.getEndInsertIndex());
            toReturn = toReturn.replace(toReplace, toInsert);
            searchIndex = applicabilityBlock.getStartInsertIndex() + toInsert.length();
            matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);
         } else {
            break;
         }
      }

      toReturn = removeEmptyLists(toReturn);
      if (applicBlockCount != 0) {
         throw new OseeCoreException("An applicability block of text is missing an End Feature/Configuration tag");
      }

      return toReturn;
   }

   private static String evaluateApplicabilityBlock(ApplicabilityBlock applicabilityBlock, String optionalEndBracket, String fullWordML, String featureDefJson, Map<String, List<String>> featureValuesAllowed, Collection<String> configurationsAllowed) {
      //Remove Logical Messages that were mistaken for optional end brackets
      if (optionalEndBracket != null && optionalEndBracket.contains(".")) {
         int originalEnd = applicabilityBlock.getEndInsertIndex();
         applicabilityBlock.setEndInsertIndex(originalEnd - optionalEndBracket.length());
      } else {
         Integer endTextIndex = applicabilityBlock.getEndTextIndex();
         Integer endInsertIndex = applicabilityBlock.getEndInsertIndex();
         String toCheck = fullWordML.substring(endTextIndex, endInsertIndex);

         int indexOf = WordCoreUtil.indexOf(toCheck, WordCoreUtil.LOGICAL_STRING);
         if (indexOf != -1) {
            applicabilityBlock.setEndInsertIndex(endTextIndex + indexOf);
         }
      }

      Map<String, String> binDataMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      saveBinData(applicabilityBlock.getFullText(), binDataMap);
      String toInsert = evaluateApplicabilityExpression(applicabilityBlock, featureDefJson, configurationsAllowed,
         featureValuesAllowed);
      toInsert = insertMissingbinData(toInsert, binDataMap);

      return toInsert;
   }

   private static String removeEmptyLists(String wordML) {
      return wordML.replaceAll(WordCoreUtil.EMPTY_LIST_REGEX, "");
   }

   private static String insertMissingbinData(String toInsert, Map<String, String> binDataMap) {
      String temp = toInsert;
      Matcher matcher = WordCoreUtil.IMG_SRC_PATTERN.matcher(temp);
      while (matcher.find()) {
         String srcId = matcher.group(1);
         if (binDataMap.containsKey(srcId)) {
            String binData = binDataMap.get(srcId);
            if (!temp.contains(binData)) {
               temp = binData + temp;
            }
         }
      }

      return temp;
   }

   private static void saveBinData(String fullText, Map<String, String> binDataMap) {
      Matcher matcher = WordCoreUtil.BIN_DATA_PATTERN.matcher(fullText);
      while (matcher.find()) {
         binDataMap.put(matcher.group(1), matcher.group(0));
      }
   }

   private static int addBeginApplicabilityBlock(ApplicabilityType type, Stack<ApplicabilityBlock> applicabilityBlocks, Matcher matcher, String applicabilityExpression, int searchIndex) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock();
      beginApplic.setType(type);
      //Remove extra space
      applicabilityExpression = applicabilityExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(applicabilityExpression);
      beginApplic.setStartInsertIndex(matcher.start());
      beginApplic.setStartTextIndex(matcher.end());
      applicabilityBlocks.push(beginApplic);
      searchIndex = matcher.end();

      return searchIndex;
   }

   private static ApplicabilityBlock getFullApplicabilityBlock(Stack<ApplicabilityBlock> applicabilityBlocks, Matcher matcher, String toReturn) {
      if (applicabilityBlocks.isEmpty()) {
         throw new OseeCoreException("An applicability block of text is missing a start Feature/Configuration tag");
      }
      ApplicabilityBlock applic = applicabilityBlocks.pop();
      applic.setEndInsertIndex(matcher.end());
      applic.setEndTextIndex(matcher.start());

      String insideText = toReturn.substring(applic.getStartTextIndex(), applic.getEndTextIndex());
      applic.setFullText(insideText);

      // Adjust start and end insert indicies if tags are inside a table
      if (!applic.getFullText().contains(WordCoreUtil.TABLE) && applic.getFullText().contains(
         WordCoreUtil.TABLE_CELL)) {
         String findStartOfRow = toReturn.substring(0, applic.getStartInsertIndex());
         int startRowIndex = findStartOfRow.lastIndexOf("<w:tr wsp:rsidR=");

         if (startRowIndex != -1) {
            // find end of row after the END configuration/feature tag
            String findEndOfRow = toReturn.substring(matcher.end());
            int endRowIndex = findEndOfRow.indexOf("</w:tr>");
            if (endRowIndex != -1) {
               endRowIndex = endRowIndex + matcher.end() + 7;
               String fullText = toReturn.substring(startRowIndex, endRowIndex);
               applic.setIsInTable(true);
               applic.setStartInsertIndex(startRowIndex);
               applic.setEndInsertIndex(startRowIndex + fullText.length());

               fullText = fullText.replaceAll(WordCoreUtil.ENDFEATURE + "|" + WordCoreUtil.ENDCONFIG, "");
               fullText = fullText.replaceAll(WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG, "");
               applic.setFullText(fullText);
            }
         }
      }

      return applic;
   }

   private static String evaluateApplicabilityExpression(ApplicabilityBlock applic, String featureDefJson, Collection<String> configurationsAllowed, Map<String, List<String>> featureValuesAllowed) {
      String applicabilityExpression = applic.getApplicabilityExpression();
      String toInsert = "";
      try {

         String fullText = applic.getFullText();

         ApplicabilityGrammarLexer lex =
            new ApplicabilityGrammarLexer(new ANTLRStringStream(applicabilityExpression.toUpperCase()));
         ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

         parser.start();

         if (applic.getType().equals(ApplicabilityType.Feature)) {
            toInsert = getValidFeatureContent(fullText, applic.isInTable(), parser.getIdValuesMap(),
               parser.getOperators(), featureDefJson, featureValuesAllowed);
         } else if (applic.getType().equals(ApplicabilityType.Configuration)) {
            toInsert = getValidConfigurationContent(fullText, parser.getIdValuesMap(), configurationsAllowed);
         }

      } catch (RecognitionException ex) {
         throw new OseeCoreException(
            "Failed to parse expression: " + applicabilityExpression + " at start Index: " + applic.getStartInsertIndex());
      }

      return toInsert;
   }

   public static String getValidConfigurationContent(String fullText, HashMap<String, List<String>> id_value_map, Collection<String> configurationsAllowed) {
      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {
         beginningText = fullText.substring(0, match.start());

         elseText = fullText.substring(match.end());
         elseText = elseText.replaceAll(WordCoreUtil.ENDCONFIG, "");
         elseText = elseText.replaceAll(WordCoreUtil.BEGINCONFIG, "");
      }

      String toReturn = "";

      // Note: this assumes only OR's are put in between configurations
      for (String id : id_value_map.keySet()) {
         boolean isIncluded = true;
         List<String> values = id_value_map.get(id);
         for (String val : values) {
            if (val.equalsIgnoreCase("excluded")) {
               isIncluded = false;
            }
         }

         if (containsIgnoreCase(configurationsAllowed, id) == isIncluded) {
            toReturn = beginningText;
            break;
         }
      }

      return toReturn;
   }

   private static String getValidFeatureContent(String fullText, boolean isInTable, HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators, String featureDefJson, Map<String, List<String>> featureValuesAllowed) {
      ScriptEngineManager sem = new ScriptEngineManager();
      ScriptEngine se = sem.getEngineByName("JavaScript");

      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {

         if (isInTable) {
            String temp = fullText.substring(0, match.end());
            // Find last occurence of table row
            int lastIndexOf = temp.lastIndexOf("<w:tr wsp:rsidR=");
            if (lastIndexOf != -1) {
               elseText = fullText.substring(lastIndexOf);
               elseText = elseText.replaceAll(WordCoreUtil.ELSE_EXP, "");
               beginningText = fullText.substring(0, lastIndexOf);
            }
         } else {
            beginningText = fullText.substring(0, match.start());
            elseText = fullText.substring(match.end());
         }

         elseText = elseText.replaceAll(WordCoreUtil.ENDFEATURE, "");
         elseText = elseText.replaceAll(WordCoreUtil.BEGINFEATURE, "");
      }

      String toReturn = "";
      String expression =
         createFeatureExpression(featureIdValuesMap, featureOperators, featureDefJson, featureValuesAllowed);

      boolean result = false;
      try {
         result = (boolean) se.eval(expression);
      } catch (ScriptException ex) {
         throw new OseeCoreException("Failed to parse expression: " + expression);
      }

      if (result) {
         toReturn = beginningText;
      } else {
         toReturn = elseText;
      }

      return toReturn;
   }

   private static String createFeatureExpression(HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators, String featureDefJson, Map<String, List<String>> featureValuesAllowed) {
      ScriptEngineManager sem = new ScriptEngineManager();
      ScriptEngine se = sem.getEngineByName("JavaScript");

      String myFeatureExpression = "";
      Iterator<String> iterator = featureOperators.iterator();

      for (String feature : featureIdValuesMap.keySet()) {
         List<String> values = featureIdValuesMap.get(feature);

         String valueExpression = createValueExpression(feature, values, featureDefJson, featureValuesAllowed);

         boolean result = false;

         try {
            result = (boolean) se.eval(valueExpression);
         } catch (ScriptException ex) {
            throw new OseeCoreException("Failed to parse expression: " + valueExpression);
         }

         myFeatureExpression += result + " ";

         if (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals("|")) {
               myFeatureExpression += "|| ";
            } else if (next.equals("&")) {
               myFeatureExpression += "&& ";
            }
         }
      }

      return myFeatureExpression;
   }

   private static String createValueExpression(String feature, List<String> values, String featureDefJson, Map<String, List<String>> featureValuesAllowed) {
      String myValueExpression = "";
      for (String value : values) {
         if (value.equals("(")) {
            myValueExpression += "( ";
         } else if (value.equals(")")) {
            myValueExpression += ") ";
         } else if (value.equals("|")) {
            myValueExpression += "|| ";
         } else if (value.equals("&")) {
            myValueExpression += "&& ";
         } else {
            boolean eval = isFeatureValuePairValid(feature, value, featureDefJson, featureValuesAllowed);
            myValueExpression += eval + " ";
         }
      }

      return myValueExpression;
   }

   private static boolean isFeatureValuePairValid(String feature, String value, String featureDefJson, Map<String, List<String>> featureValuesAllowed) {
      if (featureValuesAllowed.containsKey(feature)) {
         Collection<String> validValues = featureValuesAllowed.get(feature);

         value = value.equalsIgnoreCase("Default") ? getDefaultValue(feature, featureDefJson) : value;

         if (containsIgnoreCase(validValues, value)) {
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

   private static String getDefaultValue(String feature, String featureDefJson) {
      String toReturn = null;
      try {
         ObjectMapper mapper = new ObjectMapper();
         FeatureDefinitionData[] featDataList = mapper.readValue(featureDefJson, FeatureDefinitionData[].class);

         for (FeatureDefinitionData featData : featDataList) {
            if (featData.getName().equalsIgnoreCase(feature)) {
               toReturn = featData.getDefaultValue();
               break;
            }
         }
      } catch (Exception e) {
         throw new OseeCoreException("Error getting default value for feature: " + feature);
      }

      return toReturn;
   }
}
