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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.BranchViewDefinition;
import org.eclipse.osee.framework.core.applicability.ExtendedFeatureDefinition;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.NameValuePair;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityCacheFile;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.UserId;
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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

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

   public OrcsApplicabilityOps(OrcsApi orcsApi, Log logger) {
      this.orcsApi = orcsApi;
      this.logger = logger;
   }

   /**
    * @return config as defined in Feature artifacts
    */
   @Override
   public ApplicabilityBranchConfig getConfig(BranchId branchId, boolean showAll) {
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
      view.setConfigurationGroup(
         art.getRelated(CoreRelationTypes.PlConfigurationGroup_Group).getAtMostOneOrDefault(ArtifactReadable.SENTINEL));
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
   public XResultData createFeature(FeatureDefinition feature, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Create Feature");

         if (createFeatureDefinition(feature, tx, results).isValid()) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      if (results.getNumErrors() == 0) {
         try {

            UserId user = account;
            boolean changes = false;
            if (user == null) {
               user = SystemUser.OseeSystem;
            }
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Set Defaults for Configurations for New Feature");

            List<ArtifactToken> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
            Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
            for (ArtifactToken view : branchViews) {
               Iterable<String> appl = orcsApi.getQueryFactory().tupleQuery().getTuple2(
                  CoreTupleTypes.ViewApplicability, tx.getBranch(), view);
               if (!appl.toString().contains(feature.getName() + " = ")) {
                  tx.addTuple2(CoreTupleTypes.ViewApplicability, view,
                     feature.getName() + " = " + feature.getDefaultValue());
                  changes = true;
               }

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
   public XResultData updateFeature(FeatureDefinition feature, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Update Feature");

         if (updateFeatureDefinition(feature, tx, results).isValid()) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
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
   public XResultData deleteFeature(ArtifactId feature, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         ArtifactToken featureArt = (ArtifactToken) getFeature(feature.getIdString(), branch).getData();
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete Feature");
         List<ArtifactToken> branchViews = orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch);
         Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
         for (ArtifactToken v : branchViews) {
            Iterable<String> appl =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, tx.getBranch(), v);
            for (String app : appl) {
               if (appl.toString().contains(feature + " = ")) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, v, app);
               }
            }
         }
         tx.deleteArtifact(featureArt);
         tx.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData removeApplicabilityFromView(BranchId branch, ArtifactId viewId, String applicability, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         CreateViewDefinition view = getView(viewId.getIdString(), branch);
         if (orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
            applicability)) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Remove applicability from configuration");
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
   public XResultData updateView(CreateViewDefinition view, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
      CreateViewDefinition editView = getView(view.getIdString(), branch);
      if (editView.isInvalid()) {
         results.errorf("Edit failed: invalid view");
         return results;
      }
      if (view.copyFrom.isValid()) {
         results = copyFromView(branch, ArtifactId.valueOf(editView.getId()), view.copyFrom, account);
      }
      if (results.isErrors()) {
         return results;
      }
      if (view.getProductApplicabilities().isEmpty() && !editView.getProductApplicabilities().isEmpty()) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Remove Configuration product applicabilities");
         tx.deleteAttributes(ArtifactId.valueOf(editView), CoreAttributeTypes.ProductApplicability);
         tx.commit();
      } else if (editView.getProductApplicabilities().isEmpty() || !editView.getProductApplicabilities().equals(
         view.getProductApplicabilities())) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Update Configuration product applicabilities");
         tx.setAttributesFromValues(ArtifactId.valueOf(editView), CoreAttributeTypes.ProductApplicability,
            view.getProductApplicabilities());
         tx.commit();
      }

      if (!view.getName().equals(editView.getName())) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Update Configuration Name");
         tx.setName(ArtifactId.valueOf(editView), view.getName());
         tx.commit();
      }
      if (view.getConfigurationGroup().isValid()) {
         if (!view.getConfigurationGroup().equals(editView.getConfigurationGroup())) {
            results = relateCfgGroupToView(view.getConfigurationGroup().getIdString(), editView.getIdString(), branch,
               account);
            if (results.isErrors()) {
               return results;
            }
         }
      }

      return results;
   }

   @Override
   public XResultData createView(CreateViewDefinition view, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
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

            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Create View ");
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
            TransactionBuilder tx2 = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Create Config and Base applicabilities on New View");
            tx2.createApplicabilityForView(vDefArt, "Base");
            tx2.createApplicabilityForView(vDefArt, "Config = " + view.getName());
            tx2.commit();
         } catch (Exception ex) {
            results.errorf(Lib.exceptionToString(ex));
            return results;
         }
      }
      if (view.getCopyFrom().isValid() || view.getConfigurationGroup().isValid()) {
         if (newView.isValid()) {
            if (view.getCopyFrom().isValid()) {
               results = copyFromView(branch, ArtifactId.valueOf(newView), view.copyFrom, account);
               if (results.isErrors()) {
                  return results;
               }
            }
            if (view.getConfigurationGroup().isValid()) {
               results = relateCfgGroupToView(view.getConfigurationGroup().getIdString(), newView.getIdString(), branch,
                  account);
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
   public XResultData deleteView(String view, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         CreateViewDefinition viewDef = getView(view, branch);
         //before removing view unrelate from group and remove associated applicability tag from
         //list of valid tags from the group
         if (viewDef.getConfigurationGroup().isValid()) {
            unrelateCfgGroupToView(viewDef.getConfigurationGroup().getIdString(), viewDef.getIdString(), branch,
               account);
         }
         Iterable<String> deleteApps = orcsApi.getQueryFactory().tupleQuery().getTuple2(
            CoreTupleTypes.ViewApplicability, branch, ArtifactId.valueOf(viewDef.getId()));

         ArtifactToken viewArt = (ArtifactToken) viewDef.getData();
         if (viewArt.isValid()) {
            TransactionBuilder txApps = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Delete all applicabilities for deleted view");
            for (String app : deleteApps) {
               txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(viewDef.getId()), app);
            }
            txApps.commit();
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete View");
            tx.deleteArtifact(viewArt);
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData setApplicability(BranchId branch, ArtifactId view, ArtifactId feature, String applicability, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         ArtifactReadable featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andId(feature).asArtifactOrSentinel();
         FeatureDefinition fDef = orcsApi.getApplicabilityOps().getFeatureDefinition(featureArt);
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Set View Feature Applicability");
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
                  tx.addTuple2(CoreTupleTypes.ViewApplicability, view, newValue);
               }
            }
         } else {
            for (String newValue : newValues) {
               change = true;
               tx.addTuple2(CoreTupleTypes.ViewApplicability, view, newValue);
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

   public XResultData copyFromView(BranchId branch, ArtifactId view, ArtifactId copy_from, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Set configuration/View Feature Applicability");

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
                  tx.addTuple2(CoreTupleTypes.ViewApplicability, view, newValue);
               }
            }
         } else {
            for (String newValue : newValues) {
               change = true;
               tx.addTuple2(CoreTupleTypes.ViewApplicability, view, newValue);
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
   public XResultData createApplicabilityForView(ArtifactId viewId, String applicability, UserId account, BranchId branch) {
      XResultData results = new XResultData();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
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
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Create applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.startsWith("ConfigurationGroup =")) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Create applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.equals("Base")) {
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Create applicability");
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
            TransactionBuilder tx =
               orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");
            tx.createApplicabilityForView(viewId, applicability);
            tx.commit();
            /**
             * Once a new compound applicability tag is created, it must be evaluated whether the tag applies for each
             * view on the branch
             */
            for (ArtifactId bView : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
               updateCompoundApplicabilities(branch, bView, user, true);
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
            TransactionBuilder txMv =
               orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");
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
            updateCompoundApplicabilities(branch, viewId, user, true);
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
               TransactionBuilder tx =
                  orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");

               for (String existingValue : existingValues) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, existingValue);
               }

               tx.createApplicabilityForView(viewId, applicability);
               tx.commit();
               updateCompoundApplicabilities(branch, viewId, user, true);

            } else {
               results.error(featureValue + " is an invalid value");
            }
         }

      }

      return results;
   }

   private XResultData updateCompoundApplicabilities(BranchId branch, ArtifactId viewId, UserId user, boolean update) {
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
                  TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
                     "Remove invalid compound applicability");
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, app.getName());
                  tx.commit();
               }
               actions.add("Remove " + app.getName() + " from configuration: " + view.getName());
            }
         } else {
            if (validApplicability) {
               if (update) {
                  TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
                     "Apply valid compound applicability");
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
   public XResultData createCfgGroup(ConfigurationGroupDefinition group, BranchId branch, UserId account) {
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
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Create PL Configuration Group");
         ArtifactToken vDefArt = null;

         vDefArt = tx.createArtifact(getPlConfigurationGroupsFolder(tx.getBranch()), CoreArtifactTypes.GroupArtifact,
            group.getName());
         tx.setName(vDefArt, group.getName());
         // reload artifact to return
         tx.commit();
         ArtifactId newGrp =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               group.getName()).asArtifactId();
         TransactionBuilder tx2 = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create Config and Base applicabilities on new view");
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
               relateCfgGroupToView(newGrp.getIdString(), cfg, branch, user);
            }
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData updateCfgGroup(ConfigurationGroupDefinition group, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      ConfigurationGroupDefinition currentGroup = getConfigurationGroup(group.getId(), branch);
      if (!currentGroup.getName().equals(Strings.EMPTY_STRING)) {
         try {
            UserId user = account;
            if (user == null) {
               user = SystemUser.OseeSystem;
            }

            if (!group.getName().equals(currentGroup.getName())) {
               TransactionBuilder tx =
                  orcsApi.getTransactionFactory().createTransaction(branch, user, "Update PL Configuration Group Name");
               tx.setName(ArtifactId.valueOf(group.getId()), group.getName());
               tx.commit();
            }
            if (!group.getConfigurations().toString().equals(currentGroup.getConfigurations().toString())) {
               for (String cfg : currentGroup.getConfigurations()) {
                  if (!group.getConfigurations().contains(cfg)) {
                     results = unrelateCfgGroupToView(currentGroup.getId(), cfg, branch, user);
                  }
               }
               for (String cfg : group.getConfigurations()) {
                  if (!currentGroup.getConfigurations().contains(cfg)) {
                     results = relateCfgGroupToView(currentGroup.getId(), cfg, branch, account);
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
   public XResultData relateCfgGroupToView(String groupId, String viewId, BranchId branch, UserId account) {
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
      ArtifactReadable currentGroup =
         view.getRelated(CoreRelationTypes.PlConfigurationGroup_Group).getAtMostOneOrDefault(ArtifactReadable.SENTINEL);
      if (currentGroup.isValid() && currentGroup.equals(cfgGroup)) {
         results.errorf("View is already in the group");
         return results;
      }
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         if (currentGroup.isValid()) {
            TransactionBuilder tx1 = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Unrelate view: " + view.getName() + " to PL Configuration Group " + currentGroup.getName());
            tx1.unrelate(currentGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
            tx1.deleteTuple2(CoreTupleTypes.ViewApplicability, view, "ConfigurationGroup = " + currentGroup.getName());
            tx1.deleteTuple2(CoreTupleTypes.ViewApplicability, currentGroup, "Config = " + view.getName());
            tx1.commit();
            syncConfigGroup(branch, currentGroup.getIdString(), account, results);
         }

         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Relate view: " + view.getName() + " to PL Configuration Group " + cfgGroup.getName());
         tx.relate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
         tx.createApplicabilityForView(view, "ConfigurationGroup = " + cfgGroup.getName());
         tx.createApplicabilityForView(cfgGroup, "Config = " + view.getName());
         tx.commit();
         syncConfigGroup(branch, cfgGroup.getIdString(), account, results);

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData unrelateCfgGroupToView(String groupId, String viewId, BranchId branch, UserId account) {
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
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Un-Relate view from PL Configuration Group ");
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
   public XResultData deleteCfgGroup(String id, BranchId branch, UserId account) {
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
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         //unrelate group from each view
         //will remove applicability tag for configuration group from each view
         for (ArtifactReadable view : cfgGroup.getRelated(
            CoreRelationTypes.PlConfigurationGroup_BranchView).getList()) {
            unrelateCfgGroupToView(cfgGroup.getIdString(), view.getIdString(), branch, account);
         }
         Iterable<String> deleteApps =
            orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, cfgGroup);

         TransactionBuilder txApps = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Delete Applicabilities associated with ConfigurationGroup");
         for (String app : deleteApps) {
            txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, app);
         }
         txApps.commit();
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete Cfg Group");
         tx.deleteArtifact(cfgGroup);
         tx.commit();
         results.getIds().add(id);
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData syncConfigGroup(BranchId branch, String id, UserId account, XResultData results) {
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
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
         ApplicabilityBranchConfig current = getConfig(branch, false);
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
                     TransactionBuilder tx =
                        orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");
                     tx.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, currentApp);
                     tx.createApplicabilityForView(cfgGroup, resultApp);
                     tx.commit();
                  }
               } else if (results.isSuccess()) {
                  TransactionBuilder tx2 =
                     orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");

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
            updateCompoundApplicabilities(branch, cfgGroup, user, true);
         }
      } else {
         results.error("Invalid Configuration Group name.");
      }
      return results;
   }

   @Override
   public XResultData syncConfigGroup(BranchId branch, UserId account) {
      XResultData results = new XResultData();
      for (ArtifactToken group : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
         branch)) {
         syncConfigGroup(branch, group.getIdString(), account, results);
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
      if (sourcePath == null || stagePath == null) {
         results.error("Both a source path and stage path are required\n");
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

      stagePath = getFullStagePath(results, stagePath, ops.getOpsView().getName());
      if (results.isErrors()) {
         return results;
      }

      return ops.applyApplicabilityToFiles(results, commentNonApplicableBlocks, sourcePath, stagePath);
   }

   @Override
   public XResultData refreshStagedFiles(BlockApplicabilityStageRequest data, BranchId branch) {
      XResultData results = new XResultData();
      results.setLogToSysErr(true);

      String sourcePath = data.getSourcePath();
      String stagePath = data.getStagePath();
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

      stagePath = getFullStagePath(results, stagePath, ops.getOpsView().getName());
      if (results.isErrors()) {
         return results;
      }

      ops.setUpBlockApplicability(commentNonApplicableBlocks);

      return ops.refreshStagedFiles(results, sourcePath, stagePath, files);
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

         String fullStagePath = getFullStagePath(results, stagePath, ops.getOpsView().getName());
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

   private String getFullStagePath(XResultData results, String stagePath, String viewName) {
      File stageDir = new File(stagePath, "Staging");
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
   public XResultData validateCompoundApplicabilities(BranchId branch, UserId account, boolean update) {
      XResultData results = new XResultData();
      List<String> actions = new ArrayList<>();
      for (ArtifactId bView : orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch)) {
         actions.addAll(updateCompoundApplicabilities(branch, bView, account, update).getResults());
      }
      results.setResults(actions);
      return results;
   }
}