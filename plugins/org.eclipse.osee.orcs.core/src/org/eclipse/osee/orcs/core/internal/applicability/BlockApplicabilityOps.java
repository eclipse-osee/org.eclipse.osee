/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.orcs.core.internal.applicability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Ryan D. Brooks
 */
public class BlockApplicabilityOps {
   private static String SCRIPT_ENGINE_NAME = "JavaScript";

   private final OrcsApi orcsApi;
   private final Log logger;
   private final ScriptEngine se;
   private final BranchId branch;
   private final ArtifactToken view;
   private final List<FeatureDefinition> featureDefinition;
   private final Map<String, List<String>> viewApplicabilitiesMap;

   public BlockApplicabilityOps(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactToken view) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.branch = branch;
      this.view = view;
      this.featureDefinition = orcsApi.getApplicabilityOps().getFeatureDefinitionData(branch);
      ScriptEngineManager sem = new ScriptEngineManager();
      se = sem.getEngineByName(SCRIPT_ENGINE_NAME);
      viewApplicabilitiesMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(this.branch, view);
   }

   public ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }

   public String applyApplicabilityToFiles(String sourcePath, Map<String, String> fileExtensionToCommentPrefix) {
      Rule rule = new BlockApplicabilityRule(this, branch, view, fileExtensionToCommentPrefix);

      StringBuilder filePattern = new StringBuilder(".*\\.(");
      filePattern.append(Collections.toString("|", fileExtensionToCommentPrefix.keySet()));
      filePattern.append(")");
      rule.setFileNamePattern(filePattern.toString());

      rule.setSubdirectoryNameToPlaceResultFilesIn(view.getName());
      try {
         rule.process(new File(sourcePath));
      } catch (IOException ex) {
         throw OseeCoreException.wrap(ex);
      }
      return "ruleWasApplicable: " + rule.ruleWasApplicable();
   }

   public String evaluateApplicabilityExpression(ApplicabilityBlock applic) {
      String applicabilityExpression = applic.getApplicabilityExpression();
      String toInsert = "";
      try {

         String insideText = applic.getInsideText();

         ApplicabilityGrammarLexer lex =
            new ApplicabilityGrammarLexer(new ANTLRStringStream(applicabilityExpression.toUpperCase()));
         ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

         parser.start();

         ApplicabilityType type = applic.getType();

         if (type.equals(ApplicabilityType.Feature)) {
            toInsert =
               getValidFeatureContent(insideText, applic.isInTable(), parser.getIdValuesMap(), parser.getOperators());
         } else if (type.equals(ApplicabilityType.Configuration) || type.equals(ApplicabilityType.NotConfiguration)) {
            toInsert = getValidConfigurationContent(branch, view, type, insideText, parser.getIdValuesMap());
         } else if (type.equals(ApplicabilityType.ConfigurationGroup) || type.equals(
            ApplicabilityType.NotConfigurationGroup)) {
            toInsert = getValidConfigurationGroupContent(branch, view, type, insideText, applic.getBeginTag());
         }

      } catch (RecognitionException ex) {
         logger.error(
            "Failed to parse expression: " + applicabilityExpression + " at start Index: " + applic.getStartInsertIndex());
      }

      return toInsert;
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

   private String getValidConfigurationGroupContent(BranchId branch, ArtifactToken view, ApplicabilityType type, String fullText, String beginTag) {
      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {
         beginningText = fullText.substring(0, match.start());

         elseText = fullText.substring(match.end());
         elseText = elseText.replaceAll(WordCoreUtil.ENDCONFIGGRP, "");
         elseText = elseText.replaceAll(WordCoreUtil.BEGINCONFIGGRP, "");
      }

      String toReturn = "";
      Boolean viewInCfgGroup = false;
      // Note: this assumes only OR's are put in between configuration groups
      viewInCfgGroup = viewInCfgGroup(branch, view, beginTag);
      if (type.equals(ApplicabilityType.NotConfigurationGroup)) {
         if (viewInCfgGroup) {
            toReturn = elseText;
         } else {
            toReturn = beginningText;
         }
      } else if (!viewInCfgGroup) {
         toReturn = elseText;
      } else {
         toReturn = beginningText;
      }

      return toReturn;
   }

   private Boolean viewInCfgGroup(BranchId branch, ArtifactToken view, String beginTag) {
      String beginConfigGroup = WordCoreUtil.textOnly(beginTag);
      Boolean viewInCfgGroup = false;
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);
      String[] configGroups = applicExpText.split("&|\\|");
      for (int i = 0; i < configGroups.length; i++) {
         configGroups[i] = configGroups[i].split("=")[0].trim();
         if (orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
            configGroups[i]).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).andRelatedTo(CoreRelationTypes.PlConfigurationGroup_BranchView,
                  view).exists()) {
            viewInCfgGroup = true;
            break;
         } else {
            if (view.getName().equalsIgnoreCase(configGroups[i])) {
               viewInCfgGroup = true;
            }
         }
      }
      return viewInCfgGroup;
   }

   private String getValidConfigurationContent(BranchId branch, ArtifactToken view, ApplicabilityType type, String fullText, HashMap<String, List<String>> configIdValuesMap) {
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

      List<String> matchingTags = new ArrayList<String>();
      if (view.isTypeEqual(CoreArtifactTypes.GroupArtifact)) {
         for (ArtifactReadable memberConfig : orcsApi.getQueryFactory().fromBranch(branch).andId(
            view).getArtifact().getRelated(CoreRelationTypes.PlConfigurationGroup_BranchView).getList()) {
            if (configIdValuesMap.containsKey(memberConfig.getName().toUpperCase())) {
               matchingTags.add("Config = " + memberConfig.getName());
            }
         }
         if (matchingTags.isEmpty()) {
            matchingTags = null;
         }

      } else {
         matchingTags = configIdValuesMap.get(view.getName().toUpperCase());
      }
      if (type.equals(ApplicabilityType.NotConfiguration)) {
         //Note when publishing with view=configurationgroup, do not publish Configuration Not[configA] text
         if (orcsApi.getQueryFactory().fromBranch(branch).andId(view).andIsOfType(
            CoreArtifactTypes.BranchView).exists()) {
            if (matchingTags != null) {
               toReturn = elseText;
            } else {
               toReturn = beginningText;
            }
         }
      } else if (matchingTags == null) {
         toReturn = elseText;
      } else {
         toReturn = beginningText;
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

         value = value.equalsIgnoreCase("Default") ? getDefaultValue(feature) : value.trim();

         return Strings.containsIgnoreCase(validValues, value);
      }

      return false;
   }

   private String getDefaultValue(String feature) {
      String toReturn = null;
      for (FeatureDefinition fDef : featureDefinition) {
         if (fDef.getName().equals(feature)) {
            toReturn = fDef.getDefaultValue();
            break;
         }
      }
      return toReturn;
   }
}