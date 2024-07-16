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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.BranchViewDefinition;
import org.eclipse.osee.framework.core.applicability.ExtendedFeatureDefinition;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.NameValuePair;
import org.eclipse.osee.framework.core.applicability.ProductTypeDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ApplicabilityTokenWithConstraints;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityCacheFile;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Donald G. Dunne
 */
public class OrcsApplicabilityOps implements OrcsApplicability {

   private final OrcsApi orcsApi;
   private final Log logger;
   private ArtifactToken plFolder = ArtifactToken.SENTINEL;
   private ArtifactToken featureFolder = ArtifactToken.SENTINEL;
   private ArtifactToken configurationsFolder = ArtifactToken.SENTINEL;
   private ArtifactToken plConfigurationGroupsFolder = ArtifactToken.SENTINEL;
   private static StagedFileWatcher fileWatcher;
   private final TransactionFactory txFactory;

   public OrcsApplicabilityOps(OrcsApi orcsApi, Log logger) {
      this.orcsApi = orcsApi;
      this.logger = logger;
      txFactory = orcsApi.getTransactionFactory();
   }

   /**
    * @return config as defined in Feature artifacts
    */
   @Override
   public ApplicabilityBranchConfig getConfig(BranchId branchId, ArtifactId config) {

      ApplicabilityBranchConfig appConfig = new ApplicabilityBranchConfig();
      Branch branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getExactlyOne();

      appConfig.setBranch(branch);
      appConfig.setAssociatedArtifactId(branch.getAssociatedArtifact());
      if (branch.getBranchType().equals(BranchType.WORKING.getId())) {
         appConfig.setEditable(true);
      } else {
         appConfig.setEditable(false);
      }
      if (branch.getParentBranch().isValid()) {
         Branch parentBranch =
            orcsApi.getQueryFactory().branchQuery().andId(branch.getParentBranch()).getResults().getExactlyOne();
         appConfig.setParentBranch(new BranchViewToken(parentBranch, parentBranch.getName(), parentBranch.getViewId()));
      }
      // Load all configurations (stored as branch views)
      List<ArtifactReadable> branchViews = new ArrayList<>();
      List<ArtifactReadable> groups = new ArrayList<>();
      if (config.isInvalid()) {
         branchViews.addAll(
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).asArtifacts());
         groups.addAll(orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branch));
      } else {
         ArtifactReadable artR = orcsApi.getQueryFactory().fromBranch(branch).andId(config).asArtifactOrSentinel();
         if (artR.isOfType(CoreArtifactTypes.BranchView)) {
            branchViews.add(artR);
         }
         if (artR.isOfType(CoreArtifactTypes.GroupArtifact)) {
            groups.add(artR);
         }
      }
      Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      for (ArtifactReadable branchView : branchViews) {
         Map<String, List<String>> namedViewApplicabilityMap =
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, branchView);
         //if the Configuration (ie branchView) has more than just the Config = <config name> then
         //it set hasFeatureApplicabilities to true
         //hasFeatureApplicabilities is used to determine whether or not to display as a column on PLE web
         boolean hasFeatureApplicabilities =
            (namedViewApplicabilityMap.entrySet().stream().filter(map -> !map.getKey().equals("Config")).collect(
               Collectors.toMap(map -> map.getKey(), map -> map.getValue())).size() > 1);
         appConfig.addView(new BranchViewDefinition(branchView.getIdString(), branchView.getName(),
            branchView.getSoleAttributeAsString(CoreAttributeTypes.Description, ""),
            branchView.fetchAttributesAsStringList(CoreAttributeTypes.ProductApplicability),
            hasFeatureApplicabilities));
         branchViewsMap.put(branchView, namedViewApplicabilityMap);

      }
      for (ArtifactToken group : groups) {
         appConfig.addGroup(getConfigurationGroup(group.getIdString(), branch));
         branchViewsMap.put(group,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, group));
      }
      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).asArtifacts();
      Collections.sort(featureArts, new NamedComparator(SortOrder.ASCENDING));
      for (ArtifactReadable featureArt : featureArts) {
         FeatureDefinition fDef = getFeatureDefinition(featureArt);
         ExtendedFeatureDefinition extfDef = new ExtendedFeatureDefinition(fDef);
         List<List<NameValuePair>> groupingsList = new LinkedList<>();
         for (ConfigurationGroupDefinition group : appConfig.getGroups()) {
            String groupToFeatureValue = getViewToFeatureValue(ArtifactId.valueOf(group.getId()), fDef, branchViewsMap);
            List<NameValuePair> groupList = new LinkedList<>();
            groupList.add(new NameValuePair(group.getName(), groupToFeatureValue));
            //check if view is present in a specific group & add it to the groupings list to be added post views
            for (BranchViewDefinition memberConfig : appConfig.getViews()) {
               if (group.getConfigurations().contains(memberConfig.getId())) {
                  String viewToFeatureValue =
                     getViewToFeatureValue(ArtifactId.valueOf(memberConfig.getId()), fDef, branchViewsMap);
                  groupList.add(new NameValuePair(memberConfig.getName(), viewToFeatureValue));
               }
            }
            groupingsList.add(groupList);
         }
         //check for view present in groupingsList, if not present add configuration
         for (BranchViewDefinition view : appConfig.getViews()) {
            boolean viewPresent = false;
            for (List<NameValuePair> groupList : groupingsList) {
               for (NameValuePair configItem : groupList) {
                  if (configItem.getName() == view.getName()) {
                     viewPresent = true;
                  }
               }
            }
            if (!viewPresent) {
               String viewToFeatureValue =
                  getViewToFeatureValue(ArtifactId.valueOf(view.getId()), fDef, branchViewsMap);
               extfDef.addConfiguration(new NameValuePair(view.getName(), viewToFeatureValue));
            }
         }
         for (List<NameValuePair> groupList : groupingsList) {
            for (NameValuePair configItem : groupList) {
               extfDef.addConfiguration(configItem);
            }
         }
         appConfig.addFeature(extfDef);
      }

      return appConfig;
   }

   @Override
   public ApplicabilityBranchConfig getConfigWithCompoundApplics(BranchId branchId, ArtifactId config) {
      ApplicabilityBranchConfig appConfig = getConfig(branchId, config);
      // Get compound applicabilities
      for (ApplicabilityToken applicToken : orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(
         branchId).values()) {
         if (applicToken.getName().contains("|") || applicToken.getName().contains("&")) {
            FeatureDefinition feature = new FeatureDefinition();
            feature.setId(applicToken.getId());
            feature.setName(applicToken.getName());
            feature.setDefaultValue("");
            feature.setValues(Collections.emptyList());
            feature.setProductApplicabilities(Collections.emptyList());
            feature.setValueType("String");
            feature.setMultiValued(false);
            feature.setDescription(applicToken.getName());
            feature.setData(ArtifactReadable.SENTINEL);

            ExtendedFeatureDefinition extfDef = new ExtendedFeatureDefinition(feature);

            char operator = applicToken.getName().contains("|") ? '|' : '&';
            String[] applics = applicToken.getName().split("[|&]");
            String[] feature1 = applics[0].split(" = ");
            String[] feature2 = applics[1].split(" = ");
            String feature1Name = feature1[0].trim();
            String feature1Value = feature1[1].trim();
            String feature2Name = feature2[0].trim();
            String feature2Value = feature2[1].trim();

            ExtendedFeatureDefinition f1ExDef =
               appConfig.getFeatures().stream().filter(f -> f.getName().equals(feature1Name)).findFirst().orElse(
                  ExtendedFeatureDefinition.SENTINEL);
            ExtendedFeatureDefinition f2ExDef =
               appConfig.getFeatures().stream().filter(f -> f.getName().equals(feature2Name)).findFirst().orElse(
                  ExtendedFeatureDefinition.SENTINEL);

            if (f1ExDef.isValid() && f2ExDef.isValid()) {
               for (NameValuePair f1Config : f1ExDef.getConfigurations()) {
                  for (NameValuePair f2Config : f2ExDef.getConfigurations()) {
                     if (f1Config.getName().equals(f2Config.getName())) {
                        String compoundValue = "Excluded";
                        boolean f1Applic = feature1Value.equals(f1Config.getValue());
                        boolean f2Applic = feature2Value.equals(f2Config.getValue());
                        if ((operator == '|' && (f1Applic || f2Applic)) || (operator == '&' && (f1Applic && f2Applic))) {
                           compoundValue = "Included";
                        }
                        extfDef.addConfiguration(new NameValuePair(f1Config.getName(), compoundValue));
                     }
                  }
               }
            }

            appConfig.addFeature(extfDef);
         }
      }
      return appConfig;
   }

   private String getViewToFeatureValue(ArtifactId view, FeatureDefinition fDef,
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap) {
      Map<String, List<String>> map = branchViewsMap.get(view);
      //
      List<String> list = map.get(fDef.getName());
      if (list == null) {
         return "";
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list);
   }

   @Override
   public CreateViewDefinition getViewDefinition(ArtifactReadable art) {
      CreateViewDefinition view = new CreateViewDefinition();
      view.setId(art.getId());
      view.setName(art.getName());
      view.setProductApplicabilities(art.fetchAttributesAsStringList(CoreAttributeTypes.ProductApplicability));
      view.setConfigurationGroup(art.getRelated(CoreRelationTypes.PlConfigurationGroup_Group).getList().stream().map(
         a -> ArtifactId.valueOf(a.getId())).collect(Collectors.toList()));
      view.setData(art);
      return view;
   }

   @Override
   public FeatureDefinition getFeatureDefinition(ArtifactReadable art) {

      FeatureDefinition feature = new FeatureDefinition();
      feature.setId(art.getId());
      feature.setName(art.getName());
      feature.setDefaultValue(art.getSoleAttributeValue(CoreAttributeTypes.DefaultValue, ""));
      feature.setValues(art.fetchAttributesAsStringList(CoreAttributeTypes.Value));
      feature.setProductApplicabilities(art.fetchAttributesAsStringList(CoreAttributeTypes.ProductApplicability));
      feature.setValueType(art.getSoleAttributeAsString(CoreAttributeTypes.FeatureValueType, ""));
      feature.setMultiValued(art.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued, false));
      feature.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
      feature.setData(art);
      return feature;
   }

   @Override
   public ArtifactToken createFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx,
      XResultData results) {
      ArtifactToken fDefArt = ArtifactToken.SENTINEL;

      if (Strings.isInValid(featureDef.getName())) {
         results.error("Feature must have a name.");
      }
      if (!featureDef.getName().matches("^[A-Z0-9_()\\s\\-\\.]+$")) {
         results.error("Feature name must be all caps with no special characters except underscore, dash, and space");
      }
      if (Strings.isInValid(featureDef.getDescription())) {
         results.error("Description is required.");
      }
      if (featureDef.getValues() == null) {
         results.error("Values must be specified.  Comma delimited.");
      } else {
         for (String val : featureDef.getValues()) {
            if (!val.matches("^[a-zA-Z0-9_()\\s\\-\\.]+$")) {
               results.error("The value: " + val + " is invalid.  Must be alphanumeric.");
            }
         }
      }
      if (Strings.isInValid(featureDef.getDefaultValue())) {
         results.error("Default value is required");
      }
      if (featureDef.getValues() != null && !featureDef.getValues().contains(featureDef.getDefaultValue())) {
         results.error("Default value must be in the list of values.");
      }

      if (featureDef.getValues().contains("Included") && featureDef.getValues().contains(
         "Excluded") && featureDef.getDefaultValue().equals("Excluded")) {
         results.error("Default value must be Included for Included/Excluded feature definition");
      }

      if (Strings.isInValid(featureDef.getValueType())) {
         results.error("Value type is required.");
      }
      FeatureDefinition lFeature = getFeature(featureDef.getName(), tx.getBranch());

      if (lFeature.isValid()) {
         results.error("Feature: " + lFeature.getName() + " already exists.");
      }
      if (results.isErrors()) {
         return fDefArt;
      }
      //if its an add, create new feature else it is an update
      if (lFeature.isInvalid()) {
         ArtifactToken featuresFolder = tx.getWriteable(CoreArtifactTokens.FeaturesFolder);
         // Check current transaction first
         if (featuresFolder.isInvalid()) {
            featuresFolder = getFeaturesFolder(tx.getBranch());
         }
         if (featuresFolder.isInvalid()) {
            results.error("Features Folder cannot be null");
            return fDefArt;
         }
         fDefArt = tx.createArtifact(featuresFolder, CoreArtifactTypes.Feature, featureDef.getName());
      }
      updateFeatureDefinition(fDefArt, featureDef, tx);

      return fDefArt;
   }

   @Override
   public ArtifactToken updateFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx,
      XResultData results) {
      ArtifactToken fDefArt = ArtifactToken.SENTINEL;

      if (Strings.isInValid(featureDef.getName())) {
         results.error("Feature must have a name.");
      }
      if (!featureDef.getName().matches("^[A-Z0-9_()\\s\\-\\.]+$")) {
         results.error("Feature name must be all caps with no special characters except underscore, dash, and space");
      }
      if (Strings.isInValid(featureDef.getDescription())) {
         results.error("Description is required.");
      }
      if (featureDef.getValues() == null) {
         results.error("Values must be specified.  Comma delimited.");
      } else {
         for (String val : featureDef.getValues()) {
            if (!val.matches("^[a-zA-Z0-9_()\\s\\-\\.]+$")) {
               results.error("The value: " + val + " is invalid.  Must be alphanumeric.");
            }
         }
      }
      if (Strings.isInValid(featureDef.getDefaultValue())) {
         results.error("Default value is required");
      }
      if (featureDef.getValues() != null && !featureDef.getValues().contains(featureDef.getDefaultValue())) {
         results.error("Default value must be in the list of values.");
      }
      if (Strings.isInValid(featureDef.getValueType())) {
         results.error("Value type is required.");
      }
      FeatureDefinition lFeature = getFeature(featureDef.getName(), tx.getBranch());

      if (results.isErrors()) {
         return fDefArt;
      }
      fDefArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
         ArtifactId.valueOf(lFeature.getId())).asArtifactOrSentinel();
      updateFeatureDefinition(fDefArt, featureDef, tx);

      return fDefArt;
   }

   private void updateFeatureDefinition(ArtifactToken fDefArt, FeatureDefinition featureDef, TransactionBuilder tx) {
      tx.setName(fDefArt, featureDef.getName());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.DefaultValue, featureDef.getDefaultValue());
      tx.setAttributesFromValues(fDefArt, CoreAttributeTypes.Value, featureDef.getValues());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.FeatureValueType, featureDef.getValueType());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.FeatureMultivalued, featureDef.isMultiValued());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.Description, featureDef.getDescription());
      if (featureDef.getProductApplicabilities() != null) {
         tx.setAttributesFromValues(fDefArt, CoreAttributeTypes.ProductApplicability,
            featureDef.getProductApplicabilities());
      }
   }

   @Override
   public List<BranchId> getApplicabilityBranches() {
      List<BranchId> tokens = new ArrayList<>();
      for (Branch branch : orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(
         false).andIsOfType(BranchType.BASELINE, BranchType.WORKING).andStateIs(BranchState.CREATED,
            BranchState.MODIFIED).getResults().getList()) {
         if (orcsApi.getQueryFactory().fromBranch(branch).andId(CoreArtifactTokens.ProductLineFolder).exists()) {
            tokens.add(branch);
         }
      }
      return tokens;
   }

   @Override
   public List<BranchId> getApplicabilityBranchesByType(String branchQueryType) {
      List<BranchId> tokens = new ArrayList<>();
      List<Branch> branchList = new ArrayList<>();

      if (branchQueryType.equals("all")) {
         branchList = orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(false).andIsOfType(
            BranchType.BASELINE, BranchType.WORKING).andStateIs(BranchState.CREATED,
               BranchState.MODIFIED).getResults().getList();
      }
      if (branchQueryType.equals("baseline")) {
         branchList = orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(false).andIsOfType(
            BranchType.BASELINE).andStateIs(BranchState.CREATED, BranchState.MODIFIED).getResults().getList();
      }
      for (Branch branch : branchList) {
         if (orcsApi.getQueryFactory().fromBranch(branch).andId(CoreArtifactTokens.ProductLineFolder).exists()) {
            tokens.add(branch);
         }
      }
      return tokens;
   }

   @Override
   public ArtifactToken getFeaturesFolder(BranchId branch) {
      if (featureFolder.isInvalid()) {
         featureFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "Features").asArtifactTokenOrSentinel();
      }
      return featureFolder;
   }

   @Override
   public ArtifactToken getConfigurationsFolder(BranchId branch) {
      if (configurationsFolder.isInvalid()) {
         configurationsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "Products").asArtifactOrSentinel();
      }
      if (configurationsFolder.isInvalid()) {
         configurationsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "Configurations").asArtifactOrSentinel();
      }
      return configurationsFolder;
   }

   @Override
   public ArtifactToken getPlConfigurationGroupsFolder(BranchId branch) {
      if (plConfigurationGroupsFolder.isInvalid()) {
         plConfigurationGroupsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "PL Configuration Groups").asArtifactOrSentinel();
      }
      return plConfigurationGroupsFolder;
   }

   @Override
   public ArtifactToken getProductLineFolder(BranchId branch) {
      if (plFolder.isInvalid()) {
         plFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.ProductLineFolder).asArtifactOrSentinel();
      }
      if (plFolder.isInvalid()) {
         plFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andNameEquals("Product Line").asArtifactTokenOrSentinel();
      }
      return plFolder;
   }

   @Override
   public XResultData createFeature(FeatureDefinition feature, BranchId branch) {
      XResultData results = new XResultData();
      ArtifactToken featureArt = ArtifactToken.SENTINEL;
      TransactionBuilder tx = txFactory.createTransaction(branch, "Create Feature");
      boolean changes = false;
      try {
         featureArt = createFeatureDefinition(feature, tx, results);

         if (featureArt.isValid()) {
            changes = true;
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      if (results.getNumErrors() == 0) {
         try {

            /**
             * Adding tuples for ApplicabilityValueData (E1: ArtifactId - E2: ApplicabilityId)<br/>
             * E2 is passed as a string to find existing/create new ApplicabilityId
             */
            for (String value : feature.getValues()) {
               String applicString = feature.getName() + " = " + value;
               addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, featureArt, tx, applicString);
               changes = true;
            }
            /**
             * There are some users to have Configurations defined that have no feature apps set and any new features
             * should not be added. newPLESys and the stream check to see if the view currently has feature based apps
             * is used to determine whether the new feature should be added to the given view
             */

            boolean newPLESys = true;
            if (orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(
               branch).entrySet().stream().anyMatch(
                  a -> a.getValue().toString().startsWith("Config =") || a.getValue().toString().equals("Base"))) {
               newPLESys = false;
            }
            List<ArtifactReadable> branchViews =
               orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
            Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
            for (ArtifactToken view : branchViews) {

               List<ApplicabilityToken> currentApps =
                  orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(view, tx.getBranch());
               /**
                * For each view, check to see if there are any other non-feature applicabilities. If so AND it isn't a
                * brand new PLE System, then add the new feature; otherwise assume it is one of the configs which should
                * have only Config = xxx and Base as apps
                */
               if (!newPLESys && currentApps.stream().anyMatch(
                  a -> (!(a.getName().startsWith("Config =") || a.getName().equals("Base"))))) {
                  if (!currentApps.stream().anyMatch(a -> a.getName().equals(feature.getName() + " = "))) {
                     String applicString = feature.getName() + " = " + feature.getDefaultValue();
                     addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, applicString);
                     changes = true;
                  }
               }
            }

         } catch (Exception ex) {
            results.error(Lib.exceptionToString(ex));
         }
      }
      if (changes) {
         tx.commit();
      }
      return results;
   }

   private void addIntroduceTuple2(Tuple2Type<ArtifactId, String> tupleType, ArtifactId featureArt,
      TransactionBuilder tx, String applicString) {
      GammaId gamma = GammaId.SENTINEL;
      if (!(gamma = orcsApi.getQueryFactory().tupleQuery().getTuple2GammaFromE1E2(tupleType, featureArt,
         applicString)).isValid()) {
         tx.addTuple2(tupleType, featureArt, applicString);
      } else {
         tx.introduceTuple(tupleType, gamma);
      }
   }

   @Override
   public XResultData updateFeature(FeatureDefinition feature, BranchId branch) {
      XResultData results = new XResultData();
      ArtifactToken featureArt = ArtifactToken.SENTINEL;
      try {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Update Feature");
         featureArt = updateFeatureDefinition(feature, tx, results);
         if (featureArt.isValid()) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      if (results.getNumErrors() == 0) {
         try {
            boolean changes = false;
            TransactionBuilder tx = txFactory.createTransaction(branch, "Add new tuple values for feature");

            // Saving as a set to keep track of old entries to be deleted
            HashSet<String> applicData =
               Sets.newHashSet(orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ApplicabilityDefinition,
                  tx.getBranch(), featureArt));
            for (String value : feature.getValues()) {
               String applicString = feature.getName() + " = " + value;
               // If the string is successfully removed, then that value already exists. If not, need to add that value into the tuple table.
               if (!applicData.remove(applicString)) {
                  addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, featureArt, tx, applicString);
                  changes = true;
               }
            }
            // Any leftover strings that were not processed means that they are no longer valid and the tuple needs to be removed
            for (String invalidApplic : applicData) {
               tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, featureArt, invalidApplic);
               changes = true;
            }

            if (changes) {
               tx.commit();
            }
         } catch (Exception ex) {
            results.error(Lib.exceptionToString(ex));
         }
      }

      return results;
   }

   @Override
   public FeatureDefinition getFeature(String feature, BranchId branch) {
      if (Strings.isNumeric(feature)) {
         ArtifactReadable featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).andId(
               ArtifactId.valueOf(feature)).asArtifactOrSentinel();
         if (featureArt.isValid()) {
            return getFeatureDefinition(featureArt);
         }
      } else {
         ArtifactReadable featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).andNameEquals(
               feature).asArtifactOrSentinel();
         if (featureArt.isValid()) {
            return getFeatureDefinition(featureArt);
         }
      }
      return FeatureDefinition.SENTINEL;
   }

   @Override
   public Collection<FeatureDefinition> getFeatures(BranchId branch) {
      QueryBuilder featureQuery = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature);
      return featureQuery.asArtifacts().stream().map(art -> getFeatureDefinition(art)).collect(Collectors.toList());
   }

   @Override
   public Collection<FeatureDefinition> getFeaturesByProductApplicability(BranchId branch,
      String productApplicability) {
      QueryBuilder featureQuery =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).and(
            CoreAttributeTypes.ProductApplicability, productApplicability);
      return featureQuery.asArtifacts().stream().map(art -> getFeatureDefinition(art)).collect(Collectors.toList());
   }

   @Override
   public XResultData deleteFeature(ArtifactId feature, BranchId branch) {
      XResultData results = new XResultData();
      try {
         FeatureDefinition featureDef = getFeature(feature.getIdString(), branch);
         if (applicabilityConstraintIncludesFeature(featureDef, branch)) {
            results.error(
               "Cannot delete feature. A feature constraint exists that includes this feature. Delete any feature constraint with this feature before deleting the feature itself.");
            return results;
         }
         TransactionBuilder tx = txFactory.createTransaction(branch, "Delete Feature");
         List<ArtifactReadable> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
         Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
         for (ArtifactToken view : branchViews) {
            Iterable<String> applicData =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, tx.getBranch(), view);
            for (String value : featureDef.getValues()) {
               String applicString = featureDef.getName() + " = " + value;
               if (applicData.toString().contains(applicString)) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, view, applicString);
               }
            }
         }

         /**
          * Removing tuples for ApplicabilityValueData (E1: ArtifactId - E2: ApplicabilityId)<br/>
          * E2 is a string to find existing ApplicabilityId
          */
         Iterable<String> applicData = orcsApi.getQueryFactory().tupleQuery().getTuple2(
            CoreTupleTypes.ApplicabilityDefinition, tx.getBranch(), feature);
         for (String value : featureDef.getValues()) {
            String applicString = featureDef.getName() + " = " + value;
            if (applicData.toString().contains(applicString)) {
               tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, feature, applicString);
            }
         }
         tx.deleteArtifact(feature);
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public Boolean applicabilityConstraintIncludesFeature(FeatureDefinition featureDef, BranchId branch) {
      // query for existing feature constraints
      Multimap<Long, Long> list = ArrayListMultimap.create();
      orcsApi.getQueryFactory().tupleQuery().getTuple2E1E2FromType(CoreTupleTypes.ApplicabilityConstraint, branch,
         list::put);

      // query db for applic tokens
      Collection<ApplicabilityToken> values =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();

      // look for any constraint that includes the feature
      for (Long listKey : list.keySet()) {
         for (Long listValue : list.get(listKey)) {

            ApplicabilityToken child =
               values.stream().filter(a -> a.getIdString().equals(listKey.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);
            ApplicabilityToken parent =
               values.stream().filter(a -> a.getIdString().equals(listValue.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);

            if (child.getName().contains(featureDef.getName()) || parent.getName().contains(featureDef.getName())) {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public List<String> getApplicabilityConstraintConflicts(ApplicabilityId childApplic, ApplicabilityId parentApplic,
      BranchId branch) {
      List<String> conflictsMessage = new ArrayList<String>();

      // query for all features
      ApplicabilityBranchConfig currentBranchConfig = getConfig(branch, ArtifactId.SENTINEL);

      // query db for applic tokens
      Collection<ApplicabilityToken> values =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();

      // find the name of the input applic ids
      ApplicabilityToken childApplicToken =
         values.stream().filter(a -> a.getIdString().equals(childApplic.getId().toString())).findAny().orElse(
            ApplicabilityToken.SENTINEL);
      ApplicabilityToken parentApplicToken =
         values.stream().filter(a -> a.getIdString().equals(parentApplic.getId().toString())).findAny().orElse(
            ApplicabilityToken.SENTINEL);

      // parse the names
      String childApplicName = childApplicToken.getName();
      String parentApplicName = parentApplicToken.getName();

      // if input parent applic is OR compound applic
      // if the config has an applic that does not match input child applic, move to next config (we do not care about parent in that case)
      // each config must have at least one applic that matches one of the applics in the input parent comp applic (if the child applics match of course)
      if (parentApplicName.contains("|")) {

         // remove whitespace and split the string
         childApplicName = childApplicName.replaceAll("\\s", "");
         String[] splitChildApplicName = childApplicName.split("\\=");

         parentApplicName = parentApplicName.replaceAll("\\s", "");
         String[] splitParentApplics = parentApplicName.split("\\|");

         for (ExtendedFeatureDefinition feature1 : currentBranchConfig.getFeatures()) {
            for (NameValuePair config1 : feature1.getConfigurations()) {

               if (feature1.getName().equals(
                  splitChildApplicName[0]) && (config1.getValue().equals(splitChildApplicName[1]))) {

                  // flag and message to track the conflict status of the current config
                  Boolean configIsClean = false;
                  String currentConfigConflicts = "";

                  for (ExtendedFeatureDefinition feature2 : currentBranchConfig.getFeatures()) {

                     for (String singleParentApplic : splitParentApplics) {

                        String[] splitParentApplicName = singleParentApplic.split("\\=");
                        // if the feature name matches the feature used in current parent applic
                        if (feature2.getName().equals(splitParentApplicName[0])) {
                           for (NameValuePair config2 : feature2.getConfigurations()) {

                              if (!configIsClean) {
                                 if (config1.getName().equals(config2.getName()) && config2.getValue().equals(
                                    splitParentApplicName[1])) {
                                    configIsClean = true;
                                 } else if (config1.getName().equals(config2.getName())) {
                                    if (!currentConfigConflicts.equals("")) {
                                       currentConfigConflicts += " | ";
                                    }
                                    currentConfigConflicts +=
                                       "'" + feature2.getName() + " = " + config2.getValue() + "'";
                                 }
                              }
                           }
                        }
                     }
                  }
                  if (!configIsClean) {
                     conflictsMessage.add(currentConfigConflicts + " in Configuration: " + config1.getName());
                  }
               }
            }
         }
      }
      // if input parent applic is AND compound applic
      // if the config has an applic that does not match input child applic, move to next config (we do not care about parent in that case)
      // each config must have all applics match applics found in the input parent compound applic (if the child applics match of course)
      else if (parentApplicName.contains("&")) {

         // remove whitespace and split the string
         childApplicName = childApplicName.replaceAll("\\s", "");
         String[] splitChildApplicName = childApplicName.split("\\=");

         parentApplicName = parentApplicName.replaceAll("\\s", "");
         String[] splitParentApplics = parentApplicName.split("\\&");

         for (ExtendedFeatureDefinition feature1 : currentBranchConfig.getFeatures()) {
            for (NameValuePair config1 : feature1.getConfigurations()) {

               if (feature1.getName().equals(
                  splitChildApplicName[0]) && (config1.getValue().equals(splitChildApplicName[1]))) {

                  String currentConfigConflicts = "";

                  for (ExtendedFeatureDefinition feature2 : currentBranchConfig.getFeatures()) {

                     for (String singleParentApplic : splitParentApplics) {

                        String[] splitParentApplicName = singleParentApplic.split("\\=");
                        // if the feature name matches the feature used in current parent applic
                        if (feature2.getName().equals(splitParentApplicName[0])) {
                           for (NameValuePair config2 : feature2.getConfigurations()) {

                              if (config1.getName().equals(
                                 config2.getName()) && !config2.getValue().equals(splitParentApplicName[1])) {
                                 if (!currentConfigConflicts.equals("")) {
                                    currentConfigConflicts += " & ";
                                 }
                                 currentConfigConflicts += "'" + feature2.getName() + " = " + config2.getValue() + "'";
                              }
                           }
                        }

                     }
                  }
                  conflictsMessage.add(currentConfigConflicts + " in Configuration: " + config1.getName());

               }
            }
         }
      }
      // if input parent applic is singular applic
      else {
         // remove whitespace and split the string
         childApplicName = childApplicName.replaceAll("\\s", "");
         String[] splitChildApplicName = childApplicName.split("\\=");
         parentApplicName = parentApplicName.replaceAll("\\s", "");
         String[] splitParentApplicName = parentApplicName.split("\\=");

         for (ExtendedFeatureDefinition feature1 : currentBranchConfig.getFeatures()) {
            for (NameValuePair config1 : feature1.getConfigurations()) {
               // if child applics match for the current feature applic in the config, the parent applics MUST match
               // if child applics do not match, move on
               if (feature1.getName().equals(
                  splitChildApplicName[0]) && (config1.getValue().equals(splitChildApplicName[1]))) {
                  for (ExtendedFeatureDefinition feature2 : currentBranchConfig.getFeatures()) {
                     for (NameValuePair config2 : feature2.getConfigurations()) {
                        // if input parent applic does not match the applic in the current config, add to conflicts
                        if (config1.getName().equals(config2.getName()) && feature2.getName().equals(
                           splitParentApplicName[0]) && !(config2.getValue().equals(splitParentApplicName[1]))) {
                           conflictsMessage.add(
                              "'" + feature2.getName() + " = " + config2.getValue() + "' in Configuration: '" + config2.getName() + "'");
                        }
                     }
                  }
               }

            }
         }
      }

      return conflictsMessage;
   }

   @Override
   public List<ApplicabilityTokenWithConstraints> getApplicabilityWithConstraints(BranchId branch) {
      // create empty list to populate later
      List<ApplicabilityTokenWithConstraints> applicTokensWithConstraints = new ArrayList<>();
      // create multi map of child and parent applicability IDs in the constraint
      Multimap<Long, Long> list = ArrayListMultimap.create();
      orcsApi.getQueryFactory().tupleQuery().getTuple2E1E2FromType(CoreTupleTypes.ApplicabilityConstraint, branch,
         list::put);

      // query db for applic tokens
      Collection<ApplicabilityToken> values =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();

      // loop through each constraint
      for (Long listKey : list.keySet()) {
         for (Long listValue : list.get(listKey)) {
            boolean childExists = false;

            // find and use the names and ids of the applicabilities that match the child and parent applicabilities in the constraint
            ApplicabilityToken child =
               values.stream().filter(a -> a.getIdString().equals(listKey.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);
            ApplicabilityToken parent =
               values.stream().filter(a -> a.getIdString().equals(listValue.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);

            // check if child exists in the return list
            for (ApplicabilityTokenWithConstraints token1 : applicTokensWithConstraints) {
               boolean parentExists = false;
               // if the child exists
               if (token1.getId().equals(child.getId())) {
                  childExists = true;
                  // if the parent exists
                  for (ApplicabilityTokenWithConstraints token2 : applicTokensWithConstraints) {
                     if (token2.getId().equals(parent.getId())) {
                        parentExists = true;
                        // make existing array empty to avoid cascading list
                        token2.clearConstraints();
                        token1.addConstraint(token2);
                     }
                  }
                  // if the parent does not exist, make the parent and add to the existing child
                  if (!parentExists) {
                     ApplicabilityTokenWithConstraints applicTokenParent =
                        new ApplicabilityTokenWithConstraints(parent.getId(), parent.getName());
                     token1.addConstraint(applicTokenParent);
                  }
               }
            }

            // if the child does not exist in the return list
            if (!childExists) {
               boolean parentExists = false;
               // make the child
               ApplicabilityTokenWithConstraints applicTokenChild =
                  new ApplicabilityTokenWithConstraints(child.getId(), child.getName());
               // check to see if the parent exists
               for (ApplicabilityTokenWithConstraints token1 : applicTokensWithConstraints) {
                  if (token1.getId().equals(parent.getId())) {
                     parentExists = true;
                     applicTokenChild.addConstraint(token1);
                  }
               }
               // if parent does not exist
               if (!parentExists) {
                  ApplicabilityTokenWithConstraints applicTokenParent =
                     new ApplicabilityTokenWithConstraints(parent.getId(), parent.getName());
                  applicTokenChild.addConstraint(applicTokenParent);
               }
               // add to the return list
               applicTokensWithConstraints.add(applicTokenChild);
            }

         }
      }

      return applicTokensWithConstraints;
   }

   @Override
   public XResultData addApplicabilityConstraint(ApplicabilityId applicability1, ApplicabilityId applicability2,
      BranchId branch) {
      XResultData response = new XResultData();
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "Add Applicability Constraint");
      GammaId gamma = GammaId.SENTINEL;
      if (!(gamma = orcsApi.getQueryFactory().tupleQuery().getTuple2GammaFromE1E2(
         CoreTupleTypes.ApplicabilityConstraint, applicability1, applicability2)).isValid()) {
         tx.addTuple2(CoreTupleTypes.ApplicabilityConstraint, applicability1, applicability2);
      } else {
         tx.introduceTuple(CoreTupleTypes.ApplicabilityConstraint, gamma);
      }

      TransactionToken commit = tx.commit();

      if (!commit.isValid()) {
         response.error(
            "Error occurred during commit of adding app Constraint: " + applicability1.getIdString() + " and " + applicability2.getIdString());
      }
      return response;
   }

   @Override
   public XResultData removeApplicabilityConstraint(ApplicabilityId applicability1, ApplicabilityId applicability2,
      BranchId branch) {
      XResultData response = new XResultData();

      // loop through existing constraints to find the match
      Multimap<Long, Long> list = ArrayListMultimap.create();
      orcsApi.getQueryFactory().tupleQuery().getTuple2E1E2FromType(CoreTupleTypes.ApplicabilityConstraint, branch,
         list::put);

      // query db for applic tokens
      Collection<ApplicabilityToken> values =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();

      // loop through each constraint
      for (Long listKey : list.keySet()) {
         for (Long listValue : list.get(listKey)) {

            // find and use the names and ids of the applicabilities that match the child and parent applicabilities in the constraint
            ApplicabilityToken child =
               values.stream().filter(a -> a.getIdString().equals(listKey.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);
            ApplicabilityToken parent =
               values.stream().filter(a -> a.getIdString().equals(listValue.toString())).findAny().orElse(
                  ApplicabilityToken.SENTINEL);

            // if the child and parent match the current listKey and listValue -> delete
            if (applicability1.getId().equals(child.getId())) {
               if (applicability2.getId().equals(parent.getId())) {

                  TransactionBuilder tx =
                     orcsApi.getTransactionFactory().createTransaction(branch, "Delete Applicability Constraint");
                  tx.deleteTuple2(CoreTupleTypes.ApplicabilityConstraint, applicability1, applicability2);
                  TransactionToken commit = tx.commit();

                  if (!commit.isValid()) {
                     response.error(
                        "Error occurred during commit of removing app Constraint: " + applicability1.getIdString() + " and " + applicability2.getIdString());
                  }
               }
            }
         }
      }
      return response;
   }

   @Override
   public XResultData removeApplicabilityFromView(BranchId branch, ArtifactId viewId, String applicability) {
      XResultData results = new XResultData();
      try {
         CreateViewDefinition view = getView(viewId.getIdString(), branch);
         if (orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
            applicability)) {
            TransactionBuilder tx = txFactory.createTransaction(branch, "Remove applicability from configuration");
            tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, applicability);
            tx.commit();
         } else {
            results.error(applicability + " does not exist on configuration: " + view.getName());
         }

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public CreateViewDefinition getView(String view, BranchId branch) {
      CreateViewDefinition viewDef = new CreateViewDefinition();
      if (Strings.isNumeric(view)) {
         ArtifactReadable viewArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andId(
               ArtifactId.valueOf(view)).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();
         if (viewArt.isValid()) {
            viewDef = getViewDefinition(viewArt);
         }
      } else {
         ArtifactReadable viewArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
               view).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();
         if (viewArt.isValid()) {
            viewDef = getViewDefinition(viewArt);
         }
      }
      return viewDef;
   }

   @Override
   public ConfigurationGroupDefinition getConfigurationGroup(String cfgGroup, BranchId branch) {
      ConfigurationGroupDefinition configGroup = new ConfigurationGroupDefinition();
      ArtifactReadable groupArt = ArtifactReadable.SENTINEL;
      if (Strings.isNumeric(cfgGroup)) {
         groupArt = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andId(
            ArtifactId.valueOf(cfgGroup)).asArtifactOrSentinel();
      } else {
         groupArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               cfgGroup).asArtifactOrSentinel();
      }
      if (groupArt.isValid()) {
         configGroup.setName(groupArt.getName());
         configGroup.setId(groupArt.getIdString());
         configGroup.setDescription(groupArt.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
         List<String> views = new ArrayList<>();
         for (ArtifactId view : orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(
            CoreRelationTypes.PlConfigurationGroup_Group, groupArt).asArtifactIds()) {
            views.add(view.getIdString());
         }
         configGroup.setConfigurations(views);
      }

      return configGroup;
   }

   @Override
   public XResultData updateView(CreateViewDefinition view, BranchId branch) {
      XResultData results = new XResultData();
      CreateViewDefinition editView = getView(view.getIdString(), branch);
      if (editView.isInvalid()) {
         results.errorf("Edit failed: invalid view");
         return results;
      }
      if (view.copyFrom.isValid()) {
         results = copyFromView(branch, ArtifactId.valueOf(editView.getId()), view.copyFrom);
      }
      if (results.isErrors()) {
         return results;
      }
      if (view.getProductApplicabilities().isEmpty() && !editView.getProductApplicabilities().isEmpty()) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Remove Configuration product applicabilities");
         tx.deleteAttributes(ArtifactId.valueOf(editView), CoreAttributeTypes.ProductApplicability);
         tx.commit();
      } else if (editView.getProductApplicabilities().isEmpty() || !editView.getProductApplicabilities().equals(
         view.getProductApplicabilities())) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Update Configuration product applicabilities");
         tx.setAttributesFromValues(ArtifactId.valueOf(editView), CoreAttributeTypes.ProductApplicability,
            view.getProductApplicabilities());
         tx.commit();
      }
      if (!view.getDescription().equals(editView.getDescription())) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Update Configuration Description");
         tx.setSoleAttributeValue(ArtifactId.valueOf(view.getId()), CoreAttributeTypes.Description,
            view.getDescription());
         tx.commit();
      }
      if (!view.getName().equals(editView.getName())) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Update Configuration Name");
         tx.setName(ArtifactId.valueOf(editView), view.getName());
         tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(editView.getId()),
            "Config = " + editView.getName());
         tx.deleteTuple2(CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(editView.getId()),
            "Config = " + editView.getName());

         addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(view.getId()), tx,
            "Config = " + view.getName());
         addIntroduceTuple2(CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(view.getId()), tx,
            "Config = " + view.getName());

         tx.commit();
      }
      if (!(view.getConfigurationGroup().isEmpty()) || !(editView.getConfigurationGroup().isEmpty())) {
         if (!view.getConfigurationGroup().equals(editView.getConfigurationGroup())) {
            List<String> editViewList =
               editView.getConfigurationGroup().stream().map(a -> a.getIdString()).collect(Collectors.toList());
            List<String> newViewList =
               view.getConfigurationGroup().stream().map(a -> a.getIdString()).collect(Collectors.toList());
            List<String> editViewDoesNotContain =
               editViewList.stream().filter(b -> !newViewList.contains(b)).collect(Collectors.toList());
            List<String> newViewDoesNotContain =
               newViewList.stream().filter(b -> !editViewList.contains(b)).collect(Collectors.toList());
            for (String group : editViewDoesNotContain) {
               results = unrelateCfgGroupToView(group, editView.getIdString(), branch);
            }
            for (String group : newViewDoesNotContain) {
               results = relateCfgGroupToView(group, editView.getIdString(), branch);
            }
            if (results.isErrors()) {
               return results;
            }
         }
      }

      return results;
   }

   @Override
   public XResultData createView(CreateViewDefinition view, BranchId branch) {
      XResultData results = new XResultData();
      if (!Strings.isValid(view.getName())) {
         results.errorf("Name can not be empty for configuration %s", view.getId());
         return results;
      }
      CreateViewDefinition newView = getView(view.getName(), branch);
      if (newView.isValid()) {
         results.errorf("Configuration Name is already in use.");
         return results;
      }
      if ((newView.isInvalid())) {
         try {

            TransactionBuilder tx = txFactory.createTransaction(branch, "Create View ");
            ArtifactToken vDefArt = ArtifactToken.SENTINEL;
            vDefArt =
               tx.createArtifact(getConfigurationsFolder(tx.getBranch()), CoreArtifactTypes.BranchView, view.getName());
            tx.setSoleAttributeValue(vDefArt, CoreAttributeTypes.Description, view.getDescription());
            if (!view.getProductApplicabilities().isEmpty()) {
               tx.setAttributesFromValues(vDefArt, CoreAttributeTypes.ProductApplicability,
                  view.getProductApplicabilities());
            }
            tx.commit();

            //Had issues trying to set tuple values on a newly created artifact that hadn't yet been committed.
            //so committing first, then adding standard applicabilities
            TransactionBuilder tx2 =
               txFactory.createTransaction(branch, "Create Config and Base applicabilities on New View");
            addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, vDefArt, tx2, "Config = " + view.getName());
            tx2.createApplicabilityForView(vDefArt, "Base");
            tx2.createApplicabilityForView(vDefArt, "Config = " + view.getName());
            tx2.commit();
         } catch (Exception ex) {
            results.errorf(Lib.exceptionToString(ex));
            return results;
         }
         newView = getView(view.getName(), branch);
      }
      if (view.getCopyFrom().isValid() || !(view.getConfigurationGroup().isEmpty())) {
         if (newView.isValid()) {
            if (view.getCopyFrom().isValid()) {
               results = copyFromView(branch, ArtifactId.valueOf(newView), view.copyFrom);
               if (results.isErrors()) {
                  return results;
               }
            }
            if (!(view.getConfigurationGroup().isEmpty())) {
               for (ArtifactId group : view.getConfigurationGroup()) {
                  results = relateCfgGroupToView(group.getIdString(), newView.getIdString(), branch);
               }
               if (results.isErrors()) {
                  return results;
               }
            }
         } else {
            results.error("Errors creating new configuration: " + view.getName());
         }
      }

      return results;
   }

   @Override
   public XResultData deleteView(String view, BranchId branch) {
      XResultData results = new XResultData();
      try {
         CreateViewDefinition viewDef = getView(view, branch);
         //before removing view unrelate from group and remove associated applicability tag from
         //list of valid tags from the group
         if (!(viewDef.getConfigurationGroup().isEmpty())) {
            for (ArtifactId group : viewDef.getConfigurationGroup()) {
               unrelateCfgGroupToView(group.getIdString(), viewDef.getIdString(), branch);
            }
         }
         Iterable<String> deleteApps = orcsApi.getQueryFactory().tupleQuery().getTuple2(
            CoreTupleTypes.ViewApplicability, branch, ArtifactId.valueOf(viewDef.getId()));

         ArtifactToken viewArt = (ArtifactToken) viewDef.getData();
         if (viewArt.isValid()) {
            TransactionBuilder txApps =
               txFactory.createTransaction(branch, "Delete all applicabilities for deleted view");
            for (String app : deleteApps) {
               txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(viewDef.getId()), app);
            }
            txApps.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, viewArt, "Config = " + viewDef.getName());
            txApps.commit();
            TransactionBuilder tx = txFactory.createTransaction(branch, "Delete View");
            tx.deleteArtifact(viewArt);
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData setApplicability(BranchId branch, ArtifactId view, ArtifactId feature, String applicability) {
      XResultData results = new XResultData();
      try {
         ArtifactReadable featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andId(feature).asArtifactOrSentinel();
         FeatureDefinition fDef = orcsApi.getApplicabilityOps().getFeatureDefinition(featureArt);
         TransactionBuilder tx = txFactory.createTransaction(branch, "Set View Feature Applicability");
         List<String> existingValues = new LinkedList<>();
         for (String appl : orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch,
            view)) {
            if (appl.startsWith(fDef.getName() + " = ")) {
               existingValues.add(appl);
            }
         }
         List<String> newValues = new LinkedList<>();
         for (String value : applicability.split(";")) {
            value = value.replace("^ +", "");
            value = value.replace(" +$", "");
            value = featureArt.getName() + " = " + value;
            newValues.add(value);
         }
         boolean change = false;
         // delete existing if not match value
         if (!existingValues.toString().equals("[]")) {
            for (String existingValue : existingValues) {
               if (!newValues.contains(existingValue)) {
                  change = true;
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, view, existingValue);
               }
            }
            // add new
            for (String newValue : newValues) {
               if (!existingValues.contains(newValue)) {
                  change = true;
                  addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, newValue);
               }
            }
         } else {
            for (String newValue : newValues) {
               change = true;
               addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, newValue);
            }
         }
         if (change) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   public XResultData copyFromView(BranchId branch, ArtifactId view, ArtifactId copy_from) {
      XResultData results = new XResultData();
      try {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Set configuration/View Feature Applicability");

         List<String> existingValues = new LinkedList<>();
         for (String appl : orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability,
            tx.getBranch(), view)) {
            if (!(appl.startsWith("Config") || appl.startsWith("Base"))) {
               existingValues.add(appl);
            }
         }
         List<String> newValues = new LinkedList<>();
         for (String appl : orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability,
            tx.getBranch(), copy_from)) {
            if (!(appl.startsWith("Config") || appl.startsWith("Base"))) {
               newValues.add(appl);
            }
         }
         boolean change = false;
         // delete existing if not match value
         if (!existingValues.toString().equals("[]")) {
            for (String existingValue : existingValues) {
               if (!newValues.contains(existingValue)) {
                  change = true;
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, view, existingValue);
               }
            }
            // add new
            for (String newValue : newValues) {
               if (!existingValues.contains(newValue)) {
                  change = true;
                  addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, newValue);
               }
            }
         } else {
            for (String newValue : newValues) {
               change = true;
               addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, newValue);
            }
         }
         if (change) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData createApplicabilityForView(ArtifactId viewId, String applicability, BranchId branch) {
      XResultData results = new XResultData();

      if (results.isErrors()) {
         return results;
      }

      if (!orcsApi.getQueryFactory().applicabilityQuery().viewExistsOnBranch(branch, viewId)) {
         results.error("View is invalid.");
         return results;
      }
      if (orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
         applicability)) {
         String featureName = applicability.substring(0, applicability.indexOf("=") - 1);
         FeatureDefinition feature = getFeature(featureName, branch);
         if (!feature.isMultiValued()) {
            results.error("Applicability already exists.");
            return results;
         }
      }
      if (applicability.startsWith("Config =")) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Create applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.startsWith("ConfigurationGroup =")) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Create applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.equals("Base")) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Create applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.contains("|") || applicability.contains("&")) {
         boolean validApplicability = false;
         if (applicability.contains("|")) {
            for (String value : applicability.split("\\|")) {
               /**
                * loop through existing applicabilities for view and see if new applicability exists if so, stop else
                * check that at least one of the | separated applicability exists
                **/
               Iterable<String> existingApps =
                  orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
               for (String appl : existingApps) {
                  if (appl.equals(value.trim())) {
                     validApplicability = true;
                  }
               }
            }
         } else {
            int cnt = applicability.split("&").length;
            int validCnt = 0;
            for (String value : applicability.split("&")) {
               /**
                * loop through existing applicabilities for view and see if new applicability exists if so, stop else
                * check that ALL of the & separated applicability exist
                **/
               Iterable<String> existingApps =
                  orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
               for (String appl : existingApps) {
                  if (appl.equals(value.trim())) {
                     validCnt++;
                  }
               }
            }
            if (cnt == validCnt) {
               validApplicability = true;
            }
         }
         if (validApplicability) {
            TransactionBuilder tx = txFactory.createTransaction(branch, "Set applicability for view");
            tx.createApplicabilityForView(viewId, applicability);
            tx.commit();
            /**
             * Once a new compound applicability tag is created, it must be evaluated whether the tag applies for each
             * view on the branch
             */
            for (ArtifactId bView : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
               updateCompoundApplicabilities(branch, bView, true);
            }
         } else {
            results.error(
               "Invalid applicability tag.  One of the applicabilities used is not valid for the given view.");
         }

      } else {
         String featureName = applicability.substring(0, applicability.indexOf("=") - 1);
         String featureValue = applicability.substring(applicability.indexOf("=") + 2);
         FeatureDefinition feature = getFeature(featureName, branch);
         if (feature.isInvalid()) {
            results.error(feature.getName() + " is not a valid feature for given branch");
         } else if (feature.isMultiValued()) {
            List<String> newValues = new ArrayList<>();

            for (String val : featureValue.split(",")) {
               if (!feature.getValues().contains(val)) {
                  results.error(val + " is not a valid value for " + feature.getName());
                  return results;
               } else {
                  newValues.add(featureName + " = " + val);
               }
            }
            List<String> existingValues = new LinkedList<>();
            Iterable<String> existingApps =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
            for (String appl : existingApps) {
               if (appl.startsWith(featureName + " = ")) {
                  existingValues.add(appl);
               }
            }
            TransactionBuilder txMv = txFactory.createTransaction(branch, "Set applicability for view");
            List<String> removeValues = new ArrayList<>(existingValues);
            //existingValues minus newValues = values to remove
            removeValues.removeAll(newValues);
            for (String val : removeValues) {
               txMv.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, val);
            }
            //newValues minus existingValues = values to add
            newValues.removeAll(existingValues);
            for (String val : newValues) {
               if (passesApplicabilityConstraint(branch, viewId, applicability, results)) {
                  txMv.createApplicabilityForView(viewId, val);
               }
            }
            txMv.commit();
            updateCompoundApplicabilities(branch, viewId, true);
            for (ArtifactReadable grp : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
               branch).stream().filter(
                  a -> a.getRelated(CoreRelationTypes.PlConfigurationGroup_BranchView).getList().stream().anyMatch(
                     b -> b.getId().equals(viewId.getId()))).collect(Collectors.toList())) {
               syncConfigGroup(branch, grp.getIdString(), results);
            }
         } else {
            if (feature.getValues().contains(featureValue)) {
               List<String> existingValues = new LinkedList<>();
               Iterable<String> existingApps =
                  orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
               for (String appl : existingApps) {
                  if (appl.startsWith(featureName + " = ")) {
                     existingValues.add(appl);
                  }
               }
               if (passesApplicabilityConstraint(branch, viewId, applicability, results)) {
                  TransactionBuilder tx = txFactory.createTransaction(branch, "Set applicability for view");

                  for (String existingValue : existingValues) {
                     tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, existingValue);
                  }

                  tx.createApplicabilityForView(viewId, applicability);
                  tx.commit();
                  updateCompoundApplicabilities(branch, viewId, true);
                  for (ArtifactReadable grp : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
                     branch).stream().filter(
                        a -> a.getRelated(
                           CoreRelationTypes.PlConfigurationGroup_BranchView).getList().stream().anyMatch(
                              b -> b.getId().equals(viewId.getId()))).collect(Collectors.toList())) {
                     syncConfigGroup(branch, grp.getIdString(), results);
                  }
               }

            } else {
               results.error(featureValue + " is an invalid value");
            }
         }

      }

      return results;
   }

   public boolean passesApplicabilityConstraint(BranchId branch, ArtifactId viewId, String applic,
      XResultData results) {
      Long appId = orcsApi.getKeyValueOps().getByValue(applic);

      List<ApplicabilityToken> constraints = new LinkedList<>();

      orcsApi.getQueryFactory().tupleQuery().getTuple2NamedId(CoreTupleTypes.ApplicabilityConstraint, branch,
         ApplicabilityId.valueOf(appId), (e2, value) -> constraints.add(ApplicabilityToken.valueOf(e2, value)));

      for (ApplicabilityToken appMustExist : constraints) {

         String requiredApp = orcsApi.getKeyValueOps().getByKey(appMustExist.getId());
         if (!orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
            requiredApp)) {
            results.error("Applic: " + applic + " requires that " + requiredApp + " must also be applied for view");
            return false;
         }
      }
      return true;
   }

   @Override
   public XResultData createCompoundApplicabilityForBranch(String applicability, BranchId branch) {
      XResultData results = new XResultData();

      if (results.isErrors()) {
         return results;
      }
      /**
       * See if the applicability string already exists in tuple2 table as e2. Lookup key value for the string. See if
       * associated id exists on branch.
       */
      if (applicability.contains("|") || applicability.contains("&")) {
         Set<Entry<Long, ApplicabilityToken>> entrySet =
            orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).entrySet();

         if (entrySet.stream().anyMatch(a -> a.getValue().getName().equals(applicability.trim()))) {
            results.error("\"" + applicability + "\" already exists on the branch.");
            return results;
         }
         ;
         String[] splitString = (applicability.contains("|")) ? applicability.split("\\|") : applicability.split("&");

         for (String value : splitString) {
            /**
             * loop through existing applicabilities for branch and see if new applicability exists if so, stop else
             * check that ALL of the & separated applicability exist
             **/
            if (!entrySet.stream().anyMatch(a -> a.getValue().getName().equals(value.trim()))) {
               results.error(
                  "Invalid applicability tag. \"" + value.trim() + "\" does not match any existing applicability on the branch.");
               return results;
            }
         }

         TransactionBuilder tx = txFactory.createTransaction(branch, "Set applicability for view");

         addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.SENTINEL, tx, applicability.trim());

         tx.commit();
         /**
          * Once a new compound applicability tag is created, it must be evaluated whether the tag applies for each view
          * on the branch
          */
         for (ArtifactId bView : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
            updateCompoundApplicabilities(branch, bView, true);
         }

      } else {
         results.error("Invalid applicability tag. Must contain | or &");
      }
      return results;
   }

   @Override
   public XResultData deleteCompoundApplicabilityFromBranch(ApplicabilityId compApplicId, BranchId branch) {
      XResultData results = new XResultData();

      try {
         /**
          * Remove compound applicability from each view. Then remove compound applicability from the entire branch.
          */
         TransactionBuilder tx = txFactory.createTransaction(branch, "Delete Compound Applicability");

         Iterable<Long> listOfApplicsForViews = orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(
            CoreTupleTypes.ViewApplicability, branch, compApplicId.getId());

         for (Long viewId : listOfApplicsForViews) {
            GammaId id = orcsApi.getQueryFactory().tupleQuery().getTuple2GammaFromE1E2Raw(
               CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(viewId), compApplicId.getId());
            tx.deleteTuple2(id);
         }

         Iterable<Long> listOfApplics = orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(
            CoreTupleTypes.ApplicabilityDefinition, branch, compApplicId.getId());

         for (Long viewId : listOfApplics) {
            GammaId id = orcsApi.getQueryFactory().tupleQuery().getTuple2GammaFromE1E2Raw(
               CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(viewId), compApplicId.getId());
            tx.deleteTuple2(id);
         }

         tx.commit();

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   private XResultData updateCompoundApplicabilities(BranchId branch, ArtifactId viewId, boolean update) {
      /**
       * After updating an value on the feature value matrix for a specific view; there is a need to evaluate each of
       * the existing compound applicabilities on a branch to see if the applicability is valid for the view.
       */
      XResultData results = new XResultData();
      List<String> actions = new ArrayList<>();
      Collection<ApplicabilityToken> allApps =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();
      List<ApplicabilityToken> compoundApps =
         allApps.stream().filter(p -> p.getName().contains("|") || p.getName().contains("&")).collect(
            Collectors.toList());
      ArtifactToken view = orcsApi.getQueryFactory().fromBranch(branch).andId(viewId).asArtifactTokenOrSentinel();
      for (ApplicabilityToken app : compoundApps) {
         boolean validApplicability = false;
         if (app.getName().contains("|")) {
            for (String value : app.getName().split("\\|")) {
               /**
                * loop through existing applicabilities for view and see if new applicability exists if so, stop else
                * check that at least one of the | separated applicability exists
                **/
               Iterable<String> existingApps =
                  orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
               for (String appl : existingApps) {
                  if (appl.equals(value.trim())) {
                     validApplicability = true;
                  }
               }
            }
         } else {
            int cnt = app.getName().split("&").length;
            int validCnt = 0;
            for (String value : app.getName().split("&")) {
               /**
                * loop through existing applicabilities for view and see if new applicability exists if so, stop else
                * check that ALL of the & separated applicability exist
                **/
               Iterable<String> existingApps =
                  orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
               for (String appl : existingApps) {
                  if (appl.equals(value.trim())) {
                     validCnt++;
                  }
               }

            }
            if (cnt == validCnt) {
               validApplicability = true;
            }
         }

         if (orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
            app.getName())) {
            if (!validApplicability) {
               if (update) {
                  TransactionBuilder tx = txFactory.createTransaction(branch, "Remove invalid compound applicability");
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, app.getName());
                  tx.commit();
               }
               actions.add("Remove " + app.getName() + " from configuration: " + view.getName());
            }
         } else {
            if (validApplicability) {
               if (update) {
                  TransactionBuilder tx = txFactory.createTransaction(branch, "Apply valid compound applicability");
                  tx.createApplicabilityForView(viewId, app.getName());
                  tx.commit();
               }
               actions.add("Add " + app.getName() + " to configuration: " + view.getName());
            }
         }
      }
      if (!actions.isEmpty()) {
         results.setResults(actions);
      }
      return results;
   }

   @Override
   public List<FeatureDefinition> getFeatureDefinitionData(BranchId branch) {
      return orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(branch);

   }

   @Override
   public XResultData createCfgGroup(ConfigurationGroupDefinition group, BranchId branch) {
      XResultData results = new XResultData();
      if (!Strings.isValid(group.getName())) {
         results.errorf("Name can not be empty for Configuration Group: %s", group.getName());
         return results;
      }

      //make sure the groupName does not exist already as a group
      if (orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
         CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
            group.getName()).exists()) {
         results.errorf("Configuration Group Name already exists");
         return results;
      }
      try {

         TransactionBuilder tx = txFactory.createTransaction(branch, "Create PL Configuration Group");
         ArtifactToken vDefArt = null;

         vDefArt = tx.createArtifact(getPlConfigurationGroupsFolder(tx.getBranch()), CoreArtifactTypes.GroupArtifact,
            group.getName());
         tx.setName(vDefArt, group.getName());
         tx.setSoleAttributeValue(vDefArt, CoreAttributeTypes.Description, group.getDescription());
         addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, vDefArt, tx,
            "ConfigurationGroup = " + vDefArt.getName());
         // reload artifact to return
         tx.commit();
         ArtifactId newGrp =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               group.getName()).asArtifactId();
         TransactionBuilder tx2 =
            txFactory.createTransaction(branch, "Create Config and Base applicabilities on new view");
         tx2.createApplicabilityForView(newGrp, "Base");
         tx2.createApplicabilityForView(newGrp, "ConfigurationGroup = " + group.getName());
         for (FeatureDefinition feature : orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(
            branch)) {
            tx2.createApplicabilityForView(newGrp, feature.getName() + " = " + feature.getDefaultValue());
         }
         tx2.commit();
         results.getIds().add(newGrp.getIdString());
         if (!group.getConfigurations().isEmpty()) {
            for (String cfg : group.getConfigurations()) {
               relateCfgGroupToView(newGrp.getIdString(), cfg, branch);
            }
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData updateCfgGroup(ConfigurationGroupDefinition group, BranchId branch) {
      XResultData results = new XResultData();
      ConfigurationGroupDefinition currentGroup = getConfigurationGroup(group.getId(), branch);
      if (!currentGroup.getName().equals(Strings.EMPTY_STRING)) {
         try {
            if (!group.getName().equals(currentGroup.getName())) {
               TransactionBuilder tx = txFactory.createTransaction(branch, "Update PL Configuration Group Name");
               tx.setName(ArtifactId.valueOf(group.getId()), group.getName());
               tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(group.getId()),
                  "ConfigurationGroup = " + currentGroup.getName());
               addIntroduceTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(group.getId()), tx,
                  "ConfigurationGroup = " + group.getName());
               tx.commit();
            }
            if (!group.getDescription().equals(currentGroup.getDescription())) {
               TransactionBuilder tx = txFactory.createTransaction(branch, "Update PL Configuration Description");
               tx.setSoleAttributeValue(ArtifactId.valueOf(group.getId()), CoreAttributeTypes.Description,
                  group.getDescription());
               tx.commit();
            }
            if (!group.getConfigurations().toString().equals(currentGroup.getConfigurations().toString())) {
               for (String cfg : currentGroup.getConfigurations()) {
                  if (!group.getConfigurations().contains(cfg)) {
                     results = unrelateCfgGroupToView(currentGroup.getId(), cfg, branch);
                  }
               }
               for (String cfg : group.getConfigurations()) {
                  if (!currentGroup.getConfigurations().contains(cfg)) {
                     results = relateCfgGroupToView(currentGroup.getId(), cfg, branch);
                  }
               }
            }
         } catch (Exception ex) {
            results.error(Lib.exceptionToString(ex));
         }
      } else {
         results.errorf("Configuration Group does not exist");
      }
      return results;
   }

   @Override
   public XResultData relateCfgGroupToView(String groupId, String viewId, BranchId branch) {
      XResultData results = new XResultData();
      ArtifactToken cfgGroup;
      if (Strings.isNumeric(groupId)) {
         cfgGroup = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(groupId)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokenOrSentinel();
      } else {
         cfgGroup =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  groupId).asArtifactTokenOrSentinel();
      }
      ArtifactReadable view;
      if (Strings.isNumeric(viewId)) {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andId(
            ArtifactId.valueOf(viewId)).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();

      } else {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewId).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();
      }

      if (cfgGroup.isInvalid()) {
         results.errorf("Configuration Group does not exist");
         return results;
      }
      if (view.isInvalid()) {
         results.errorf("View name does not exist");
         return results;
      }
      List<ArtifactReadable> currentGroup = view.getRelated(CoreRelationTypes.PlConfigurationGroup_Group).getList();
      if (!(currentGroup.isEmpty()) && !(currentGroup.stream().filter(o -> o.equals(cfgGroup)).collect(
         Collectors.toList()).isEmpty())) {
         results.errorf("View is already in the group");
         return results;
      }
      try {

         TransactionBuilder tx = txFactory.createTransaction(branch,
            "Relate view: " + view.getName() + " to PL Configuration Group " + cfgGroup.getName());
         tx.relate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
         tx.createApplicabilityForView(view, "ConfigurationGroup = " + cfgGroup.getName());
         tx.createApplicabilityForView(cfgGroup, "Config = " + view.getName());
         tx.commit();
         syncConfigGroup(branch, cfgGroup.getIdString(), results);

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData unrelateCfgGroupToView(String groupId, String viewId, BranchId branch) {
      XResultData results = new XResultData();
      ArtifactToken cfgGroup;
      if (Strings.isNumeric(groupId)) {
         cfgGroup = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(groupId)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokenOrSentinel();
      } else {
         cfgGroup =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  groupId).asArtifactTokenOrSentinel();
      }
      ArtifactToken view;
      if (Strings.isNumeric(viewId)) {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andId(
            ArtifactId.valueOf(viewId)).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();

      } else {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewId).follow(CoreRelationTypes.PlConfigurationGroup_Group).asArtifactOrSentinel();
      }

      if (cfgGroup.isInvalid()) {
         results.errorf("Configuration Group does not exist");
         return results;
      }
      if (view.isInvalid()) {
         results.errorf("View name does not exist");
         return results;
      }
      try {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Un-Relate view from PL Configuration Group ");
         //relate to group
         tx.unrelate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
         tx.deleteTuple2(CoreTupleTypes.ViewApplicability, view, "ConfigurationGroup = " + cfgGroup.getName());
         tx.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, "Config = " + view.getName());
         tx.commit();
         syncConfigGroup(branch, cfgGroup.getIdString(), results);
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData deleteCfgGroup(String id, BranchId branch) {
      XResultData results = new XResultData();
      ArtifactReadable cfgGroup;
      if (Strings.isNumeric(id)) {
         cfgGroup = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(id)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).asArtifactOrSentinel();
      } else {
         cfgGroup =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  id).asArtifactOrSentinel();
      }
      if (cfgGroup.isInvalid()) {
         results.errorf("Configuration Group does not exist");
         return results;
      }
      try {
         //unrelate group from each view
         //will remove applicability tag for configuration group from each view
         for (ArtifactReadable view : cfgGroup.getRelated(
            CoreRelationTypes.PlConfigurationGroup_BranchView).getList()) {
            unrelateCfgGroupToView(cfgGroup.getIdString(), view.getIdString(), branch);
         }
         Iterable<String> deleteApps =
            orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, cfgGroup);

         TransactionBuilder txApps =
            txFactory.createTransaction(branch, "Delete Applicabilities associated with ConfigurationGroup");
         for (String app : deleteApps) {
            txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, app);
         }
         txApps.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, cfgGroup,
            "ConfigurationGroup = " + cfgGroup.getName());
         txApps.commit();
         TransactionBuilder tx = txFactory.createTransaction(branch, "Delete Cfg Group");
         tx.deleteArtifact(cfgGroup);
         tx.commit();
         results.getIds().add(id);
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData syncConfigGroup(BranchId branch, String id, XResultData results) {
      if (results == null) {
         results = new XResultData();
      }
      ArtifactReadable cfgGroup;
      if (Strings.isNumeric(id)) {
         cfgGroup = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(id)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).asArtifact();
      } else {
         cfgGroup =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  id).asArtifact();
      }
      if (cfgGroup.isValid()) {
         List<ArtifactReadable> views =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andRelatedTo(
               CoreRelationTypes.PlConfigurationGroup_Group, cfgGroup).asArtifacts();
         ApplicabilityBranchConfig current = getConfig(branch, ArtifactId.SENTINEL);
         ConfigurationGroupDefinition currentGroup =
            current.getGroups().stream().filter(p -> p.getId().equals(id)).findFirst().get();
         if (!currentGroup.getConfigurations().isEmpty()) {
            for (ExtendedFeatureDefinition feature : current.getFeatures()) {
               String resultApp = null;
               List<String> memberApps = new ArrayList<>();
               List<String> groupApps = new ArrayList<>();
               String currentApp = "";
               //applicability is the full text of [feature] = [value]
               //the values stored in NameValuePair is just the right hand of the syntax
               //in order to update the tuple the applicability must be constructed

               if (feature.isMultiValued()) {
                  for (NameValuePair pair : feature.getConfigurations()) {

                     //if the current name/value is for the group use this to populate the current apps in the group
                     if (pair.getName().equals(cfgGroup.getName())) {
                        for (String val : pair.getValues()) {
                           groupApps.add(feature.getName() + " = " + val);
                        }
                     }
                     boolean isInGroup = !views.stream().noneMatch(v -> v.getName().equals(pair.getName()));

                     //if current name is not the group name populate the memberValues array which
                     //represents what should be in the group
                     if (isInGroup) {
                        for (String val : pair.getValues()) {
                           String applicability = feature.getName() + " = " + val;

                           if (!memberApps.contains(applicability)) {
                              memberApps.add(applicability);
                           }
                        }
                     }
                  }
               } else {
                  if (feature.getValues().contains("Included")) {
                     resultApp = feature.getName() + " = Excluded";
                  }
                  for (NameValuePair pair : feature.getConfigurations()) {
                     boolean isInGroup = false;

                     if (pair.getName().equals(cfgGroup.getName())) {
                        currentApp = feature.getName() + " = " + pair.getValue();
                     } else {
                        isInGroup = !views.stream().noneMatch(v -> v.getName().equals(pair.getName()));
                     }
                     views.get(0).getName();
                     if (isInGroup) {
                        String applicability = feature.getName() + " = " + pair.getValue();
                        if (feature.getValues().contains("Included")) {
                           if (pair.getValue().equals("Included")) {

                              resultApp = applicability;
                              break;
                           }
                        } else {
                           if (resultApp == null) {
                              resultApp = applicability;
                           } else {
                              if (!resultApp.equals(applicability)) {
                                 results.error(
                                    "Updating Group: " + cfgGroup.getName() + ". Applicabilities differ for non-binary feature: " + feature.getName());
                              }
                           }
                        }
                     }
                  }
               }

               if (!feature.isMultiValued()) {

                  if (!currentApp.equals(resultApp) && results.isSuccess()) {
                     TransactionBuilder tx = txFactory.createTransaction(branch, "Set applicability for view");
                     tx.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, currentApp);
                     tx.createApplicabilityForView(cfgGroup, resultApp);
                     tx.commit();
                  }
               } else if (results.isSuccess()) {
                  TransactionBuilder tx2 = txFactory.createTransaction(branch, "Set applicability for view");

                  List<String> removeValues = new ArrayList<>(groupApps);
                  //groupValues minus memberValues = values to remove
                  removeValues.removeAll(memberApps);
                  for (String val : removeValues) {
                     tx2.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, val);
                  }
                  //memberValues minus groupValues = values to add
                  memberApps.removeAll(groupApps);
                  for (String val : memberApps) {
                     tx2.createApplicabilityForView(cfgGroup, val);
                  }
                  tx2.commit();
               }
            }
            updateCompoundApplicabilities(branch, cfgGroup, true);
         }
      } else {
         results.error("Invalid Configuration Group name.");
      }
      return results;
   }

   @Override
   public XResultData syncConfigGroup(BranchId branch) {
      XResultData results = new XResultData();
      for (ArtifactToken group : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
         branch)) {
         syncConfigGroup(branch, group.getIdString(), results);
      }
      return results;
   }

   @Override
   public String evaluateApplicabilityExpression(BranchId branch, ArtifactToken view, ApplicabilityBlock applic) {
      return new BlockApplicabilityOps(orcsApi, logger, branch, view).evaluateApplicabilityExpression(applic);
   }

   @Override
   public XResultData applyApplicabilityToFiles(BlockApplicabilityStageRequest data, BranchId branch) {
      XResultData results = new XResultData();
      results.setLogToSysErr(true);
      String sourcePath = data.getSourcePath();
      String stagePath = data.getStagePath();
      String customStageDir = data.getCustomStageDir();
      if (sourcePath == null || stagePath == null) {
         results.error("Both a source path and stage path are required\n");
         return results;
      }

      if (sourcePath.equalsIgnoreCase(stagePath)) {
         results.error("Source path and stage path can't be same\n");
         return results;
      }

      boolean commentNonApplicableBlocks = data.isCommentNonApplicableBlocks();
      Entry<Long, String> viewInfo = data.getViews().entrySet().iterator().next();
      ArtifactId viewId = ArtifactId.valueOf(viewInfo.getKey());
      String cachePath = viewInfo.getValue();
      BlockApplicabilityOps ops = getBlockApplicabilityOps(results, data, viewId, cachePath, branch);
      if (ops == null || results.isErrors()) {
         return results;
      }

      stagePath = getFullStagePath(results, stagePath, customStageDir, ops.getOpsView().getName());
      if (results.isErrors()) {
         return results;
      }

      return ops.applyApplicabilityToFiles(results, commentNonApplicableBlocks, sourcePath, stagePath, customStageDir);
   }

   @Override
   public XResultData refreshStagedFiles(BlockApplicabilityStageRequest data, BranchId branch) {
      XResultData results = new XResultData();
      results.setLogToSysErr(true);

      String sourcePath = data.getSourcePath();
      String stagePath = data.getStagePath();
      String stageDir = data.getCustomStageDir();
      List<String> files = data.getFiles();

      if (sourcePath == null || stagePath == null) {
         results.error("Both a source path and stage path are required");
         return results;
      }

      boolean commentNonApplicableBlocks = data.isCommentNonApplicableBlocks();
      Entry<Long, String> viewInfo = data.getViews().entrySet().iterator().next();
      ArtifactId viewId = ArtifactId.valueOf(viewInfo.getKey());
      String cachePath = viewInfo.getValue();
      BlockApplicabilityOps ops = getBlockApplicabilityOps(results, data, viewId, cachePath, branch);
      if (ops == null) {
         return results;
      }

      stagePath = getFullStagePath(results, stagePath, stageDir, ops.getOpsView().getName());
      if (results.isErrors()) {
         return results;
      }

      ops.setUpBlockApplicability(commentNonApplicableBlocks);

      return ops.refreshStagedFiles(results, sourcePath, stagePath, stageDir, files);
   }

   @Override
   public XResultData startWatcher(BlockApplicabilityStageRequest data, BranchId branch) {
      XResultData results = new XResultData();
      results.setLogToSysErr(true);

      if (fileWatcher == null) {
         fileWatcher = new StagedFileWatcher(results);
      }

      String sourcePath = data.getSourcePath();
      String stagePath = data.getStagePath();
      String stageDir = data.getCustomStageDir();

      if (sourcePath == null || stagePath == null) {
         results.error("Both a source path and stage path are required");
         return results;
      }

      boolean commentNonApplicableBlocks = data.isCommentNonApplicableBlocks();

      for (Map.Entry<Long, String> viewInfo : data.getViews().entrySet()) {
         ArtifactId viewId = ArtifactId.valueOf(viewInfo.getKey());
         String cachePath = viewInfo.getValue();
         BlockApplicabilityOps ops = getBlockApplicabilityOps(results, data, viewId, cachePath, branch);
         if (ops == null) {
            return results;
         }

         String fullStagePath = getFullStagePath(results, stagePath, stageDir, ops.getOpsView().getName());
         if (results.isErrors()) {
            return results;
         }

         ops.setUpBlockApplicability(commentNonApplicableBlocks);
         fileWatcher.addView(ops.getOpsView(), fullStagePath, ops);
      }

      Thread watcherThread = new Thread(new Runnable() {
         @Override
         public void run() {
            fileWatcher.runWatcher(data, data.getSourcePath());
         }
      }, "Starting StagedFileWatcher");
      watcherThread.start();

      results.log("Watcher is running");
      return results;
   }

   @Override
   public XResultData stopWatcher() {
      XResultData results = new XResultData();
      if (fileWatcher == null) {
         results.error("File Watcher has yet to be started");
         return results;
      }

      fileWatcher.stopWatcher();
      fileWatcher = null;

      results.log("Watcher has stopped");
      return results;
   }

   private BlockApplicabilityOps getBlockApplicabilityOps(XResultData results, BlockApplicabilityStageRequest data,
      ArtifactId viewId, String cachePath, BranchId branch) {
      BlockApplicabilityOps ops = null;
      ArtifactToken viewToken;

      if (cachePath.isEmpty()) {
         // The user has not given a cache to use for processing
         viewToken = orcsApi.getQueryFactory().fromBranch(branch).andId(viewId).asArtifactToken();
         ops = new BlockApplicabilityOps(orcsApi, logger, branch, viewToken);
      } else {
         // The user has given a cache to use
         File cacheFile = new File(cachePath);
         if (cacheFile.exists()) {
            ObjectMapper objMap = new ObjectMapper();
            BlockApplicabilityCacheFile cache;
            try {
               cache = objMap.readValue(cacheFile, BlockApplicabilityCacheFile.class);
            } catch (IOException ex) {
               results.error("There was a problem reading the cache file given");
               return ops;
            }
            if (cache == null) {
               results.error("The cache is null");
               return ops;
            }
            Long cachedViewId = cache.getViewId();
            if (!cachedViewId.equals(viewId.getId())) {
               results.errorf("The entered view id (%s) does not match up with the cached view id (%s)\n",
                  viewId.getId(), cachedViewId);
               return ops;
            }

            // The token is created/queried from the token service as this should not be stored within the DB
            viewToken = ArtifactToken.valueOf(cachedViewId, cache.getViewName(),
               orcsApi.tokenService().getArtifactType(cache.getViewTypeId()));

            ops = new BlockApplicabilityOps(orcsApi, logger, branch, viewToken, cache);
         } else {
            results.error("A cache path was given but no file was found\n");
            return ops;
         }
      }

      return ops;
   }

   private String getFullStagePath(XResultData results, String stagePath, String customStage, String viewName) {

      File stageDir = null;
      if (customStage == null || customStage.equals("")) {
         stageDir = new File(stagePath, "Staging");
      } else {
         stageDir = new File(stagePath, customStage);
      }

      if (!stageDir.exists() && !stageDir.mkdir()) {
         results.errorf("Could not create stage directory %s\n", stageDir.toString());
         return "";
      }

      File stageViewDir = new File(stageDir.getPath(), viewName.replaceAll(" ", "_"));
      if (!stageViewDir.exists() && !stageViewDir.mkdir()) {
         results.errorf("Could not create stage directory %s\n", stageViewDir.toString());
         return "";
      }

      return stageViewDir.getPath();
   }

   @Override
   public XResultData validate(BranchId branch, boolean update, XResultData results) {

      //for each configuration group validate that the ConfigurationGroup tag exists in itself and its members

      for (ArtifactToken cfggroup : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
         branch)) {
         //get all app tokens for the given cfg group
         List<ApplicabilityToken> groupApps =
            orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(cfggroup, branch);

         //if this cfg group is missing its own config app token then add it
         if (!groupApps.stream().anyMatch(a -> a.getName().equals("ConfigurationGroup = " + cfggroup.getName()))) {
            results.addRaw("Add ConfigurationGroup = " + cfggroup.getName() + "to " + cfggroup.getName());
            if (update) {
               createApplicabilityForView(cfggroup, "ConfigurationGroup = " + cfggroup.getName(), branch);
            }
         }

         //if this cfg group has any tokens that are for another config group remove them
         for (ApplicabilityToken applicabilityToken : groupApps.stream().filter(
            a -> a.getName().startsWith("ConfigurationGroup =")).collect(Collectors.toList())) {
            if (!applicabilityToken.getName().equals("ConfigurationGroup = " + cfggroup.getName())) {
               results.addRaw("Remove from " + cfggroup.getName() + " " + applicabilityToken.getName());
               if (update) {
                  removeApplicabilityFromView(branch, cfggroup, applicabilityToken.getName());
               }
            }
         }

         //if this cfg group is missing its own config app token then add it
         if (!groupApps.stream().anyMatch(a -> a.getName().equals("Base"))) {
            results.addRaw("Add Base to" + cfggroup.getName());
            if (update) {
               createApplicabilityForView(cfggroup, "Base", branch);
            }
         }
         //make sure there are no non-member config apps in the group
         //for each cfg in the cfg group, make sure the cfg group token is in the cfg AND the cfg token appears in the cfg group list

         List<String> configAppsToRemoveFromGroup =
            groupApps.stream().filter(a -> a.getName().startsWith("Config =")).map(b -> b.getName()).collect(
               Collectors.toList());
         for (ArtifactReadable cfg : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
            CoreArtifactTypes.BranchView).andRelatedTo(CoreRelationTypes.PlConfigurationGroup_Group,
               cfggroup).asArtifacts()) {

            configAppsToRemoveFromGroup.remove("Config = " + cfg.getName());
            //check that the current cfg app token exists in cfg group list
            //if not add it
            if (!groupApps.stream().anyMatch(a -> a.getName().equals("Config = " + cfg.getName()))) {
               results.addRaw("Add Config = " + cfg.getName() + " to " + cfggroup.getName());
               if (update) {
                  createApplicabilityForView(cfggroup, "Config = " + cfg.getName(), branch);
               }
            }
            //check that the current cfg has the cfg group token
            List<ApplicabilityToken> cfgApps =
               orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(cfg, branch);
            if (!cfgApps.stream().anyMatch(a -> a.getName().equals("ConfigurationGroup = " + cfggroup.getName()))) {
               results.addRaw("Add ConfigurationGroup = " + cfggroup.getName() + " to " + cfg.getName());
               if (update) {
                  createApplicabilityForView(cfg, "ConfigurationGroup = " + cfggroup.getName(), branch);
               }
            }
         }
         for (String str : configAppsToRemoveFromGroup) {
            results.addRaw("Remove from " + cfggroup.getName() + " " + str);
            if (update) {
               removeApplicabilityFromView(branch, cfggroup, str);
            }
         }
      }
      for (ArtifactToken cfgArt : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationsForBranch(branch)) {
         List<ApplicabilityToken> viewApps =
            orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(cfgArt, branch);
         //check that there aren't invalid Config tags in the given config's app token list
         for (ApplicabilityToken appToken : viewApps.stream().filter(a -> a.getName().startsWith("Config =")).collect(
            Collectors.toList())) {
            if (!appToken.getName().equals("Config = " + cfgArt.getName())) {
               results.addRaw("Remove from " + cfgArt.getName() + " " + appToken.getName());
               if (update) {
                  removeApplicabilityFromView(branch, cfgArt, appToken.getName());
               }
            }
         }

         //check that the current cfg app token exists in cfg list
         //if not add it
         if (!viewApps.stream().anyMatch(a -> a.getName().equals("Config = " + cfgArt.getName()))) {
            results.addRaw("Add Config = " + cfgArt.getName() + " to " + cfgArt.getName());
            if (update) {
               createApplicabilityForView(cfgArt, "Config = " + cfgArt.getName(), branch);
            }
         }
         //if this cfg is missing Base then add it
         if (!viewApps.stream().anyMatch(a -> a.getName().equals("Base"))) {
            results.addRaw("Add Base to " + cfgArt.getName());
            if (update) {
               createApplicabilityForView(cfgArt, "Base", branch);
            }
         }
      }

      List<String> syncAllApplicabilityTuples = syncAllApplicabilityTuples(branch, update, results);
      results.addRaw("Results of sync All Applicability Tuples: " + syncAllApplicabilityTuples.toString());

      return results;
   }

   /**
    * This method was first used to initialize the tuple2 type ApplicabilityDefinition on baseline branches with Product
    * Line capabilities. This was taken from the old getPossibleApplicabilities rest call and modified to add/introduce
    * tuples. Currently this method is used to sync the ApplicabiltiyDefinition Tuples with Feature, Configuration and
    * Configuration Group definitions
    */

   public List<String> syncAllApplicabilityTuples(BranchId branch, boolean update, XResultData results) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem,
         "Syncing Tuple2 Entries for ApplicabilityDefinition Type");
      TupleQuery query = orcsApi.getQueryFactory().tupleQuery();

      GammaId gamma = GammaId.SENTINEL;
      if (!(gamma = query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L),
         "Base")).isValid()) {
         tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L), "Base");
      } else {
         tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
      }

      List<String> apps = new ArrayList<String>();
      /**
       * getApplicabilityTokens returns tokens based on tuples for ApplicabilityDefinition, which is what this method is
       * initializing. For actual use, the tokens returned would need to be from the ViewApplicability tuple.
       */
      HashMap<Long, ApplicabilityToken> appTokens =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch);

      for (ApplicabilityToken app : appTokens.values()) {
         apps.add(app.getName());
      }

      List<String> applicsNoArtifacts = new ArrayList<>(); // Used to keep track of applicability's that exist but are not found within the 3 main loops
      applicsNoArtifacts.addAll(apps);
      applicsNoArtifacts.remove("Base");

      for (ArtifactToken view : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationsForBranch(branch)) {
         String str = "Config = " + view.getName();
         Long strId = orcsApi.getKeyValueOps().getByValue(str);
         Iterator<Long> iterator =
            orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(CoreTupleTypes.ApplicabilityDefinition, branch,
               strId).iterator();

         List<Long> list = new ArrayList<>();
         // Add each element of iterator to the List
         iterator.forEachRemaining(list::add);
         if (list.size() > 1 && list.contains(0L)) {
            results.addRaw("Delete " + str + " from AppDef");
            if (update) {
               tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L), str);
            }
         } else if (list.size() == 0) {
            results.addRaw("Add " + str + " to AppDef");
            if (update) {
               if (!(gamma =
                  query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition, view, str)).isValid()) {
                  tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, view, str);
               } else {
                  tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
               }
            }
         }
         if (!apps.contains(str)) {
            apps.add(str);
         }
         applicsNoArtifacts.remove(str);
      }
      for (FeatureDefinition feature : getFeatureDefinitionData(branch)) {
         for (String val : feature.getValues()) {
            String str = feature.getName() + " = " + val;
            Long strId = orcsApi.getKeyValueOps().getByValue(str);
            Iterator<Long> iterator =
               orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(CoreTupleTypes.ApplicabilityDefinition, branch,
                  strId).iterator();

            List<Long> list = new ArrayList<>();
            // Add each element of iterator to the List
            iterator.forEachRemaining(list::add);
            if (list.size() > 1 && list.contains(0L)) {
               results.addRaw("Delete " + str + " from AppDef");
               if (update) {
                  tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L), str);
               }
            } else if (list.size() == 0) {
               results.addRaw("Add " + str + " to AppDef");
               if (update) {
                  if (!(gamma = query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition,
                     ArtifactId.valueOf(feature.getId()), str)).isValid()) {
                     tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(feature.getId()), str);
                  } else {
                     tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
                  }
               }
            }
            if (!apps.contains(str)) {
               apps.add(str);
            }
            applicsNoArtifacts.remove(str);
         }
      }
      for (ArtifactToken group : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
         branch)) {
         String str = "ConfigurationGroup = " + group.getName();
         Long strId = orcsApi.getKeyValueOps().getByValue(str);
         Iterator<Long> iterator =
            orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(CoreTupleTypes.ApplicabilityDefinition, branch,
               strId).iterator();

         List<Long> list = new ArrayList<>();
         // Add each element of iterator to the List
         iterator.forEachRemaining(list::add);
         if (list.size() > 1 && list.contains(0L)) {
            results.addRaw("Remove " + str + " from AppDef");
            if (update) {
               tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L), str);
            }
         } else if (list.size() == 0) {
            results.addRaw("Add " + str + " from AppDef");
            if (update) {
               if (!(gamma =
                  query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition, group, str)).isValid()) {
                  tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, group, str);
               } else {
                  tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
               }
            }
         }
         if (!apps.contains(str)) {
            apps.add(str);
         }
         applicsNoArtifacts.remove(str);
      }
      for (String app : applicsNoArtifacts) {
         // These applicabilities are mostly compounds and/or don't have associated artifacts.  Just using ID of 0 for E1
         if (app.contains("&") || app.contains("|")) {
            if (!(gamma = query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L),
               app)).isValid()) {
               tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0L), app);
            } else {
               tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
            }
         } else {

            Long strId = orcsApi.getKeyValueOps().getByValue(app);
            Iterator<Long> iterator =
               orcsApi.getQueryFactory().tupleQuery().getTuple2E1ListRaw(CoreTupleTypes.ApplicabilityDefinition, branch,
                  strId).iterator();

            List<Long> list = new ArrayList<>();
            // Add each element of iterator to the List
            iterator.forEachRemaining(list::add);
            for (Long e1Id : list) {
               results.addRaw("Delete " + app + " from AppDef");
               if (update) {
                  tx.deleteTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(e1Id), app);
               }
            }
         }

      }
      tx.commit();
      List<String> appsNoDups = new ArrayList<>(new HashSet<>(apps));
      Collections.sort(appsNoDups);
      return appsNoDups;
   }

   @Override
   public XResultData validateCompoundApplicabilities(BranchId branch, boolean update, XResultData results) {

      List<String> actions = results.getResults();
      for (ArtifactId bView : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
         actions.addAll(updateCompoundApplicabilities(branch, bView, update).getResults());
      }
      results.setResults(actions);
      return results;
   }

   @Override
   public XResultData createProductType(ProductTypeDefinition productType, BranchId branch) {
      XResultData results = new XResultData();
      if (!Strings.isValid(productType.getName())) {
         results.error("Name can not be empty for product type");
         return results;
      }
      ProductTypeDefinition existingProductType = getProductType(productType.getName(), branch);
      if (existingProductType.isValid()) {
         results.errorf("Product Type Name is already in use.");
         return results;
      }
      if (existingProductType.isInvalid()) {
         try {

            TransactionBuilder tx = txFactory.createTransaction(branch, "Create View ");
            ArtifactToken productArt = ArtifactToken.SENTINEL;
            productArt = tx.createArtifact(CoreArtifactTypes.ProductType, productType.getName());

            tx.setSoleAttributeValue(productArt, CoreAttributeTypes.Description, productType.getDescription());

            tx.commit();
         } catch (Exception ex) {
            results.errorf(Lib.exceptionToString(ex));
            return results;
         }
      }
      return results;
   }

   @Override
   public ProductTypeDefinition getProductType(String productType, BranchId branch) {
      ProductTypeDefinition productDef = new ProductTypeDefinition();
      if (Strings.isNumeric(productType)) {
         ArtifactReadable productArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.ProductType).andId(
               ArtifactId.valueOf(productType)).asArtifactOrSentinel();
         if (productArt.isValid()) {
            productDef = getProductTypeDefinition(productArt);
         }
      } else {
         ArtifactReadable productArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.ProductType).andNameEquals(
               productType).asArtifactOrSentinel();
         if (productArt.isValid()) {
            productDef = getProductTypeDefinition(productArt);
         }
      }
      return productDef;
   }

   @Override
   public ProductTypeDefinition getProductTypeDefinition(ArtifactReadable artifact) {
      return new ProductTypeDefinition(artifact.getId(), artifact.getName(),
         artifact.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
   }

   @Override
   public Collection<ProductTypeDefinition> getProductTypeDefinitions(BranchId branch, long pageNum, long pageSize,
      AttributeTypeToken orderByAttributeType) {
      QueryBuilder productQuery =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.ProductType);
      if (orderByAttributeType != null && orderByAttributeType.isValid()) {
         productQuery = productQuery.setOrderByAttribute(orderByAttributeType);
      }
      if (pageNum > 0L && pageSize > 0L) {
         productQuery = productQuery.isOnPage(pageNum, pageSize);
      }
      return productQuery.asArtifacts().stream().map(art -> getProductTypeDefinition(art)).collect(Collectors.toList());
   }

   @Override
   public XResultData updateProductType(ProductTypeDefinition productType, BranchId branch) {
      XResultData results = new XResultData();

      if (productType.isInvalid()) {
         results.error("Product Type must have an id");
         return results;
      }
      if (!Strings.isValid(productType.getName())) {
         results.error("Name can not be empty for product type");
         return results;
      }
      ProductTypeDefinition existingProductType = getProductType(productType.getIdString(), branch);
      if (existingProductType.isValid() && existingProductType.getName().equals(
         productType.getName()) && !existingProductType.getId().equals(productType.getId())) {
         results.errorf("Product Type Name is already in use.");
         return results;
      }
      if (existingProductType.isInvalid()) {
         results.error("Product Type not found.");
         return results;
      }
      TransactionBuilder tx = txFactory.createTransaction(branch, "Updating Product Type");
      if (!existingProductType.getName().equals(productType.getName())) {
         tx.setSoleAttributeValue(ArtifactId.valueOf(productType.getId()), CoreAttributeTypes.Name,
            productType.getName());
         for (FeatureDefinition feature : getFeaturesByProductApplicability(branch, existingProductType.getName())) {
            if (feature.getProductApplicabilities().contains(existingProductType.getName())) {
               feature.getProductApplicabilities().remove(existingProductType.getName());
               feature.getProductApplicabilities().add(productType.getName());
               tx.setAttributesFromValues(ArtifactId.valueOf(feature.getId()), CoreAttributeTypes.ProductApplicability,
                  feature.getProductApplicabilities());
            }
         }
         for (CreateViewDefinition view : getViewsDefinitionsByProductApplicability(branch,
            existingProductType.getName())) {
            if (view.getProductApplicabilities().contains(existingProductType.getName())) {
               view.getProductApplicabilities().remove(existingProductType.getName());
               view.getProductApplicabilities().add(productType.getName());
               tx.setAttributesFromValues(ArtifactId.valueOf(view.getId()), CoreAttributeTypes.ProductApplicability,
                  view.getProductApplicabilities());
            }
         }
      }

      if (!existingProductType.getDescription().equals(productType.getDescription())) {
         tx.setSoleAttributeValue(ArtifactId.valueOf(productType.getId()), CoreAttributeTypes.Description,
            productType.getDescription());
      }
      tx.commit();

      return results;
   }

   @Override
   public XResultData deleteProductType(ArtifactId productType, BranchId branch) {
      XResultData results = new XResultData();
      ProductTypeDefinition existingProductType = getProductType(productType.getIdString(), branch);
      TransactionBuilder tx = txFactory.createTransaction(branch, "Delete Product Type");
      for (FeatureDefinition feature : getFeaturesByProductApplicability(branch, existingProductType.getName())) {
         if (feature.getProductApplicabilities().contains(existingProductType.getName())) {
            feature.getProductApplicabilities().remove(existingProductType.getName());
            tx.setAttributesFromValues(ArtifactId.valueOf(feature.getId()), CoreAttributeTypes.ProductApplicability,
               feature.getProductApplicabilities());
         }
      }
      for (CreateViewDefinition view : getViewsDefinitionsByProductApplicability(branch,
         existingProductType.getName())) {
         if (view.getProductApplicabilities().contains(existingProductType.getName())) {
            view.getProductApplicabilities().remove(existingProductType.getName());
            tx.setAttributesFromValues(ArtifactId.valueOf(view.getId()), CoreAttributeTypes.ProductApplicability,
               view.getProductApplicabilities());
         }
      }
      tx.deleteArtifact(productType);

      tx.commit();

      return results;
   }

   @Override
   public Collection<CreateViewDefinition> getViewDefinitions(BranchId branch) {
      QueryBuilder viewQuery =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).follow(
            CoreRelationTypes.PlConfigurationGroup_Group);
      return viewQuery.asArtifacts().stream().map(art -> getViewDefinition(art)).collect(Collectors.toList());
   }

   @Override
   public Collection<CreateViewDefinition> getViewsDefinitionsByProductApplicability(BranchId branch,
      String productApplicability) {
      QueryBuilder viewQuery =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).and(
            CoreAttributeTypes.ProductApplicability, productApplicability).follow(
               CoreRelationTypes.PlConfigurationGroup_Group);
      return viewQuery.asArtifacts().stream().map(art -> getViewDefinition(art)).collect(Collectors.toList());
   }

   @Override
   public String uploadBlockApplicability(InputStream zip) {
      String serverDataPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(String.format("%s%sblockApplicability", serverDataPath, File.separator));
      if (!serverApplicDir.exists()) {
         serverApplicDir.mkdirs();
      }
      String uniqueId = generateUniqueFilePath(serverApplicDir);
      String uniqueIdDir = String.format("%s%s%s", serverApplicDir.getPath(), File.separator, uniqueId);
      String sourceNameDir = String.format("%s%ssource", uniqueIdDir, File.separator);
      ZipInputStream zis = null;
      OutputStream outStream = null;
      try {
         new File(uniqueIdDir).mkdir();
         String fileZip = String.format("%s.zip", sourceNameDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();
         zip.close();

         outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);
         outStream.close();

         zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(sourceNameDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = newFile(unzipLocation, zipEntry);
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  zis.close();
                  throw new IOException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // fix for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  zis.close();
                  throw new IOException("Failed to create directory " + parent);
               }
               // write file content
               try (FileOutputStream fos = new FileOutputStream(uploadedDirectory);) {
                  int len;
                  while ((len = zis.read(buffer)) > 0) {
                     fos.write(buffer, 0, len);
                  }
               }
            }
            zipEntry = zis.getNextEntry();
         }
         zis.closeEntry();
         zis.close();
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Unable to upload zip file");
      } finally {
         Lib.close(zis);
         Lib.close(outStream);
      }

      return uniqueId;
   }

   @Override
   public XResultData applyBlockVisibilityOnServer(String blockApplicId, BlockApplicabilityStageRequest data,
      BranchId branch) {
      XResultData results = new XResultData();

      String serverDataPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(serverDataPath + File.separator + "blockApplicability");
      if (!serverApplicDir.exists()) {
         serverApplicDir.mkdirs();
      }

      String uniqueIdDir = String.format("%s%s%s", serverApplicDir.getPath(), File.separator, blockApplicId);
      String sourceDir = String.format("%s%ssource", uniqueIdDir, File.separator);

      try {
         File[] contents = new File(sourceDir + File.separator).listFiles();
         if (contents.length == 1) {
            sourceDir = contents[0].getPath();
         }
      } catch (Exception ex) {
         //DO NOTHING
      }

      data.setSourcePath(sourceDir);
      data.setStagePath(uniqueIdDir);
      data.setCustomStageDir("staging");

      results = applyApplicabilityToFiles(data, branch);

      if (!data.getViews().keySet().isEmpty()) {
         results.logf("View: %s\nSource Directory: %s\nStage Directory: %s\nStage Directory Name: %s\n",
            data.getViews().keySet().iterator().next().toString(), data.getSourcePath(), data.getStagePath(),
            data.getCustomStageDir());
      } else {
         results.logf("Source Directory: %s\nStage Directory: %s\nStage Directory Name: %s\n", data.getSourcePath(),
            data.getStagePath(), data.getCustomStageDir());
      }

      results.logf("Source Directory: %s\nStage Directory: %s", sourceDir, uniqueIdDir);
      try {
         String stagingDir = String.format("%s%s%s", data.getStagePath(), File.separator, data.getCustomStageDir());
         String stagingZip = String.format("%s%s%s.zip", data.getStagePath(), File.separator, data.getCustomStageDir());

         results.logf("Staging Directory: %s\nStaging Zip: %s\n", stagingDir, stagingZip);

         zipFolder(new File(stagingDir), new File(stagingZip));

      } catch (Exception ex) {
         results.error("Zipping file failed. Check if file being zipped exists.");
      }

      return results;
   }

   @Override
   public XResultData deleteBlockApplicability(String blockApplicId) {
      XResultData results = new XResultData();

      String serverDataPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(serverDataPath + File.separator + "blockApplicability");
      if (!serverApplicDir.isDirectory()) {
         results.warning(String.format("Directory %s does not exist", serverApplicDir.getPath()));
         return results;
      }

      File uniqueIdDir = new File(String.format("%s%s%s", serverApplicDir.getPath(), File.separator, blockApplicId));

      File[] contents = uniqueIdDir.listFiles();
      if (contents != null) {
         for (File f : contents) {
            if (!Files.isSymbolicLink(f.toPath())) {
               deleteDir(f);
            }
         }
      }
      uniqueIdDir.delete();
      return results;
   }

   @Override
   public String uploadRunBlockApplicability(Long view, InputStream zip, BranchId branch) {
      String serverDataPath = System.getProperty(OseeClient.OSEE_APPLICATION_SERVER_DATA);
      if (serverDataPath == null) {
         serverDataPath = System.getProperty("user.home");
      }
      File serverApplicDir = new File(String.format("%s%sblockApplicability", serverDataPath, File.separator));
      if (!serverApplicDir.exists()) {
         serverApplicDir.mkdirs();
      }
      String uniqueId = generateUniqueFilePath(serverApplicDir);
      String uniqueIdDir = String.format("%s%s%s", serverApplicDir.getPath(), File.separator, uniqueId);
      String sourceNameDir = String.format("%s%ssource", uniqueIdDir, File.separator);
      OutputStream outStream = null;
      ZipInputStream zis = null;
      try {
         new File(uniqueIdDir).mkdir();
         String fileZip = String.format("%s.zip", sourceNameDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();

         outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);

         zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(sourceNameDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = newFile(unzipLocation, zipEntry);
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  zis.close();
                  throw new IOException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // fix for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  zis.close();
                  throw new IOException("Failed to create directory " + parent);
               }
               // write file content
               try (FileOutputStream fos = new FileOutputStream(uploadedDirectory);) {
                  int len;
                  while ((len = zis.read(buffer)) > 0) {
                     fos.write(buffer, 0, len);
                  }
               }

            }
            zipEntry = zis.getNextEntry();
         }

         try {
            File[] contents = new File(sourceNameDir + File.separator).listFiles();
            if (contents.length == 1) {
               sourceNameDir = contents[0].getPath();
            }
         } catch (Exception ex) {
            //DO NOTHING
         }

         Map<Long, String> views = new HashMap<>();
         views.put(view, "");

         BlockApplicabilityStageRequest data =
            new BlockApplicabilityStageRequest(views, false, sourceNameDir, uniqueIdDir, "staging");

         applyApplicabilityToFiles(data, branch);
         String stagingDir = String.format("%s%sstaging", uniqueIdDir, File.separator);
         String stagingZip = String.format("%s%sstaging.zip", uniqueIdDir, File.separator);

         zipFolder(new File(stagingDir), new File(stagingZip));

         zip.close();
         outStream.close();
         zis.closeEntry();
         zis.close();
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "BAT Operation Failed");
      } finally {
         if (zis != null) {
            Lib.close(zis);
         }
         Lib.close(outStream);
      }
      return uniqueId;
   }

   private void deleteDir(File file) {
      File[] contents = file.listFiles();
      if (contents != null) {
         for (File f : contents) {
            deleteDir(f);
         }
      }
      file.delete();
   }

   private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();

      if (!destFilePath.startsWith(destDirPath + File.separator)) {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      }
      return destFile;
   }

   private String generateUniqueFilePath(File serverApplicDir) {
      String uniqueId = UUID.randomUUID().toString();
      for (File file : serverApplicDir.listFiles()) {
         if (file.getName().equals(uniqueId)) {
            return generateUniqueFilePath(serverApplicDir);
         }
      }
      return uniqueId;
   }

   public void zipFolder(File srcFolder, File destZipFile) throws Exception {
      try (FileOutputStream fileWriter = new FileOutputStream(destZipFile);
         ZipOutputStream zip = new ZipOutputStream(fileWriter)) {
         addFolderToZip(srcFolder, srcFolder, zip);
      }
   }

   //recursive function with addFileToZip
   private void addFolderToZip(File rootPath, File srcFolder, ZipOutputStream zip) throws Exception {
      for (File fileName : srcFolder.listFiles()) {
         addFileToZip(rootPath, fileName, zip);
      }
   }

   //recursive function with addFolderToZip
   private void addFileToZip(File rootPath, File srcFile, ZipOutputStream zip) throws Exception {

      if (srcFile.isDirectory()) {
         addFolderToZip(rootPath, srcFile, zip);
      } else {
         byte[] buf = new byte[1024];
         int len;
         try (FileInputStream in = new FileInputStream(srcFile)) {
            String name = srcFile.getPath();
            name = name.replace(rootPath.getPath(), "");
            zip.putNextEntry(new ZipEntry(name));
            while ((len = in.read(buf)) > 0) {
               zip.write(buf, 0, len);
            }
         }
      }
   }

   private String getConstraint(String name, String constraint_setting) {
      return "constraint_value(\r\n" + "    name = \"" + name + "\",\r\n" + "    constraint_setting = \":" + constraint_setting + "\",\r\n" + ")\r\n\r\n";
   }

   private String getFeatureConfigSettingGroup(String featureName, int size) {
      String content =
         "selects.config_setting_group(\r\n" + "    name = \"" + featureName + "\",\r\n" + "    match_any = [\r\n";
      for (int i = 0; i < size; i++) {
         content = content + "        \":" + i + "_" + featureName + "\",\r\n";
      }
      content = content + "    ],\r\n" + ")\r\n\r\n";
      return content;
   }

   @Override
   public String getFeatureBazelFile(BranchId branchId) {
      String prefix =
         "package(default_visibility = [\"//visibility:public\"])\r\n\r\n" + "load(\"@bazel_skylib//lib:selects.bzl\", \"selects\")\r\n\r\n";
      String content = "";
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(
         CoreArtifactTypes.Feature).asArtifacts()) {

         //create the constraint setting based on the name of the feature

         //create the constraint values based on the values of the feature
         List<String> values = art.fetchAttributesAsStringList(CoreAttributeTypes.Value);
         if (art.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued).equals(true)) {
            //write out multi-value features so they can be used in an OR'd fashion since bazel can't have multiple constraints per platform see #8763 on bazel's github
            for (int i = 0; i < values.size(); i++) {

               content = content + "constraint_setting(name = \"constraint" + i + "_" + art.getName() + "\")\r\n\r\n";
               for (String value : values) {
                  content = content + getConstraint(i + "_" + art.getName() + "_" + value,
                     "constraint" + i + "_" + art.getName());
               }
            }
            content = content + values.stream().map(
               value -> getFeatureConfigSettingGroup(art.getName() + "_" + value, values.size())).collect(
                  Collectors.joining(""));

         } else {
            content = content + "constraint_setting(name = \"constraint_" + art.getName() + "\")\r\n\r\n";
            for (String value : values) {
               if (value.equals("Included")) {
                  content = content + getConstraint(art.getName(), "constraint_" + art.getName());
               } else {

                  content = content + getConstraint(art.getName() + "_" + value, "constraint_" + art.getName());
               }
            }

         }
      }

      return prefix + content;
   }

   @Override
   public String getConfigurationPlatformBazelFile(BranchId branchId) {
      String prefix = "package(default_visibility = [\"//visibility:public\"])\r\n\r\n";
      // Switch these two lines when Bazel 7.2 releases.
      //      String content = "load(\"@platforms//host:constraints.bzl\",\"HOST_CONSTRAINTS\")\r\n";
      String content = "load(\"@local_config_platform//:constraints.bzl\",\"HOST_CONSTRAINTS\")\r\n";

      // Defined here: https://github.com/bazelbuild/platforms
      List<String> preDefinedOsPlatforms = new ArrayList<String>();
      preDefinedOsPlatforms.add("@platforms//os:android");
      preDefinedOsPlatforms.add("@platforms//os:chromiumos");
      preDefinedOsPlatforms.add("@platforms//os:freebsd");
      preDefinedOsPlatforms.add("@platforms//os:fuchsia");
      preDefinedOsPlatforms.add("@platforms//os:haiku");
      preDefinedOsPlatforms.add("@platforms//os:ios");
      preDefinedOsPlatforms.add("@platforms//os:linux");
      preDefinedOsPlatforms.add("@platforms//os:macos");
      preDefinedOsPlatforms.add("@platforms//os:netbsd");
      preDefinedOsPlatforms.add("@platforms//os:nixos");
      preDefinedOsPlatforms.add("@platforms//os:none");
      preDefinedOsPlatforms.add("@platforms//os:openbsd");
      preDefinedOsPlatforms.add("@platforms//os:osx");
      preDefinedOsPlatforms.add("@platforms//os:qnx");
      preDefinedOsPlatforms.add("@platforms//os:tvos");
      preDefinedOsPlatforms.add("@platforms//os:visionos");
      preDefinedOsPlatforms.add("@platforms//os:vxworks");
      preDefinedOsPlatforms.add("@platforms//os:wasi");
      preDefinedOsPlatforms.add("@platforms//os:watchos");
      preDefinedOsPlatforms.add("@platforms//os:windows");

      List<String> preDefinedCpuPlatforms = new ArrayList<String>();
      preDefinedCpuPlatforms.add("@platforms//cpu:aarch32");
      preDefinedCpuPlatforms.add("@platforms//cpu:aarch64");
      preDefinedCpuPlatforms.add("@platforms//cpu:all");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64_32");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64e");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv6-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7e-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7e-mf");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7k");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv8-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:cortex-r52");
      preDefinedCpuPlatforms.add("@platforms//cpu:cortex-r82");
      preDefinedCpuPlatforms.add("@platforms//cpu:i386");
      preDefinedCpuPlatforms.add("@platforms//cpu:mips64");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc32");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc64le");
      preDefinedCpuPlatforms.add("@platforms//cpu:riscv32");
      preDefinedCpuPlatforms.add("@platforms//cpu:riscv64");
      preDefinedCpuPlatforms.add("@platforms//cpu:s390x");
      preDefinedCpuPlatforms.add("@platforms//cpu:wasm32");
      preDefinedCpuPlatforms.add("@platforms//cpu:wasm64");
      preDefinedCpuPlatforms.add("@platforms//cpu:x86_32");
      preDefinedCpuPlatforms.add("@platforms//cpu:x86_64");

      // Load all configurations (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.BranchView).asArtifacts();
      List<ArtifactReadable> groups =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branchId);
      List<ArtifactReadable> features =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.Feature).asArtifacts();
      for (ArtifactReadable branchView : branchViews) {
         content = content + "platform(\r\n" + "\tname = \"" + branchView.getName().replace(" ", "_") + "\",\r\n";
         Map<String, List<String>> namedViewApplicabilityMap =
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branchId, branchView);
         //check if the view belongs to a group, and write a parents array(limited to one parent due to bazel limitations) if it does
         List<String> matchingGroups =
            namedViewApplicabilityMap.entrySet().stream().filter(map -> map.getKey().equals("ConfigurationGroup")).map(
               entry -> entry.getValue().get(0)).collect(Collectors.toList());
         if (matchingGroups.size() > 0) {
            content = content + "\tparents = [\"//platforms/configuration-groups:" + matchingGroups.get(0) + "\"],\r\n";
         }
         content = content + "\tconstraint_values = [\r\n";
         //configuration's view applicabilities should only be written if different from configuration group barring the configuration which should always be different
         Optional<List<Entry<String, List<String>>>> groupValues =
            groups.stream().filter(group -> matchingGroups.contains(group.getName())).findFirst().map(
               group -> orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branchId,
                  group).entrySet().stream().collect(Collectors.toList()));
         List<Entry<String, List<String>>> filteredContent =
            namedViewApplicabilityMap.entrySet().stream().filter(map -> !map.getKey().equals(
               "Config") && !map.getKey().equals("Configuration") && !map.getKey().equals("ConfigurationGroup")).filter(
                  value -> groupValues.isPresent() && !groupValues.get().contains(
                     value) || !groupValues.isPresent()).collect(Collectors.toList());
         for (Entry<String, List<String>> entry : filteredContent) {
            Optional<Object> isMultiValued =
               features.stream().filter(value -> value.getName().equals(entry.getKey())).map(
                  value -> value.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued)).filter(
                     value -> value.equals(true)).findFirst();
            if (isMultiValued.isEmpty()) {
               if (entry.getValue().get(0).equals("Included")) {
                  content = content + "\t\t\"//feature:" + entry.getKey() + "\",\r\n";
               } else {
                  content = content + "\t\t\"//feature:" + entry.getKey() + "_" + entry.getValue().get(0) + "\",\r\n";
               }
            } else {
               for (int i = 0; i < entry.getValue().size(); i++) {
                  content = content + "\t\t\"//feature:" + i + "_" + entry.getKey() + "_" + entry.getValue().get(
                     i) + "\",\r\n";
               }

            }
         }
         content = content + "\t\t\"//config:" + branchView.getName().replace(" ", "_") + "\"\r\n";
         content = content + "\t]\r\n)\r\n";

         //write out predefined extensions on top of bazel's build in platforms for convenience
         for (String osPlatform : preDefinedOsPlatforms) {
            for (String cpuPlatform : preDefinedCpuPlatforms) {

               content = content + "platform( \r\n\tname=\"" + branchView.getName().replace(" ",
                  "_") + "_" + osPlatform.replace("@platforms//os:",
                     "") + "_" + cpuPlatform.replace("@platforms//cpu:", "") + "\",\r\n";
               content = content + "\tparents =[\":" + branchView.getName().replace(" ", "_") + "\"],\r\n";
               content = content + "\tconstraint_values = [\r\n";
               content = content + "\t\"" + osPlatform + "\",\r\n";
               content = content + "\t\"" + cpuPlatform + "\"\r\n";
               content = content + "]\r\n";
               content = content + ")\r\n";
               content = content + "\r\n";
            }
         }
         //wriete out
         content = content + "platform( \r\n\tname=\"" + branchView.getName().replace(" ", "_") + "_host" + "\",\r\n";
         content = content + "\tparents =[\":" + branchView.getName().replace(" ", "_") + "\"],\r\n";
         content = content + "\tconstraint_values = ";
         content = content + "" + "HOST_CONSTRAINTS" + "\r\n";
         content = content + ")\r\n";
         content = content + "\r\n";
      }
      return prefix + content;
   }

   @Override
   public String getConfigurationGroupBazelFile(BranchId branchId) {
      String prefix = "package(default_visibility = [\"//visibility:public\"])\r\n\r\n";
      // Switch these two lines when Bazel 7.2 releases.
      //      String content = "load(\"@platforms//host:constraints.bzl\",\"HOST_CONSTRAINTS\")\r\n";
      String content = "load(\"@local_config_platform//:constraints.bzl\",\"HOST_CONSTRAINTS\")\r\n";
      // Defined here: https://github.com/bazelbuild/platforms
      List<String> preDefinedOsPlatforms = new ArrayList<String>();
      preDefinedOsPlatforms.add("@platforms//os:android");
      preDefinedOsPlatforms.add("@platforms//os:chromiumos");
      preDefinedOsPlatforms.add("@platforms//os:freebsd");
      preDefinedOsPlatforms.add("@platforms//os:fuchsia");
      preDefinedOsPlatforms.add("@platforms//os:haiku");
      preDefinedOsPlatforms.add("@platforms//os:ios");
      preDefinedOsPlatforms.add("@platforms//os:linux");
      preDefinedOsPlatforms.add("@platforms//os:macos");
      preDefinedOsPlatforms.add("@platforms//os:netbsd");
      preDefinedOsPlatforms.add("@platforms//os:nixos");
      preDefinedOsPlatforms.add("@platforms//os:none");
      preDefinedOsPlatforms.add("@platforms//os:openbsd");
      preDefinedOsPlatforms.add("@platforms//os:osx");
      preDefinedOsPlatforms.add("@platforms//os:qnx");
      preDefinedOsPlatforms.add("@platforms//os:tvos");
      preDefinedOsPlatforms.add("@platforms//os:visionos");
      preDefinedOsPlatforms.add("@platforms//os:vxworks");
      preDefinedOsPlatforms.add("@platforms//os:wasi");
      preDefinedOsPlatforms.add("@platforms//os:watchos");
      preDefinedOsPlatforms.add("@platforms//os:windows");

      List<String> preDefinedCpuPlatforms = new ArrayList<String>();
      preDefinedCpuPlatforms.add("@platforms//cpu:aarch32");
      preDefinedCpuPlatforms.add("@platforms//cpu:aarch64");
      preDefinedCpuPlatforms.add("@platforms//cpu:all");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64_32");
      preDefinedCpuPlatforms.add("@platforms//cpu:arm64e");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv6-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7e-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7e-mf");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv7k");
      preDefinedCpuPlatforms.add("@platforms//cpu:armv8-m");
      preDefinedCpuPlatforms.add("@platforms//cpu:cortex-r52");
      preDefinedCpuPlatforms.add("@platforms//cpu:cortex-r82");
      preDefinedCpuPlatforms.add("@platforms//cpu:i386");
      preDefinedCpuPlatforms.add("@platforms//cpu:mips64");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc32");
      preDefinedCpuPlatforms.add("@platforms//cpu:ppc64le");
      preDefinedCpuPlatforms.add("@platforms//cpu:riscv32");
      preDefinedCpuPlatforms.add("@platforms//cpu:riscv64");
      preDefinedCpuPlatforms.add("@platforms//cpu:s390x");
      preDefinedCpuPlatforms.add("@platforms//cpu:wasm32");
      preDefinedCpuPlatforms.add("@platforms//cpu:wasm64");
      preDefinedCpuPlatforms.add("@platforms//cpu:x86_32");
      preDefinedCpuPlatforms.add("@platforms//cpu:x86_64");
      List<ArtifactReadable> groups =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branchId);
      List<ArtifactReadable> features =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.Feature).asArtifacts();
      for (ArtifactToken group : groups) {
         content = content + "platform(\r\n" + "\tname = \"" + group.getName().replace(" ",
            "_") + "\",\r\n" + "\tconstraint_values = [\r\n";
         List<Entry<String, List<String>>> constraintsToApply =
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branchId,
               group).entrySet().stream().filter(
                  map -> !map.getKey().equals("Config") && !map.getKey().equals(
                     "Configuration") && !map.getKey().equals("ConfigurationGroup")).collect(Collectors.toList());
         for (Entry<String, List<String>> entry : constraintsToApply) {
            Optional<Object> isMultiValued =
               features.stream().filter(value -> value.getName().equals(entry.getKey())).map(
                  value -> value.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued)).filter(
                     value -> value.equals(true)).findFirst();
            if (isMultiValued.isEmpty()) {
               if (entry.getValue().get(0).equals("Included")) {
                  content = content + "\t\t\"//feature:" + entry.getKey() + "\",\r\n";
               } else {
                  content = content + "\t\t\"//feature:" + entry.getKey() + "_" + entry.getValue().get(0) + "\",\r\n";
               }
            } else {
               for (int i = 0; i < entry.getValue().size(); i++) {
                  content = content + "\t\t\"//feature:" + i + "_" + entry.getKey() + "_" + entry.getValue().get(
                     i) + "\",\r\n";
               }

            }
         }
         content = content + "\t\t\"//config:" + group.getName().replace(" ", "_") + "\"\r\n";
         content = content + "\t]\r\n)\r\n";
         for (String osPlatform : preDefinedOsPlatforms) {
            for (String cpuPlatform : preDefinedCpuPlatforms) {

               content =
                  content + "platform( \r\n\tname=\"" + group.getName().replace(" ", "_") + "_" + osPlatform.replace(
                     "@platforms//os:", "") + "_" + cpuPlatform.replace("@platforms//cpu:", "") + "\",\r\n";
               content = content + "\tparents =[\":" + group.getName().replace(" ", "_") + "\"],\r\n";
               content = content + "\tconstraint_values = [\r\n";
               content = content + "\t\"" + osPlatform + "\",\r\n";
               content = content + "\t\"" + cpuPlatform + "\"\r\n";
               content = content + "]\r\n";
               content = content + ")\r\n";
               content = content + "\r\n";
            }
         }
      }

      return prefix + content;
   }

   @Override
   public String getBazelBuildFile() {
      return "package(default_visibility = [\"//visibility:public\"])\r\n" + "\r\n" + "filegroup(\r\n" + "    name = \"srcs\",\r\n" + "    srcs = [\r\n" + "        \"BUILD.bazel\",\r\n" + "        \"WORKSPACE\",\r\n" + "        \"//platforms/configurations:BUILD.bazel\",\r\n" + "        \"//platforms/configuration-groups:BUILD.bazel\",\r\n" + "        \"//config:BUILD.bazel\",\r\n" + "        \"//feature:BUILD.bazel\",\r\n" + "    ],\r\n" + ")";
   }

   @Override
   public String getBazelWorkspaceFile() {
      return "workspace(name = \"osee_applicability\") \r\n load(\"@bazel_tools//tools/build_defs/repo:http.bzl\", \"http_archive\") \r\n" + "http_archive(\r\n" + "    name = \"bazel_skylib\",\r\n" + "    sha256 = \"cd55a062e763b9349921f0f5db8c3933288dc8ba4f76dd9416aac68acee3cb94\",\r\n" + "    urls = [\r\n" + "        \"https://mirror.bazel.build/github.com/bazelbuild/bazel-skylib/releases/download/1.5.0/bazel-skylib-1.5.0.tar.gz\",\r\n" + "        \"https://github.com/bazelbuild/bazel-skylib/releases/download/1.5.0/bazel-skylib-1.5.0.tar.gz\",\r\n" + "    ],\r\n" + ")\r\n\r\n" + "load(\"@bazel_skylib//:workspace.bzl\", \"bazel_skylib_workspace\") +\r\n bazel_skylib_workspace()";
   }

   @Override
   public String getBazelModuleFile() {
      return "module(name = \"osee_applicability\"," + "version = \"1.0.0\", bazel_compatibility = [\">=7.0.0\"], compatibility_level =1,) \r\n\r\n bazel_dep(name = \"bazel_skylib\", version = \"1.5.0\")";
   }

   @Override
   public String getConfigurationBazelFile(BranchId branchId) {
      String prefix =
         "package(default_visibility = [\"//visibility:public\"])\r\n" + "\r\n" + "#note: Configuration and configuration groups have to be declared here since they are a mutually exclusive selection.\r\n" + "constraint_setting(name = \"configuration\")\r\n";
      String content = "";
      // Load all configurations (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.BranchView).asArtifacts();
      for (ArtifactReadable branchView : branchViews) {
         content = content + "constraint_value(\r\n" + "    name = \"" + branchView.getName().replace(" ",
            "_") + "\",\r\n" + "    constraint_setting = \":configuration\",\r\n" + ")\r\n";
      }
      List<ArtifactReadable> groups =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branchId);
      for (ArtifactToken group : groups) {
         content = content + "constraint_value(\r\n" + "    name = \"" + group.getName().replace(" ",
            "_") + "\",\r\n" + "    constraint_setting = \":configuration\",\r\n" + ")\r\n";
      }
      return prefix + content;
   }

   @Override
   public String getBatConfigurationFile(BranchId branchId, ArtifactReadable art) {
      /**
       * @TODO implement groups once supported in BAT tool
       */
      String content = "[{";
      content = content + " \"normalizedName\": \"" + art.getName().replace(" ", "_") + "\",";
      content = content + " \"features\":[";
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      Map<String, List<String>> namedViewApplicabilityMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branchId, art);
      branchViewsMap.put(art, namedViewApplicabilityMap);
      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.Feature).asArtifacts();
      Iterator<ArtifactReadable> featureIter = featureArts.iterator();
      while (featureIter.hasNext()) {
         ArtifactReadable featureArt = featureIter.next();
         FeatureDefinition fDef = getFeatureDefinition(featureArt);
         content = content + "\"" + fDef.getName() + "=" + getViewToFeatureValue(ArtifactId.valueOf(art.getId()), fDef,
            branchViewsMap) + "\"";
         if (featureIter.hasNext()) {
            content = content + ",";
         }
      }
      content = content + "]";
      content = content + "}]";
      return content;
   }

   @Override
   public String getBatConfigurationGroupFile(BranchId branchId, ArtifactReadable art) {
      String content = "[{";
      content = content + " \"normalizedName\": \"" + art.getName().replace(" ", "_") + "\",";
      content = content + " \"features\":[";
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      Map<String, List<String>> namedViewApplicabilityMap =
         orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branchId, art);
      branchViewsMap.put(art, namedViewApplicabilityMap);
      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branchId).andIsOfType(CoreArtifactTypes.Feature).asArtifacts();
      Iterator<ArtifactReadable> featureIter = featureArts.iterator();
      while (featureIter.hasNext()) {
         ArtifactReadable featureArt = featureIter.next();
         FeatureDefinition fDef = getFeatureDefinition(featureArt);
         content = content + "\"" + fDef.getName() + "=" + getViewToFeatureValue(ArtifactId.valueOf(art.getId()), fDef,
            branchViewsMap) + "\"";
         if (featureIter.hasNext()) {
            content = content + ",";
         }
      }
      content = content + "]";
      content = content + "}]";
      return content;
   }

   @Override
   public String getBazelConfigFileBuildFile(List<ArtifactReadable> arts) {
      String content = "package(default_visibility = [\"//visibility:public\"])\r\n" + "\r\n" + "exports_files([";
      Iterator<ArtifactReadable> artIter = arts.iterator();
      while (artIter.hasNext()) {
         content += "\"" + artIter.next().getName().replace(" ", "_") + ".json" + "\"";
         if (artIter.hasNext()) {
            content += ",";
         }
      }
      content += "]) \r\n";
      Iterator<ArtifactReadable> artIter2 = arts.iterator();
      content += "filegroup(\r\n name=\"resolved_config\",\r\n srcs=select({\r\n";
      while (artIter2.hasNext()) {
         ArtifactReadable next = artIter2.next();
         content += "\"//config:" + next.getName().replace(" ", "_") + "\":[\":" + next.getName().replace(" ",
            "_") + ".json" + "\"]";
         if (artIter2.hasNext()) {
            content += ",\r\n";
         }
      }
      content += "\r\n }) \r\n )\r\n";

      return content;

   }

   @Override
   public String getBazelConfigFileDefsFile(List<ArtifactReadable> arts) {

      String content = "AVAILABLE_PLATFORMS = {";
      Iterator<ArtifactReadable> artIter = arts.iterator();
      while (artIter.hasNext()) {
         ArtifactReadable next = artIter.next();
         content += "\"" + next.getName().replace(" ", "_") + "\":";
         content += "\"" + next.getName().replace(" ", "_") + ".json" + "\"";
         if (artIter.hasNext()) {
            content += ",";
         }
      }
      content += "}";
      return content;

   }
}