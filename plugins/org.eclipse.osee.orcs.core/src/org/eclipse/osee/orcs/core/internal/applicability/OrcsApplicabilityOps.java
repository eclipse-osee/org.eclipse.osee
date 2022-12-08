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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
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
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
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
   public ApplicabilityBranchConfig getConfig(BranchId branchId) {
      ApplicabilityBranchConfig config = new ApplicabilityBranchConfig();
      Branch branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getExactlyOne();

      config.setBranch(branch);
      config.setAssociatedArtifactId(branch.getAssociatedArtifact());
      if (branch.getBranchType().equals(BranchType.WORKING.getId())) {
         config.setEditable(true);
      } else {
         config.setEditable(false);
      }
      if (branch.getParentBranch().isValid()) {
         Branch parentBranch =
            orcsApi.getQueryFactory().branchQuery().andId(branch.getParentBranch()).getResults().getExactlyOne();
         config.setParentBranch(new BranchViewToken(parentBranch, parentBranch.getName(), parentBranch.getViewId()));
      }
      // Load all configurations (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).asArtifacts();
      List<ArtifactToken> groups =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branch);

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
         config.addView(new BranchViewDefinition(branchView.getIdString(), branchView.getName(),
            branchView.fetchAttributesAsStringList(CoreAttributeTypes.ProductApplicability),
            hasFeatureApplicabilities));
         branchViewsMap.put(branchView, namedViewApplicabilityMap);

      }
      for (ArtifactToken group : groups) {
         config.addGroup(getConfigurationGroup(group.getIdString(), branch));
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
         for (ConfigurationGroupDefinition group : config.getGroups()) {
            String groupToFeatureValue = getViewToFeatureValue(ArtifactId.valueOf(group.getId()), fDef, branchViewsMap);
            List<NameValuePair> groupList = new LinkedList<>();
            groupList.add(new NameValuePair(group.getName(), groupToFeatureValue));
            //check if view is present in a specific group & add it to the groupings list to be added post views
            for (BranchViewDefinition memberConfig : config.getViews()) {
               if (group.getConfigurations().contains(memberConfig.getId())) {
                  String viewToFeatureValue =
                     getViewToFeatureValue(ArtifactId.valueOf(memberConfig.getId()), fDef, branchViewsMap);
                  groupList.add(new NameValuePair(memberConfig.getName(), viewToFeatureValue));
               }
            }
            groupingsList.add(groupList);
         }
         //check for view present in groupingsList, if not present add configuration
         for (BranchViewDefinition view : config.getViews()) {
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
         config.addFeature(extfDef);
      }

      return config;
   }

   @Override
   public ApplicabilityBranchConfig getConfigWithCompoundApplics(BranchId branchId) {
      ApplicabilityBranchConfig config = getConfig(branchId);
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
               config.getFeatures().stream().filter(f -> f.getName().equals(feature1Name)).findFirst().orElse(
                  ExtendedFeatureDefinition.SENTINEL);
            ExtendedFeatureDefinition f2ExDef =
               config.getFeatures().stream().filter(f -> f.getName().equals(feature2Name)).findFirst().orElse(
                  ExtendedFeatureDefinition.SENTINEL);

            if (f1ExDef.isValid() && f2ExDef.isValid()) {
               for (NameValuePair f1Config : f1ExDef.getConfigurations()) {
                  for (NameValuePair f2Config : f2ExDef.getConfigurations()) {
                     if (f1Config.getName().equals(f2Config.getName())) {
                        String compoundValue = "Excluded";
                        boolean f1Applic = feature1Value.equals(f1Config.getValue());
                        boolean f2Applic = feature2Value.equals(f2Config.getValue());
                        if ((operator == '|' && (f1Applic || f2Applic))
                           || (operator == '&' && (f1Applic && f2Applic))) {
                           compoundValue = "Included";
                        }
                        extfDef.addConfiguration(new NameValuePair(f1Config.getName(), compoundValue));
                     }
                  }
               }
            }

            config.addFeature(extfDef);
         }
      }
      return config;
   }

   private String getViewToFeatureValue(ArtifactId view, FeatureDefinition fDef, Map<ArtifactId, Map<String, List<String>>> branchViewsMap) {
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
   public ArtifactToken createFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx, XResultData results) {
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
   public ArtifactToken updateFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx, XResultData results) {
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

            List<ArtifactToken> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
            Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
            for (ArtifactToken view : branchViews) {
               Iterable<String> appl = orcsApi.getQueryFactory().tupleQuery().getTuple2(
                  CoreTupleTypes.ViewApplicability, tx.getBranch(), view);
               if (!appl.toString().contains(feature.getName() + " = ")) {
                  String applicString = feature.getName() + " = " + feature.getDefaultValue();
                  addIntroduceTuple2(CoreTupleTypes.ViewApplicability, view, tx, applicString);
                  changes = true;
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

   private void addIntroduceTuple2(Tuple2Type<ArtifactId, String> tupleType, ArtifactId featureArt, TransactionBuilder tx, String applicString) {
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
   public Collection<FeatureDefinition> getFeaturesByProductApplicability(BranchId branch, String productApplicability) {
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
         TransactionBuilder tx = txFactory.createTransaction(branch, "Delete Feature");
         List<ArtifactToken> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
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
      ArtifactToken groupArt = ArtifactToken.SENTINEL;
      if (Strings.isNumeric(cfgGroup)) {
         groupArt = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andId(
            ArtifactId.valueOf(cfgGroup)).asArtifactTokenOrSentinel();

      } else {
         groupArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               cfgGroup).asArtifactTokenOrSentinel();
      }
      if (groupArt.isValid()) {
         configGroup.setName(groupArt.getName());
         configGroup.setId(groupArt.getIdString());
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
      } else if (editView.getProductApplicabilities().isEmpty()
         || !editView.getProductApplicabilities().equals(view.getProductApplicabilities())) {
         TransactionBuilder tx = txFactory.createTransaction(branch, "Update Configuration product applicabilities");
         tx.setAttributesFromValues(ArtifactId.valueOf(editView), CoreAttributeTypes.ProductApplicability,
            view.getProductApplicabilities());
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
               txMv.createApplicabilityForView(viewId, val);
            }
            txMv.commit();
            updateCompoundApplicabilities(branch, viewId, true);
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
               TransactionBuilder tx = txFactory.createTransaction(branch, "Set applicability for view");

               for (String existingValue : existingValues) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, existingValue);
               }

               tx.createApplicabilityForView(viewId, applicability);
               tx.commit();
               updateCompoundApplicabilities(branch, viewId, true);

            } else {
               results.error(featureValue + " is an invalid value");
            }
         }

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
      if (!(currentGroup.isEmpty())
         && !(currentGroup.stream().filter(o -> o.equals(cfgGroup)).collect(Collectors.toList()).isEmpty())) {
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
         ApplicabilityBranchConfig current = getConfig(branch);
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
                                 results.error("Updating Group: " + cfgGroup.getName()
                                    + ". Applicabilities differ for non-binary feature: " + feature.getName());
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

   private BlockApplicabilityOps getBlockApplicabilityOps(XResultData results, BlockApplicabilityStageRequest data, ArtifactId viewId, String cachePath, BranchId branch) {
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
   public XResultData validateCompoundApplicabilities(BranchId branch, boolean update) {
      XResultData results = new XResultData();
      List<String> actions = new ArrayList<>();
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
   public Collection<ProductTypeDefinition> getProductTypeDefinitions(BranchId branch, long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
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
      if (existingProductType.isValid() && existingProductType.getName().equals(productType.getName())
         && !existingProductType.getId().equals(productType.getId())) {
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
   public Collection<CreateViewDefinition> getViewsDefinitionsByProductApplicability(BranchId branch, String productApplicability) {
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
      try {
         new File(uniqueIdDir).mkdir();
         String fileZip = String.format("%s.zip", sourceNameDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();
         zip.close();

         OutputStream outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);
         outStream.close();

         ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(sourceNameDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = newFile(unzipLocation, zipEntry);
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  throw new IOException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // fix for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  throw new IOException("Failed to create directory " + parent);
               }
               // write file content
               FileOutputStream fos = new FileOutputStream(uploadedDirectory);
               int len;
               while ((len = zis.read(buffer)) > 0) {
                  fos.write(buffer, 0, len);
               }
               fos.close();
            }
            zipEntry = zis.getNextEntry();
         }
         zis.closeEntry();
         zis.close();
      } catch (IOException ex) {
         throw new OseeCoreException(ex, "Unable to upload zip file");
      }

      return uniqueId;
   }

   @Override
   public XResultData applyBlockVisibilityOnServer(String blockApplicId, BlockApplicabilityStageRequest data, BranchId branch) {
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
      try {
         new File(uniqueIdDir).mkdir();
         String fileZip = String.format("%s.zip", sourceNameDir);
         File uploadedZip = new File(fileZip);
         byte[] buffer = zip.readAllBytes();

         OutputStream outStream = new FileOutputStream(uploadedZip);
         outStream.write(buffer);

         ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
         ZipEntry zipEntry = zis.getNextEntry();
         File unzipLocation = new File(sourceNameDir);
         unzipLocation.mkdirs();
         while (zipEntry != null) {
            File uploadedDirectory = newFile(unzipLocation, zipEntry);
            if (zipEntry.isDirectory()) {
               if (!uploadedDirectory.isDirectory() && !uploadedDirectory.mkdirs()) {
                  throw new IOException("Failed to create directory " + uploadedDirectory);
               }
            } else {
               // fix for Windows-created archives
               File parent = uploadedDirectory.getParentFile();
               if (!parent.isDirectory() && !parent.mkdirs()) {
                  throw new IOException("Failed to create directory " + parent);
               }
               // write file content
               FileOutputStream fos = new FileOutputStream(uploadedDirectory);
               int len;
               while ((len = zis.read(buffer)) > 0) {
                  fos.write(buffer, 0, len);
               }
               fos.close();
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
}