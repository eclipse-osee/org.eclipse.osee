/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.applicability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
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
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class OrcsApplicabilityOps implements OrcsApplicability {

   private final OrcsApi orcsApi;
   private ArtifactToken plFolder = ArtifactToken.SENTINEL;
   private ArtifactToken featureFolder = ArtifactToken.SENTINEL;
   private ArtifactToken productsFolder = ArtifactToken.SENTINEL;

   public OrcsApplicabilityOps(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * @return config as defined in Feature artifacts
    */
   @Override
   public ApplicabilityBranchConfig getConfig(BranchId branchId, boolean showAll) {
      ApplicabilityBranchConfig config = new ApplicabilityBranchConfig();
      Branch branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getExactlyOne();
      config.setBranch(branch);

      // Load all products (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).getResults().getList();
      Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      for (ArtifactToken branchView : branchViews) {
         config.addView(branchView);
         branchViewsMap.put(branchView,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, branchView));
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
         return null;
      }

      FeatureDefinition lFeature = getFeature(featureDef.getName(), tx.getBranch());
      if (action != null && action.equals("add") && lFeature.isValid()) {
         results.error("Feature: " + lFeature.getName() + " already exists.");
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
   }

   @Override
   public List<BranchViewToken> getApplicabilityBranches() {
      List<BranchViewToken> tokens = new ArrayList<>();
      for (Branch branch : orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(
         false).andIsOfType(BranchType.BASELINE, BranchType.WORKING).andStateIs(BranchState.CREATED,
            BranchState.MODIFIED).getResults().getList()) {
         if (orcsApi.getQueryFactory().fromBranch(branch).andId(CoreArtifactTokens.ProductLineFolder).exists()) {
            tokens.add(new BranchViewToken(branch, branch.getName(), branch.getViewId()));
         }
      }
      return tokens;
   }

   @Override
   public ArtifactToken getFeaturesFolder(BranchId branch) {
      if (featureFolder.isInvalid()) {
         featureFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.FeaturesFolder).getArtifactOrSentinal();
      }
      if (featureFolder.isInvalid()) {
         featureFolder = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals(
            CoreArtifactTokens.FeaturesFolder.getName()).asArtifactTokenOrSentinel();
      }
      return featureFolder;
   }

   @Override
   public ArtifactToken getProductsFolder(BranchId branch) {
      if (productsFolder.isInvalid()) {
         productsFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.ProductsFolder).getArtifactOrSentinal();
      }
      if (productsFolder.isInvalid()) {
         productsFolder = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals(
            CoreArtifactTokens.ProductsFolder.getName()).asArtifactTokenOrSentinel();
      }
      if (productsFolder.isInvalid()) {
         productsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andNameEquals("Products").asArtifactTokenOrSentinel();
      }
      return productsFolder;
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
   public String convertConfigToArtifact(BranchId branch) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem,
         "Convert Feature Defs to Artifact");
      List<ArtifactReadable> featureDefinitionArts = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.FeatureDefinition).getResults().getList();

      List<FeatureDefinition> featureDefinitionFromJson = new ArrayList<>();

      for (ArtifactReadable art : featureDefinitionArts) {
         String json = art.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
         // convert legacy field name to new
         json = json.replaceAll("\"type\"", "\"valueType\"");
         FeatureDefinition[] readValue = JsonUtil.readValue(json, FeatureDefinition[].class);
         featureDefinitionFromJson.addAll(Arrays.asList(readValue));
      }
      XResultData results = null;
      for (FeatureDefinition feature : featureDefinitionFromJson) {
         FeatureDefinition featureArt = orcsApi.getApplicabilityOps().getFeature(feature.getName(), branch);
         if (featureArt.isValid()) {
            if (!featureArt.equals(feature)) {
               // type goes to multivalued
               if (feature.getValueType().equals("single")) {
                  // do nothing
               } else if (feature.getValueType().equals("multiple")) {
                  feature.setMultiValued(true);
               } else {
                  throw new IllegalArgumentException(
                     String.format("Unexpected value type [%s]", feature.getValueType()));
               }

               // set feature type
               feature.setType("String");
               createUpdateFeatureDefinition(feature, "update", tx, results);
            }
         } else {
            feature.setType("String");
            createUpdateFeatureDefinition(feature, "add", tx, results);
         }

      }
      tx.commit();
      return AHTML.simplePage("Completed");
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
               } catch (Exception ex) {
                  results.error(Lib.exceptionToString(ex));
               }
            }
            //If copyFrom indicated; set applicability for each feature to match
            if (view.copyFrom != null) {
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
   public List<FeatureDefinition> getFeatureDefinitionData(BranchId branch) {
      return orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(branch);

   }
}
