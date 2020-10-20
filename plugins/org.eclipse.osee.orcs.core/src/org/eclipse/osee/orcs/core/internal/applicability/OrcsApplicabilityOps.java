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
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.ViewDefinition;
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
      for (ArtifactToken branchView : branchViews) {
         config.addView(branchView);
         branchViewsMap.put(branchView,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, branchView));
      }
      for (ArtifactToken group : groups) {
         config.addGroup(group);
         branchViewsMap.put(group,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, group));
      }
      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).getResults().getList();
      Collections.sort(featureArts, new NamedComparator(SortOrder.ASCENDING));
      for (ArtifactToken featureArt : featureArts) {
         FeatureDefinition fDef = getFeatureDefinition(featureArt);
         config.addFeature(fDef);
      }

      // Setup Features column and add maps in order
      for (FeatureDefinition fDef : config.getFeatureIdToFeature().values()) {
         Map<String, String> viewToValue = new HashMap<>(config.getFeatureIdToFeature().values().size() + 1);
         /**
          * Note: Confusing, but Features is the header of the first column, so add it here. The rest of the columns
          * headers will be the view names added below.
          */
         viewToValue.put("feature", fDef.getName());
         viewToValue.put("id", fDef.getIdString());
         viewToValue.put("description", fDef.getDescription());
         if (showAll) {
            viewToValue.put("valueType", fDef.getValueType());
            viewToValue.put("values",
               org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", fDef.getValues()));
            viewToValue.put("defaultValue", fDef.getDefaultValue());
            viewToValue.put("multiValued", String.valueOf(fDef.isMultiValued()));
         }
         config.addFeatureToValueMap(viewToValue);

      }

      // Add views and values
      int count = 0;
      for (FeatureDefinition fDef : config.getFeatureIdToFeature().values()) {
         for (ArtifactToken view : config.getViews()) {
            Map<String, String> viewToValue = config.getFeatureToValues(count);
            String viewToFeatureValue = getViewToFeatureValue(view, fDef, branchViewsMap);
            viewToValue.put(view.getName().toLowerCase(), viewToFeatureValue);
         }
         for (ArtifactToken group : config.getGroups()) {
            Map<String, String> viewToValue = config.getFeatureToValues(count);
            String viewToFeatureValue = getViewToFeatureValue(group, fDef, branchViewsMap);
            viewToValue.put(group.getName().toLowerCase(), viewToFeatureValue);
         }
         count++;
      }

      return config;
   }

   private String getViewToFeatureValue(ArtifactToken view, FeatureDefinition fDef, Map<ArtifactId, Map<String, List<String>>> branchViewsMap) {
      Map<String, List<String>> map = branchViewsMap.get(view);
      //
      List<String> list = map.get(fDef.getName());
      if (list == null) {
         return "";
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list);
   }

   @Override
   public ViewDefinition getViewDefinition(ArtifactToken featureArt) {
      ArtifactReadable art = (ArtifactReadable) featureArt;
      ViewDefinition view = new ViewDefinition();
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
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Update Feature " + feature.toStringWithId());

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
               "Set Defaults for Products/Views for New Feature:  " + feature.toStringWithId());

            List<ArtifactReadable> branchViews = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andIsOfType(
               CoreArtifactTypes.BranchView).getResults().getList();
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
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Delete Feature " + featureArt.toStringWithId());
         List<ArtifactReadable> branchViews = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andIsOfType(
            CoreArtifactTypes.BranchView).getResults().getList();
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
         ViewDefinition view = getView(viewId.getIdString(), branch);
         if (orcsApi.getQueryFactory().applicabilityQuery().applicabilityExistsOnBranchView(branch, viewId,
            applicability)) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Remove applicability " + applicability + " from configuration: " + view.getName());
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
   public ViewDefinition getView(String view, BranchId branch) {
      ViewDefinition viewDef = new ViewDefinition();
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
   public XResultData createUpdateView(ViewDefinition view, String action, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      if (!Strings.isValid(view.getName())) {
         results.errorf("Name can not be empty for product %s", view.getId());
         return results;
      }

      ViewDefinition xView = getView(view.getName(), branch);
      if ((action.equals("edit"))) {
         results = copyFromView(branch, ArtifactId.valueOf(xView), view.copyFrom, account);
      } else {
         if ((action.equals("add"))) {
            if (xView.isValid()) {
               results.errorf("Product Name is already in use.");
               return results;
            }
            if ((xView.isInvalid())) {
               try {
                  UserId user = account;
                  if (user == null) {
                     user = SystemUser.OseeSystem;
                  }
                  TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
                     "Create View " + view.toStringWithId());
                  createUpdateViewDefinition(view, tx);
                  tx.commit();
                  ViewDefinition newView = getView(view.getName(), branch);
                  TransactionBuilder tx2 = orcsApi.getTransactionFactory().createTransaction(branch, user,
                     "Create Config and Base applicabilities on new view: " + view.getName());
                  tx2.createApplicabilityForView(ArtifactId.valueOf(newView.getId()), "Base");
                  tx2.createApplicabilityForView(ArtifactId.valueOf(newView.getId()), "Config = " + view.getName());
                  tx2.commit();
               } catch (Exception ex) {
                  results.error(Lib.exceptionToString(ex));
               }
            }
            //If copyFrom indicated; set applicability for each feature to match
            if (view.getCopyFrom().isValid()) {
               ViewDefinition nView = getView(view.getName(), branch);
               if (nView.isValid()) {
                  results = copyFromView(branch, ArtifactId.valueOf(nView), view.copyFrom, account);
               } else {
                  results.error("View to copy from does not exist");
               }
            }
         }
      }
      return results;
   }

   private ArtifactToken createUpdateViewDefinition(ViewDefinition view, TransactionBuilder tx) {
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
         ViewDefinition viewDef = getView(view, branch);
         Iterable<String> deleteApps = orcsApi.getQueryFactory().tupleQuery().getTuple2(
            CoreTupleTypes.ViewApplicability, branch, ArtifactId.valueOf(viewDef.getId()));

         ArtifactToken viewArt = (ArtifactToken) viewDef.getData();
         if (viewArt.isValid()) {
            TransactionBuilder txApps = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Delete Applicabilities associated with " + view);
            for (String app : deleteApps) {
               txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, ArtifactId.valueOf(viewDef.getId()), app);
            }
            txApps.commit();
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Delete View " + viewArt.toStringWithId());
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
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create " + applicability + " applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.startsWith("ConfigurationGroup =")) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create " + applicability + " applicability");
         tx.createApplicabilityForView(viewId, applicability);
         tx.commit();
         return results;
      }
      if (applicability.equals("Base")) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create " + applicability + " applicability on view: " + getView(viewId.getIdString(), branch).getName());
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
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Apply " + applicability + " applicability");
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
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
               "Apply " + applicability + " applicability");
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
                  "Remove invalid " + app.getName() + " compound applicability");
               tx.deleteTuple2(CoreTupleTypes.ViewApplicability, viewId, app.getName());
               tx.commit();
            }
         } else {
            if (validApplicability) {
               TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
                  "Apply " + app.getName() + " now valid compound applicability");
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
   public XResultData createCfgGroup(String groupName, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      if (!Strings.isValid(groupName)) {
         results.errorf("Name can not be empty for Configuration Group: %s", groupName);
         return results;
      }

      //make sure the groupName does not exist already as a group
      if (orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
         CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
            groupName).exists()) {
         results.errorf("Configuration Group Name already exists");
         return results;
      }
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create PL Configuration Group " + groupName);
         createConfigurationGroup(groupName, tx);
         tx.commit();
         ArtifactId newGrp =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andNameEquals(
               groupName).asArtifactId();
         TransactionBuilder tx2 = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Create Config and Base applicabilities on new view: " + groupName);
         tx2.createApplicabilityForView(newGrp, "Base");
         tx2.createApplicabilityForView(newGrp, "ConfigurationGroup = " + groupName);
         for (FeatureDefinition feature : orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(
            branch)) {
            tx2.createApplicabilityForView(newGrp, feature.getName() + " = " + feature.getDefaultValue());
         }
         tx2.commit();
      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   private ArtifactToken createConfigurationGroup(String groupName, TransactionBuilder tx) {
      ArtifactToken vDefArt = null;
      Long artId = Lib.generateArtifactIdAsInt();

      vDefArt = tx.createArtifact(getPlConfigurationGroupsFolder(tx.getBranch()), CoreArtifactTypes.GroupArtifact,
         groupName, artId);
      tx.setName(vDefArt, groupName);
      // reload artifact to return

      return orcsApi.getQueryFactory().fromBranch(vDefArt.getBranch()).andId(vDefArt).getArtifactOrSentinal();
   }

   @Override
   public XResultData relateCfgGroupToView(String groupName, String viewName, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      //make sure the groupName does not exist already as a group
      ArtifactToken cfgGroup =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
               groupName).getArtifactOrSentinal();
      ArtifactToken view =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewName).getArtifactOrSentinal();
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
            "Relate view: " + viewName + " to PL Configuration Group " + groupName);
         //relate to group
         tx.relate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
         tx.createApplicabilityForView(view, "ConfigurationGroup = " + groupName);
         tx.createApplicabilityForView(cfgGroup, "Config = " + view.getName());
         tx.commit();

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData unrelateCfgGroupToView(String groupName, String viewName, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      //make sure the groupName does not exist already as a group
      ArtifactToken cfgGroup =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
               groupName).getArtifactOrSentinal();
      ArtifactToken view =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).andNameEquals(
            viewName).getArtifactOrSentinal();
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
            "Un-Relate view: " + viewName + " to PL Configuration Group " + groupName);
         //relate to group
         tx.unrelate(cfgGroup, CoreRelationTypes.PlConfigurationGroup_Group, view);
         tx.deleteTuple2(CoreTupleTypes.ViewApplicability, view, "ConfigurationGroup = " + groupName);
         tx.commit();

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }

      return results;
   }

   @Override
   public XResultData deleteCfgGroup(String groupName, BranchId branch, UserId account) {
      XResultData results = new XResultData();
      ArtifactToken cfgGroup =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
            CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
               groupName).getArtifactOrSentinal();

      if (cfgGroup.isInvalid()) {
         results.errorf("Configuration Group does not exist");
         return results;
      }
      try {
         UserId user = account;
         if (user == null) {
            user = SystemUser.OseeSystem;
         }

         Iterable<String> deleteApps =
            orcsApi.getQueryFactory().tupleQuery().getTuple2(CoreTupleTypes.ViewApplicability, branch, cfgGroup);

         TransactionBuilder txApps = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Delete Applicabilities associated with " + groupName);
         for (String app : deleteApps) {
            txApps.deleteTuple2(CoreTupleTypes.ViewApplicability, cfgGroup, app);
         }
         txApps.commit();
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, user,
            "Delete Cfg Group " + cfgGroup.toStringWithId());
         tx.deleteArtifact(cfgGroup);
         tx.commit();

      } catch (Exception ex) {
         results.error(Lib.exceptionToString(ex));
      }
      return results;
   }

   @Override
   public XResultData updateConfigGroup(BranchId branch, String cfgGroup, UserId account, XResultData results) {
      if (results == null) {
         results = new XResultData();
      }
      ArtifactToken cfgGroupArtToken;
      if (Strings.isNumeric(cfgGroup)) {
         cfgGroupArtToken =
            orcsApi.getQueryFactory().fromBranch(branch).andId(ArtifactId.valueOf(cfgGroup)).andIsOfType(
               CoreArtifactTypes.GroupArtifact).andRelatedTo(CoreRelationTypes.DefaultHierarchical_Parent,
                  CoreArtifactTokens.PlCfgGroupsFolder).asArtifactTokenOrSentinel();
      } else {
         cfgGroupArtToken =
            orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.GroupArtifact).andRelatedTo(
               CoreRelationTypes.DefaultHierarchical_Parent, CoreArtifactTokens.PlCfgGroupsFolder).andNameEquals(
                  cfgGroup).asArtifactTokenOrSentinel();
      }
      if (cfgGroupArtToken.isValid()) {
         List<ArtifactToken> views =
            orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(CoreRelationTypes.PlConfigurationGroup_Group,
               cfgGroupArtToken).asArtifactTokens();

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
                           "Updating Group: " + cfgGroupArtToken.getName() + " (" + views.toString() + "). Applicabilities differ for non-binary feature: " + feature.getName());
                     }
                  }
               }

            }
            if (results.isSuccess()) {
               createApplicabilityForView(cfgGroupArtToken, resultApp, account, branch);
            }
         }
         for (ArtifactId viewId : views) {
            for (ApplicabilityToken applicabilityToken : orcsApi.getQueryFactory().applicabilityQuery().getViewApplicabilityTokens(
               viewId, branch)) {
               if (applicabilityToken.getName().contains("|") || applicabilityToken.getName().contains("&")) {
                  createApplicabilityForView(cfgGroupArtToken, applicabilityToken.getName(), account, branch);
               }
            }
         }
      } else {
         results.error("Invalid Configuration Group name.");
      }
      return results;
   }

   @Override
   public XResultData updateConfigGroup(BranchId branch, UserId account) {
      XResultData results = new XResultData();
      for (ArtifactToken group : orcsApi.getQueryFactory().applicabilityQuery().getConfigurationGroupsForBranch(
         branch)) {
         updateConfigGroup(branch, group.getIdString(), account, results);
      }
      return results;
   }

   @Override
   public String evaluateApplicabilityExpression(BranchId branch, ArtifactToken view, ApplicabilityBlock applic) {
      return new BlockApplicabilityOps(orcsApi, logger, branch, view).evaluateApplicabilityExpression(applic);
   }

   @Override
   public String applyApplicabilityToFiles(BranchId branch, ArtifactId view, String sourcePath, Map<String, String> fileExtensionToCommentPrefix) {
      ArtifactToken viewToken = orcsApi.getQueryFactory().fromBranch(branch).andId(view).asArtifactToken();
      return new BlockApplicabilityOps(orcsApi, logger, branch, viewToken).applyApplicabilityToFiles(sourcePath,
         fileExtensionToCommentPrefix);
   }
}