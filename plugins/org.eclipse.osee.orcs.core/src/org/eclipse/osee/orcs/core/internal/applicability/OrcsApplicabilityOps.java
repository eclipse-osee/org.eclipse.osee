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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.BranchViewDefinition;
import org.eclipse.osee.framework.core.applicability.ExtendedFeatureDefinition;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.NameValuePair;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
import org.eclipse.osee.framework.core.util.JsonUtil;
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
   private ArtifactToken productsFolder = ArtifactToken.SENTINEL;
   private ArtifactToken plConfigurationGroupsFolder = ArtifactToken.SENTINEL;

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
      // Load all products (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).getResults().getList();
      List<ArtifactToken> groups =
         orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(branch);

      Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      for (ArtifactReadable branchView : branchViews) {
         List<ApplicabilityToken> appTokens =
            orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(branchView, branch);
         appTokens.removeIf(n -> n.getName().startsWith("Config ="));

         boolean hasFeatureApplicabilities = (appTokens.size() > 1);
         config.addView(new BranchViewDefinition(branchView.getIdString(), branchView.getName(),
            branchView.getAttributeValues(CoreAttributeTypes.ProductApplicability), hasFeatureApplicabilities));
         branchViewsMap.put(branchView,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, branchView));
      }
      for (ArtifactToken group : groups) {
         config.addGroup(getConfigurationGroup(group.getIdString(), branch));
         branchViewsMap.put(group,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, group));
      }
      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).getResults().getList();
      Collections.sort(featureArts, new NamedComparator(SortOrder.ASCENDING));
      for (ArtifactToken featureArt : featureArts) {
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
   public CreateViewDefinition getViewDefinition(ArtifactToken featureArt) {
      ArtifactReadable art = (ArtifactReadable) featureArt;
      CreateViewDefinition view = new CreateViewDefinition();
      view.setId(art.getId());
      view.setName(art.getName());
      view.setProductApplicabilities(art.getAttributeValues(CoreAttributeTypes.ProductApplicability));
      view.setData(art);
      return view;
   }

   @Override
   public FeatureDefinition getFeatureDefinition(ArtifactToken featureArt) {
      ArtifactReadable art = (ArtifactReadable) featureArt;

      FeatureDefinition feature = new FeatureDefinition();
      feature.setId(art.getId());
      feature.setName(art.getName());
      feature.setDefaultValue(art.getSoleAttributeValue(CoreAttributeTypes.DefaultValue, ""));
      feature.setValues(art.getAttributeValues(CoreAttributeTypes.Value));
      feature.setProductApplicabilities(art.getAttributeValues(CoreAttributeTypes.ProductApplicability));
      feature.setValueType(art.getSoleAttributeAsString(CoreAttributeTypes.FeatureValueType, ""));
      feature.setMultiValued(art.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued, false));
      feature.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
      feature.setData(featureArt);
      return feature;
   }

   @Override
   public ArtifactToken createUpdateFeatureDefinition(FeatureDefinition featureDef, String action, TransactionBuilder tx, XResultData results) {
      ArtifactToken fDefArt = null;

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

      if (action != null && action.equals("add") && lFeature.isValid()) {
         results.error("Feature: " + lFeature.getName() + " already exists.");
      }
      if (results.isErrors()) {
         return null;
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
            return null;
         }
         Long artId = featureDef.getId();
         if (artId == null || artId <= 0) {
            artId = Lib.generateArtifactIdAsInt();
         }
         fDefArt = tx.createArtifact(featuresFolder, CoreArtifactTypes.Feature, featureDef.getName(), artId);
      } else {
         fDefArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
            ArtifactId.valueOf(lFeature.getId())).getResults().getAtMostOneOrDefault(ArtifactReadable.SENTINEL);

      }
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
                  "Features").getArtifactOrSentinal();
      }
      return featureFolder;
   }

   @Override
   public ArtifactToken getProductsFolder(BranchId branch) {
      if (productsFolder.isInvalid()) {
         productsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "Products").getArtifactOrSentinal();
      }
      return productsFolder;
   }

   @Override
   public ArtifactToken getPlConfigurationGroupsFolder(BranchId branch) {
      if (plConfigurationGroupsFolder.isInvalid()) {
         plConfigurationGroupsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Folder).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.ProductLineFolder).andNameEquals(
                  "PL Configuration Groups").getArtifactOrSentinal();
      }
      return plConfigurationGroupsFolder;
   }

   @Override
   public ArtifactToken getProductLineFolder(BranchId branch) {
      if (plFolder.isInvalid()) {
         plFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.ProductLineFolder).getArtifactOrSentinal();
      }
      if (plFolder.isInvalid()) {
         plFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andNameEquals("Product Line").asArtifactTokenOrSentinel();
      }
      return plFolder;
   }

   @Override
   public XResultData convertConfigToArtifact(BranchId branch) {
      XResultData results = new XResultData();
      BranchId ah64eRootBranch = BranchId.valueOf("5968659056771480963");
      BranchId aopRootBranch = BranchId.valueOf("6155750414142653974");
      BranchId rootBranch = BranchId.SENTINEL;
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem,
         "Convert Feature Defs to Artifact");
      List<ArtifactReadable> featureDefinitionArts = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.FeatureDefinition).getResults().getList();

      List<FeatureDefinition> featureDefinitionFromJson = new ArrayList<>();

      for (ArtifactReadable art : featureDefinitionArts) {
         String json = art.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
         FeatureDefinition[] readValue = JsonUtil.readValue(json, FeatureDefinition[].class);
         featureDefinitionFromJson.addAll(Arrays.asList(readValue));
      }
      for (FeatureDefinition feature : featureDefinitionFromJson) {
         ArtifactToken featureArt = ArtifactToken.SENTINEL;
         boolean introduce = false;
         if (feature.getDescription().isEmpty()) {
            feature.setDescription(feature.getName());
         }
         if (feature.getDefaultValue().isEmpty()) {
            feature.setDefaultValue(feature.getValues().iterator().next());
         }
         FeatureDefinition featureDef = orcsApi.getApplicabilityOps().getFeature(feature.getName(), branch);
         if (featureDef.isInvalid()) {
            //see if feature is on ah64e product line or aop product line... if so, introduce
            featureArt = orcsApi.getQueryFactory().fromBranch(ah64eRootBranch).andIsOfType(
               CoreArtifactTypes.Feature).andNameEquals(feature.getName()).getResults().getAtMostOneOrNull();
            if (featureArt.isValid()) {
               results.warning("Introduce " + feature.getName() + " from AH64E Product Line");
               introduce = true;
            } else {
               featureArt = orcsApi.getQueryFactory().fromBranch(aopRootBranch).andIsOfType(
                  CoreArtifactTypes.Feature).andNameEquals(feature.getName()).getResults().getAtMostOneOrNull();
               if (featureArt.isValid()) {
                  results.warning("Introduce " + feature.getName() + " from AOP Product Line");
                  introduce = true;
               }
            }

         }
         if (feature.getType().equals("single")) {
            feature.setMultiValued(false);
         } else if (feature.getType().equals("multiple")) {
            feature.setMultiValued(true);
         } else {
            throw new IllegalArgumentException(String.format("Unexpected value type [%s]", feature.getValueType()));
         }

         if (featureDef.isValid()) {
            // set feature value type
            feature.setValueType("String");
            createUpdateFeatureDefinition(feature, "update", tx, results);

         } else {
            if (!introduce) {
               feature.setValueType("String");
               createUpdateFeatureDefinition(feature, "add", tx, results);
            }
         }
         if (results.isErrors()) {
            results.error("Errors on branch: " + orcsApi.getQueryFactory().branchQuery().andId(
               branch).getResults().getOneOrNull().getName() + " creating feature: " + feature.getName());
            return results;
         }
      }

      if (!results.isErrors()) {
         tx.commit();
      }
      return results;
   }

   @Override
   public XResultData createUpdateFeature(FeatureDefinition feature, String action, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Update Feature");

         if (createUpdateFeatureDefinition(feature, action, tx, results) != null) {
            tx.commit();
         }
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      if (action.equals("add") && results.getNumErrors() == 0) {
         try {

            UserId user = account;
            boolean changes = false;
            if (user == null) {
               user = SystemUser.OseeSystem;
            }
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Set Defaults for Products/Views for New Feature");

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
   public FeatureDefinition getFeature(String feature, BranchId branch) {
      if (Strings.isNumeric(feature)) {
         ArtifactToken featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).andId(
               ArtifactId.valueOf(feature)).getResults().getAtMostOneOrNull();
         if (featureArt != null) {
            return getFeatureDefinition(featureArt);
         }
      } else {
         ArtifactToken featureArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).andNameEquals(
               feature).getResults().getAtMostOneOrNull();
         if (featureArt != null) {
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
         ArtifactToken viewArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andId(
               ArtifactId.valueOf(view)).getResults().getAtMostOneOrNull();
         if (viewArt != null) {
            viewDef = getViewDefinition(viewArt);
         }
      } else {
         ArtifactToken viewArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
               view).getResults().getAtMostOneOrNull();
         if (viewArt != null) {
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
            ArtifactId.valueOf(cfgGroup)).getResults().getAtMostOneOrNull();

      } else {
         groupArt =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               cfgGroup).getResults().getAtMostOneOrNull();
      }
      if (groupArt != null) {
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
   public XResultData createUpdateView(CreateViewDefinition view, String action, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      UserId user = account;
      if (user == null) {
         user = SystemUser.OseeSystem;
      }
      if ((action.equals("edit"))) {
         ArtifactToken editView =
            orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(view.getId())).asArtifactToken();
         if (editView.isInvalid()) {
            results.errorf("Edit failed: invalid view");
            return results;
         }
         if (view.copyFrom.isValid()) {
            results = copyFromView(branch, editView, view.copyFrom, account);
         }
         if (results.isErrors()) {
            return results;
         }
         if (!view.getProductApplicabilities().isEmpty()) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Update Configuration Product Applicabilities");
            tx.setAttributesFromValues(editView, CoreAttributeTypes.ProductApplicability,
               view.getProductApplicabilities());
            tx.commit();
         }

         if (!view.getName().equals(editView.getName())) {
            TransactionBuilder tx =
               orcsApi.getTransactionFactory().createTransaction(branch, user, "Update Configuration/View Name");
            tx.setName(editView, view.getName());
            tx.commit();
         }
         if (view.getConfigurationGroup().isValid()) {
            results = relateCfgGroupToView(view.getConfigurationGroup().getIdString(), editView.getIdString(), branch,
               account);
            if (results.isErrors()) {
               return results;
            }
         }

      } else {
         if ((action.equals("add"))) {
            if (!Strings.isValid(view.getName())) {
               results.errorf("Name can not be empty for product %s", view.getId());
               return results;
            }

            CreateViewDefinition xView = getView(view.getName(), branch);
            if (xView.isValid()) {
               results.errorf("Product Name is already in use.");
               return results;
            }
            if ((xView.isInvalid())) {
               try {

                  TransactionBuilder tx =
                     orcsApi.getTransactionFactory().createTransaction(branch, user, "Create View ");
                  createUpdateViewDefinition(view, tx);
                  tx.commit();
                  CreateViewDefinition newView = getView(view.getName(), branch);
                  TransactionBuilder tx2 = orcsApi.getTransactionFactory().createTransaction(branch, user,
                     "Create Config and Base applicabilities on New View");
                  tx2.createApplicabilityForView(ArtifactId.valueOf(newView.getId()), "Base");
                  tx2.createApplicabilityForView(ArtifactId.valueOf(newView.getId()), "Config = " + view.getName());
                  tx2.commit();
               } catch (Exception ex) {
                  results.errorf(Lib.exceptionToString(ex));
                  return results;
               }
            }
            if (view.getCopyFrom().isValid() || view.getConfigurationGroup().isValid()) {
               CreateViewDefinition nView = getView(view.getName(), branch);

               if (nView.isValid()) {
                  if (view.getCopyFrom().isValid()) {
                     results = copyFromView(branch, ArtifactId.valueOf(nView), view.copyFrom, account);
                     if (results.isErrors()) {
                        return results;
                     }
                  }
                  if (view.getConfigurationGroup().isValid()) {
                     results = relateCfgGroupToView(view.getConfigurationGroup().getIdString(), nView.getIdString(),
                        branch, account);
                     if (results.isErrors()) {
                        return results;
                     }
                  }
               } else {
                  results.error("Errors creating new configuration: " + view.getName());
               }
            }
         }
      }
      return results;
   }

   private ArtifactToken createUpdateViewDefinition(CreateViewDefinition view, TransactionBuilder tx) {
      ArtifactToken vDefArt = null;
      if (view.isValid()) {
         vDefArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
            ArtifactId.valueOf(view.getId())).getResults().getAtMostOneOrNull();
      }
      if (vDefArt == null) {
         Long artId = view.getId();
         if (artId == null || artId <= 0) {
            artId = Lib.generateArtifactIdAsInt();
         }
         vDefArt =
            tx.createArtifact(getProductsFolder(tx.getBranch()), CoreArtifactTypes.BranchView, view.getName(), artId);
      }
      tx.setName(vDefArt, view.getName());
      if (!view.getProductApplicabilities().isEmpty()) {
         tx.setAttributesFromValues(vDefArt, CoreAttributeTypes.ProductApplicability, view.getProductApplicabilities());
      }
      // reload artifact to return

      return orcsApi.getQueryFactory().fromBranch(vDefArt.getBranch()).andId(vDefArt).getArtifactOrSentinal();
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
         ArtifactToken featureArt = orcsApi.getQueryFactory().fromBranch(branch).andId(feature).getArtifactOrNull();
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
         TransactionBuilder tx =
            orcsApi.getTransactionFactory().createTransaction(branch, user, "Set Product/View Feature Applicability");

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
         results.error("Applicability already exists.");
         return results;
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
         } else {
            results.error(
               "Invalid applicability tag.  One of the applicabilities used is not valid for the given view.");
         }

      } else {
         String featureName = applicability.substring(0, applicability.indexOf("=") - 1);
         String featureValue = applicability.substring(applicability.indexOf("=") + 2);
         if (orcsApi.getQueryFactory().applicabilityQuery().featureExistsOnBranch(branch,
            featureName) && orcsApi.getQueryFactory().applicabilityQuery().featureValueIsValid(branch, featureName,
               featureValue)) {
            List<String> existingValues = new LinkedList<>();
            Iterable<String> existingApps =
               orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, viewId);
            for (String appl : existingApps) {
               if (appl.startsWith(featureName + " = ") || appl.contains("| " + featureName + "=")) {
                  existingValues.add(appl);
               }
            }
            TransactionBuilder tx =
               orcsApi.getTransactionFactory().createTransaction(branch, user, "Set applicability for view");
            boolean multiValued = false;
            if (existingValues.size() > 0) {
               List<FeatureDefinition> featureDefinitionData = getFeatureDefinitionData(branch);
               for (FeatureDefinition feat : featureDefinitionData) {
                  if (feat.getName().toUpperCase().equals(featureName)) {
                     multiValued = feat.isMultiValued();
                     break;
                  }
               }
            }
            if (!multiValued) {
               for (String existingValue : existingValues) {
                  tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, existingValue);
               }
            }
            tx.createApplicabilityForView(viewId, applicability);
            tx.commit();
            updateCompoundApplicabilities(branch, viewId, user);

         } else {
            results.error("Feature is not defined or Value is invalid.");
         }

      }

      return results;
   }

   private XResultData updateCompoundApplicabilities(BranchId branch, ArtifactId viewId, UserId user) {
      /**
       * After updating an value on the feature value matrix for a specific view; there is a need to evaluate each of
       * the existing compound applicabilities on a branch to see if the applicability is valid for the view.
       */
      XResultData results = new XResultData();
      Collection<ApplicabilityToken> allApps =
         orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityTokens(branch).values();
      List<ApplicabilityToken> compoundApps =
         allApps.stream().filter(p -> p.getName().contains("|") || p.getName().contains("&")).collect(
            Collectors.toList());
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
               TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
                  "Remove invalid compound applicability");
               tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, app.getName());
               tx.commit();
            }
         } else {
            if (validApplicability) {
               TransactionBuilder tx =
                  orcsApi.getTransactionFactory().createTransaction(branch, user, "Apply valid compound applicability");
               tx.createApplicabilityForView(viewId, app.getName());
               tx.commit();
            }
         }
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
         Long artId = Lib.generateArtifactIdAsInt();

         vDefArt = tx.createArtifact(getPlConfigurationGroupsFolder(tx.getBranch()), CoreArtifactTypes.GroupArtifact,
            group.getName(), artId);
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
      ArtifactToken view;
      if (Strings.isNumeric(viewId)) {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andId(
            ArtifactId.valueOf(viewId)).getArtifactOrSentinal();

      } else {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewId).getArtifactOrSentinal();
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
            "Relate view: " + view.getName() + " to PL Configuration Group " + cfgGroup.getName());
         tx.relate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, ArtifactId.valueOf(view.getIdString()));
         tx.createApplicabilityForView(view, "ConfigurationGroup = " + cfgGroup.getName());
         tx.createApplicabilityForView(cfgGroup, "Config = " + view.getName());
         tx.commit();

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
            ArtifactId.valueOf(viewId)).getArtifactOrSentinal();

      } else {
         view = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewId).getArtifactOrSentinal();
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
         tx.commit();

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData deleteCfgGroup(String id, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      ArtifactToken cfgGroupArtToken;
      if (Strings.isNumeric(id)) {
         cfgGroupArtToken = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(id)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokenOrSentinel();
      } else {
         cfgGroupArtToken =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  id).asArtifactTokenOrSentinel();
      }
      if (cfgGroupArtToken.isInvalid()) {
         results.errorf("Configuration Group does not exist");
         return results;
      }
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }

         Iterable<String> deleteApps = orcsApi.getQueryFactory().tupleQuery().getTuple2(
            CoreTupleTypes.ViewApplicability, branch, cfgGroupArtToken);

         TransactionBuilder txApps = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Delete Applicabilities associated with ConfigurationGroup");
         for (String app : deleteApps) {
            txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroupArtToken, app);
         }
         txApps.commit();
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user, "Delete Cfg Group");
         tx.deleteArtifact(cfgGroupArtToken);
         tx.commit();
         results.getIds().add(id);
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData syncConfigGroup(BranchId branch, String id, UserId account, XResultData results) {
      if (results == null) {
         results = new XResultData();
      }
      ArtifactReadable cfgGroup;
      if (Strings.isNumeric(id)) {
         cfgGroup = orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(id)).andIsOfType(
            CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
               CoreArtifactTokens.PlCfgGroupsFolder).getArtifact();
      } else {
         cfgGroup =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  id).getArtifact();
      }

      if (cfgGroup.isValid() & cfgGroup.getExistingRelationTypes().contains(CoreRelationTypes.PlConfigurationGroup)) {
         List<ArtifactToken> views =
            orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(CoreRelationTypes.PlConfigurationGroup_Group,
               cfgGroup).asArtifactTokens();

         for (FeatureDefinition feature : getFeatureDefinitionData(branch)) {
            String resultApp = null;
            if (feature.getValues().contains("Included")) {
               resultApp = feature.getName() + " = Excluded";
            }
            for (ArtifactId viewId : views) {

               String applicability = orcsApi.getQueryFactory().applicabilityQuery().getExistingFeatureApplicability(
                  branch, viewId, feature.getName());

               if (feature.getValues().contains("Included")) {
                  if (applicability.equals(feature.getName() + " = Included")) {

                     resultApp = applicability;
                     break;
                  }
               } else {
                  if (resultApp == null) {
                     resultApp = applicability;
                  } else {
                     if (!resultApp.equals(applicability)) {
                        //error
                        results.error(
                           "Updating Group: " + cfgGroup.getName() + " (" + views.toString() + "). Applicabilities differ for non-binary feature: " + feature.getName());
                     }
                  }
               }

            }
            if (results.isSuccess()) {
               createApplicabilityForView(cfgGroup, resultApp, account, branch);
            }
         }
         for (ArtifactId viewId : views) {
            for (ApplicabilityToken applicabilityToken : orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(
               viewId, branch)) {
               if (applicabilityToken.getName().contains("|") || applicabilityToken.getName().contains("&")) {
                  createApplicabilityForView(cfgGroup, applicabilityToken.getName(), account, branch);
               }
            }
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
   public String applyApplicabilityToFiles(BranchId branch, ArtifactId view, boolean commentNonApplicableBlocks, String sourcePath, String stagePath) {
      ArtifactToken viewToken = orcsApi.getQueryFactory().fromBranch(branch).andId(view).asArtifactToken();
      return new BlockApplicabilityOps(orcsApi, logger, branch, viewToken).applyApplicabilityToFiles(
         commentNonApplicableBlocks, sourcePath, stagePath);
   }

}