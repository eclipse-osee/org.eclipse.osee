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

package org.eclipse.osee.define.rest.internal.wordupdate;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public class WordMLApplicabilityHandler {

   private final Set<String> validConfigurations;
   private final Set<String> validConfigurationGroups;
   private final Map<String, List<String>> viewApplicabilitiesMap;
   private final Stack<ApplicabilityBlock> applicBlocks;
   private final Log logger;
   private final OrcsApplicability applicabilityOps;
   private final BranchId branch;
   private final ArtifactToken view;

   public WordMLApplicabilityHandler(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactId view) {
      this.applicBlocks = new Stack<>();
      this.logger = logger;
      applicabilityOps = orcsApi.getApplicabilityOps();

      QueryFactory query = orcsApi.getQueryFactory();

      this.branch = getProductLineBranch(query, branch);
      validConfigurations = getValidConfigurations(query, this.branch);
      validConfigurationGroups = getValidConfigurationGroups(query, this.branch);
      this.view = query.fromBranch(this.branch).andId(view).asArtifactToken();
      viewApplicabilitiesMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(this.branch, view);

   }

   public static BranchId getProductLineBranch(QueryFactory query, BranchId branch) {
      Branch br = query.branchQuery().andId(branch).getResults().getExactlyOne();
      if (br.getBranchType().equals(BranchType.MERGE)) {
         branch = br.getParentBranch();
      }
      return branch;
   }

   public String previewValidApplicabilityContent(String content) {
      String toReturn = content;
      int searchIndex = 0;
      int applicBlockCount = 0;

      Matcher matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);

      while (searchIndex < toReturn.length() && matcher.find(searchIndex)) {
         String beginFeature = matcher.group(WordCoreUtil.beginFeatureMatcherGroup);
         String beginConfigGroup = matcher.group(WordCoreUtil.beginConfigGroupMatcherGroup);
         String beginConfig = matcher.group(WordCoreUtil.beginConfigMatcherGroup);

         String endFeature = matcher.group(WordCoreUtil.endFeatureMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(WordCoreUtil.endFeatureMatcherGroup)).toLowerCase() : null;
         String endConfigGroup = matcher.group(WordCoreUtil.endConfigGroupMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(WordCoreUtil.endConfigGroupMatcherGroup)).toLowerCase() : null;
         String endConfig = matcher.group(WordCoreUtil.endConfigMatcherGroup) != null ? WordCoreUtil.textOnly(
            matcher.group(WordCoreUtil.endConfigMatcherGroup)).toLowerCase() : null;

         if (beginFeature != null && WordCoreUtil.textOnly(beginFeature).toLowerCase().contains(
            WordCoreUtil.FEATUREAPP)) {
            applicBlockCount += 1;
            searchIndex =
               addApplicabilityBlock(ApplicabilityType.Feature, matcher, beginFeature, searchIndex, toReturn);

         } else if (beginConfig != null && WordCoreUtil.textOnly(beginConfig).toLowerCase().contains(
            WordCoreUtil.CONFIGAPP)) {
            if (isValidConfigurationBracket(beginConfig)) {
               applicBlockCount += 1;
               ApplicabilityType type = ApplicabilityType.Configuration;
               if (beginConfig.contains("Not")) {
                  type = ApplicabilityType.NotConfiguration;
               }
               searchIndex = addApplicabilityBlock(type, matcher, beginConfig, searchIndex, toReturn);
            } else {
               searchIndex = matcher.end();
            }

         } else if (beginConfigGroup != null && WordCoreUtil.textOnly(beginConfigGroup).toLowerCase().contains(
            WordCoreUtil.CONFIGGRPAPP)) {
            if (isValidConfigurationGroupBracket(beginConfigGroup)) {
               applicBlockCount += 1;
               ApplicabilityType type = ApplicabilityType.ConfigurationGroup;
               if (beginConfigGroup.contains("Not")) {
                  type = ApplicabilityType.NotConfigurationGroup;
               }
               searchIndex = addApplicabilityBlock(type, matcher, beginConfigGroup, searchIndex, toReturn);
            } else {
               searchIndex = matcher.end();
            }

         } else if (endFeature != null && endFeature.contains(
            WordCoreUtil.FEATUREAPP) || endConfig != null && endConfig.contains(
               WordCoreUtil.CONFIGAPP) || endConfigGroup != null && endConfigGroup.contains(
                  WordCoreUtil.CONFIGGRPAPP)) {

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

      toReturn = WordCoreUtil.removeEmptyLists(toReturn).toString();

      if (applicBlockCount != 0) {
         logger.error("An applicability block of text is missing an End Feature/Configuration tag");
      }

      return toReturn;
   }

   private boolean isValidConfigurationBracket(String beginConfig) {
      beginConfig = WordCoreUtil.textOnly(beginConfig);
      int start = beginConfig.indexOf("[") + 1;
      int end = beginConfig.indexOf("]");
      String applicExpText = beginConfig.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");
      for (int i = 0; i < configs.length; i++) {
         configs[i] = configs[i].split("=")[0].trim();
         if (!Strings.containsIgnoreCase(validConfigurations, configs[i])) {
            return false;
         }
      }

      return true;
   }

   private boolean isValidConfigurationGroupBracket(String beginConfigGroup) {
      beginConfigGroup = WordCoreUtil.textOnly(beginConfigGroup);
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);

      String[] configGroups = applicExpText.split("&|\\|");
      for (int i = 0; i < configGroups.length; i++) {
         configGroups[i] = configGroups[i].split("=")[0].trim();
         if (!Strings.containsIgnoreCase(validConfigurationGroups, configGroups[i])) {
            return false;
         }
      }

      return true;
   }

   // End Bracket can contain multiple feature/value pairs
   private boolean isValidFeatureBracket(String optionalEndBracket) {
      String text = WordCoreUtil.textOnly(optionalEndBracket);
      text = text.replaceAll("\\[", "");
      text = text.replaceAll("\\]", "").trim();

      // Split on ORs and ANDs
      String[] featureValueStrings = text.split("\\||&");
      for (String featureValueString : featureValueStrings) {
         String[] split = featureValueString.split("=");
         String featName = split[0].trim().toUpperCase();
         String featVal = split.length > 1 ? split[1].trim() : null;

         if (viewApplicabilitiesMap.containsKey(featName)) {
            List<String> values = viewApplicabilitiesMap.get(featName);
            if (featVal != null && !Strings.containsIgnoreCase(values, featVal)) {
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
      saveBinData(applicabilityBlock.getInsideText(), binDataMap);

      String toInsert = applicabilityOps.evaluateApplicabilityExpression(branch, view, applicabilityBlock);
      return insertMissingbinData(toInsert, binDataMap);
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
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(type);
      //Remove extra space
      String applicExpText = WordCoreUtil.textOnly(applicabilityExpression).toLowerCase().replace(" [", "[");
      beginApplic.setApplicabilityExpression(applicExpText);
      beginApplic.setStartInsertIndex(matcher.start());
      beginApplic.setStartTextIndex(matcher.end());
      beginApplic.setBeginTag(applicabilityExpression);
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
      String optionalEndBracket = null;
      boolean isValidBracket = false;
      if (applic.getType().equals(ApplicabilityType.ConfigurationGroup)) {
         int endBracketGroup = WordCoreUtil.endConfigGroupBracketMatcherGroup;
         optionalEndBracket = matcher.group(endBracketGroup);
         isValidBracket = optionalEndBracket == null ? false : isValidConfigurationGroupBracket(optionalEndBracket);
      } else if (applic.getType().equals(ApplicabilityType.Configuration)) {
         int endBracketGroup = WordCoreUtil.endConfigBracketMatcherGroup;
         optionalEndBracket = matcher.group(endBracketGroup);
         isValidBracket = optionalEndBracket == null ? false : isValidConfigurationBracket(optionalEndBracket);
      } else {
         int endBracketGroup = WordCoreUtil.endFeatureBracketMatcherGroup;
         optionalEndBracket = matcher.group(endBracketGroup);
         isValidBracket = optionalEndBracket == null ? false : isValidFeatureBracket(optionalEndBracket);
      }

      if (optionalEndBracket != null && !isValidBracket) {
         int newEndInsertIndex = matcher.end() - optionalEndBracket.length();
         applic.setEndInsertIndex(newEndInsertIndex);
      } else {
         applic.setEndInsertIndex(matcher.end());
      }
      applic.setEndTextIndex(matcher.start());

      String insideText = toReturn.substring(applic.getStartTextIndex(), applic.getEndTextIndex());
      applic.setInsideText(insideText);

      // Adjust start and end insert indicies if tags are inside a table
      if (!applic.getInsideText().contains(WordCoreUtil.TABLE) && applic.getInsideText().contains(
         WordCoreUtil.TABLE_COLUMN)) {
         String findStartOfRow = toReturn.substring(0, applic.getStartInsertIndex());
         int startRowIndex = findStartOfRow.lastIndexOf(WordCoreUtil.START_TABLE_ROW);

         if (startRowIndex != -1) {
            // find end of row after the END configuration/feature tag
            String findEndOfRow = toReturn.substring(matcher.end());
            int endRowIndex = findEndOfRow.indexOf(WordCoreUtil.TABLE_ROW_END);
            if (endRowIndex != -1) {
               endRowIndex = endRowIndex + matcher.end() + 7;
               String fullText = toReturn.substring(startRowIndex, endRowIndex);
               applic.setIsInTable(true);
               applic.setStartInsertIndex(startRowIndex);
               applic.setEndInsertIndex(startRowIndex + fullText.length());

               fullText = fullText.replaceFirst(
                  "(?i)(" + WordCoreUtil.ENDFEATURE + "|" + WordCoreUtil.ENDCONFIGGRP + "|" + WordCoreUtil.ENDCONFIG + ")",
                  "");
               fullText = fullText.replaceFirst(
                  "(?i)(" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIGGRP + "|" + WordCoreUtil.BEGINCONFIG + ")",
                  "");
               applic.setInsideText(fullText);
            }
         }
      }

      return applic;
   }

   public static HashSet<String> getValidConfigurations(QueryFactory query, BranchId branch) {
      HashSet<String> validConfigurations = new HashSet<>();

      List<ArtifactToken> views =
         query.fromBranch(branch).andTypeEquals(CoreArtifactTypes.BranchView).asArtifactTokens();
      for (ArtifactToken view : views) {
         validConfigurations.add(view.getName().toUpperCase());
      }
      return validConfigurations;
   }

   public static HashSet<String> getValidConfigurationGroups(QueryFactory query, BranchId branch) {
      HashSet<String> validConfigurationGroups = new HashSet<>();

      List<ArtifactToken> views =
         query.fromBranch(branch).andTypeEquals(CoreArtifactTypes.GroupArtifact).andRelationExists(
            CoreRelationTypes.PlConfigurationGroup_Group).asArtifactTokens();
      for (ArtifactToken view : views) {
         validConfigurationGroups.add(view.getName().toUpperCase());
      }
      return validConfigurationGroups;
   }
}
