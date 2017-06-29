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
import java.util.Map.Entry;
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class WordMLApplicabilityHandler {

   private static String SCRIPT_ENGINE_NAME = "JavaScript";

   private Map<String, List<String>> viewApplicabilitiesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final Collection<String> configurationsAllowed;
   private final Stack<ApplicabilityBlock> applicBlocks;
   private final String featureDefinitionJson;
   private final ScriptEngine se;
   private final Log logger;

   public WordMLApplicabilityHandler(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactId view) {
      this.applicBlocks = new Stack<>();
      this.logger = logger;

      ScriptEngineManager sem = new ScriptEngineManager();
      se = sem.getEngineByName(SCRIPT_ENGINE_NAME);

      BranchId branchToUse = getBranchToUse(orcsApi, branch, view);

      viewApplicabilitiesMap =
         orcsApi.getQueryFactory().applicabilityQuery().getBranchViewFeatureValues(branchToUse, view);
      configurationsAllowed = viewApplicabilitiesMap.get("config");

      ArtifactReadable featureDefArt = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.FeatureDefinition).getResults().getExactlyOne();
      featureDefinitionJson = featureDefArt.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
   }

   public String previewValidApplicabilityContent(String content) throws OseeCoreException {
      String toReturn = content;
      int searchIndex = 0;
      int applicBlockCount = 0;

      Matcher matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);

      while (searchIndex < toReturn.length() && matcher.find(searchIndex)) {
         String beginFeature = matcher.group(1) != null ? matcher.group(1) : null;
         String beginConfig = matcher.group(26) != null ? matcher.group(26) : null;

         String endFeature = matcher.group(12) != null ? WordCoreUtil.textOnly(matcher.group(12)).toLowerCase() : null;
         String endConfig = matcher.group(43) != null ? WordCoreUtil.textOnly(matcher.group(43)).toLowerCase() : null;

         if (beginFeature != null && WordCoreUtil.textOnly(beginFeature).toLowerCase().contains(
            WordCoreUtil.FEATUREAPP)) {
            applicBlockCount += 1;
            searchIndex =
               addApplicabilityBlock(ApplicabilityType.Feature, matcher, beginFeature, searchIndex, toReturn);

         } else if (beginConfig != null && WordCoreUtil.textOnly(beginConfig).toLowerCase().contains(
            WordCoreUtil.CONFIGAPP)) {
            applicBlockCount += 1;
            searchIndex =
               addApplicabilityBlock(ApplicabilityType.Configuration, matcher, beginConfig, searchIndex, toReturn);

         } else if ((endFeature != null && endFeature.contains(
            WordCoreUtil.FEATUREAPP)) || (endConfig != null && endConfig.contains(WordCoreUtil.CONFIGAPP))) {

            ApplicabilityBlock applicabilityBlock = getFullApplicabilityBlock(matcher, toReturn);

            if (applicabilityBlock == null) {
               searchIndex = matcher.end();
            } else {
               applicBlockCount -= 1;
               String toInsert = evaluateApplicabilityBlock(applicabilityBlock, toReturn);
               String toReplace =
                  toReturn.substring(applicabilityBlock.getStartInsertIndex(), applicabilityBlock.getEndInsertIndex());
               toReturn = toReturn.replace(toReplace, toInsert);
               searchIndex =
                  applicabilityBlock.getStartInsertIndex() + (applicabilityBlock.isInTable() ? 0 : toInsert.length());
               matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);
            }
         } else {
            break;
         }
      }

      toReturn = removeEmptyLists(toReturn);
      if (applicBlockCount != 0) {
         logger.error("An applicability block of text is missing an End Feature/Configuration tag");
      }

      return toReturn;
   }

   private String removeExtraParagraphs(String fullWordMl, String toInsert, ApplicabilityBlock applicabilityBlock) {
      int startInsertIndex = applicabilityBlock.getStartInsertIndex();

      if (!applicabilityBlock.isInTable() && (toInsert.isEmpty() || toInsert.startsWith(
         WordCoreUtil.WHOLE_END_PARAGRAPH))) {
         String findParagraphStart = fullWordMl.substring(0, startInsertIndex);
         int paragraphStartIndex = findParagraphStart.lastIndexOf(WordCoreUtil.START_PARAGRAPH);

         // check this doesn't contain feature/config tags
         String beginningText = fullWordMl.substring(paragraphStartIndex, startInsertIndex);

         if (toInsert.isEmpty() && paragraphStartIndex >= 0 && !beginningText.matches(
            "(?i).*?(" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG + "|" + WordCoreUtil.ENDCONFIG + "|" + WordCoreUtil.ENDFEATURE + ").*?")) {
            int endInsertIndex = applicabilityBlock.getEndInsertIndex();
            String findParagraphEnd = fullWordMl.substring(endInsertIndex);

            int paragraphEndIndex = findParagraphEnd.indexOf(WordCoreUtil.END_PARAGRAPH) + endInsertIndex + 6;
            if (paragraphEndIndex >= 0) {
               applicabilityBlock.setStartInsertIndex(paragraphStartIndex);
               applicabilityBlock.setStartTextIndex(paragraphStartIndex);
               applicabilityBlock.setEndInsertIndex(paragraphEndIndex);
               applicabilityBlock.setEndTextIndex(paragraphEndIndex);
            }

            // check this doesn't contain feature/config tags
            String endText = fullWordMl.substring(endInsertIndex, paragraphEndIndex);

            if (paragraphEndIndex >= 0 && !endText.matches(
               "(?i).*?(" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG + "|" + WordCoreUtil.ENDCONFIG + "|" + WordCoreUtil.ENDFEATURE + ").*?")) {
               applicabilityBlock.setStartInsertIndex(paragraphStartIndex);
               applicabilityBlock.setStartTextIndex(paragraphStartIndex);
               applicabilityBlock.setEndInsertIndex(paragraphEndIndex);
               applicabilityBlock.setEndTextIndex(paragraphEndIndex);
            }
         } else {
            String findParagraphEnd = fullWordMl.substring(startInsertIndex);
            int paragraphEndIndex = findParagraphEnd.indexOf(WordCoreUtil.END_PARAGRAPH) + startInsertIndex + 6;

            if (paragraphStartIndex >= 0 && paragraphEndIndex >= 0 && paragraphEndIndex > paragraphStartIndex) {
               String fullParagraph = fullWordMl.substring(paragraphStartIndex, paragraphEndIndex);
               fullParagraph =
                  fullParagraph.replaceFirst("(?i)" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG, "");

               if (WordCoreUtil.textOnly(fullParagraph).isEmpty()) {
                  toInsert = toInsert.replaceFirst(WordCoreUtil.WHOLE_END_PARAGRAPH, "");
                  applicabilityBlock.setStartInsertIndex(paragraphStartIndex);
                  applicabilityBlock.setStartTextIndex(paragraphEndIndex);
               }
            }
         }
      }

      if (!applicabilityBlock.isInTable() && toInsert.matches(
         ".*?<w:p wsp:rsid[^>]+><w:pPr><w:spacing w:after=[^>]+></w:spacing></w:pPr><w:r><w:t>$")) {

         int origLength = toInsert.length();
         int lastParaIndex = toInsert.lastIndexOf(WordCoreUtil.START_PARAGRAPH);
         if (lastParaIndex >= 0) {
            toInsert = toInsert.substring(0, lastParaIndex);
            applicabilityBlock.setEndTextIndex(applicabilityBlock.getEndTextIndex() - (origLength - lastParaIndex));
            applicabilityBlock.setEndInsertIndex(
               applicabilityBlock.getEndInsertIndex() + WordCoreUtil.WHOLE_END_PARAGRAPH.length());
         }
      }

      return toInsert;
   }

   // End Bracket can contain multiple feature/value pairs
   private boolean isValidEndBracket(String optionalEndBracket) {
      String text = WordCoreUtil.textOnly(optionalEndBracket);
      text = text.replaceAll("\\[", "");
      text = text.replaceAll("\\]", "").trim();

      // Split on ORs and ANDs
      String[] featureValueStrings = text.split("\\||&");
      for (String featureValueString : featureValueStrings) {
         String[] split = featureValueString.split("=");
         String featName = split[0].trim();
         String featVal = split.length > 1 ? split[1].trim() : null;

         if (viewApplicabilitiesMap.containsKey(featName)) {
            List<String> values = viewApplicabilitiesMap.get(featName);
            if (featVal != null && !containsIgnoreCase(values, featVal)) {
               return false;
            }
         } else {
            return false;
         }
      }

      return true;
   }

   private String evaluateApplicabilityBlock(ApplicabilityBlock applicabilityBlock, String fullWordML) {
      Map<String, String> binDataMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      saveBinData(applicabilityBlock.getFullText(), binDataMap);

      String toInsert = evaluateApplicabilityExpression(applicabilityBlock);
      toInsert = insertMissingbinData(toInsert, binDataMap);
      toInsert = removeExtraParagraphs(fullWordML, toInsert, applicabilityBlock);

      return toInsert;
   }

   private String removeEmptyLists(String wordML) {
      return wordML.replaceAll(WordCoreUtil.EMPTY_LIST_REGEX, "");
   }

   private String insertMissingbinData(String toInsert, Map<String, String> binDataMap) {
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

   private void saveBinData(String fullText, Map<String, String> binDataMap) {
      Matcher matcher = WordCoreUtil.BIN_DATA_PATTERN.matcher(fullText);
      while (matcher.find()) {
         binDataMap.put(matcher.group(1), matcher.group(0));
      }
   }

   private int addApplicabilityBlock(ApplicabilityType type, Matcher matcher, String applicabilityExpression, int searchIndex, String fullWordMl) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock();
      beginApplic.setType(type);
      //Remove extra space
      String applicExpText = WordCoreUtil.textOnly(applicabilityExpression).toLowerCase().replace(" [", "[");
      beginApplic.setApplicabilityExpression(applicExpText);
      beginApplic.setStartInsertIndex(matcher.start());
      beginApplic.setStartTextIndex(matcher.end());
      applicBlocks.push(beginApplic);
      searchIndex = matcher.end();

      return searchIndex;
   }

   private ApplicabilityBlock getFullApplicabilityBlock(Matcher matcher, String toReturn) {
      if (applicBlocks.isEmpty()) {
         logger.error("An applicability block of text is missing a start Feature/Configuration tag");
         return null;
      }
      ApplicabilityBlock applic = applicBlocks.pop();

      // set end insert index - Check if End Bracket is valid
      int endBracketGroup = applic.getType().equals(ApplicabilityType.Feature) ? 23 : 60;
      String optionalEndBracket = matcher.group(endBracketGroup) != null ? matcher.group(endBracketGroup) : null;

      if (optionalEndBracket != null && !isValidEndBracket(optionalEndBracket)) {
         int newEndInsertIndex = matcher.end() - optionalEndBracket.length();
         applic.setEndInsertIndex(newEndInsertIndex);
      } else {
         applic.setEndInsertIndex(matcher.end());
      }
      applic.setEndTextIndex(matcher.start());

      String insideText = toReturn.substring(applic.getStartTextIndex(), applic.getEndTextIndex());
      applic.setFullText(insideText);

      // Adjust start and end insert indicies if tags are inside a table
      if (!applic.getFullText().contains(WordCoreUtil.TABLE) && applic.getFullText().contains(
         WordCoreUtil.TABLE_CELL)) {
         String findStartOfRow = toReturn.substring(0, applic.getStartInsertIndex());
         int startRowIndex = findStartOfRow.lastIndexOf(WordCoreUtil.START_TABLE_ROW);

         if (startRowIndex != -1) {
            // find end of row after the END configuration/feature tag
            String findEndOfRow = toReturn.substring(matcher.end());
            int endRowIndex = findEndOfRow.indexOf(WordCoreUtil.END_TABLE_ROW);
            if (endRowIndex != -1) {
               endRowIndex = endRowIndex + matcher.end() + 7;
               String fullText = toReturn.substring(startRowIndex, endRowIndex);
               applic.setIsInTable(true);
               applic.setStartInsertIndex(startRowIndex);
               applic.setEndInsertIndex(startRowIndex + fullText.length());

               fullText =
                  fullText.replaceFirst("(?i)(" + WordCoreUtil.ENDFEATURE + "|" + WordCoreUtil.ENDCONFIG + ")", "");
               fullText =
                  fullText.replaceFirst("(?i)(" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG + ")", "");
               applic.setFullText(fullText);
            }
         }
      }

      return applic;
   }

   private static BranchId getBranchToUse(OrcsApi orcsApi, BranchId branch, ArtifactId viewId) {
      BranchId branchView = findBranchView(orcsApi, viewId);
      return branchView == null ? branch : branchView;
   }

   private static BranchId findBranchView(OrcsApi orcsApi, ArtifactId viewId) {
      BranchId branchToUse = null;
      boolean foundBranchView = false;
      List<BranchViewData> views = orcsApi.getQueryFactory().applicabilityQuery().getViews();
      for (BranchViewData viewData : views) {
         List<ArtifactId> branchViews = viewData.getBranchViews();
         for (ArtifactId id : branchViews) {
            if (viewId.equals(id)) {
               branchToUse = viewData.getBranch();
               foundBranchView = true;
               break;
            }
         }
         if (foundBranchView) {
            break;
         }
      }
      return branchToUse;
   }

   private String evaluateApplicabilityExpression(ApplicabilityBlock applic) {
      String applicabilityExpression = applic.getApplicabilityExpression();
      String toInsert = "";
      try {

         String fullText = applic.getFullText();

         ApplicabilityGrammarLexer lex =
            new ApplicabilityGrammarLexer(new ANTLRStringStream(applicabilityExpression.toUpperCase()));
         ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

         parser.start();

         if (applic.getType().equals(ApplicabilityType.Feature)) {
            toInsert =
               getValidFeatureContent(fullText, applic.isInTable(), parser.getIdValuesMap(), parser.getOperators());
         } else if (applic.getType().equals(ApplicabilityType.Configuration)) {
            toInsert = getValidConfigurationContent(fullText, parser.getIdValuesMap());
         }

      } catch (RecognitionException ex) {
         logger.error(
            "Failed to parse expression: " + applicabilityExpression + " at start Index: " + applic.getStartInsertIndex());
      }

      return toInsert;
   }

   public String getValidConfigurationContent(String fullText, HashMap<String, List<String>> id_value_map) {
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
      List<String> values = id_value_map.get(configurationsAllowed.iterator().next().toUpperCase());
      if (values != null) {
         String value = values.get(0);
         if (!value.toLowerCase().equals("excluded")) {
            toReturn = beginningText;
         }
      } else {
         boolean isExcluded = false;
         for (Entry<String, List<String>> entry : id_value_map.entrySet()) {
            List<String> value = entry.getValue();
            isExcluded = value.get(0).toLowerCase().equals("excluded") ? true : false;
            if (!isExcluded) {
               break;
            }
         }
         toReturn = isExcluded ? beginningText : "";
      }
      return toReturn;
   }

   private String getValidFeatureContent(String fullText, boolean isInTable, HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators) {

      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {

         if (isInTable) {
            String temp = fullText.substring(0, match.end());
            // Find last occurence of table row
            int lastIndexOf = temp.lastIndexOf(WordCoreUtil.START_TABLE_ROW);
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
      String expression = createFeatureExpression(featureIdValuesMap, featureOperators);

      boolean result = false;
      try {
         result = (boolean) se.eval(expression);
      } catch (ScriptException ex) {
         logger.error("Failed to parse expression: " + expression);
      }

      if (result) {
         toReturn = beginningText;
      } else {
         toReturn = elseText;
      }

      return toReturn;
   }

   private String createFeatureExpression(HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators) {

      String myFeatureExpression = "";
      Iterator<String> iterator = featureOperators.iterator();

      for (String feature : featureIdValuesMap.keySet()) {
         List<String> values = featureIdValuesMap.get(feature);

         String valueExpression = createValueExpression(feature, values);

         boolean result = false;

         try {
            result = (boolean) se.eval(valueExpression);
         } catch (ScriptException ex) {
            logger.error("Failed to parse expression: " + valueExpression);
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

   private String createValueExpression(String feature, List<String> values) {
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
            boolean eval = isFeatureValuePairValid(feature, value);
            myValueExpression += eval + " ";
         }
      }

      return myValueExpression;
   }

   private boolean isFeatureValuePairValid(String feature, String value) {
      if (viewApplicabilitiesMap.containsKey(feature)) {
         Collection<String> validValues = viewApplicabilitiesMap.get(feature);

         value = value.equalsIgnoreCase("Default") ? getDefaultValue(feature) : value;

         if (containsIgnoreCase(validValues, value)) {
            return true;
         }
      }

      return false;
   }

   private boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }

   private String getDefaultValue(String feature) {
      String toReturn = null;
      try {
         ObjectMapper mapper = new ObjectMapper();
         FeatureDefinitionData[] featDataList = mapper.readValue(featureDefinitionJson, FeatureDefinitionData[].class);

         for (FeatureDefinitionData featData : featDataList) {
            if (featData.getName().equalsIgnoreCase(feature)) {
               toReturn = featData.getDefaultValue();
               break;
            }
         }
      } catch (Exception e) {
         logger.error("Error getting default value for feature: " + feature);
      }

      return toReturn;
   }
}
