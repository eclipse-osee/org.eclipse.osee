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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityCacheFile;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FileTypeApplicabilityData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
public class BlockApplicabilityOps {
   private static String SCRIPT_ENGINE_NAME = "JavaScript";

   /**
    * Regex for use withing BlockApplicability.<br/>
    * (?!Group) is included within Configuration in order to void matching ConfigurationGroup text but leaving 'Group'
    * behind. This ensures that ConfigurationGroup tags will properly match their own regex.
    */
   public static final String INLINE_WHITESPACE = "[ \\t]*";
   public static final String SINGLE_NEW_LINE = "\\r\\n|[\\n\\r]";
   public static final String BEGINFEATURE = " ?(Feature ?(\\[(.*?)\\])) ?";
   public static final String ENDFEATURE = " ?(End Feature ?((\\[.*?\\]))?) ?";
   public static final String BEGINCONFIG = " ?(Configuration(?!Group)( Not)? ?(\\[(.*?)\\])) ?";
   public static final String ENDCONFIG = " ?(End Configuration(?!Group) ?((\\[.*?\\]))?) ?";
   public static final String BEGINCONFIGGRP = " ?(ConfigurationGroup( Not)? ?(\\[(.*?)\\])) ?";
   public static final String ENDCONFIGGRP = " ?(End ConfigurationGroup ?((\\[.*?\\]))?) ?";
   public static final String FEATURE_ELSE = " ?(Feature Else ?((\\[.*?\\]))?) ?";
   public static final String CONFIGURE_ELSE = " ?(Configuration Else ?((\\[.*?\\]))?) ?";
   public static final String CONFIGURE_GROUP_ELSE = " ?(ConfigurationGroup Else ?((\\[.*?\\]))?) ?";
   public static final String COMMENT_EXTRA_CHARS = INLINE_WHITESPACE + "(" + SINGLE_NEW_LINE + ")?";

   public static final String ELSE =
      WordCoreUtil.ELSE_PATTERN + "|(" + INLINE_WHITESPACE + " ?((Feature|ConfigurationGroup|Configuration) Else) ?" + COMMENT_EXTRA_CHARS + ")";
   public static final Pattern ELSE_PATTERN = Pattern.compile(ELSE, Pattern.DOTALL | Pattern.MULTILINE);

   public static final int beginFeatureCommentMatcherGroup = 1;
   public static final int beginFeatureTagMatcherGroup = 2;
   public static final int endFeatureCommentMatcherGroup = 6;
   public static final int endFeatureTagMatcherGroup = 7;

   public static final int beginConfigCommentMatcherGroup = 11;
   public static final int beginConfigTagMatcherGroup = 12;
   public static final int endConfigCommentMatcherGroup = 17;
   public static final int endConfigTagMatcherGroup = 18;

   public static final int beginConfigGrpCommentMatcherGroup = 22;
   public static final int beginConfigGrpTagMatcherGroup = 23;
   public static final int endConfigGrpCommentMatcherGroup = 28;
   public static final int endConfigGrpTagMatcherGroup = 29;

   public static final int featureElseCommentMatcherGroup = 33;
   public static final int featureElseTagMatcherGroup = 34;

   public static final int configureElseCommentMatcherGroup = 38;
   public static final int configureElseTagMatcherGroup = 39;

   public static final int configureGroupElseCommentMatcherGroup = 43;
   public static final int configureGroupElseTagMatcherGroup = 44;

   private final OrcsApi orcsApi;
   private final Log logger;
   private final ScriptEngine se;
   private final BranchId branch;
   private final ArtifactToken view;
   private final List<FeatureDefinition> featureDefinition;
   private final Map<String, List<String>> viewApplicabilitiesMap;
   private final boolean useCachedConfig;
   private BatStagingCreator batCreator;
   private BlockApplicabilityCacheFile cache;
   private String plPreferences;
   private Map<String, FileTypeApplicabilityData> fileTypeApplicabilityDataMap;
   private Map<String, List<String>> configurationMap;
   private Map<String, Set<String>> fileApplicabilityCache = new ConcurrentHashMap<>();

   public BlockApplicabilityOps(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactToken view, BlockApplicabilityCacheFile cache) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.branch = branch;
      this.view = view;
      ScriptEngineManager sem = new ScriptEngineManager();
      this.se = sem.getEngineByName(SCRIPT_ENGINE_NAME);
      this.featureDefinition = cache.getFeatureDefinition();
      this.viewApplicabilitiesMap = cache.getViewApplicabilitiesMap();
      this.configurationMap = cache.getConfigurationMap();
      this.plPreferences = cache.getProductLinePreferences();
      this.fileTypeApplicabilityDataMap = populateFileTypeApplicabilityDataMap(plPreferences);
      this.cache = cache;
      this.useCachedConfig = true;
   }

   public BlockApplicabilityOps(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactToken view) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      this.branch = branch;
      this.view = view;
      ScriptEngineManager sem = new ScriptEngineManager();
      this.se = sem.getEngineByName(SCRIPT_ENGINE_NAME);
      this.featureDefinition = orcsApi.getApplicabilityOps().getFeatureDefinitionData(branch);
      this.viewApplicabilitiesMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, view);
      this.configurationMap = new HashMap<>();
      this.plPreferences = getProductLinePreferences();
      this.fileTypeApplicabilityDataMap = populateFileTypeApplicabilityDataMap(plPreferences);
      this.useCachedConfig = false;
   }

   public ApplicabilityBlock createApplicabilityBlock(ApplicabilityType applicType, String beginExpression) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock(applicType);
      beginExpression = beginExpression.replace(" [", "[");
      beginApplic.setApplicabilityExpression(beginExpression);
      return beginApplic;
   }

   public XResultData applyApplicabilityToFiles(XResultData results, boolean commentNonApplicableBlocks, String sourcePath, String stagePath, String customStageDir) {
      HashSet<String> excludedFiles = new HashSet<>();
      excludedFiles.add("Staging");
      if (customStageDir != null && !customStageDir.equals("")) {
         excludedFiles.add(customStageDir);
      }

      setUpBlockApplicability(commentNonApplicableBlocks);
      File sourceFile = new File(sourcePath);
      File stageFile = new File(stagePath);

      batCreator.processDirectory(results, sourceFile, stageFile, excludedFiles);

      if (!useCachedConfig) {
         createCacheFile(results, stagePath);
      }

      if (sourceFile.isDirectory()) {
         writeFileApplicabilityCache(results, sourceFile.getName(), stagePath);
      }

      return results;
   }

   public XResultData refreshStagedFiles(XResultData results, String sourcePath, String stagePath, String customStageDir, List<String> files) {
      File sourceDir = new File(sourcePath);
      File stageDir = new File(stagePath, sourceDir.getName());

      File fileApplicCache = readFileApplicabilityCache(results, stageDir);

      Set<String> excludedFiles;
      for (String sourceFileString : files) {
         File sourceFile = new File(sourceDir, sourceFileString);
         File stageFile = new File(stageDir, sourceFileString).getParentFile();

         if (!stageFile.exists()) {
            if (sourceFile.exists()) {
               stageFile.mkdirs();
            } else {
               results.warningf("The path for %s does not exist", sourceFile.getPath());
               continue;
            }
         }

         if (sourceFile.getName().equals(".fileApplicability")) {
            /**
             * If it's a .fileApplicability file, it could be making changes to its' siblings and therefore those all
             * need to be processed
             */
            sourceFile = sourceFile.getParentFile();
            stageFile = stageFile.getParentFile();
         }

         if (customStageDir != null && !customStageDir.equals("")) {
            excludedFiles =
               fileApplicabilityCache.getOrDefault(stageFile.getPath(), Sets.newHashSet("Staging", customStageDir));
         } else {
            excludedFiles = fileApplicabilityCache.getOrDefault(stageFile.getPath(), Sets.newHashSet("Staging"));
         }

         batCreator.processDirectory(results, sourceFile, stageFile, excludedFiles);
      }

      writeFileApplicabilityCache(results, fileApplicCache);

      return results;

   }

   /**
    * This method can be used internally or externally to set up the BatStagingCreator class for the
    * BlockApplicabilityTool's use.
    */
   public void setUpBlockApplicability(boolean commentNonApplicableBlocks) {
      if (useCachedConfig) {
         configurationMap = cache.getConfigurationMap();
         plPreferences = cache.getProductLinePreferences();
         fileTypeApplicabilityDataMap = populateFileTypeApplicabilityDataMap(plPreferences);
      } else {
         configurationMap = new HashMap<>();
         plPreferences = getProductLinePreferences();
         fileTypeApplicabilityDataMap = populateFileTypeApplicabilityDataMap(plPreferences);
      }

      StringBuilder filePattern = new StringBuilder(".*\\.(");
      filePattern.append(Collections.toString("|", fileTypeApplicabilityDataMap.keySet()));
      filePattern.append(")");
      Pattern validFileExtensions = Pattern.compile(filePattern.toString(), Pattern.CASE_INSENSITIVE);

      batCreator =
         new BatStagingCreator(this, fileTypeApplicabilityDataMap, validFileExtensions, commentNonApplicableBlocks);

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
            toInsert = getValidConfigurationContent(type, insideText, parser.getIdValuesMap());
         } else if (type.equals(ApplicabilityType.ConfigurationGroup) || type.equals(
            ApplicabilityType.NotConfigurationGroup)) {
            toInsert = getValidConfigurationGroupContent(type, insideText, applic.getBeginTag());
         }

      } catch (RecognitionException ex) {
         logger.error(
            "Failed to parse expression: " + applicabilityExpression + " at start Index: " + applic.getStartInsertIndex());
      }

      return toInsert;
   }

   private String getValidFeatureContent(String fullText, boolean isInTable, HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators) {
      Matcher match = ELSE_PATTERN.matcher(fullText);
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

      String expression = createFeatureExpression(featureIdValuesMap, featureOperators);

      boolean result = false;
      try {
         result = (boolean) se.eval(expression);
      } catch (ScriptException ex) {
         logger.error("Failed to parse expression: " + expression);
      }

      StringBuilder toReturn = new StringBuilder();
      if (result) {
         toReturn.append(beginningText);
      } else {
         toReturn.append(elseText);
      }

      return toReturn.toString();
   }

   private String getValidConfigurationGroupContent(ApplicabilityType type, String fullText, String beginTag) {
      Matcher match = ELSE_PATTERN.matcher(fullText);
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
      viewInCfgGroup = viewInCfgGroup(beginTag);
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

   private Boolean viewInCfgGroup(String beginTag) {
      String beginConfigGroup = WordCoreUtil.textOnly(beginTag);
      Boolean viewInCfgGroup = false;
      int start = beginConfigGroup.indexOf("[") + 1;
      int end = beginConfigGroup.indexOf("]");
      String applicExpText = beginConfigGroup.substring(start, end);
      String[] configGroups = applicExpText.split("&|\\|");
      for (int i = 0; i < configGroups.length; i++) {
         configGroups[i] = configGroups[i].split("=")[0].trim();
         if (queryGroup(configGroups[i])) {
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

   private boolean queryGroup(String configGroup) {
      if (useCachedConfig) {
         return configurationMap.containsKey(configGroup);
      } else {
         return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
            configGroup).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).andRelatedTo(CoreRelationTypes.PlConfigurationGroup_BranchView,
                  view).exists();
      }

   }

   private String getValidConfigurationContent(ApplicabilityType type, String fullText, HashMap<String, List<String>> configIdValuesMap) {
      Matcher match = ELSE_PATTERN.matcher(fullText);
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
         getGroupConfigMatchingTags(matchingTags, configIdValuesMap);
         if (matchingTags.isEmpty()) {
            matchingTags = null;
         }

      } else {
         matchingTags = configIdValuesMap.get(view.getName().toUpperCase());
      }
      if (type.equals(ApplicabilityType.NotConfiguration)) {
         //Note when publishing with view=configurationgroup, do not publish Configuration Not[configA] text
         if (branchViewExists()) {
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

   private void getGroupConfigMatchingTags(List<String> matchingTags, HashMap<String, List<String>> configIdValuesMap) {
      if (useCachedConfig) {
         List<String> configs = configurationMap.getOrDefault(view.getName(), new ArrayList<String>());
         for (String config : configs) {
            if (configIdValuesMap.containsKey(config.toUpperCase())) {
               matchingTags.add("Config = " + config);
            }
         }
      } else {
         for (ArtifactReadable memberConfig : orcsApi.getQueryFactory().fromBranch(branch).andId(
            view).getArtifact().getRelated(CoreRelationTypes.PlConfigurationGroup_BranchView).getList()) {
            if (configIdValuesMap.containsKey(memberConfig.getName().toUpperCase())) {
               matchingTags.add("Config = " + memberConfig.getName());
            }
         }
      }
   }

   private boolean branchViewExists() {
      if (useCachedConfig) {
         return true;
      } else {
         return orcsApi.getQueryFactory().fromBranch(branch).andId(view).andIsOfType(
            CoreArtifactTypes.BranchView).exists();
      }
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

   public void addFileApplicabilityEntry(String path, Set<String> excludedFiles) {
      Set<String> excludedSet = new HashSet<>();
      excludedSet.addAll(excludedFiles);
      fileApplicabilityCache.put(path, excludedSet);
   }

   public ArtifactToken getOpsView() {
      return view;
   }

   private File readFileApplicabilityCache(XResultData results, File stageDir) {
      File fileApplicCache = new File(stageDir, ".fileApplicabilityCache");
      ObjectMapper objMap = new ObjectMapper();
      JavaType type = objMap.getTypeFactory().constructMapType(HashMap.class, String.class, Set.class);
      try {
         fileApplicabilityCache = objMap.readValue(fileApplicCache, type);
      } catch (IOException ex) {
         results.error("Error reading fileApplicabilityCache");
      }

      return fileApplicCache;
   }

   private void writeFileApplicabilityCache(XResultData results, String sourceFileName, String stagePath) {
      File fileApplicCache = new File(stagePath, sourceFileName);
      fileApplicCache = new File(fileApplicCache, ".fileApplicabilityCache");
      writeFileApplicabilityCache(results, fileApplicCache);
   }

   private void writeFileApplicabilityCache(XResultData results, File fileApplicCache) {
      ObjectMapper objMap = new ObjectMapper();
      try {
         objMap.writeValue(fileApplicCache, fileApplicabilityCache);
      } catch (IOException ex) {
         results.error("Error writing file applicability cache file");
      }
   }

   /**
    * This cache file is created to store necessary information to process applicability within this class. Anything
    * that is normally queried from the database, should be stored in this json file and saved within the stagePath.
    */
   public void createCacheFile(XResultData results, String stagePath) {
      ObjectMapper objMap = new ObjectMapper();
      try {
         BlockApplicabilityCacheFile cache = new BlockApplicabilityCacheFile();
         cache.setViewId(view.getId());
         cache.setViewName(view.getName());
         cache.setViewTypeId(view.getArtifactType().getId());
         cache.setViewApplicabilitiesMap(viewApplicabilitiesMap);
         cache.setFeatureDefinition(featureDefinition);
         cache.setConfigurationMap(createCacheConfigurationMap());
         cache.setProductLinePreferences(plPreferences);

         File cacheFile = new File(stagePath, ".applicabilityCache");
         objMap.writeValue(cacheFile, cache);

      } catch (IOException ex) {
         results.errorf("There was a problem while writing the cache file %s\n", ex.getMessage());
      }
   }

   /**
    * The configurationMap should be specific to the view given. If the view is a GroupConfig, it will map that group
    * config to all of the configurations related to it. If the view is a configuration, it will map each related group
    * config to its' configurations.
    */
   private Map<String, List<String>> createCacheConfigurationMap() {
      Map<String, List<String>> configMap = new HashMap<>();
      List<ArtifactReadable> groupConfigs = new ArrayList<>();
      if (view.isOfType(CoreArtifactTypes.GroupArtifact)) {
         groupConfigs =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).follow(
                  CoreRelationTypes.PlConfigurationGroup_BranchView).asArtifacts();
      } else {
         groupConfigs =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andRelatedTo(
                  CoreRelationTypes.PlConfigurationGroup_BranchView, view).follow(
                     CoreRelationTypes.PlConfigurationGroup_BranchView).asArtifacts();
      }

      for (ArtifactReadable groupConfig : groupConfigs) {
         List<ArtifactReadable> branchViews =
            groupConfig.getRelatedList(CoreRelationTypes.PlConfigurationGroup_BranchView);
         configMap.put(groupConfig.getName(), Named.getNames(branchViews));
      }

      return configMap;
   }

   private String getProductLinePreferences() {
      ArtifactReadable globalPreferences = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(
         CoreArtifactTokens.GlobalPreferences).asArtifact();
      String preferences = globalPreferences.getSoleAttributeValue(CoreAttributeTypes.ProductLinePreferences, "");

      return preferences;
   }

   private Map<String, FileTypeApplicabilityData> populateFileTypeApplicabilityDataMap(String plPreferences) {
      Map<String, FileTypeApplicabilityData> fileTypeApplicabilityDataMap = new HashMap<>();

      JsonNode preferencesJson = orcsApi.jaxRsApi().readTree(plPreferences);
      // Product Line preferences should be set in the GlobalPreferences on every installed OSEE
      if (preferencesJson == null) {
         return fileTypeApplicabilityDataMap;
      }
      JsonNode commentStyles = preferencesJson.findValue("FileExtensionCommentStyle");
      Iterator<JsonNode> iter = commentStyles.elements();

      while (iter.hasNext()) {
         JsonNode currNode = iter.next();
         String fileExtension = currNode.findValue("FileExtension").asText();

         String commentPrefixRegex = currNode.findValue("CommentPrefixRegex").asText();
         JsonNode optionalSuffixRegex = currNode.findValue("CommentSuffixRegex");
         String commentSuffixRegex = optionalSuffixRegex == null ? "" : optionalSuffixRegex.asText();

         Pattern pattern = createFullPatternFromCommentStyle(commentPrefixRegex, commentSuffixRegex);

         String commentPrefix = currNode.findValue("CommentPrefix").asText();
         JsonNode optionalSuffix = currNode.findValue("CommentSuffix");
         String commentSuffix = optionalSuffix == null ? "" : optionalSuffix.asText();

         FileTypeApplicabilityData data = new FileTypeApplicabilityData(pattern, commentPrefixRegex, commentSuffixRegex,
            commentPrefix, commentSuffix);

         fileTypeApplicabilityDataMap.put(fileExtension.toLowerCase(), data);
      }

      return fileTypeApplicabilityDataMap;
   }

   /**
    * Using the given comments for the file type, creates an applicability pattern used to match Product Line tagging.
    * Places content inside parenthesis to group each type together, uses an OR statement between each potential match
    * pattern. Ex. Start Feature or End Feature. Also appends regex after the potential suffix to match any extra
    * spaces, plus a single new line character. This is to maintain formatting in the newly written file.
    */
   private Pattern createFullPatternFromCommentStyle(String commentPrefix, String commentSuffix) {
      String commentedFeatureStart =
         "(" + INLINE_WHITESPACE + commentPrefix + BEGINFEATURE + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedFeatureEnd =
         "(" + INLINE_WHITESPACE + commentPrefix + ENDFEATURE + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigStart =
         "(" + INLINE_WHITESPACE + commentPrefix + BEGINCONFIG + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigEnd =
         "(" + INLINE_WHITESPACE + commentPrefix + ENDCONFIG + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigGrpStart =
         "(" + INLINE_WHITESPACE + commentPrefix + BEGINCONFIGGRP + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigGrpEnd =
         "(" + INLINE_WHITESPACE + commentPrefix + ENDCONFIGGRP + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedFeatureElse =
         "(" + INLINE_WHITESPACE + commentPrefix + FEATURE_ELSE + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigureElse =
         "(" + INLINE_WHITESPACE + commentPrefix + CONFIGURE_ELSE + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";
      String commentedConfigureGroupElse =
         "(" + INLINE_WHITESPACE + commentPrefix + CONFIGURE_GROUP_ELSE + INLINE_WHITESPACE + commentSuffix + COMMENT_EXTRA_CHARS + ")";

      String pattern =
         commentedFeatureStart + "|" + commentedFeatureEnd + "|" + commentedConfigStart + "|" + commentedConfigEnd + "|" + commentedConfigGrpStart + "|" + commentedConfigGrpEnd + "|" + commentedFeatureElse + "|" + commentedConfigureElse + "|" + commentedConfigureGroupElse;
      return Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE);
   }
}